package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.Color;

@ConfigGroup("spelltracker")
public interface SpellTrackerConfig extends Config {

	// Enum for Mode selection
	enum Mode {
		MODE1("Fixed Color"),
		MODE2("Placeholder 2"),
		MODE3("Placeholder 3"),
		MODE4("Placeholder 4");

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
			keyName = "ModeSelectionMenu",
			name = "Mode Selection",
			description = "Select the mode for the plugin."
	)
	default Mode selectedMode() {
		return Mode.MODE1; // Default to Mode 1
	}

	// Color picker for Mode 1
	@ConfigItem(
			position = 2,
			keyName = "StaffColor",
			name = "Staff Color",
			description = "Select the color of your staff."
	)
	default Color StaffColor() {
		return Color.RED; // Default color
	}
}
