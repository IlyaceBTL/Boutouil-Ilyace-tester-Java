package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Utility class responsible for reading user input from the console.
 * Handles both numerical selections and vehicle registration numbers.
 */
public class InputReaderUtil {

    // Scanner for reading input from console
    private static final Scanner scan = new Scanner(System.in);

    // Logger instance for logging errors and information
    private static final Logger logger = LogManager.getLogger("InputReaderUtil");

    /**
     * Reads and parses a numerical selection input from the user.
     *
     * @return the integer value entered by the user, or -1 in case of an error
     */
    public int readSelection() {
        try {
            return Integer.parseInt(scan.nextLine());
        } catch (Exception e) {
            logger.error("Error while reading user input from Shell", e);
            logger.info("Error reading input. Please enter valid number for proceeding further");
            return -1;
        }
    }

    /**
     * Reads the vehicle registration number from user input.
     *
     * @return the trimmed vehicle registration number entered by the user
     * @throws IllegalArgumentException if the input is null or empty
     */
    public String readVehicleRegistrationNumber() {
        try {
            String vehicleRegNumber = scan.nextLine();
            // Validate that the input is not empty or null
            if (vehicleRegNumber == null || vehicleRegNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber.trim();
        } catch (Exception e) {
            logger.error("Error while reading user input from Shell", e);
            logger.info("Error reading input. Please enter a valid string for vehicle registration number");
            throw e; // Re-throwing the exception so caller can handle it
        }
    }
}
