// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.Set;

/**
 * Decoration that appears across multiple altar rooms. The Altar, exit Portal, and
 * anything functional (ladders, etc.) are never listed here.
 */
public final class SharedAltarScenery
{
	public static final Set<Integer> OBJECTS = Set.of(
		34780, 34781, 34782, // Pillar
		34786, // unnamed pillar variant
		1421, // unnamed
		34789, 34790, 34791, 34792, 34793, 34794, // Mysterious glow
		34803, 34804, 34805, 34806, // Rubble
		724, // Standing torch
		// trees: choppable, but these only ever appear inside altar rooms
		1276, 1278, 1282, 1286, 1289, 1384, 10820,
		// flowers and plants
		1133, 1166, 1189, 1194, 1195, 1197, 1198, 1391,
		1246, 1247, // unnamed
		// unnamed decor observed across the altar rooms during full-circuit sweeps
		168, 319, 320, 321, 344, 345, 660, 1127, 1128, 1129, 1130, 1131, 1132, 1134,
		1135, 1137, 1138, 1143, 1178, 1428, 1452, 1453, 1454, 1455, 1820, 2669,
		4033, 10601, 11554, 11555, 11556, 11557, 11915, 11916, 11919, 11929, 12581,
		12584, 16449, 20312, 20313, 20314, 37708, 37774, 37775, 41909, 42307,
		43481, 43482, 43483
	);

	private SharedAltarScenery()
	{
	}
}
