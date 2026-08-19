// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class CosmicAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Cosmic";
	}

	@Override
	public int baseRegion()
	{
		return 8523;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			119 /* Party Balloon */,
			982 /* unnamed */,
			984 /* unnamed */,
			985 /* unnamed */,
			986 /* unnamed */,
			16318 /* unnamed */);
	}
}
