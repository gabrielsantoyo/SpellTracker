package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import java.awt.Color;
import net.runelite.client.config.Alpha;

@ConfigGroup("spelltracker")
public interface SpellTrackerConfig extends Config {

	// Enum to define the modes
	enum Mode {
		MODE1("Autocast Over Manual Selection"),
		MODE2("Blend Manual and Autocast Behaviors"),
		MODE3("Split or Animation Effect"),
		MODE4("Fixed Color");

		private final String description;

		Mode(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	// Dropdown to select the mode
	@ConfigItem(
			position = 1,
			keyName = "selectedMode",
			name = "Select Mode",
			description = "Choose one of the available modes: Mode 1, Mode 2, Mode 3, Mode 4."
	)
	default Mode selectedMode() {
		return Mode.MODE1; // Default to Mode 1
	}

	// Primary fixed color for Mode.MODE4
	@ConfigItem(
			position = 2,
			keyName = "fixedColor",
			name = "Fixed Color",
			description = "Select the primary color when using 'Fixed Color' mode."
	)
	@Alpha // Enables alpha transparency for the color picker
	default Color fixedColor() {
		return Color.RED; // Default color
	}

	// Secondary fixed color for Mode.MODE4 (optional)
	@ConfigItem(
			position = 3,
			keyName = "secondaryColor",
			name = "Secondary Color",
			description = "Select the secondary color (optional) for 'Fixed Color' mode."
	)
	@Alpha
	default Color secondaryColor() {
		return Color.BLUE; // Default color
	}

	// Tertiary fixed color for Mode.MODE4 (optional)
	@ConfigItem(
			position = 4,
			keyName = "tertiaryColor",
			name = "Tertiary Color",
			description = "Select the tertiary color (optional) for 'Fixed Color' mode."
	)
	@Alpha
	default Color tertiaryColor() {
		return Color.GREEN; // Default color
	}
}
