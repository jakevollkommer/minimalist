// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
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
