// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.gotr;

import java.util.Set;

/**
 * Curated NPC IDs at Guardians of the Rift.
 */
public final class GotrNpcs
{
	/** Abyssal guardian, Abyssal walker, Abyssal leech — decorative creatures in the arena. */
	public static final Set<Integer> ABYSSAL_CREATURES = Set.of(11405, 11406, 11407);

	/** The Weak/Medium/Strong/Overcharged catalytic and elemental guardians players summon. */
	public static final Set<Integer> SUMMONED_GUARDIANS = Set.of(
		11408, 11411, 11412, 11413, // catalytic
		11414, 11415, 11416, 11417  // elemental
	);

	/** Apprentices Tamara, Cordelia, and Felix, in all their variants. */
	public static final Set<Integer> APPRENTICES = Set.of(
		11426, 11440, 11441, 11442, 11464, 11465, // Apprentice Tamara
		6717, 11443, 11444, 11445, 12179, 12180,  // Apprentice Cordelia
		11404, 11446, 11447, 11448                // Apprentice Felix
	);

	/** Rick. */
	public static final Set<Integer> RICK = Set.of(11409, 11410);

	/**
	 * The invisible NPCs that hold the barriers' hitpoints (2- and 3-tile-wide variants).
	 * Hiding these also hides the hitsplats and health bars drawn on the barriers.
	 */
	public static final Set<Integer> BARRIER_HITPOINT_HOLDERS = Set.of(
		11418, 11419, 11420, 11421, 11422, 11423, 11424, 11425
	);

	private GotrNpcs()
	{
	}
}
