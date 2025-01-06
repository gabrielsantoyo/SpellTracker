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
import java.util.Map;

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

	private String autoCastSpell = "None";
	private String lastSelectedSpell = "None";
	private String lastCastSpell = "None";

	private static final Map<Integer, String> SPRITE_TO_SPELL = Map.of(
			15, "Wind Strike",
			17, "Water Strike"
	);

	private static final Map<Integer, String> SPOT_ANIM_TO_SPELL = Map.of(
			90, "Wind Strike",
			93, "Water Strike"
	);

	private static final Map<Integer, String> ANIMATION_TO_SPELL = Map.of(
			711, "Bare-hand Spell",
			1162, "Staff Casted Spell"
	);

	@Override
	protected void startUp() throws Exception {
		log.debug("SpellTracker plugin started!");
		log.info("Initial configuration: Selected Mode = {}", config.selectedMode());
	}

	@Override
	protected void shutDown() throws Exception {
		log.debug("SpellTracker plugin stopped!");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getKey().equals("selectedMode")) {
			SpellTrackerConfig.Mode selectedMode = config.selectedMode();
			if (selectedMode == SpellTrackerConfig.Mode.MODE4) {
				log.debug("Mode 4 selected - showing color options.");
			} else {
				log.debug("Other mode selected - hiding color options.");
			}
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		String menuOption = event.getMenuOption();
		if (menuOption == null) {
			log.warn("Menu option is null.");
			return;
		}

		SpellTrackerConfig.Mode mode = config.selectedMode();
		if (mode == null) {
			log.warn("Selected mode is null. Defaulting behavior.");
			return;
		}

		log.debug("MenuOption clicked: {}, Selected Mode: {}", menuOption, mode);

		switch (mode) {
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
			default:
				log.warn("Unhandled mode: {}", mode);
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
		log.debug("Mode 2 behavior triggered for: {}", menuOption);
	}

	private void handleMode3(String menuOption) {
		log.debug("Mode 3 behavior triggered for: {}", menuOption);
	}

	private void handleMode4(String menuOption) {
		Color primaryColor = config.fixedColor();
		Color secondaryColor = config.secondaryColor();
		Color tertiaryColor = config.tertiaryColor();
		log.debug("Mode 4 triggered - Primary: {}, Secondary: {}, Tertiary: {}",
				primaryColor, secondaryColor, tertiaryColor);
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event) {
		if (event.getActor() == client.getLocalPlayer()) {
			int spotAnimId = client.getLocalPlayer().getGraphic();
			lastCastSpell = SPOT_ANIM_TO_SPELL.getOrDefault(spotAnimId, "Unknown");
			log.debug("Spell casted (graphic): {}", lastCastSpell);
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (event.getActor() == client.getLocalPlayer()) {
			int animationId = client.getLocalPlayer().getAnimation();
			lastCastSpell = ANIMATION_TO_SPELL.getOrDefault(animationId, "Unknown");
			log.debug("Spell casted (animation): {}", lastCastSpell);
		}
	}

	private String getSpellNameByWidget(Widget widget) {
		if (widget == null) {
			log.warn("Widget is null. Cannot determine spell name.");
			return "Unknown Spell";
		}
		return SPRITE_TO_SPELL.getOrDefault(widget.getSpriteId(), "Unknown Spell");
	}

	@Provides
	SpellTrackerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SpellTrackerConfig.class);
	}
}
