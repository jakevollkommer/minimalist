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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.DynamicObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.NPC;
import net.runelite.api.Renderable;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.WallObjectSpawned;
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
	// written on the client thread; volatile because the renderer thread reads
	// hiddenNpcIds and hiddenStatueRenderables through the draw listener
	private volatile Set<Integer> hiddenObjectIds = Set.of();
	private volatile Set<Integer> hiddenNpcIds = Set.of();
	private volatile Set<Integer> hiddenWidgetComponents = Set.of();
	private volatile Set<Renderable> hiddenStatueRenderables = Set.of();
	private boolean hideInactiveStatues;

	/**
	 * The guardian statues present in the scene. Statues stay for the whole game and
	 * animate between active and inactive, so they are tracked and hidden per-frame
	 * rather than removed.
	 */
	private final Set<GameObject> guardianStatues = new HashSet<>();

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
		// all set mutation happens on the client thread so spawn events, rescans,
		// and the draw listener always agree on the current sets
		clientThread.invokeLater(() ->
		{
			rebuildHiddenSets();
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
			boolean objectsWereHidden = !hiddenObjectIds.isEmpty();
			hiddenObjectIds = Set.of();
			hiddenNpcIds = Set.of();
			hiddenWidgetComponents = Set.of();
			hiddenStatueRenderables = Set.of();
			hideInactiveStatues = false;
			guardianStatues.clear();

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
		rebuildHiddenSets();

		previousWidgets.stream()
			.filter(component -> !hiddenWidgetComponents.contains(component))
			.forEach(component -> setWidgetHidden(component, false));

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		// Removed objects can only come back with a scene reload; newly added ones
		// are handled by a rescan of the already-loaded scene.
		boolean needsSceneReload = previousObjectIds.stream()
			.anyMatch(objectId -> !hiddenObjectIds.contains(objectId));
		if (needsSceneReload)
		{
			client.setGameState(GameState.LOADING);
			return;
		}

		rescanSceneIfLoggedIn();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING)
		{
			guardianStatues.clear();
			hiddenStatueRenderables = Set.of();
			return;
		}

		// safety net: after every scene load (login, region change, or our own
		// restore reload) sweep the fresh scene for anything spawn events missed
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(this::rescanSceneIfLoggedIn);
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		trackIfGuardianStatue(event.getGameObject());
		hideIfCurated(event.getGameObject(), event.getTile());
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		guardianStatues.remove(event.getGameObject());
	}

	private void trackIfGuardianStatue(GameObject gameObject)
	{
		if (GuardiansOfTheRift.GUARDIAN_STATUE_OBJECTS.contains(gameObject.getId()))
		{
			guardianStatues.add(gameObject);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Set<Renderable> previouslyHidden = hiddenStatueRenderables;
		hiddenStatueRenderables = currentlyInactiveStatueRenderables();

		// TODO temporary diagnostics for statue behavior; remove before hub submission
		if (previouslyHidden.size() != hiddenStatueRenderables.size())
		{
			log.info("[minimalist-diag] inactive statues hidden: {} of {} tracked",
				hiddenStatueRenderables.size(), guardianStatues.size());
		}
	}

	private Set<Renderable> currentlyInactiveStatueRenderables()
	{
		if (!hideInactiveStatues || guardianStatues.isEmpty())
		{
			return Set.of();
		}

		return guardianStatues.stream()
			.filter(MinimalistPlugin::isInactiveStatue)
			.map(GameObject::getRenderable)
			.filter(Objects::nonNull)
			.collect(Collectors.toUnmodifiableSet());
	}

	private static boolean isInactiveStatue(GameObject statue)
	{
		Renderable renderable = statue.getRenderable();
		if (!(renderable instanceof DynamicObject))
		{
			return false;
		}

		Animation animation = ((DynamicObject) renderable).getAnimation();
		boolean isActive = animation != null && animation.getId() == GuardiansOfTheRift.ACTIVE_GUARDIAN_ANIMATION;
		return !isActive;
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

	private boolean shouldDraw(Renderable renderable, boolean drawingUi)
	{
		if (renderable instanceof NPC)
		{
			return !hiddenNpcIds.contains(((NPC) renderable).getId());
		}

		return !hiddenStatueRenderables.contains(renderable);
	}

	private void hideIfCurated(TileObject spawnedObject, Tile tile)
	{
		if (hiddenObjectIds.isEmpty() || !isCuratedObject(spawnedObject))
		{
			return;
		}

		removeFromScene(spawnedObject, tile);
	}

	private boolean isCuratedObject(TileObject spawnedObject)
	{
		// exact spawned-id matching only: curated sets deliberately exclude multiloc
		// bases, so transformed states are never hidden by accident
		return hiddenObjectIds.contains(spawnedObject.getId());
	}

	private void removeFromScene(TileObject hiddenObject, Tile tile)
	{
		WorldView worldView = hiddenObject.getWorldView();
		if (worldView == null)
		{
			return;
		}

		if (hiddenObject instanceof GameObject)
		{
			worldView.getScene().removeGameObject((GameObject) hiddenObject);
			return;
		}

		if (hiddenObject instanceof GroundObject)
		{
			// removing just the ground object keeps the tile's floor intact
			tile.setGroundObject(null);
			return;
		}

		// Wall and decorative objects have no individual removal API; removing the
		// tile takes everything on it with it, including the floor.
		worldView.getScene().removeTile(tile);
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

		Arrays.stream(worldView.getScene().getTiles())
			.flatMap(Arrays::stream)
			.flatMap(Arrays::stream)
			.filter(Objects::nonNull)
			.forEach(this::scanTile);
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
				trackIfGuardianStatue(gameObject);
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
			toggled(config.gotrBarriersAndCells(), GuardiansOfTheRift.BARRIER_NPCS));

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
}
