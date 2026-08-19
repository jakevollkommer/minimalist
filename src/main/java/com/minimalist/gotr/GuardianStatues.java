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

import java.util.Map;
import java.util.Set;

/**
 * The twelve "Guardian of ..." statues ringing the arena. They stay in the scene for
 * the whole game and animate between active and inactive, so hiding happens per-frame
 * through the render callback — never by scene removal — keeping them clickable and
 * visible to other plugins.
 */
public final class GuardianStatues
{
	public static final Set<Integer> STATUE_OBJECTS = Set.of(
		43701, 43702, 43703, 43704, 43705, 43706, 43707, 43708, 43709, 43710, 43711, 43712
	);

	/** Active elemental altar index (from the HUD script) to statue object id. */
	public static final Map<Integer, Integer> STATUE_BY_ELEMENTAL_INDEX = Map.of(
		1, 43701, 2, 43702, 3, 43703, 4, 43704);

	/** Active catalytic altar index (from the HUD script) to statue object id. */
	public static final Map<Integer, Integer> STATUE_BY_CATALYTIC_INDEX = Map.of(
		1, 43705, 2, 43709, 3, 43710, 4, 43706, 5, 43711, 6, 43712, 7, 43707, 8, 43708);

	/**
	 * Each guardian statue's matching portal talisman item — holding it allows entering
	 * that guardian even while inactive, so it stays visible and clickable.
	 */
	public static final Map<Integer, Integer> TALISMAN_BY_STATUE = Map.ofEntries(
		Map.entry(43701, 26887), // Air
		Map.entry(43702, 26888), // Water
		Map.entry(43703, 26889), // Earth
		Map.entry(43704, 26890), // Fire
		Map.entry(43705, 26891), // Mind
		Map.entry(43706, 26892), // Chaos
		Map.entry(43707, 26893), // Death
		Map.entry(43708, 26894), // Blood
		Map.entry(43709, 26895), // Body
		Map.entry(43710, 26896), // Cosmic
		Map.entry(43711, 26897), // Nature
		Map.entry(43712, 26898)  // Law
	);

	private GuardianStatues()
	{
	}
}
