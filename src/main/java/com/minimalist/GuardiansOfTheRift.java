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
	 * the Abyssal Rift decoration, and unnamed decorative models.
	 */
	static final Set<Integer> ABYSS_SCENERY_OBJECTS = Set.of(
		43521, 43522, 43523, 43524, 43525, 43526, 43527, 43528, 43529, 43530,
		43604, 43605, 43607, 43608, 43609, 43610, 43611,
		43616, 43617, 43618, 43619, 43620, 43621, 43622, 43623, 43624, 43625,
		43629, // Whale-fall
		43632, 43633, 43634, // Elk kelp
		43635, 43636, 43637, // Dark lace
		43638, 43639, 43640, 43641, 43642, // Elk kelp
		43644, 43645, 43646, // Dark lace
		43647, 43648, 43649, 43650, 43651, 43652, // Elk kelp
		43653, 43654, 43655, // Dark lace
		43656, 43657, 43658, // Elk kelp
		43660, 43661, 43662, 43663, 43664, 43665, 43667, 43668, 43669, 43671, 43672,
		43675, // Pineapple
		43676, // Head
		43677, // Rock
		43684, 43685, 43686, 43687,
		43698, 43699, // Statue
		43713, // Abyssal Rift
		43826, 43828
	);

	/**
	 * The twelve inactive "Guardian of ..." statues ringing the arena (Air through Law).
	 */
	static final Set<Integer> GUARDIAN_STATUE_OBJECTS = Set.of(
		43701, 43702, 43703, 43704, 43705, 43706, 43707, 43708, 43709, 43710, 43711, 43712
	);

	/**
	 * Mineable guardian debris: Guardian parts, Guardian remains (all sizes), Fallen guardian.
	 */
	static final Set<Integer> GUARDIAN_REMAINS_OBJECTS = Set.of(
		43715, 43716, // Guardian parts
		43717, 43718, // Guardian remains
		43719, // Large guardian remains
		43720, // Huge guardian remains
		43721  // Fallen guardian
	);

	/** Essence pile (elemental) and Essence pile (catalytic). */
	static final Set<Integer> ESSENCE_PILE_OBJECTS = Set.of(43722, 43723);

	/**
	 * Weak cells table, the four charged Barriers, and the Elemental/Catalytic guides.
	 */
	static final Set<Integer> BARRIER_AND_CELL_OBJECTS = Set.of(
		43733, // Weak cells
		43744, 43747, 43748, 43750, 43751, // Barriers
		43752, // Elemental guide
		43753  // Catalytic guide
	);

	/**
	 * Lobby and entrance decoration: Skeleton, Pillars, Ruined Pillars, Rubble, Cart, Fountain.
	 */
	static final Set<Integer> ENTRANCE_SCENERY_OBJECTS = Set.of(
		43508, // Skeleton
		43509, 43510, 43511, 43512, 43513, 43514, 43515, 43516, // Pillars
		43517, 43518, 43519, // Rubble
		43535, // Cart
		43689, // Fountain
		43724, 43726 // Rubble
	);

	/** The rain effect objects inside the temple. */
	static final Set<Integer> RAIN_OBJECTS = Set.of(43503, 43504);

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
