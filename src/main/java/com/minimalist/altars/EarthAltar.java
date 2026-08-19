// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class EarthAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Earth";
	}

	@Override
	public int baseRegion()
	{
		return 10571;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			1190 /* Sunflower */,
			1191 /* Sunflowers */,
			1417 /* unnamed */,
			1434 /* unnamed */,
			11174 /* Cave rocks */,
			11175 /* Cave rocks */,
			11184 /* Column */,
			11185 /* Column */,
			11186 /* Column */,
			11190 /* Rockslide */,
			11191 /* Rockslide */,
			11192 /* Rockslide */,
			11193 /* Rockslide */,
			12575 /* Stalagmite */,
			12576 /* Stalagmite */,
			12577 /* Stalactite */);
	}
}
