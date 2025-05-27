package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Unit tests for {@link FareCalculatorService}.
 * This class tests fare calculation logic for different vehicle types,
 * various parking durations, discount application, and error handling.
 */
class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    /**
     * Initialize the FareCalculatorService instance once before all tests.
     */
    @BeforeAll
    static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    /**
     * Create a new Ticket instance before each test.
     */
    @BeforeEach
    void setUpPerTest() {
        ticket = new Ticket();
    }

    /**
     * Test fare calculation for a car parked for exactly one hour.
     */
    @Test
    void calculateFareCar() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 hour ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice(), 0.1);
    }

    /**
     * Test fare calculation for a bike parked for exactly one hour.
     */
    @Test
    void calculateFareBike() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 hour ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice(), 0.1);
    }

    /**
     * Test that a NullPointerException is thrown when parking type is unknown (null).
     */
    @Test
    void calculateFareUnknownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 hour ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Test that an IllegalArgumentException is thrown when the inTime is in the future.
     */
    @Test
    void calculateFareBikeWithFutureInTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000)); // 1 hour in the future
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Test fare calculation for a bike parked less than one hour (45 minutes),
     * expecting a proportional fare (3/4 of hourly rate).
     */
    @Test
    void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(0.75 * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }

    /**
     * Test fare calculation for a car parked less than one hour (45 minutes),
     * expecting a proportional fare (3/4 of hourly rate).
     */
    @Test
    void calculateFareCarWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(0.75 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    /**
     * Test fare calculation for a car parked more than a day (24 hours),
     * expecting 24 times the hourly rate.
     */
    @Test
    void calculateFareCarWithMoreThanADayParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000)); // 24 hours ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(24 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    /**
     * Test fare calculation for a car parked less than 30 minutes,
     * expecting a free parking (price = 0).
     */
    @Test
    void calculateFareCarWithLessThanThirtyMinutesParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000)); // 30 minutes ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(0 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    /**
     * Test fare calculation for a bike parked less than 30 minutes,
     * expecting a free parking (price = 0).
     */
    @Test
    void calculateFareBikeWithLessThanThirtyMinutesParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000)); // 30 minutes ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(0 * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }

    /**
     * Test fare calculation for a car with a 5% discount applied,
     * expecting 95% of the normal fare for 1 hour parking.
     */
    @Test
    void calculateFareCarWithDiscountDescription() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 hour ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, Boolean.TRUE);

        assertEquals(0.95 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    /**
     * Test fare calculation for a bike with a 5% discount applied,
     * expecting 95% of the normal fare for 1 hour parking.
     */
    @Test
    void calculateFareBikeWithDiscountDescription() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 hour ago
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, Boolean.TRUE);

        assertEquals(0.95 * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }
}
