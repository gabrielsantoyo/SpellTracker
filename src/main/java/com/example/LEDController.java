package com.example;

import com.fazecast.jSerialComm.SerialPort;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * LEDController manages communication with the ESP8266 device over USB (COM8).
 */
@Slf4j
public class LEDController {

    private static final String DEVICE_PORT = "COM8"; // USB port where ESP8266 is connected (COM8)
    private SerialPort serialPort;
    private OutputStream outputStream;

    /**
     * Initializes the connection to the ESP8266 over COM8.
     */
    public LEDController() {
        try {
            serialPort = SerialPort.getCommPort(DEVICE_PORT);
            serialPort.setBaudRate(9600); // Ensure baud rate matches your ESP8266 code
            serialPort.openPort(); // Open the serial port
            outputStream = serialPort.getOutputStream();
            log.info("Connected to ESP8266 over {} at 9600 baud rate", DEVICE_PORT);
        } catch (Exception e) {
            log.error("Failed to connect to ESP8266 over {}: {}", DEVICE_PORT, e.getMessage());
        }
    }

    /**
     * Sends a command to the ESP8266 to update the LED mode.
     *
     * @param mode The LED mode to set (e.g., DIM, CAST).
     */
    public void setLEDMode(LEDMode mode) {
        if (outputStream == null) {
            log.error("Output stream is not available. Check the connection to ESP8266.");
            return;
        }

        try {
            String command = mode.getCommand();
            outputStream.write(command.getBytes());
            outputStream.flush();
            log.debug("Sent command to ESP8266: {}", command);
        } catch (Exception e) {
            log.error("Failed to send command to ESP8266: {}", e.getMessage());
        }
    }

    /**
     * Closes the connection to the ESP8266.
     */
    public void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort(); // Close the serial port
            }
            log.info("Connection to ESP8266 closed.");
        } catch (Exception e) {
            log.error("Failed to close connection to ESP8266: {}", e.getMessage());
        }
    }
}
