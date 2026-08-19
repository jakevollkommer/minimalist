// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class LawAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Law";
	}

	@Override
	public int baseRegion()
	{
		return 9803;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of();
	}
}
