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

class DeathAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Death";
	}

	@Override
	public int baseRegion()
	{
		return 8779;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			652 /* Bloodsplatter */,
			653 /* Bloodsplatter */,
			654 /* Bloodsplatter */,
			664 /* Corpse */,
			665 /* Corpse */,
			666 /* Corpse */,
			667 /* Corpse */,
			701 /* Curved bone */,
			736 /* Animal skull */,
			1448 /* unnamed */,
			1449 /* unnamed */,
			1450 /* unnamed */,
			1451 /* unnamed */,
			1502 /* unnamed */,
			1503 /* unnamed */,
			11941 /* Column */,
			11942 /* Column */,
			11944 /* Stalagmites */);
	}

	@Override
	public Set<Integer> hiddenNpcs()
	{
		return Set.of(85 /* Ghost */, 88 /* Ghost */, 89 /* Ghost */, 90 /* Ghost */, 91 /* Ghost */, 92 /* Ghost */);
	}
}
