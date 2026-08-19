// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist;

import com.google.inject.Provides;
import com.minimalist.altars.Altars;
import com.minimalist.gotr.GotrArena;
import com.minimalist.gotr.GotrHud;
import com.minimalist.gotr.GotrNpcs;
import com.minimalist.gotr.GuardianStatues;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.Renderable;
import net.runelite.api.Scene;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.PostMenuSort;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;
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
public class MinimalistPlugin extends Plugin implements RenderCallback
{
	// Draw suppression happens through RenderCallback: static scenery is filtered when
	// the scene uploads, and animated objects (the guardian statues) are filtered every
	// frame — so nothing is ever removed from the scene, everything stays hoverable,
	// clickable, and visible to other plugins.
	//
	// All fields below are written on the client thread and volatile because drawObject
	// is also called from the maploader thread during scene upload.
	private volatile Set<Integer> hiddenObjectIds = Set.of();
	private volatile Set<Integer> hiddenNpcIds = Set.of();
	private volatile Set<Integer> hiddenWidgetComponents = Set.of();
	private volatile Set<Integer> heldTalismanStatues = Set.of();
	private volatile boolean hideInactiveStatues;
	private volatile boolean hideProjectiles;
	private volatile boolean hideOtherPlayers;
	private volatile boolean hideOtherPlayers2d;
	private volatile boolean hideOtherPlayersPets;
	private volatile boolean hideAltarScenery;
	private volatile boolean hideArenaGenericScenery;
	private volatile boolean sceneIsGotr;
	private volatile boolean sceneHasAltar;
	private volatile Set<Integer> currentAltarHiddenNpcIds = Set.of();
	private volatile int activeElementalStatue = -1;
	private volatile int activeCatalyticStatue = -1;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private RenderCallbackManager renderCallbackManager;

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
		renderCallbackManager.register(this);
		clientThread.invokeLater(() ->
		{
			rebuildHiddenSets();
			refreshHeldTalismans();
			refreshSceneFlags();
			reloadSceneIfLoggedIn();
		});
	}

	@Override
	protected void shutDown()
	{
		renderCallbackManager.unregister(this);
		clientThread.invokeLater(() ->
		{
			Set<Integer> widgetsToRestore = hiddenWidgetComponents;
			hiddenObjectIds = Set.of();
			hiddenNpcIds = Set.of();
			hiddenWidgetComponents = Set.of();
			hideInactiveStatues = false;
			hideAltarScenery = false;
			hideArenaGenericScenery = false;

			widgetsToRestore.forEach(component -> setWidgetHidden(component, false));
			reloadSceneIfLoggedIn();
		});
	}

	// --- RenderCallback: the single place visibility is decided ---

	@Override
	public boolean addEntity(Renderable renderable, boolean drawingUi)
	{
		if (renderable instanceof NPC)
		{
			return !isHiddenNpc((NPC) renderable);
		}

		if (renderable instanceof Player)
		{
			return !isHiddenPlayer((Player) renderable, drawingUi);
		}

		if (renderable instanceof Projectile)
		{
			return !(hideProjectiles && isAtGotrContent());
		}

		return true;
	}

	@Override
	public boolean drawObject(Scene scene, TileObject object)
	{
		int objectId = object.getId();
		if (hiddenObjectIds.contains(objectId))
		{
			return false;
		}

		if (GuardianStatues.STATUE_OBJECTS.contains(objectId))
		{
			// animated objects pass through here every frame, so statue visibility
			// follows rotations and inventory instantly
			return !isHiddenStatue(objectId);
		}

		return !isHiddenAltarScenery(scene, objectId) && !isHiddenArenaGenericScenery(scene, objectId);
	}

	private boolean isHiddenNpc(NPC npc)
	{
		if (hiddenNpcIds.contains(npc.getId()))
		{
			return true;
		}

		if (hideOtherPlayersPets && isAtGotrContent() && isSomeoneElsesPet(npc))
		{
			return true;
		}

		return hideAltarScenery && currentAltarHiddenNpcIds.contains(npc.getId());
	}

	private boolean isSomeoneElsesPet(NPC npc)
	{
		return npc.getComposition().isFollower() && npc != client.getFollower();
	}

	private boolean isHiddenPlayer(Player player, boolean drawingUi)
	{
		boolean isOtherPlayerAtGotr = isAtGotrContent() && player != client.getLocalPlayer();
		if (!isOtherPlayerAtGotr)
		{
			return false;
		}

		return drawingUi ? hideOtherPlayers2d : hideOtherPlayers;
	}

	private boolean isHiddenStatue(int statueObjectId)
	{
		return hideInactiveStatues
			&& statueObjectId != activeElementalStatue
			&& statueObjectId != activeCatalyticStatue
			&& !heldTalismanStatues.contains(statueObjectId);
	}

	/**
	 * Altar decoration uses generic world IDs, so it is only hidden when the loaded
	 * scene spans altar regions. The gate reads the Scene parameter — correct even
	 * during scene upload — never per-object world coordinates.
	 */
	private boolean isHiddenAltarScenery(Scene scene, int objectId)
	{
		return hideAltarScenery && Altars.isAltarSceneryInScene(scene, objectId);
	}

	private boolean isHiddenArenaGenericScenery(Scene scene, int objectId)
	{
		return hideArenaGenericScenery
			&& GotrArena.ARENA_GENERIC_SCENERY_OBJECTS.contains(objectId)
			&& sceneContainsRegion(scene, GotrArena.ARENA_REGION);
	}

	private static boolean sceneContainsRegion(Scene scene, int regionId)
	{
		int[] sceneRegions = scene.getMapRegions();
		if (sceneRegions == null)
		{
			// no scene is loaded yet (login screen)
			return false;
		}

		return Arrays.stream(sceneRegions).anyMatch(sceneRegion -> sceneRegion == regionId);
	}

	// --- game state tracking ---

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		if (event.getScriptId() != GotrHud.UPDATE_SCRIPT)
		{
			return;
		}

		Object[] arguments = event.getScriptEvent().getArguments();
		if (arguments == null || arguments.length <= GotrHud.ARG_ACTIVE_CATALYTIC)
		{
			return;
		}

		int elementalIndex = asInt(arguments[GotrHud.ARG_ACTIVE_ELEMENTAL]);
		int catalyticIndex = asInt(arguments[GotrHud.ARG_ACTIVE_CATALYTIC]);
		activeElementalStatue = GuardianStatues.STATUE_BY_ELEMENTAL_INDEX.getOrDefault(elementalIndex, -1);
		activeCatalyticStatue = GuardianStatues.STATUE_BY_CATALYTIC_INDEX.getOrDefault(catalyticIndex, -1);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INV)
		{
			refreshHeldTalismans();
		}
	}

	private void refreshHeldTalismans()
	{
		ItemContainer inventory = client.getItemContainer(InventoryID.INV);
		if (inventory == null)
		{
			heldTalismanStatues = Set.of();
			return;
		}

		Set<Integer> held = new HashSet<>();
		GuardianStatues.TALISMAN_BY_STATUE.forEach((statueId, talismanId) ->
		{
			if (inventory.contains(talismanId))
			{
				held.add(statueId);
			}
		});
		heldTalismanStatues = Set.copyOf(held);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(this::refreshSceneFlags);
		}
	}

	private void refreshSceneFlags()
	{
		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			sceneIsGotr = false;
			sceneHasAltar = false;
			currentAltarHiddenNpcIds = Set.of();
			return;
		}

		Scene scene = worldView.getScene();
		sceneIsGotr = sceneContainsRegion(scene, GotrArena.ARENA_REGION);
		sceneHasAltar = Altars.hasAltarInScene(scene);
		currentAltarHiddenNpcIds = Altars.hiddenNpcsForScene(scene);
		warnIfSceneryHidingUnavailable(scene);

	}

	/** Entity hiding applies at GOTR and inside the runecrafting altars reached from it. */
	private boolean isAtGotrContent()
	{
		return sceneIsGotr || sceneHasAltar;
	}

	private boolean warnedAboutSoftwareRenderer;

	/**
	 * Scenery hiding works through the render callback, which only GPU renderers
	 * (GPU, GPU with region locker, 117HD) consult — the software renderer never asks.
	 * Warn once per session so players are not left wondering why nothing hides.
	 */
	private void warnIfSceneryHidingUnavailable(Scene scene)
	{
		boolean sceneHasHideableContent = sceneIsGotr || Altars.hasAltarInScene(scene);
		boolean sceneryHidingWanted = !hiddenObjectIds.isEmpty() || hideAltarScenery;
		boolean rendererSupportsHiding = client.getDrawCallbacks() != null;
		if (warnedAboutSoftwareRenderer || !sceneHasHideableContent || !sceneryHidingWanted || rendererSupportsHiding)
		{
			return;
		}

		warnedAboutSoftwareRenderer = true;
		client.addChatMessage(net.runelite.api.ChatMessageType.GAMEMESSAGE, "",
			"<col=e00a19>Minimalist:</col> hiding scenery requires a GPU renderer - enable the GPU plugin (or 117HD).",
			null);
	}

	// --- widgets ---

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		// interfaces are rebuilt by clientscripts whenever their values update, which
		// resets the hidden flag, so re-hide every client tick
		hiddenWidgetComponents.forEach(component -> setWidgetHidden(component, true));
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

	// --- menus ---

	/**
	 * Visually hidden statues stay clickable (they are still in the scene), so their
	 * menu entries are stripped to prevent misclicks. Active and talisman-held statues
	 * keep their menus, matching their visibility.
	 */
	@Subscribe
	public void onPostMenuSort(PostMenuSort event)
	{
		if (!hideInactiveStatues || !sceneIsGotr)
		{
			return;
		}

		MenuEntry[] entries = client.getMenuEntries();
		// cheap scan first: PostMenuSort runs every frame, so only allocate when needed
		boolean hasHiddenStatueEntry = false;
		for (MenuEntry entry : entries)
		{
			if (isHiddenStatueEntry(entry))
			{
				hasHiddenStatueEntry = true;
				break;
			}
		}

		if (!hasHiddenStatueEntry)
		{
			return;
		}

		client.setMenuEntries(Arrays.stream(entries)
			.filter(entry -> !isHiddenStatueEntry(entry))
			.toArray(MenuEntry[]::new));
	}

	private boolean isHiddenStatueEntry(MenuEntry entry)
	{
		return GuardianStatues.STATUE_OBJECTS.contains(entry.getIdentifier())
			&& isObjectAction(entry.getType())
			&& isHiddenStatue(entry.getIdentifier());
	}

	private static boolean isObjectAction(MenuAction action)
	{
		switch (action)
		{
			case GAME_OBJECT_FIRST_OPTION:
			case GAME_OBJECT_SECOND_OPTION:
			case GAME_OBJECT_THIRD_OPTION:
			case GAME_OBJECT_FOURTH_OPTION:
			case GAME_OBJECT_FIFTH_OPTION:
			case EXAMINE_OBJECT:
				return true;
			default:
				return false;
		}
	}

	// --- config ---

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
		rebuildHiddenSets();

		previousWidgets.stream()
			.filter(component -> !hiddenWidgetComponents.contains(component))
			.forEach(component -> setWidgetHidden(component, false));

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		// static scenery filtering is applied when the scene uploads, so changes to it
		// take one reload; statue, NPC, projectile, and widget changes apply live
		boolean staticSceneryChanged = !previousObjectIds.equals(hiddenObjectIds)
			|| previousAltarScenery != hideAltarScenery
			|| previousArenaGenerics != hideArenaGenericScenery;
		if (staticSceneryChanged)
		{
			reloadSceneIfLoggedIn();
		}
	}

	private void reloadSceneIfLoggedIn()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			client.setGameState(GameState.LOADING);
		}
	}

	private void rebuildHiddenSets()
	{
		hideInactiveStatues = config.gotrGuardianStatues();
		hideProjectiles = config.gotrProjectiles();
		hideOtherPlayers = config.gotrOtherPlayers();
		hideOtherPlayers2d = config.gotrOtherPlayers2d();
		hideOtherPlayersPets = config.gotrOtherPlayersPets();
		hideAltarScenery = config.gotrAltarScenery();
		hideArenaGenericScenery = config.gotrAbyssScenery();

		hiddenObjectIds = union(
			toggled(config.gotrAbyssScenery(), GotrArena.ABYSS_SCENERY_OBJECTS),
			toggled(config.gotrGuardianRemains(), GotrArena.GUARDIAN_REMAINS_OBJECTS),
			toggled(config.gotrEssencePiles(), GotrArena.ESSENCE_PILE_OBJECTS),
			toggled(config.gotrBarriersAndCells(), GotrArena.BARRIER_AND_CELL_OBJECTS),
			toggled(config.gotrEntranceScenery(), GotrArena.ENTRANCE_SCENERY_OBJECTS),
			toggled(config.gotrRain(), GotrArena.RAIN_OBJECTS));

		hiddenNpcIds = union(
			toggled(config.gotrAbyssalCreatures(), GotrNpcs.ABYSSAL_CREATURES),
			toggled(config.gotrSummonedGuardians(), GotrNpcs.SUMMONED_GUARDIANS),
			toggled(config.gotrApprentices(), GotrNpcs.APPRENTICES),
			toggled(config.gotrRick(), GotrNpcs.RICK),
			toggled(config.gotrBarrierHitsplats(), GotrNpcs.BARRIER_HITPOINT_HOLDERS));

		hiddenWidgetComponents = union(
			toggled(config.gotrHudPortalTimer(), Set.of(GotrHud.PORTAL_TIMER_COMPONENT)),
			toggled(config.gotrHudGuardianCounter(), Set.of(GotrHud.GUARDIAN_COUNTER_COMPONENT)),
			toggled(config.gotrHudPortalLocation(), Set.of(GotrHud.PORTAL_LOCATION_COMPONENT)));
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
}
