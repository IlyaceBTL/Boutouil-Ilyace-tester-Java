package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;


    @BeforeAll
    static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    void setUpPerTest() throws Exception {
        lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
        lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    static void tearDown() {
    }

    @Test
    void testParkingACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket);
        assertNotNull(ticket.getInTime());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());

        ParkingSpot parking = ticket.getParkingSpot();
        assertNotNull(parking);
        assertFalse(parking.isAvailable());

    }

    @Test
    void testParkingLotExit() {
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket.setInTime(inTime);
        ticket.setVehicleRegNumber("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticketDAO.saveTicket(ticket);


        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        Ticket ticketExit = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticketExit);
        assertNotNull(ticketExit.getOutTime());
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticketExit.getPrice(), 0.01);
        assertEquals(1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));

        ParkingSpot parking = ticket.getParkingSpot();
        assertNotNull(parking);
        assertFalse(parking.isAvailable());
    }

    @Test
    void testParkingLotExitRecurringUser() {

        Ticket ticket = new Ticket();
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        ticket.setInTime(inTime);
        ticket.setVehicleRegNumber("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setParkingSpot(parkingSpot);
        ticketDAO.saveTicket(ticket);


        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();

        Ticket ticketExit = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticketExit);
        assertNotNull(ticketExit.getOutTime());
        assertEquals(0.95 * Fare.CAR_RATE_PER_HOUR, ticketExit.getPrice(), 0.01);
        assertEquals(1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }

    @Test
    void testGetNbTicketForRecurringUser() {
        String vehicleRegNumber = "ZZ123ZZ";

        Ticket ticket1 = new Ticket();
        ticket1.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket1.setOutTime(new Date());
        ticket1.setVehicleRegNumber(vehicleRegNumber);
        ticket1.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticketDAO.saveTicket(ticket1);

        Ticket ticket2 = new Ticket();
        ticket2.setInTime(new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000)));
        ticket2.setOutTime(new Date());
        ticket2.setVehicleRegNumber(vehicleRegNumber);
        ticket2.setParkingSpot(new ParkingSpot(2, ParkingType.CAR, false));
        ticketDAO.saveTicket(ticket2);

        int nbTickets = ticketDAO.getNbTicket(vehicleRegNumber);

        assertEquals(2, nbTickets);
    }


}
