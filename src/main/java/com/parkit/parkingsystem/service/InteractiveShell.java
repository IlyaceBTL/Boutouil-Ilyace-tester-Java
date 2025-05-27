package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * InteractiveShell is the entry point of the Parking System CLI (Command Line Interface).
 */
public class InteractiveShell {

    private static final Logger logger = LogManager.getLogger("InteractiveShell");

    /**
     * Starts the Parking System application and loads the interactive menu.
     * Initializes the required services and handles user input to perform operations.
     */
    public static void loadInterface() {
        logger.info("App initialized!!!");
        logger.info("Welcome to Parking System!");

        boolean continueApp = true;

        // Initialize configuration and DAO objects
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        InputReaderUtil inputReaderUtil = new InputReaderUtil();
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO(dataBaseConfig);
        TicketDAO ticketDAO = new TicketDAO(dataBaseConfig);

        // Service layer responsible for business logic
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Main loop that keeps the application running until the user exits
        while (continueApp) {
            loadMenu();  // Show menu options
            int option = inputReaderUtil.readSelection();  // Read user selection
            switch (option) {
                case 1: {
                    parkingService.processIncomingVehicle();  // Handle vehicle entry
                    break;
                }
                case 2: {
                    parkingService.processExitingVehicle();  // Handle vehicle exit
                    break;
                }
                case 3: {
                    logger.info("Exiting from the system!");
                    continueApp = false;  // Exit the loop and stop the application
                    break;
                }
                default:
                    logger.info("Unsupported option. Please enter a number corresponding to the provided menu");
            }
        }
    }

    /**
     * Displays the interactive menu with available actions.
     */
    private static void loadMenu() {
        logger.info("Please select an option. Simply enter the number to choose an action");
        logger.info("1 New Vehicle Entering - Allocate Parking Space");
        logger.info("2 Vehicle Exiting - Generate Ticket Price");
        logger.info("3 Shutdown System");
    }
}
