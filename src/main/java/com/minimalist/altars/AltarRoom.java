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
