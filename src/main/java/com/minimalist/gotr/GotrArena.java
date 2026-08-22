// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.gotr;

import java.util.Set;

/**
 * Curated scenery IDs for the Guardians of the Rift arena, temple, and mines.
 * Sourced from the game cache; unnamed ("null") IDs are purely decorative models
 * with no menu entries and no varbit transforms.
 */
public final class GotrArena
{
	/** The map region of the GOTR arena and temple. */
	public static final int ARENA_REGION = 14484;

	/**
	 * The abyss backdrop around the arena: Whale-fall, Elk kelp, Dark lace, statues,
	 * the Abyssal Rift decoration, and unnamed decorative models (fossils, growths).
	 */
	public static final Set<Integer> ABYSS_SCENERY_OBJECTS = Set.of(
		43500, 43501, 43502, 43505, 43506, 43507, 43520, 43531,
		43521, 43522, 43523, 43524, 43525, 43526, 43527, 43528, 43529, 43530,
		43573, 43574, 43575, 43576,
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
		43678, 43679, 43680, 43681, 43682, 43683, 43684, 43685, 43686, 43687, 43688,
		43690, 43691, 43694,
		43692, 43693, // Portal (decorative frames; the functional mine portal is 43730)
		43698, 43699, // Statue
		43713, // Abyssal Rift
		43725, 43727, 43728,
		43823, 43826, 43828, 43829, 43830, 43831, 43832, 43833, 43834, 43835, 43836,
		43837, 43839, 43850
	);

	/**
	 * Small guardian parts and the depleted (mined-out) remains. The larger mineable
	 * remains (43719-43721: Large, Huge, Fallen guardian) are never hidden so mining
	 * targets always stay visible.
	 */
	public static final Set<Integer> GUARDIAN_REMAINS_OBJECTS = Set.of(
		43715, 43716, // Guardian parts (small)
		43717, 43718, // Guardian remains (small)
		43796, 43797, 43798, 43799, 43800, 43801, 43804, 43805, // Guardian remains (depleted)
		43803 // Rubble (depleted fallen guardian)
	);

	/** Essence pile (elemental) and Essence pile (catalytic). */
	public static final Set<Integer> ESSENCE_PILE_OBJECTS = Set.of(43722, 43723);

	/**
	 * Weak cells table, the charged Barriers in both width variants, and the
	 * Elemental/Catalytic guides.
	 */
	public static final Set<Integer> BARRIER_AND_CELL_OBJECTS = Set.of(
		43733, // Weak cells
		43744, 43745, // Weak Barrier
		43746, 43747, // Medium Barrier
		43748, 43749, // Strong Barrier
		43750, 43751, // Overcharged Barrier
		43752, 43753 // Elemental/Catalytic guide
	);

	/**
	 * Lobby and entrance decoration. Deliberately excludes Rubble 43724/43726, those
	 * have a Climb option (agility shortcut).
	 */
	public static final Set<Integer> ENTRANCE_SCENERY_OBJECTS = Set.of(
		43508, // Skeleton
		43509, 43510, 43511, 43512, 43513, 43514, 43515, 43516, // Pillars
		43517, 43518, 43519, // Rubble (decorative only)
		43535, // Cart
		43689 // Fountain
	);

	/** The rain effect objects inside the temple. */
	public static final Set<Integer> RAIN_OBJECTS = Set.of(43503, 43504);

	/**
	 * Generic decorative models placed in the arena that also exist elsewhere in the
	 * game world, only hidden while the GOTR scene is loaded.
	 */
	public static final Set<Integer> ARENA_GENERIC_SCENERY_OBJECTS = Set.of(85, 738, 2735, 2738, 7389);

	private GotrArena()
	{
	}
}
