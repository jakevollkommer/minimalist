// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist.gotr;

import net.runelite.api.widgets.WidgetUtil;

/**
 * The GOTR HUD interface and the clientscript that updates it. The script's argument
 * vector carries the live game state, including which altars are currently active.
 */
public final class GotrHud
{
	/** HUD: time since last portal. */
	public static final int PORTAL_TIMER_COMPONENT = WidgetUtil.packComponentId(746, 5);
	/** HUD: guardian counter (icon + count). */
	public static final int GUARDIAN_COUNTER_COMPONENT = WidgetUtil.packComponentId(746, 25);
	/** HUD: portal location text. */
	public static final int PORTAL_LOCATION_COMPONENT = WidgetUtil.packComponentId(746, 28);

	/** The HUD update clientscript. */
	public static final int UPDATE_SCRIPT = 5980;
	/** Index into the script args holding the active elemental altar (1-4, 0 = none). */
	public static final int ARG_ACTIVE_ELEMENTAL = 6;
	/** Index into the script args holding the active catalytic altar (1-8, 0 = none). */
	public static final int ARG_ACTIVE_CATALYTIC = 7;

	private GotrHud()
	{
	}
}
