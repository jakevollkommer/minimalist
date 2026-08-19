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

class NatureAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Nature";
	}

	@Override
	public int baseRegion()
	{
		return 9547;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			1164 /* Mushroom */,
			1165 /* Mushroom */,
			1169 /* Mushroom */,
			1179 /* Heather */,
			1180 /* Heather */,
			1196 /* Flowers */,
			1394 /* Plant */,
			10790 /* Boulder */,
			10791 /* Boulder */,
			10793 /* Stones */);
	}

	@Override
	public Set<Integer> hiddenNpcs()
	{
		return Set.of(5240 /* unnamed critter */);
	}
}
