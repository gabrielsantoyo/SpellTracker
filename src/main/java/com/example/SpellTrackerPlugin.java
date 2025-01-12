package com.example;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.AnimationChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.api.widgets.Widget;

import net.runelite.api.widgets.WidgetInfo;

import com.google.inject.Provides;
import net.runelite.api.events.MenuOptionClicked;



import com.google.inject.Provides;
import com.fazecast.jSerialComm.SerialPort;

import java.io.OutputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@PluginDescriptor(
		name = "SpellTracker",
		description = "Tracks spells and sends commands to ESP32 based on mode",
		tags = {"spell", "tracking", "magic"}
)
public class SpellTrackerPlugin extends Plugin {


	@Inject
	private SpellTrackerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Client client;

	private SerialPort selectedPort; // Serial port for communication
	private OutputStream outputStream; // Output stream for sending commands
	private Timer mode1Timer; // Timer for periodic color sending in Mode1
	private boolean glowEffectSent = false; // Flag for glow effect


	// Last cast spell
	private String lastCastSpell = "None"; // Declare this variable


	// Mappings for spell detection
	private static final Map<Integer, String> SPRITE_TO_SPELL = Map.of(
			15, "Wind Strike",
			17, "Water Strike"
	);

	private static final Map<Integer, String> SPOT_ANIM_TO_SPELL = Map.of(
			90, "Wind Strike",
			93, "Water Strike"
	);

	@Override
	protected void startUp() throws Exception {
		log.debug("SpellTracker plugin started!");

		initializeSerialCommunication();
		setMode(config.selectedMode());
		sendCommandToESP32("on");

		// Register shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Shutdown hook triggered - sending 'off' command to ESP32.");
			sendCommandToESP32("off");
		}));
	}

	@Override
	protected void shutDown() throws Exception {
		log.debug("SpellTracker plugin stopped!");

		sendCommandToESP32("off");
		if (selectedPort != null && selectedPort.isOpen()) {
			selectedPort.closePort();
		}
		if (mode1Timer != null) {
			mode1Timer.cancel();
			mode1Timer.purge();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getKey().equals("StaffColor")) {
			log.info("Staff color changed to: " + config.StaffColor());
			sendCommandToESP32("color|" + getColorHex());
		}

		if (event.getKey().equals("selectedMode")) {
			log.info("Mode changed: " + config.selectedMode());
			setMode(config.selectedMode());
		}
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event) {
		if (event.getActor() == client.getLocalPlayer()) {
			// Use client.getLocalPlayer().getGraphic() to get the graphic ID
			int graphicId = client.getLocalPlayer().getGraphic(); // Correct method for getting the graphic ID
			lastCastSpell = SPOT_ANIM_TO_SPELL.getOrDefault(graphicId, "Unknown");
			log.debug("Spell casted (graphic): {}", lastCastSpell);
			triggerGlowEffect(lastCastSpell);
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (event.getActor() == client.getLocalPlayer()) {
			// Get the widget associated with the animation
			Widget widget = client.getWidget(218, 1); // Valid group and child ID for the spellbook spells widget

			// Get the spell name based on the widget's sprite ID
			lastCastSpell = getSpellNameByWidget(widget);

			log.debug("Spell casted (widget): {}", lastCastSpell);
		}
	}

	private String getSpellNameByWidget(Widget widget) {
		if (widget == null) {
			log.warn("Widget is null. Cannot determine spell name.");
			return "Unknown Spell";
		}
		return SPRITE_TO_SPELL.getOrDefault(widget.getSpriteId(), "Unknown Spell");
	}



	private void initializeSerialCommunication() {
		SerialPort[] ports = SerialPort.getCommPorts();
		if (ports.length == 0) {
			log.error("No serial ports found.");
			return;
		}

		String portName = "COM8";
		selectedPort = null;
		for (SerialPort port : ports) {
			if (port.getSystemPortName().equalsIgnoreCase(portName)) {
				selectedPort = port;
				break;
			}
		}

		if (selectedPort == null) {
			log.error("Port " + portName + " not found.");
			return;
		}

		log.info("Using port: " + selectedPort.getSystemPortName());
		if (selectedPort.openPort()) {
			log.info("Successfully opened the port.");
			selectedPort.setBaudRate(9600);
			selectedPort.setNumDataBits(8);
			selectedPort.setNumStopBits(1);
			selectedPort.setParity(SerialPort.NO_PARITY);

			try {
				outputStream = selectedPort.getOutputStream();
			} catch (Exception e) {
				log.error("Failed to get output stream.", e);
			}
		} else {
			log.error("Failed to open the port.");
		}
	}

private void sendCommandToESP32(String command) {
		log.debug("Preparing to send command to ESP32: {}", command);
		if (outputStream != null) {
			try {
				outputStream.write((command + "\n").getBytes());
				outputStream.flush();
				log.info("Command '{}' sent successfully to ESP32.", command);
			} catch (Exception e) {
				log.error("Error occurred while sending command '{}' to ESP32: {}", command, e.getMessage(), e);
			}
		} else {
			log.error("Cannot send command '{}'. Output stream is not initialized.", command);
		}
	}

private void setMode(SpellTrackerConfig.Mode mode) {
		log.info("Initiating mode switch to: " + mode);

		if (mode1Timer != null) {
			log.debug("Canceling existing Mode 1 timer before switching modes.");
			mode1Timer.cancel();
			mode1Timer.purge();
		}

		switch (mode) {
			case MODE1:
				log.info("Mode 1 selected. Initializing Mode 1.");
				initializeMode1();
				break;
			case MODE2:
				log.info("Mode 2 selected. Sending Mode 2 command.");
				sendMode2Command();
				break;
			case MODE3:
				log.warn("Mode 3 is selected, but implementation is pending.");
				break;
			case MODE4:
				log.warn("Mode 4 is selected, but implementation is pending.");
				break;
			default:
				log.error("Unknown mode selected: " + mode);
				break;
		}
	}

	private void initializeMode1() {
		if (mode1Timer != null) {
			log.warn("Mode 1 Timer is already initialized. Reinitializing...");
			mode1Timer.cancel();
			mode1Timer.purge();
		}
		log.debug("Starting Mode 1 timer.");
		mode1Timer = new Timer();
		mode1Timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				String colorHex = getColorHex();
				log.debug("Sending periodic color to ESP32: {}", colorHex);
				sendCommandToESP32("color|" + colorHex);
			}
		}, 0, 25000); // Send color updates to keep processor on, needed for if client crashes or forced closed
	}
	private void triggerGlowEffect(String spellName) {
		log.info("Triggering glow effect for spell: " + spellName);
		if (config.selectedMode() == SpellTrackerConfig.Mode.MODE1) {
			new Thread(() -> {
				try {
					for (int brightness = 30; brightness <= 255; brightness += 7) {
						sendCommandToESP32("brightness|" + brightness);
						Thread.sleep(23);
					}
					for (int brightness = 255; brightness >= 30; brightness -= 5) {
						sendCommandToESP32("brightness|" + brightness);
						Thread.sleep(23);
					}
				} catch (InterruptedException e) {
					log.error("Glow effect interrupted.", e);
				}
			}).start();
		}
	}

	private String getColorHex() {
		// Assuming color is a part of config (could be RGB or Hex format)
		java.awt.Color staffColor = config.StaffColor();
		return String.format("#%02x%02x%02x", staffColor.getRed(), staffColor.getGreen(), staffColor.getBlue());
	}

	private void sendMode2Command() {
		log.info("Sending rainbow effect for Mode 2.");
		sendCommandToESP32("effect|rainbow");
	}

	@Provides
	SpellTrackerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SpellTrackerConfig.class);
	}
}
