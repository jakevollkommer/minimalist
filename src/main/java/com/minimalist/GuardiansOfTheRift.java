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

import java.util.Set;
import net.runelite.api.widgets.WidgetUtil;

/**
 * Curated IDs for Guardians of the Rift scenery. Sourced from the game cache;
 * unnamed ("null") IDs are purely decorative models with no menu entries.
 */
final class GuardiansOfTheRift
{
	/**
	 * The abyss backdrop around the arena: Whale-fall, Elk kelp, Dark lace, statues,
	 * the Abyssal Rift decoration, and unnamed decorative models (fossils, growths).
	 * All unnamed entries verified against the cache to have no menu actions and no
	 * varbit transforms.
	 */
	static final Set<Integer> ABYSS_SCENERY_OBJECTS = Set.of(
		43500, 43501, 43502, 43505, 43506, 43507, 43520, 43531,
		43521, 43522, 43523, 43524, 43525, 43526, 43527, 43528, 43529, 43530,
		43600, 43601, 43602, 43603, 43604, 43605, 43606, 43607, 43608, 43609, 43610, 43611,
		43612, 43613, 43614, 43615,
		43616, 43617, 43618, 43619, 43620, 43621, 43622, 43623, 43624, 43625,
		43626, 43627, 43628, 43630, 43631,
		43629, // Whale-fall
		43632, 43633, 43634, // Elk kelp
		43635, 43636, 43637, // Dark lace
		43638, 43639, 43640, 43641, 43642, // Elk kelp
		43643,
		43644, 43645, 43646, // Dark lace
		43647, 43648, 43649, 43650, 43651, 43652, // Elk kelp
		43653, 43654, 43655, // Dark lace
		43656, 43657, 43658, // Elk kelp
		43659, 43660, 43661, 43662, 43663, 43664, 43665, 43666, 43667, 43668, 43669,
		43670, 43671, 43672, 43673, 43674,
		43675, // Pineapple
		43676, // Head
		43677, // Rock
		43573, 43574, 43575, 43576,
		43678, 43679, 43680, 43681, 43682, 43683, 43684, 43685, 43686, 43687, 43688,
		43690, 43691, 43694,
		43725, 43727, 43728,
		43692, 43693, // Portal (decorative frames; the functional mine portal is 43730)
		43698, 43699, // Statue
		43713, // Abyssal Rift
		43823, 43826, 43828, 43829, 43830, 43831, 43832, 43833, 43834, 43835, 43836,
		43837, 43839, 43850
	);

	/**
	 * The twelve "Guardian of ..." statues ringing the arena (Air through Law). They stay
	 * in the scene for the whole game; whether one is active is signaled purely by its
	 * renderable playing the active animation. Hiding is therefore done per-frame through
	 * the draw listener (inactive only), never by scene removal.
	 */
	static final Set<Integer> GUARDIAN_STATUE_OBJECTS = Set.of(
		43701, 43702, 43703, 43704, 43705, 43706, 43707, 43708, 43709, 43710, 43711, 43712
	);

	/**
	 * Each guardian statue's matching portal talisman item — holding it allows entering
	 * that guardian even while inactive, so its menu must never be stripped.
	 */
	static final java.util.Map<Integer, Integer> TALISMAN_BY_STATUE = java.util.Map.ofEntries(
		java.util.Map.entry(43701, 26887), // Air
		java.util.Map.entry(43702, 26888), // Water
		java.util.Map.entry(43703, 26889), // Earth
		java.util.Map.entry(43704, 26890), // Fire
		java.util.Map.entry(43705, 26891), // Mind
		java.util.Map.entry(43706, 26892), // Chaos
		java.util.Map.entry(43707, 26893), // Death
		java.util.Map.entry(43708, 26894), // Blood
		java.util.Map.entry(43709, 26895), // Body
		java.util.Map.entry(43710, 26896), // Cosmic
		java.util.Map.entry(43711, 26897), // Nature
		java.util.Map.entry(43712, 26898)  // Law
	);

	/** The animation a guardian statue plays while its altar is active. */
	static final int ACTIVE_GUARDIAN_ANIMATION = 9363;

	/** The HUD update clientscript; its args carry the live game state. */
	static final int HUD_UPDATE_SCRIPT = 5980;
	/** Index into the HUD script args holding the active elemental altar (1-4, 0 = none). */
	static final int HUD_ARG_ACTIVE_ELEMENTAL = 6;
	/** Index into the HUD script args holding the active catalytic altar (1-8, 0 = none). */
	static final int HUD_ARG_ACTIVE_CATALYTIC = 7;

	/** Active elemental altar index (from the HUD script) to statue object id. */
	static final java.util.Map<Integer, Integer> ELEMENTAL_STATUE_BY_INDEX = java.util.Map.of(
		1, 43701, 2, 43702, 3, 43703, 4, 43704);

	/** Active catalytic altar index (from the HUD script) to statue object id. */
	static final java.util.Map<Integer, Integer> CATALYTIC_STATUE_BY_INDEX = java.util.Map.of(
		1, 43705, 2, 43709, 3, 43710, 4, 43706, 5, 43711, 6, 43712, 7, 43707, 8, 43708);

	/** The pedestal model shared by all guardian statues. */
	static final int STATUE_PEDESTAL_MODEL = 43995;

	/** Each statue's rune-specific top model, merged with the pedestal for stand-ins. */
	static final java.util.Map<Integer, Integer> RUNE_MODEL_BY_STATUE = java.util.Map.ofEntries(
		java.util.Map.entry(43701, 43983), // Air
		java.util.Map.entry(43702, 43985), // Water
		java.util.Map.entry(43703, 43991), // Earth
		java.util.Map.entry(43704, 43984), // Fire
		java.util.Map.entry(43705, 43989), // Mind
		java.util.Map.entry(43706, 43990), // Chaos
		java.util.Map.entry(43707, 43988), // Death
		java.util.Map.entry(43708, 43993), // Blood
		java.util.Map.entry(43709, 43980), // Body
		java.util.Map.entry(43710, 43986), // Cosmic
		java.util.Map.entry(43711, 43994), // Nature
		java.util.Map.entry(43712, 43987)  // Law
	);

	/** Statue object id to guardian name, for synthesized menu entries. */
	static final java.util.Map<Integer, String> STATUE_NAMES = java.util.Map.ofEntries(
		java.util.Map.entry(43701, "Guardian of Air"),
		java.util.Map.entry(43702, "Guardian of Water"),
		java.util.Map.entry(43703, "Guardian of Earth"),
		java.util.Map.entry(43704, "Guardian of Fire"),
		java.util.Map.entry(43705, "Guardian of Mind"),
		java.util.Map.entry(43706, "Guardian of Chaos"),
		java.util.Map.entry(43707, "Guardian of Death"),
		java.util.Map.entry(43708, "Guardian of Blood"),
		java.util.Map.entry(43709, "Guardian of Body"),
		java.util.Map.entry(43710, "Guardian of Cosmic"),
		java.util.Map.entry(43711, "Guardian of Nature"),
		java.util.Map.entry(43712, "Guardian of Law")
	);

	/**
	 * The animation a guardian statue plays while INACTIVE. Unique to the statues, so
	 * the draw listener can identify an inactive statue purely by this animation.
	 */
	static final int INACTIVE_GUARDIAN_ANIMATION = 9362;

	/**
	 * Small guardian parts and the depleted (mined-out) remains. The larger mineable
	 * remains (43717-43721: Guardian remains, Large, Huge, Fallen guardian) are never
	 * hidden so mining targets always stay visible.
	 */
	static final Set<Integer> GUARDIAN_REMAINS_OBJECTS = Set.of(
		43715, 43716, // Guardian parts (small)
		43717, 43718, // Guardian remains (small)
		43796, 43797, 43798, 43799, 43800, 43801, 43804, 43805, // Guardian remains (depleted)
		43803 // Rubble (depleted fallen guardian)
	);

	/** Essence pile (elemental) and Essence pile (catalytic). */
	static final Set<Integer> ESSENCE_PILE_OBJECTS = Set.of(43722, 43723);

	/**
	 * Weak cells table, the four charged Barriers, and the Elemental/Catalytic guides.
	 */
	static final Set<Integer> BARRIER_AND_CELL_OBJECTS = Set.of(
		43733, // Weak cells
		43744, 43745, // Weak Barrier
		43746, 43747, // Medium Barrier
		43748, 43749, // Strong Barrier
		43750, 43751, // Overcharged Barrier
		43752, 43753 // Elemental/Catalytic guide
	);

	/**
	 * Lobby and entrance decoration: Skeleton, Pillars, Ruined Pillars, Rubble, Cart, Fountain.
	 * Deliberately excludes Rubble 43724/43726 — those have a Climb option (agility shortcut).
	 */
	static final Set<Integer> ENTRANCE_SCENERY_OBJECTS = Set.of(
		43508, // Skeleton
		43509, 43510, 43511, 43512, 43513, 43514, 43515, 43516, // Pillars
		43517, 43518, 43519, // Rubble (decorative only)
		43535, // Cart
		43689 // Fountain
	);

	/** The rain effect objects inside the temple. */
	static final Set<Integer> RAIN_OBJECTS = Set.of(43503, 43504);

	/** The map region of the GOTR arena and temple. */
	static final int ARENA_REGION = 14484;

	/**
	 * Generic decorative models placed in the arena that also exist elsewhere in the
	 * game world — only hidden while inside {@link #ARENA_REGION}.
	 */
	static final Set<Integer> ARENA_GENERIC_SCENERY_OBJECTS = Set.of(85, 738, 2735, 2738, 7389);

	/** The map regions of the twelve runecrafting altar rooms, plus their scene neighbors. */
	static final Set<Integer> ALTAR_REGIONS = Set.of(
		11339, // air
		11083, // mind
		10827, // water
		10571, // earth
		10315, // fire
		10059, // body
		8523, 8522, 8266, 8267, // cosmic + neighbors
		9035,  // chaos
		9547,  // nature
		9803,  // law
		8779, 8778, // death + neighbor
		12875  // blood
	);

	/**
	 * Decoration inside the altar rooms: pillars, rubble, columns, stalagmites, corpses,
	 * bloodsplatters, bones, and unnamed models. These are generic world IDs, so they are
	 * only hidden while inside {@link #ALTAR_REGIONS}. The Altar, Portal, and Mysterious
	 * glow (exit marker) always stay visible.
	 */
	static final Set<Integer> ALTAR_SCENERY_OBJECTS = Set.of(
		652, 653, 654, // Bloodsplatter
		664, 665, 666, 667, // Corpse
		701, // Curved bone
		731, 732, // unnamed
		736, // Animal skull
		982, 984, 985, 986, // unnamed
		1189, // Daisies
		1246, 1247, // unnamed
		1448, 1449, 1450, 1451, 1502, 1503, // unnamed
		1688, 1689, 1690, // unnamed wall decor
		16318, // unnamed
		11941, 11942, // Column
		11944, // Stalagmites
		34780, 34781, 34782, // Pillar
		34786, // unnamed
		34803, 34804, 34805 // Rubble
	);

	/** Abyssal guardian, Abyssal walker, Abyssal leech — decorative creatures in the arena. */
	static final Set<Integer> ABYSSAL_CREATURE_NPCS = Set.of(11405, 11406, 11407);

	/** The Weak/Medium/Strong/Overcharged catalytic and elemental guardians players summon. */
	static final Set<Integer> SUMMONED_GUARDIAN_NPCS = Set.of(
		11408, 11411, 11412, 11413, // catalytic
		11414, 11415, 11416, 11417  // elemental
	);

	/** Apprentices Tamara, Cordelia, and Felix, in all their variants. */
	static final Set<Integer> APPRENTICE_NPCS = Set.of(
		11426, 11440, 11441, 11442, 11464, 11465, // Apprentice Tamara
		6717, 11443, 11444, 11445, 12179, 12180,  // Apprentice Cordelia
		11404, 11446, 11447, 11448                // Apprentice Felix
	);

	/** Rick. */
	static final Set<Integer> RICK_NPCS = Set.of(11409, 11410);

	/**
	 * The invisible NPCs that hold the barriers' hitpoints (2- and 3-tile-wide variants).
	 * Hiding these also hides the hitsplats and health bars drawn on the barriers.
	 */
	static final Set<Integer> BARRIER_NPCS = Set.of(
		11418, 11419, 11420, 11421, 11422, 11423, 11424, 11425
	);

	/** HUD: time since last portal. */
	static final int HUD_PORTAL_TIMER = WidgetUtil.packComponentId(746, 5);
	/** HUD: guardian counter (icon + count). */
	static final int HUD_GUARDIAN_COUNTER = WidgetUtil.packComponentId(746, 25);
	/** HUD: portal location text. */
	static final int HUD_PORTAL_LOCATION = WidgetUtil.packComponentId(746, 28);

	private GuardiansOfTheRift()
	{
	}
}
