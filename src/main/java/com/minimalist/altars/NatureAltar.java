// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
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
