// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class FireAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Fire";
	}

	@Override
	public int baseRegion()
	{
		return 10315;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			731 /* unnamed */,
			732 /* unnamed */);
	}
}
