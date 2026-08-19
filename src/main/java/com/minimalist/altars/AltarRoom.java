// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

/**
 * One runecrafting altar room. Every ID here is decorative-only, verified against the
 * game cache to have no functional menu actions (with the deliberate exception of
 * choppable trees, which only ever appear inside altar rooms).
 */
public interface AltarRoom
{
	String altarName();

	/**
	 * The room's map region; the scene it loads in may also span neighboring regions.
	 * Note: the altar-name-to-region assignments are best-effort documentation — hiding
	 * matches the union of all altar decoration whenever any altar scene is loaded, so
	 * a mislabeled region cannot affect behavior.
	 */
	int baseRegion();

	/** Scenery observed only at this altar. Shared decoration lives in {@link SharedAltarScenery}. */
	Set<Integer> distinctiveScenery();

	/** Decorative NPCs inside this altar, hidden only while its scene is loaded. */
	default Set<Integer> hiddenNpcs()
	{
		return Set.of();
	}
}
