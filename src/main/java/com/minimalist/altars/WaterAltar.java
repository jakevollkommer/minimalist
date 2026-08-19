// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class WaterAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Water";
	}

	@Override
	public int baseRegion()
	{
		return 10827;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			1175 /* Waterlily */,
			1176 /* Waterlily */,
			1177 /* Waterlily */);
	}
}
