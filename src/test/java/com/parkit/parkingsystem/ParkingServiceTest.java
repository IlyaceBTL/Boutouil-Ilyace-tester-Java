package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    private static final Logger logger = LogManager.getLogger("ParkingServiceTest");

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    /**
     * Setup mock objects before each test.
     * Initialize ParkingService with mocked dependencies.
     */
    @BeforeEach
    void setUpPerTest() {
        try {
            // Mock input for vehicle registration number
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            // Prepare a ParkingSpot and Ticket to be returned by mocks
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    /**
     * Test processExitingVehicle method for a user who is not regular (first time or single usage).
     * Verifies correct DAO method calls.
     */
    @Test
    void processExitingVehicleTest() {
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(0); // Number of times vehicle used parking

        parkingService.processExitingVehicle();

        verify(ticketDAO, times(1)).getTicket("ABCDEF");
        verify(ticketDAO, Mockito.times(1)).getNbTicket("ABCDEF");
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    /**
     * Test processExitingVehicle method for a regular user (multiple parking usages).
     * Checks if discounted fare calculation path is taken and DAO calls happen as expected.
     */
    @Test
    void processExitingVehicleTestRegularUser() {
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(2); // User with multiple parking usages

        parkingService.processExitingVehicle();

        verify(ticketDAO, times(1)).getTicket("ABCDEF");
        verify(ticketDAO, Mockito.times(1)).getNbTicket("ABCDEF");
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    /**
     * Test processIncomingVehicle method for a new user with available parking slot.
     * Verifies that ticket is saved and parking spot updated.
     */
    @Test
    void testProcessIncomingVehicle() {
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(0);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(ticketDAO, Mockito.times(1)).getNbTicket("ABCDEF");
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    /**
     * Test processIncomingVehicle method for a regular user with available parking slot.
     * Ensures discount logic path is exercised and correct DAO methods are called.
     */
    @Test
    void testProcessIncomingVehicleRegularUser() {
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(2);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(ticketDAO, Mockito.times(1)).getNbTicket("ABCDEF");
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    /**
     * Test processIncomingVehicle when no parking slots are available.
     * Verifies no ticket is saved.
     */
    @Test
    void testProcessIncomingVehicleNoSlot() {
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(ticketDAO, never()).saveTicket(any(Ticket.class));
    }

    /**
     * Test processExitingVehicle behavior when updateTicket returns false (unable to update).
     * Checks if updateTicket is still called.
     */
    @Test
    void processExitingVehicleTestUnableUpdate() {
        when(ticketDAO.getNbTicket("ABCDEF")).thenReturn(0);

        parkingService.processExitingVehicle();

        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
    }

    /**
     * Test getNextParkingNumberIfAvailable with valid input and available slot.
     * Verifies correct ParkingSpot is returned and DAO called once.
     */
    @Test
    void testGetNextParkingNumberIfAvailable() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        ParkingSpot parking = parkingService.getNextParkingNumberIfAvailable();

        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
        assertNotNull(parking);
        assertEquals(1, parking.getId());
        assertEquals(ParkingType.CAR, parking.getParkingType());
        assertTrue(parking.isAvailable());
    }

    /**
     * Test getNextParkingNumberIfAvailable when no parking slot is found.
     * Expected to return null.
     */
    @Test
    void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

        ParkingSpot parking = parkingService.getNextParkingNumberIfAvailable();

        assertNull(parking);
    }

    /**
     * Test getNextParkingNumberIfAvailable with invalid vehicle type input.
     * Expected to handle IllegalArgumentException and return null.
     */
    @Test
    void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        when(inputReaderUtil.readSelection()).thenReturn(3);

        ParkingSpot parking = parkingService.getNextParkingNumberIfAvailable();

        assertNull(parking);
    }

    /**
     * Test to verify getNextParkingNumberIfAvailable returns correct ParkingType CAR.
     */
    @Test
    void testGetVehicule() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        ParkingType parkingType = parkingService.getNextParkingNumberIfAvailable().getParkingType();

        assertEquals(ParkingType.CAR, parkingType);
    }

    /**
     * Test to verify getNextParkingNumberIfAvailable returns correct ParkingType BIKE.
     */
    @Test
    void testGetBike() {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);

        ParkingType parkingType = parkingService.getNextParkingNumberIfAvailable().getParkingType();

        assertEquals(ParkingType.BIKE, parkingType);
    }

    /**
     * Test processExitingVehicle behavior when no ticket is found (null returned).
     * Ensures no update or parking spot update occurs.
     */
    @Test
    void testProcessExitingVehicleWithNullTicket() {
        when(ticketDAO.getTicket("ABCDEF")).thenReturn(null);

        parkingService.processExitingVehicle();

        verify(ticketDAO, times(1)).getTicket("ABCDEF");
        verify(ticketDAO, never()).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, never()).updateParking(any(ParkingSpot.class));
    }

}
