package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * ParkingService is responsible for the core business logic of the parking system.
 * It manages vehicle entry and exit, ticket creation, fare calculation, and parking spot updates.
 */
public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    // Responsible for fare calculation logic
    private static final FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private final InputReaderUtil inputReaderUtil;
    private final ParkingSpotDAO parkingSpotDAO;
    private final TicketDAO ticketDAO;

    /**
     * Constructor to initialize the ParkingService with required utilities and DAOs.
     */
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    /**
     * Processes the entry of a new vehicle:
     * - Gets the next available parking spot
     * - Asks user for registration number
     * - Creates and saves a new ticket
     * - Marks the parking spot as unavailable
     */
    public void processIncomingVehicle() {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if (parkingSpot != null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehichleRegNumber();

                // Mark the parking spot as occupied
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);

                // Create and store the ticket
                Date inTime = new Date();
                Ticket ticket = new Ticket();
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);

                logger.info("Generated Ticket and saved in DB");

                // Check for recurring user
                if (ticketDAO.getNbTicket(vehicleRegNumber) > 1) {
                    logger.info("Welcome back! As a regular user of our parking, you will receive a {} discount.", "5%");
                }

                logger.info("Please park your vehicle in spot number: {}", parkingSpot.getId());
                logger.info("Recorded in-time for vehicle number: {} is: {}", vehicleRegNumber, inTime);
            }
        } catch (Exception e) {
            logger.error("Unable to process incoming vehicle", e);
        }
    }

    /**
     * Prompts the user to enter their vehicle registration number.
     *
     * @return the entered registration number
     */
    private String getVehichleRegNumber() {
        logger.info("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    /**
     * Finds the next available parking slot based on the vehicle type selected by the user.
     *
     * @return a ParkingSpot object if available, otherwise null
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);

            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            logger.error("Error parsing user input for type of vehicle", ie);
        } catch (Exception e) {
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    /**
     * Prompts the user to choose the type of vehicle and returns the corresponding ParkingType.
     *
     * @return the ParkingType selected by the user
     * @throws IllegalArgumentException if the input is invalid
     */
    private ParkingType getVehichleType() {
        logger.info("Please select vehicle type from menu");
        logger.info("1 CAR");
        logger.info("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1:
                return ParkingType.CAR;
            case 2:
                return ParkingType.BIKE;
            default:
                logger.info("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
        }
    }

    /**
     * Processes the exit of a vehicle:
     * - Retrieves the vehicle ticket
     * - Sets out time and calculates fare
     * - Applies discount if user is a regular
     * - Updates the ticket and frees the parking spot
     */
    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

            Date outTime = new Date();
            ticket.setOutTime(outTime);

            // Check if user is a regular to apply discount
            if (ticketDAO.getNbTicket(vehicleRegNumber) > 2) {
                fareCalculatorService.calculateFare(ticket, true);
            } else {
                fareCalculatorService.calculateFare(ticket);
            }

            // Update ticket and make the parking spot available again
            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);

                logger.info("Please pay the parking fare: {}", ticket.getPrice());
                logger.info("Recorded out-time for vehicle number: {} is: {}", ticket.getVehicleRegNumber(), outTime);
            } else {
                logger.info("Unable to update ticket information. Error occurred");
            }
        } catch (Exception e) {
            logger.error("Unable to process exiting vehicle", e);
        }
    }
}
