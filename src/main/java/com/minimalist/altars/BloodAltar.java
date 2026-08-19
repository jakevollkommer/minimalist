// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
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
