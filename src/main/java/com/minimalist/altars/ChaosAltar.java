// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class ChaosAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Chaos";
	}

	@Override
	public int baseRegion()
	{
		return 9035;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			169 /* unnamed */,
			662 /* Corpse */);
	}
}
