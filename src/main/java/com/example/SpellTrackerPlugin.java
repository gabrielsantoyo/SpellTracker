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
import net.runelite.client.config.ConfigManager;
import com.google.inject.Provides;
import net.runelite.client.events.ConfigChanged;

import java.awt.Color;

@Slf4j
@PluginDescriptor(
		name = "SpellTracker",
		description = "Tracks what spell is selected.",
		tags = {"spell", "tracking", "magic"}
)
public class SpellTrackerPlugin extends Plugin {

	@Inject
	private SpellTrackerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Client client;

	// Define variables to hold the current spell selections
	private String autoCastSpell = "None";
	private String lastSelectedSpell = "None";
	private String lastCastSpell = "None";  // Declare lastCastSpell here

	@Override
	protected void startUp() throws Exception {
		log.debug("SpellTracker plugin started!");
	}

	@Override
	protected void shutDown() throws Exception {
		log.debug("SpellTracker plugin stopped!");
	}

	// Listen to configuration changes
	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getKey().equals("selectedMode")) {
			SpellTrackerConfig.Mode selectedMode = config.selectedMode();  // Corrected line
			if (selectedMode == SpellTrackerConfig.Mode.MODE4) {
				// Show the additional color fields for Mode 4
				// You can update UI logic here or trigger a UI refresh
				log.debug("Mode 4 selected - showing color options.");
			} else {
				// Hide the additional color fields if another mode is selected
				log.debug("Other mode selected - hiding color options.");
			}
		}
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

		log.debug("MenuOption clicked: {}", menuOption);

		// Based on the selected mode, handle each scenario
		switch (config.selectedMode()) {
			case MODE1:
				handleMode1(menuOption, event.getWidget());
				break;
			case MODE2:
				handleMode2(menuOption);
				break;
			case MODE3:
				handleMode3(menuOption);
				break;
			case MODE4:
				handleMode4(menuOption);
				break;
		}
	}

	private void handleMode1(String menuOption, Widget widget) {
		if (menuOption.contains("Autocast")) {
			autoCastSpell = getSpellNameByWidget(widget);
			log.debug("Autocast spell selected (Mode 1): {}", autoCastSpell);
		} else if (menuOption.contains("Cast")) {
			lastSelectedSpell = getSpellNameByWidget(widget);
			log.debug("Spellbook spell selected (Mode 1): {}", lastSelectedSpell);
		}
	}

	private void handleMode2(String menuOption) {
		// Example of blending behavior
		log.debug("Mode 2 behavior triggered for: {}", menuOption);
	}

	private void handleMode3(String menuOption) {
		// Example of split or animation-specific behavior
		log.debug("Mode 3 behavior triggered for: {}", menuOption);
	}

	private void handleMode4(String menuOption) {
		Color primaryColor = config.fixedColor();
		Color secondaryColor = config.secondaryColor();
		Color tertiaryColor = config.tertiaryColor();

		log.debug("Mode 4 triggered - Primary: {}, Secondary: {}, Tertiary: {}",
				primaryColor, secondaryColor, tertiaryColor);

		// Implement actions for Fixed Color mode here
	}

	/**
	 * Tracks spell casting using graphical changes.
	 */
	@Subscribe
	public void onGraphicChanged(GraphicChanged event) {
		if (event.getActor() == client.getLocalPlayer()) {
			int spotAnimId = client.getLocalPlayer().getGraphic();
			lastCastSpell = getSpellNameBySpotAnimId(spotAnimId);  // Now this will work because lastCastSpell is declared
			log.debug("Spell casted (graphic): {}", lastCastSpell);
		}
	}

	/**
	 * Tracks spell casting using animations.
	 */
	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (event.getActor() == client.getLocalPlayer()) {
			int animationId = client.getLocalPlayer().getAnimation();
			lastCastSpell = getSpellNameByAnimationId(animationId);
			log.debug("Spell casted (animation): {}", lastCastSpell);
		}
	}

	/**
	 * Helper method to map Widget details to spell names.
	 */
	private String getSpellNameByWidget(Widget widget) {
		if (widget == null) {
			return "Unknown Spell";
		}
		int spriteId = widget.getSpriteId();
		return getSpellNameBySpriteId(spriteId);
	}

	/**
	 * Helper method to map SpriteID to spell names.
	 */
	private String getSpellNameBySpriteId(int spriteId) {
		switch (spriteId) {
			case 15:
				return "Wind Strike";
			case 17:
				return "Water Strike";
			default:
				return "Unknown Spell";
		}
	}

	/**
	 * Helper method to map SpotAnimID to spell names.
	 */
	private String getSpellNameBySpotAnimId(int spotAnimId) {
		switch (spotAnimId) {
			case 90:
				return "Wind Strike";
			case 93:
				return "Water Strike";
			default:
				return "Unknown";
		}
	}

	/**
	 * Helper method to map AnimationID to spell names.
	 */
	private String getSpellNameByAnimationId(int animationId) {
		switch (animationId) {
			case 711:
				return "Bare-hand Spell";
			case 1162:
				return "Staff Casted Spell";
			default:
				return "Unknown";
		}
	}

	// This method binds the config
	@Provides
	SpellTrackerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SpellTrackerConfig.class);
	}
}
