/*
 * Copyright (c) 2026, Jake Vollkommer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.minimalist.altars;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.runelite.api.Scene;

/**
 * Registry of the twelve runecrafting altar rooms and the scene-to-altar resolution
 * used for region-gated hiding.
 */
public final class Altars
{
	public static final List<AltarRoom> ALL = List.of(
		new AirAltar(), new MindAltar(), new WaterAltar(), new EarthAltar(),
		new FireAltar(), new BodyAltar(), new CosmicAltar(), new ChaosAltar(),
		new NatureAltar(), new LawAltar(), new DeathAltar(), new BloodAltar());

	private static final Map<Integer, AltarRoom> ALTAR_BY_BASE_REGION = ALL.stream()
		.collect(Collectors.toUnmodifiableMap(AltarRoom::baseRegion, altar -> altar));

	/**
	 * Neighbor regions an altar's scene may also span. The classic altars form a chain
	 * of adjacent regions, so one altar's neighbor is often another altar's base region
	 * — base regions always win, and overlapping neighbor claims keep the first owner.
	 */
	private static final Map<Integer, AltarRoom> ALTAR_BY_NEIGHBOR_REGION = ALL.stream()
		.flatMap(altar -> neighborsOf(altar.baseRegion())
			.map(region -> Map.entry(region, altar)))
		.filter(entry -> !ALTAR_BY_BASE_REGION.containsKey(entry.getKey()))
		.collect(Collectors.toUnmodifiableMap(
			Map.Entry::getKey, Map.Entry::getValue, (firstOwner, otherOwner) -> firstOwner));

	private static Stream<Integer> neighborsOf(int baseRegion)
	{
		return Stream.of(
			baseRegion + 1, baseRegion - 1,
			baseRegion + 256, baseRegion - 256,
			baseRegion + 257, baseRegion - 257,
			baseRegion + 255, baseRegion - 255);
	}

	/**
	 * The altar the loaded scene belongs to, or null when the scene is not an altar.
	 * Resolves from the Scene itself so it is correct even during scene upload. Base
	 * regions are checked before neighbor regions so adjacent altars never shadow
	 * each other.
	 */
	@Nullable
	public static AltarRoom forScene(Scene scene)
	{
		int[] sceneRegions = scene.getMapRegions();
		if (sceneRegions == null)
		{
			return null;
		}

		for (int regionId : sceneRegions)
		{
			AltarRoom altar = ALTAR_BY_BASE_REGION.get(regionId);
			if (altar != null)
			{
				return altar;
			}
		}

		for (int regionId : sceneRegions)
		{
			AltarRoom altar = ALTAR_BY_NEIGHBOR_REGION.get(regionId);
			if (altar != null)
			{
				return altar;
			}
		}

		return null;
	}

	/**
	 * True when this object is decoration to hide inside the given altar: either
	 * decoration shared by all altars, or this altar's own.
	 */
	public static boolean isAltarScenery(AltarRoom altar, int objectId)
	{
		return SharedAltarScenery.OBJECTS.contains(objectId)
			|| altar.distinctiveScenery().contains(objectId);
	}

	private Altars()
	{
	}
}
