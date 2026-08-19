// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

class MindAltar implements AltarRoom
{
	@Override
	public String altarName()
	{
		return "Mind";
	}

	@Override
	public int baseRegion()
	{
		return 11083;
	}

	@Override
	public Set<Integer> distinctiveScenery()
	{
		return Set.of(
			1688 /* unnamed wall decor */,
			1689 /* unnamed wall decor */,
			1690 /* unnamed wall decor */);
	}
}
