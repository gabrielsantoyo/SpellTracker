package com.example;

import com.fazecast.jSerialComm.SerialPort;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * LEDController manages communication with the ESP32 device over USB (COM8).
 */
@Slf4j
public class LEDController {

    private static final String DEVICE_PORT = "COM8"; // USB port where ESP32 is connected
    private SerialPort serialPort;
    private OutputStream outputStream;

    /**
     * Initializes the connection to the ESP32 over COM8.
     */
    public LEDController() {
        try {
            serialPort = SerialPort.getCommPort(DEVICE_PORT);

            if (serialPort == null) {
                log.error("Port {} not found.", DEVICE_PORT);
                return;
            }

            serialPort.setBaudRate(9600);
            serialPort.setNumDataBits(8);
            serialPort.setNumStopBits(1);
            serialPort.setParity(SerialPort.NO_PARITY);

            if (!serialPort.openPort()) {
                log.error("Failed to open port {}. Ensure it is not in use and permissions are correct.", DEVICE_PORT);
                return;
            }

            outputStream = serialPort.getOutputStream();
            log.info("Connected to ESP32 over {} at 9600 baud rate", DEVICE_PORT);
        } catch (Exception e) {
            log.error("Failed to connect to ESP32 over {}: {}", DEVICE_PORT, e.getMessage());
        }
    }

    /**
     * Sends a command to the ESP32 to update the LED mode.
     *
     * @param mode The LED mode to set (e.g., DIM, CAST, OFF).
     */
    public void setLEDMode(LEDMode mode) {
        if (outputStream == null) {
            log.error("Output stream is not available. Check the connection to ESP32.");
            return;
        }

        try {
            String command = mode.getCommand() + "\n"; // Ensure command ends with newline
            outputStream.write(command.getBytes());
            outputStream.flush();
            log.debug("Sent command to ESP32: {}", command.trim());
        } catch (Exception e) {
            log.error("Failed to send command to ESP32: {}", e.getMessage());
        }
    }

    /**
     * Closes the connection to the ESP32.
     */
    public void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
            }
            log.info("Connection to ESP32 closed.");
        } catch (Exception e) {
            log.error("Failed to close connection to ESP32: {}", e.getMessage());
        }
    }
}
