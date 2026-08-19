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
	private volatile boolean hideAltarScenery;
	private volatile boolean hideArenaGenericScenery;
	private volatile boolean sceneIsGotr;
	private volatile boolean sceneHasAltar;
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
			refreshSceneIsGotr();
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

			widgetsToRestore.forEach(component -> setWidgetHidden(component, false));
			reloadSceneIfLoggedIn();
		});
	}

	// --- RenderCallback ---

	@Override
	public boolean addEntity(Renderable renderable, boolean drawingUi)
	{
		if (renderable instanceof NPC)
		{
			return !isHiddenNpc((NPC) renderable);
		}

		if (renderable instanceof Projectile)
		{
			return !(hideProjectiles && sceneIsGotr);
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

		if (GuardiansOfTheRift.GUARDIAN_STATUE_OBJECTS.contains(objectId))
		{
			// animated objects pass through here every frame, so statue visibility
			// follows rotations and inventory instantly
			return !isHiddenStatue(objectId);
		}

		// Generic world IDs are hidden only when the loaded scene is the relevant area.
		// The gate uses the Scene parameter (always correct, even mid-upload) rather
		// than per-object world coordinates, which are unreliable during scene upload.
		boolean isAltarScenery = hideAltarScenery
			&& GuardiansOfTheRift.ALTAR_SCENERY_OBJECTS.contains(objectId)
			&& sceneContainsAnyRegion(scene, GuardiansOfTheRift.ALTAR_REGIONS);
		if (isAltarScenery)
		{
			return false;
		}

		boolean isArenaGenericScenery = hideArenaGenericScenery
			&& GuardiansOfTheRift.ARENA_GENERIC_SCENERY_OBJECTS.contains(objectId)
			&& sceneContainsAnyRegion(scene, Set.of(GuardiansOfTheRift.ARENA_REGION));
		return !isArenaGenericScenery;
	}

	private static boolean sceneContainsAnyRegion(Scene scene, Set<Integer> regions)
	{
		return Arrays.stream(scene.getMapRegions()).anyMatch(regions::contains);
	}

	private boolean isHiddenNpc(NPC npc)
	{
		if (hiddenNpcIds.contains(npc.getId()))
		{
			return true;
		}

		// generic world NPCs are hidden only while an altar scene is loaded
		return hideAltarScenery
			&& sceneHasAltar
			&& GuardiansOfTheRift.ALTAR_NPCS.contains(npc.getId());
	}

	private boolean isHiddenStatue(int statueObjectId)
	{
		return hideInactiveStatues
			&& statueObjectId != activeElementalStatue
			&& statueObjectId != activeCatalyticStatue
			&& !heldTalismanStatues.contains(statueObjectId);
	}

	// --- game state tracking ---

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
		activeElementalStatue = GuardiansOfTheRift.ELEMENTAL_STATUE_BY_INDEX.getOrDefault(elementalIndex, -1);
		activeCatalyticStatue = GuardiansOfTheRift.CATALYTIC_STATUE_BY_INDEX.getOrDefault(catalyticIndex, -1);
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
		GuardiansOfTheRift.TALISMAN_BY_STATUE.forEach((statueId, talismanId) ->
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
			clientThread.invokeLater(this::refreshSceneIsGotr);
		}
	}

	private void refreshSceneIsGotr()
	{
		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			sceneIsGotr = false;
			sceneHasAltar = false;
			return;
		}

		sceneIsGotr = Arrays.stream(worldView.getScene().getMapRegions())
			.anyMatch(regionId -> regionId == GuardiansOfTheRift.ARENA_REGION);
		sceneHasAltar = sceneContainsAnyRegion(worldView.getScene(), GuardiansOfTheRift.ALTAR_REGIONS);
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
		return GuardiansOfTheRift.GUARDIAN_STATUE_OBJECTS.contains(entry.getIdentifier())
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

		// static scenery filtering is applied when the scene uploads, so changes to it
		// take one reload; statue, NPC, projectile, and widget changes apply live
		boolean staticSetChanged = !previousObjectIds.equals(hiddenObjectIds)
			|| previousAltarScenery != hideAltarScenery
			|| previousArenaGenerics != hideArenaGenericScenery;
		if (staticSetChanged)
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
}
