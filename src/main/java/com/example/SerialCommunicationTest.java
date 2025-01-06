package com.example;

import com.fazecast.jSerialComm.SerialPort;
import java.io.OutputStream;
import java.util.Scanner;

public class SerialCommunicationTest {

    public static void main(String[] args) {
        // Get all available serial ports
        SerialPort[] ports = SerialPort.getCommPorts();

        if (ports.length == 0) {
            System.out.println("No serial ports found.");
            return;
        }

        // Display available ports
        System.out.println("Available serial ports:");
        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName());
        }

        // Specify the port (e.g., COM8) directly for simplicity
        String portName = "COM8"; // Change this to the correct port if needed

        // Find the specified port
        SerialPort selectedPort = null;
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equalsIgnoreCase(portName)) {
                selectedPort = port;
                break;
            }
        }

        if (selectedPort == null) {
            System.out.println("Port " + portName + " not found.");
            return;
        }

        System.out.println("Using port: " + selectedPort.getSystemPortName());

        // Open the serial port
        if (selectedPort.openPort()) {
            System.out.println("Successfully opened the port.");

            // Set up serial port parameters
            selectedPort.setBaudRate(9600);
            selectedPort.setNumDataBits(8);
            selectedPort.setNumStopBits(1);
            selectedPort.setParity(SerialPort.NO_PARITY);

            // Send the "dim" command to the ESP32
            try {
                OutputStream outputStream = selectedPort.getOutputStream();
                outputStream.write("off\n".getBytes()); // Send "dim" command
                outputStream.flush();
                System.out.println("Sent 'dim' command to ESP32.");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Close the port after a short delay
            try {
                Thread.sleep(5000); // Keep the port open for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            selectedPort.closePort();
            System.out.println("Port closed.");
        } else {
            System.out.println("Failed to open the port.");
            System.out.println("Possible reasons:");
            System.out.println("- The port is in use by another application.");
            System.out.println("- Insufficient permissions to access the port.");
            System.out.println("- Incorrect or unavailable port name.");
        }
    }
}
