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
