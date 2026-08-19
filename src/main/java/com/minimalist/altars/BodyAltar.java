// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class BodyAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Body";
	}

	@Override
	public int baseRegion()
	{
		return 10059;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			344 /* unnamed */,
			345 /* unnamed */,
			1385 /* unnamed */,
			1386 /* Roots */,
			1388 /* Roots */,
			1389 /* Roots */,
			1479 /* unnamed */,
			1480 /* unnamed */,
			1481 /* unnamed */,
			1482 /* unnamed */,
			1483 /* unnamed */,
			1484 /* unnamed */);
	}
}
