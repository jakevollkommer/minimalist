// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.altars;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
	 * Every decorative ID across all altar rooms. Decoration IDs recur between rooms
	 * (waterlilies at non-water altars, bloodsplatters outside death), so matching uses
	 * this union whenever ANY altar scene is loaded — per-altar lists exist to document
	 * where each ID was observed, never to scope hiding.
	 */
	private static final Set<Integer> ALL_ALTAR_SCENERY = Stream.concat(
			SharedAltarScenery.OBJECTS.stream(),
			ALL.stream().flatMap(altar -> altar.distinctiveScenery().stream()))
		.collect(Collectors.toUnmodifiableSet());

	/**
	 * True when this object is altar decoration to hide in the loaded scene. Resolves
	 * from the Scene itself so it is correct even during scene upload, and allocates
	 * nothing (it runs per object).
	 */
	public static boolean isAltarSceneryInScene(Scene scene, int objectId)
	{
		return ALL_ALTAR_SCENERY.contains(objectId) && hasAltarInScene(scene);
	}

	/**
	 * The decorative NPC ids of every altar the loaded scene spans. Called once per
	 * scene load, so the allocation is fine.
	 */
	public static Set<Integer> hiddenNpcsForScene(Scene scene)
	{
		int[] sceneRegions = scene.getMapRegions();
		if (sceneRegions == null)
		{
			return Set.of();
		}

		return java.util.Arrays.stream(sceneRegions)
			.mapToObj(Altars::altarForRegion)
			.filter(java.util.Objects::nonNull)
			.flatMap(altar -> altar.hiddenNpcs().stream())
			.collect(Collectors.toUnmodifiableSet());
	}

	/** True when the loaded scene spans any altar's regions. */
	public static boolean hasAltarInScene(Scene scene)
	{
		int[] sceneRegions = scene.getMapRegions();
		if (sceneRegions == null)
		{
			return false;
		}

		for (int regionId : sceneRegions)
		{
			if (altarForRegion(regionId) != null)
			{
				return true;
			}
		}

		return false;
	}

	@Nullable
	private static AltarRoom altarForRegion(int regionId)
	{
		AltarRoom baseOwner = ALTAR_BY_BASE_REGION.get(regionId);
		if (baseOwner != null)
		{
			return baseOwner;
		}

		return ALTAR_BY_NEIGHBOR_REGION.get(regionId);
	}

	private Altars()
	{
	}
}
