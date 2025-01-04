package com.example;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
		name = "SpellTracker"
)
public class SpellTrackerPlugin extends Plugin {
	@Inject
	private Client client;

	// Variables to store the last selected spell, last cast spell, and autocast spell
	private String lastSelectedSpell = "None";
	private String lastCastSpell = "None";
	private String autoCastSpell = "None";

	@Override
	protected void startUp() throws Exception {
		log.debug("SpellTracker plugin started!");
	}

	@Override
	protected void shutDown() throws Exception {
		log.debug("SpellTracker plugin stopped!");
	}

	/**
	 * Tracks spell selection from the spellbook or auto-cast setup.
	 */
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		String menuOption = event.getMenuOption();
		if (menuOption == null) {
			return;
		}

		// Log all menu options for debugging
		log.debug("MenuOption clicked: {}", menuOption);

		// Handle spell selection from the autocast menu or spellbook
		if (menuOption.contains("Autocast") || menuOption.contains("Cast")) {
			Widget widget = event.getWidget();

			// Log widget details for debugging
			if (widget != null) {
				log.debug("Widget ID: {}, Sprite ID: {}", widget.getId(), widget.getSpriteId());
			} else {
				log.debug("Widget is null for MenuOption: {}", menuOption);
				return;
			}

			// Handle autocast-specific selection
			if (menuOption.contains("Autocast")) {
				int spriteId = widget.getSpriteId();
				autoCastSpell = getSpellNameBySpriteId(spriteId);
				log.debug("Autocast spell selected: {}", autoCastSpell);

				// Default to autocast spell if no spellbook spell is selected
				if (lastSelectedSpell.equals("None")) {
					lastSelectedSpell = autoCastSpell;
				}
			}

			// Handle spellbook-specific selection
			if (menuOption.contains("Cast")) {
				int spriteId = widget.getSpriteId();
				lastSelectedSpell = getSpellNameBySpriteId(spriteId);
				log.debug("Spellbook spell selected: {}", lastSelectedSpell);
			}
		}
	}


	/**
	 * Tracks spell casting using graphical changes.
	 */
	@Subscribe
	public void onGraphicChanged(GraphicChanged event) {
		if (event.getActor() == client.getLocalPlayer()) {
			int spotAnimId = client.getLocalPlayer().getGraphic();
			String spellName = getSpellNameBySpotAnimId(spotAnimId);

			if (!spellName.equals("Unknown")) {
				lastCastSpell = spellName;
				log.debug("Spell casted: {}", lastCastSpell);

				// Reset lastSelectedSpell to autocast spell after a cast
				if (!autoCastSpell.equals("None")) {
					lastSelectedSpell = autoCastSpell;
					log.debug("Reverting to autocast spell: {}", autoCastSpell);
				} else {
					lastSelectedSpell = "None";
				}
			}
		}
	}

	/**
	 * Tracks spell casting using animations.
	 */
	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (event.getActor() == client.getLocalPlayer()) {
			int animationId = client.getLocalPlayer().getAnimation();
			String spellName = getSpellNameByAnimationId(animationId);

			if (!spellName.equals("Unknown")) {
				lastCastSpell = spellName;
				log.debug("Spell casted (via animation): {}", lastCastSpell);

				// Reset lastSelectedSpell to autocast spell after a cast
				if (!autoCastSpell.equals("None")) {
					lastSelectedSpell = autoCastSpell;
					log.debug("Reverting to autocast spell: {}", autoCastSpell);
				} else {
					lastSelectedSpell = "None";
				}
			}
		}
	}

	/**
	 * Helper method to map SpriteID to spell names.
	 */
	private String getSpellNameBySpriteId(int spriteId) {
		switch (spriteId) {
			case 15: // Replace with actual SpriteID for Wind Strike
				return "Wind Strike";
			case 17: // Replace with actual SpriteID for Water Strike
				return "Water Strike";
			// Add more cases for other spells
			default:
				return "Unknown Spell";
		}
	}

	/**
	 * Helper method to map SpotAnimID to spell names.
	 */
	private String getSpellNameBySpotAnimId(int spotAnimId) {
		switch (spotAnimId) {
			case 90: // Replace with actual SpotAnimID for Wind Strike
				return "Wind Strike";
			case 93: // Replace with actual SpotAnimID for Water Strike
				return "Water Strike";
			// Add more cases for other spells
			default:
				return "Unknown";
		}
	}

	/**
	 * Helper method to map AnimationID to spell names.
	 */
	private String getSpellNameByAnimationId(int animationId) {
		switch (animationId) {
			case 711: // Replace with actual AnimationID for Wind Strike
				return "Bare-hand Spell";
			case 1162: // Replace with actual AnimationID for Fire Bolt
				return "Staff Casted Spell";
			// Add more cases for other spells
			default:
				return "Unknown";
		}
	}
}
