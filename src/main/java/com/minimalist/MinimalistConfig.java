// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(MinimalistConfig.GROUP)
public interface MinimalistConfig extends Config
{
	String GROUP = "minimalist";

	@ConfigSection(
		name = "Guardians of the Rift",
		description = "Hide non-interactable scenery at Guardians of the Rift",
		position = 0
	)
	String gotrSection = "gotrSection";

	@ConfigItem(keyName = "gotrAbyssScenery", name = "Hide abyss scenery", description = "Whale-fall, kelp, lace, statues, and other backdrop decoration", section = gotrSection, position = 0)
	default boolean gotrAbyssScenery()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrGuardianStatues", name = "Hide inactive guardian statues", description = "Show only the two active guardians and any whose portal talisman you hold; the rest are hidden and unclickable until they activate", section = gotrSection, position = 1)
	default boolean gotrGuardianStatues()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrGuardianRemains", name = "Hide guardian remains", description = "Small guardian parts and depleted remains; large mineable remains always show", section = gotrSection, position = 2)
	default boolean gotrGuardianRemains()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrEssencePiles", name = "Hide essence piles", description = "Elemental and catalytic essence piles", section = gotrSection, position = 3)
	default boolean gotrEssencePiles()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrBarriersAndCells", name = "Hide barriers and cells", description = "Barriers, the weak cells table, and the elemental/catalytic guides", section = gotrSection, position = 4)
	default boolean gotrBarriersAndCells()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrBarrierHitsplats", name = "Hide barrier hitsplats", description = "Hitsplats and health bars on the barriers", section = gotrSection, position = 5)
	default boolean gotrBarrierHitsplats()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrEntranceScenery", name = "Hide entrance scenery", description = "Pillars, rubble, skeleton, cart, and fountain in the lobby", section = gotrSection, position = 6)
	default boolean gotrEntranceScenery()
	{
		return false;
	}

	@ConfigItem(keyName = "gotrAltarScenery", name = "Hide altar scenery", description = "Pillars, rubble, corpses, and other decoration inside the runecrafting altars", section = gotrSection, position = 7)
	default boolean gotrAltarScenery()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrRain", name = "Hide rain", description = "The rain effect inside the temple", section = gotrSection, position = 8)
	default boolean gotrRain()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrAbyssalCreatures", name = "Hide abyssal creatures", description = "Abyssal guardians, walkers, and leeches wandering the arena", section = gotrSection, position = 9)
	default boolean gotrAbyssalCreatures()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrSummonedGuardians", name = "Hide summoned guardians", description = "Catalytic and elemental guardians summoned by players", section = gotrSection, position = 10)
	default boolean gotrSummonedGuardians()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrApprentices", name = "Hide apprentices", description = "Apprentices Tamara, Cordelia, and Felix", section = gotrSection, position = 11)
	default boolean gotrApprentices()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrRick", name = "Hide Rick", description = "Rick", section = gotrSection, position = 12)
	default boolean gotrRick()
	{
		return false;
	}

	@ConfigItem(keyName = "gotrProjectiles", name = "Hide projectiles", description = "Projectiles from abyssal creatures attacking the barriers and guardian", section = gotrSection, position = 13)
	default boolean gotrProjectiles()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrOtherPlayers", name = "Hide other players", description = "Hide other players while at Guardians of the Rift", section = gotrSection, position = 14)
	default boolean gotrOtherPlayers()
	{
		return false;
	}

	@ConfigItem(keyName = "gotrOtherPlayers2d", name = "Hide other players 2D", description = "Hide other players' overhead text, hitsplats, and health bars while at Guardians of the Rift", section = gotrSection, position = 15)
	default boolean gotrOtherPlayers2d()
	{
		return false;
	}

	@ConfigItem(keyName = "gotrOtherPlayersPets", name = "Hide other players' pets", description = "Hide pets that are not yours while at Guardians of the Rift", section = gotrSection, position = 16)
	default boolean gotrOtherPlayersPets()
	{
		return false;
	}

	@ConfigItem(keyName = "gotrHudPortalTimer", name = "Hide HUD portal timer", description = "The 'time since last portal' HUD text", section = gotrSection, position = 17)
	default boolean gotrHudPortalTimer()
	{
		return false;
	}

	@ConfigItem(keyName = "gotrHudGuardianCounter", name = "Hide HUD guardian counter", description = "The guardian count on the HUD", section = gotrSection, position = 18)
	default boolean gotrHudGuardianCounter()
	{
		return true;
	}

	@ConfigItem(keyName = "gotrHudPortalLocation", name = "Hide HUD portal location", description = "The portal location text on the HUD", section = gotrSection, position = 19)
	default boolean gotrHudPortalLocation()
	{
		return false;
	}

	@ConfigSection(
		name = "Feedback",
		description = "Minimalist covers Guardians of the Rift for now, with more content planned — suggestions welcome",
		position = 1
	)
	String feedbackSection = "feedbackSection";

	@ConfigItem(
		keyName = "scopeNote",
		name = "About",
		description = "What Minimalist covers today and where it is going",
		section = feedbackSection,
		position = 0
	)
	default String scopeNote()
	{
		return "Minimalist currently supports Guardians of the Rift only. "
			+ "The intention is to bring minimalism to other areas of the game over time - "
			+ "feature requests are encouraged!";
	}

	@ConfigItem(
		keyName = "suggestionsLink",
		name = "Suggest content",
		description = "Want another minigame or area covered, or found something that should be hidden? Open a GitHub issue at this link",
		section = feedbackSection,
		position = 1
	)
	default String suggestionsLink()
	{
		return "github.com/jakevollkommer/osrs-minimalist/issues";
	}
}
