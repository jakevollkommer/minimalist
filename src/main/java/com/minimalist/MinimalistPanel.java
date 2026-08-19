// SPDX-License-Identifier: BSD-2-Clause
// Copyright (c) 2026, Jake Vollkommer
package com.minimalist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

/**
 * A small About panel: what Minimalist covers, where to suggest more content, and a
 * support button — the config panel cannot host clickable buttons, so they live here.
 */
class MinimalistPanel extends PluginPanel
{
	private static final String ISSUES_URL = "https://github.com/jakevollkommer/osrs-minimalist/issues";
	private static final String SUPPORT_URL = "https://ko-fi.com/jakevollkommer";

	MinimalistPanel()
	{
		setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		setLayout(new BorderLayout(0, 12));

		JLabel title = new JLabel("Minimalist");
		title.setForeground(ColorScheme.BRAND_ORANGE);

		JLabel about = new JLabel("<html>Hides the scenery you can't interact with."
			+ "<br><br>Currently supports Guardians of the Rift, with more areas of the"
			+ " game planned — feature requests are encouraged!</html>");

		JButton suggestButton = new JButton("Suggest content");
		suggestButton.setToolTipText("Open a GitHub issue with the minigame or area you want covered");
		suggestButton.addActionListener(event -> LinkBrowser.browse(ISSUES_URL));

		JButton supportButton = new JButton("Buy me a coffee",
			new ImageIcon(ImageUtil.loadImageResource(MinimalistPlugin.class, "heart.png")));
		supportButton.setToolTipText("Enjoying Minimalist? Support development :)");
		supportButton.addActionListener(event -> LinkBrowser.browse(SUPPORT_URL));

		JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 8));
		buttons.setOpaque(false);
		buttons.add(suggestButton);
		buttons.add(supportButton);

		JPanel content = new JPanel(new BorderLayout(0, 12));
		content.setOpaque(false);
		content.add(title, BorderLayout.NORTH);
		content.add(about, BorderLayout.CENTER);
		content.add(buttons, BorderLayout.SOUTH);
		content.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 200));

		add(content, BorderLayout.NORTH);
	}
}
