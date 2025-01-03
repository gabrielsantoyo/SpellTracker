package com.example;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.widgets.WidgetID;

@Slf4j
@PluginDescriptor(
		name = "SpellTracker"
)
public class SpellTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	private static final int SPELLBOOK_PARENT_ID = WidgetID.SPELLBOOK_GROUP_ID;


	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget spellbookWidget = client.getWidget(SPELLBOOK_PARENT_ID);
		if (spellbookWidget == null)
		{
			return; // Spellbook not open
		}

		// Iterate through child widgets (spells)
		Widget[] spellWidgets = spellbookWidget.getChildren();
		if (spellWidgets != null)
		{
			for (Widget spell : spellWidgets)
			{
				if (spell.isHidden())
				{
					continue; // Skip hidden spells (e.g., unavailable due to level)
				}

				// Log spell details (example: ID, text, or tooltip)
				log.debug("Spell widget details: ID={}, Text={}", spell.getId(), spell.getText());
			}
		}
	}
}
