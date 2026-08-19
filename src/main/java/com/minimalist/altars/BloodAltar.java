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

class BloodAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Blood";
	}

	@Override
	public int baseRegion()
	{
		return 12875;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			20665 /* Skeleton */,
			20780 /* Skeleton */,
			27707 /* Sink */,
			37706 /* unnamed */,
			37707 /* unnamed */,
			37772 /* unnamed */,
			37773 /* unnamed */,
			39309 /* unnamed */,
			39310 /* unnamed */,
			41907 /* unnamed */,
			43480 /* unnamed */,
			43504 /* Rain */,
			43508 /* Skeleton */,
			43509 /* Pillar */,
			43510 /* Pillar */,
			43512 /* Ruined Pillar */,
			43513 /* Ruined Pillar */,
			43516 /* Pillar */);
	}
}
