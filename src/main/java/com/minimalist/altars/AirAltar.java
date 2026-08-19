// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class AirAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Air";
	}

	@Override
	public int baseRegion()
	{
		return 11339;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of();
	}
}
