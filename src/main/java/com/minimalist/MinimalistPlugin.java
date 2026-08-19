/*
 * Copyright (c) 2026, Jake Vollkommer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.minimalist;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Value;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.Renderable;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.Scene;
import net.runelite.api.SceneTileModel;
import net.runelite.api.SceneTilePaint;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Minimalist",
	description = "Hide non-interactable scenery objects, NPCs, and HUD elements at supported content",
	tags = {"hide", "hider", "scenery", "declutter", "minimal", "gotr", "guardians", "rift"}
)
@lombok.extern.slf4j.Slf4j
public class MinimalistPlugin extends Plugin
{
	// written on the client thread; volatile because the renderer thread reads them
	// through the draw listener
	private volatile Set<Integer> hiddenObjectIds = Set.of();
	private volatile Set<Integer> hiddenNpcIds = Set.of();
	private volatile Set<Integer> hiddenWidgetComponents = Set.of();
	private volatile boolean hideInactiveStatues;
	private volatile boolean hideProjectiles;
	private volatile boolean sceneIsGotr;
	private boolean hideAltarScenery;
	private boolean hideArenaGenericScenery;

	// --- guardian statue stand-ins ---
	// The real statues are removed from the scene; client-side stand-in models are
	// rendered for the two active guardians and any guardian whose portal talisman is
	// in the inventory. Active state comes from the HUD update script, event-driven.
	private final Map<Integer, StatueSite> statueSites = new HashMap<>();
	private final Map<Integer, RuneLiteObject> standIns = new HashMap<>();
	private final Set<Integer> heldTalismanStatues = new HashSet<>();
	private int activeElementalStatue = -1;
	private int activeCatalyticStatue = -1;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Hooks hooks;

	@Inject
	private MinimalistConfig config;

	@Provides
	MinimalistConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MinimalistConfig.class);
	}

	@Override
	protected void startUp()
	{
		hooks.registerRenderableDrawListener(drawListener);
		clientThread.invokeLater(() ->
		{
			rebuildHiddenSets();
			refreshHeldTalismans();
			rescanSceneIfLoggedIn();
		});
	}

	@Override
	protected void shutDown()
	{
		hooks.unregisterRenderableDrawListener(drawListener);
		clientThread.invokeLater(() ->
		{
			Set<Integer> widgetsToRestore = hiddenWidgetComponents;
			boolean objectsWereHidden = !hiddenObjectIds.isEmpty() || hideInactiveStatues;
			hiddenObjectIds = Set.of();
			hiddenNpcIds = Set.of();
			hiddenWidgetComponents = Set.of();
			hideInactiveStatues = false;
			clearStandIns();
			statueSites.clear();

			widgetsToRestore.forEach(component -> setWidgetHidden(component, false));
			if (objectsWereHidden && client.getGameState() == GameState.LOGGED_IN)
			{
				client.setGameState(GameState.LOADING);
			}
		});
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!MinimalistConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		clientThread.invokeLater(this::applyConfigChange);
	}

	private void applyConfigChange()
	{
		Set<Integer> previousObjectIds = hiddenObjectIds;
		Set<Integer> previousWidgets = hiddenWidgetComponents;
		boolean previousAltarScenery = hideAltarScenery;
		boolean previousArenaGenerics = hideArenaGenericScenery;
		boolean previousStatues = hideInactiveStatues;
		rebuildHiddenSets();

		previousWidgets.stream()
			.filter(component -> !hiddenWidgetComponents.contains(component))
			.forEach(component -> setWidgetHidden(component, false));

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		// object hiding only reliably takes effect during a scene load, so any change
		// to the hidden object sets is applied through a reload
		boolean objectSetChanged = !previousObjectIds.equals(hiddenObjectIds)
			|| previousAltarScenery != hideAltarScenery
			|| previousArenaGenerics != hideArenaGenericScenery
			|| previousStatues != hideInactiveStatues;
		if (objectSetChanged)
		{
			client.setGameState(GameState.LOADING);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			statueSites.clear();
			clearStandIns();
			return;
		}

		// safety net: after every scene load sweep the fresh scene for anything the
		// spawn events missed
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(this::rescanSceneIfLoggedIn);
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		captureIfStatue(event.getGameObject());
		hideIfCurated(event.getGameObject(), event.getTile());
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		hideIfCurated(event.getWallObject(), event.getTile());
	}

	@Subscribe
	public void onDecorativeObjectSpawned(DecorativeObjectSpawned event)
	{
		hideIfCurated(event.getDecorativeObject(), event.getTile());
	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{
		hideIfCurated(event.getGroundObject(), event.getTile());
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		// interfaces are rebuilt by clientscripts whenever their values update, which
		// resets the hidden flag, so re-hide every client tick
		hiddenWidgetComponents.forEach(component -> setWidgetHidden(component, true));
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() != GuardiansOfTheRift.HUD_UPDATE_SCRIPT)
		{
			return;
		}

		Object[] arguments = event.getScriptEvent().getArguments();
		if (arguments == null || arguments.length <= GuardiansOfTheRift.HUD_ARG_ACTIVE_CATALYTIC)
		{
			return;
		}

		int elementalIndex = asInt(arguments[GuardiansOfTheRift.HUD_ARG_ACTIVE_ELEMENTAL]);
		int catalyticIndex = asInt(arguments[GuardiansOfTheRift.HUD_ARG_ACTIVE_CATALYTIC]);
		int newElemental = GuardiansOfTheRift.ELEMENTAL_STATUE_BY_INDEX.getOrDefault(elementalIndex, -1);
		int newCatalytic = GuardiansOfTheRift.CATALYTIC_STATUE_BY_INDEX.getOrDefault(catalyticIndex, -1);
		boolean changed = newElemental != activeElementalStatue || newCatalytic != activeCatalyticStatue;
		if (!changed)
		{
			return;
		}

		activeElementalStatue = newElemental;
		activeCatalyticStatue = newCatalytic;
		updateStandIns();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INV)
		{
			return;
		}

		refreshHeldTalismans();
		updateStandIns();
	}

	private void refreshHeldTalismans()
	{
		heldTalismanStatues.clear();
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory == null)
		{
			return;
		}

		GuardiansOfTheRift.TALISMAN_BY_STATUE.forEach((statueId, talismanId) ->
		{
			if (inventory.contains(talismanId))
			{
				heldTalismanStatues.add(statueId);
			}
		});
	}

	private void captureIfStatue(GameObject gameObject)
	{
		if (!GuardiansOfTheRift.GUARDIAN_STATUE_OBJECTS.contains(gameObject.getId()))
		{
			return;
		}

		statueSites.put(gameObject.getId(), new StatueSite(
			gameObject.getLocalLocation(), gameObject.getPlane(), gameObject.getOrientation()));
	}

	private void updateStandIns()
	{
		Set<Integer> desired = desiredVisibleStatues();

		standIns.entrySet().removeIf(entry ->
		{
			if (desired.contains(entry.getKey()))
			{
				return false;
			}

			entry.getValue().setActive(false);
			return true;
		});

		desired.forEach(this::ensureStandIn);
	}

	private Set<Integer> desiredVisibleStatues()
	{
		if (!hideInactiveStatues || !sceneIsGotr)
		{
			return Set.of();
		}

		Set<Integer> desired = new HashSet<>(heldTalismanStatues);
		if (activeElementalStatue != -1)
		{
			desired.add(activeElementalStatue);
		}
		if (activeCatalyticStatue != -1)
		{
			desired.add(activeCatalyticStatue);
		}

		desired.retainAll(statueSites.keySet());
		return desired;
	}

	private void ensureStandIn(int statueId)
	{
		StatueSite site = statueSites.get(statueId);
		RuneLiteObject standIn = standIns.get(statueId);
		if (standIn == null)
		{
			standIn = client.createRuneLiteObject();
			standIn.setModel(client.mergeModels(
				client.loadModelData(GuardiansOfTheRift.STATUE_PEDESTAL_MODEL),
				client.loadModelData(GuardiansOfTheRift.RUNE_MODEL_BY_STATUE.get(statueId))).light());
			standIn.setLocation(site.getLocalPoint(), site.getPlane());
			standIn.setOrientation(site.getOrientation());
			standIn.setShouldLoop(true);
			standIn.setActive(true);
			standIns.put(statueId, standIn);
		}

		boolean isActivePair = statueId == activeElementalStatue || statueId == activeCatalyticStatue;
		int animationId = isActivePair
			? GuardiansOfTheRift.ACTIVE_GUARDIAN_ANIMATION
			: GuardiansOfTheRift.INACTIVE_GUARDIAN_ANIMATION;
		Animation currentAnimation = standIn.getAnimation();
		if (currentAnimation == null || currentAnimation.getId() != animationId)
		{
			standIn.setAnimation(client.loadAnimation(animationId));
		}
	}

	private void clearStandIns()
	{
		standIns.values().forEach(standIn -> standIn.setActive(false));
		standIns.clear();
	}

	/**
	 * The real statues are gone from the scene, so a synthesized Enter entry is added
	 * when hovering a stand-in's tile. The click fires the original object action; the
	 * object still exists server-side.
	 */
	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.getType() != MenuAction.WALK.getId() || standIns.isEmpty())
		{
			return;
		}

		standIns.keySet().stream()
			.filter(statueId -> isSiteAt(statueId, event.getActionParam0(), event.getActionParam1()))
			.findFirst()
			.ifPresent(statueId -> addEnterEntry(statueId, event));
	}

	private boolean isSiteAt(int statueId, int sceneX, int sceneY)
	{
		StatueSite site = statueSites.get(statueId);
		return site != null
			&& site.getLocalPoint().getSceneX() == sceneX
			&& site.getLocalPoint().getSceneY() == sceneY;
	}

	private void addEnterEntry(int statueId, MenuEntryAdded event)
	{
		StatueSite site = statueSites.get(statueId);
		String target = "<col=ffff>" + GuardiansOfTheRift.STATUE_NAMES.get(statueId);
		client.createMenuEntry(-1)
			.setOption("Enter")
			.setTarget(target)
			.setType(MenuAction.RUNELITE)
			.onClick(entry -> client.menuAction(
				site.getLocalPoint().getSceneX(),
				site.getLocalPoint().getSceneY(),
				MenuAction.GAME_OBJECT_FIRST_OPTION,
				statueId,
				-1,
				"Enter",
				target));
	}

	private boolean shouldDraw(Renderable renderable, boolean drawingUi)
	{
		if (renderable instanceof NPC)
		{
			return !hiddenNpcIds.contains(((NPC) renderable).getId());
		}

		if (renderable instanceof Projectile)
		{
			return !(hideProjectiles && sceneIsGotr);
		}

		return true;
	}

	private void hideIfCurated(TileObject spawnedObject, Tile tile)
	{
		recordIfUncuratedGotrObject(spawnedObject);

		// Removing during a scene load strips the tile references without affecting
		// rendering, which then blinds the post-load rescan — the only pass whose
		// removals visually stick. So removal only ever happens after the load.
		if (client.getGameState() != GameState.LOGGED_IN || !isHiddenObject(spawnedObject))
		{
			return;
		}

		removeFromScene(spawnedObject, tile);
	}

	private boolean isHiddenObject(TileObject spawnedObject)
	{
		int objectId = spawnedObject.getId();
		if (hiddenObjectIds.contains(objectId))
		{
			return true;
		}

		if (hideInactiveStatues && GuardiansOfTheRift.GUARDIAN_STATUE_OBJECTS.contains(objectId))
		{
			return true;
		}

		// generic world IDs are hidden only inside their intended regions
		boolean isAltarScenery = hideAltarScenery
			&& GuardiansOfTheRift.ALTAR_SCENERY_OBJECTS.contains(objectId)
			&& GuardiansOfTheRift.ALTAR_REGIONS.contains(spawnedObject.getWorldLocation().getRegionID());
		if (isAltarScenery)
		{
			return true;
		}

		return hideArenaGenericScenery
			&& GuardiansOfTheRift.ARENA_GENERIC_SCENERY_OBJECTS.contains(objectId)
			&& spawnedObject.getWorldLocation().getRegionID() == GuardiansOfTheRift.ARENA_REGION;
	}

	private void removeFromScene(TileObject hiddenObject, Tile tile)
	{
		WorldView worldView = hiddenObject.getWorldView();
		if (worldView == null)
		{
			return;
		}

		// TODO temporary diagnostics; remove before hub submission
		diagRemovedById.merge(hiddenObject.getId() + "/" + hiddenObject.getClass().getSimpleName(), 1, Integer::sum);

		Scene scene = worldView.getScene();
		if (hiddenObject instanceof GameObject)
		{
			scene.removeGameObject((GameObject) hiddenObject);
			return;
		}

		removeTilePreservingFloor(scene, tile);
	}

	/**
	 * Ground, wall, and decorative objects have no individual removal API, and clearing
	 * them off the tile does not un-draw them. Removing the whole tile does, but also
	 * deletes the floor — so capture the floor and put it back. Removal is skipped
	 * entirely when anything functional shares the tile (e.g. the passable Barrier on
	 * the guide tiles), because removeTile would take it too.
	 */
	private void removeTilePreservingFloor(Scene scene, Tile tile)
	{
		boolean tileHasKeptGameObject = Arrays.stream(tile.getGameObjects())
			.filter(Objects::nonNull)
			.anyMatch(gameObject -> !isHiddenObject(gameObject));
		if (tileHasKeptGameObject)
		{
			return;
		}

		SceneTilePaint floorPaint = tile.getSceneTilePaint();
		SceneTileModel floorModel = tile.getSceneTileModel();

		scene.removeTile(tile);

		tile.setSceneTilePaint(floorPaint);
		tile.setSceneTileModel(floorModel);
	}

	private void setWidgetHidden(int componentId, boolean hidden)
	{
		Widget widget = client.getWidget(componentId);
		boolean alreadyInDesiredState = widget == null || widget.isHidden() == hidden;
		if (alreadyInDesiredState)
		{
			return;
		}

		widget.setHidden(hidden);
	}

	private void rescanSceneIfLoggedIn()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			return;
		}

		sceneIsGotr = Arrays.stream(worldView.getScene().getMapRegions())
			.anyMatch(regionId -> regionId == GuardiansOfTheRift.ARENA_REGION);
		diagSceneIsGotr = sceneIsGotr;

		Arrays.stream(worldView.getScene().getTiles())
			.flatMap(Arrays::stream)
			.flatMap(Arrays::stream)
			.filter(Objects::nonNull)
			.forEach(this::scanTile);

		updateStandIns();
		logUncuratedGotrObjects();
	}

	private void scanTile(Tile tile)
	{
		Stream.of(tile.getWallObject(), tile.getDecorativeObject(), tile.getGroundObject())
			.filter(Objects::nonNull)
			.forEach(tileObject -> hideIfCurated(tileObject, tile));

		Arrays.stream(tile.getGameObjects())
			.filter(Objects::nonNull)
			.filter(gameObject -> isPrimaryTile(gameObject, tile))
			.forEach(gameObject ->
			{
				captureIfStatue(gameObject);
				hideIfCurated(gameObject, tile);
			});
	}

	/**
	 * Game objects can span multiple tiles; only handle them from their south-west tile.
	 */
	private static boolean isPrimaryTile(GameObject gameObject, Tile tile)
	{
		return gameObject.getSceneMinLocation().equals(tile.getSceneLocation());
	}

	private void rebuildHiddenSets()
	{
		hideInactiveStatues = config.gotrGuardianStatues();
		hideProjectiles = config.gotrProjectiles();
		hideAltarScenery = config.gotrAltarScenery();
		hideArenaGenericScenery = config.gotrAbyssScenery();
		hiddenObjectIds = union(
			toggled(config.gotrAbyssScenery(), GuardiansOfTheRift.ABYSS_SCENERY_OBJECTS),
			toggled(config.gotrGuardianRemains(), GuardiansOfTheRift.GUARDIAN_REMAINS_OBJECTS),
			toggled(config.gotrEssencePiles(), GuardiansOfTheRift.ESSENCE_PILE_OBJECTS),
			toggled(config.gotrBarriersAndCells(), GuardiansOfTheRift.BARRIER_AND_CELL_OBJECTS),
			toggled(config.gotrEntranceScenery(), GuardiansOfTheRift.ENTRANCE_SCENERY_OBJECTS),
			toggled(config.gotrRain(), GuardiansOfTheRift.RAIN_OBJECTS));

		hiddenNpcIds = union(
			toggled(config.gotrAbyssalCreatures(), GuardiansOfTheRift.ABYSSAL_CREATURE_NPCS),
			toggled(config.gotrSummonedGuardians(), GuardiansOfTheRift.SUMMONED_GUARDIAN_NPCS),
			toggled(config.gotrApprentices(), GuardiansOfTheRift.APPRENTICE_NPCS),
			toggled(config.gotrRick(), GuardiansOfTheRift.RICK_NPCS),
			toggled(config.gotrBarrierHitsplats(), GuardiansOfTheRift.BARRIER_NPCS));

		hiddenWidgetComponents = union(
			toggled(config.gotrHudPortalTimer(), Set.of(GuardiansOfTheRift.HUD_PORTAL_TIMER)),
			toggled(config.gotrHudGuardianCounter(), Set.of(GuardiansOfTheRift.HUD_GUARDIAN_COUNTER)),
			toggled(config.gotrHudPortalLocation(), Set.of(GuardiansOfTheRift.HUD_PORTAL_LOCATION)));
	}

	private static Set<Integer> toggled(boolean enabled, Set<Integer> curatedIds)
	{
		return enabled ? curatedIds : Set.of();
	}

	@SafeVarargs
	private static Set<Integer> union(Set<Integer>... sets)
	{
		return Stream.of(sets)
			.flatMap(Set::stream)
			.collect(Collectors.toUnmodifiableSet());
	}

	private static int asInt(Object argument)
	{
		return argument instanceof Integer ? (Integer) argument : Integer.parseInt(argument.toString());
	}

	@Value
	private static class StatueSite
	{
		LocalPoint localPoint;
		int plane;
		int orientation;
	}

	// TODO temporary diagnostics: inventory of visible uncurated objects while the GOTR
	// scene is loaded (arena + mines), logged once per rescan; remove before hub submission
	private final Map<Integer, String> diagUncuratedById = new HashMap<>();
	private boolean diagSceneIsGotr;
	private final Map<String, Integer> diagRemovedById = new HashMap<>();

	private void recordIfUncuratedGotrObject(TileObject tileObject)
	{
		boolean isCurated = hiddenObjectIds.contains(tileObject.getId())
			|| GuardiansOfTheRift.GUARDIAN_STATUE_OBJECTS.contains(tileObject.getId())
			|| GuardiansOfTheRift.ALTAR_SCENERY_OBJECTS.contains(tileObject.getId())
			|| GuardiansOfTheRift.ARENA_GENERIC_SCENERY_OBJECTS.contains(tileObject.getId());
		if (!diagSceneIsGotr || isCurated)
		{
			return;
		}

		diagUncuratedById.put(tileObject.getId(),
			client.getObjectDefinition(tileObject.getId()).getName() + "/" + tileObject.getClass().getSimpleName());
	}

	private void logUncuratedGotrObjects()
	{
		if (diagUncuratedById.isEmpty())
		{
			return;
		}

		log.info("[minimalist-diag] uncurated GOTR objects ({}) regions={}: {}", diagUncuratedById.size(),
			Arrays.toString(client.getTopLevelWorldView().getScene().getMapRegions()),
			new java.util.TreeMap<>(diagUncuratedById));
		diagUncuratedById.clear();

		log.info("[minimalist-diag] removed this rescan: {}", new java.util.TreeMap<>(diagRemovedById));
		diagRemovedById.clear();
	}
}
