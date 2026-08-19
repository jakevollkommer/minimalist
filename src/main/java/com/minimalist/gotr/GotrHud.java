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
