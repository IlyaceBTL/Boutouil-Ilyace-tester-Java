package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * Service responsible for calculating the fare of a parking ticket.
 * It supports calculating normal fares and applying discounts for recurring users.
 */
public class FareCalculatorService {

    /**
     * Calculates the fare for a given ticket, with an optional discount applied.
     * If the parking duration is 30 minutes or less, parking is free.
     *
     * @param ticket   The ticket for which to calculate the fare. Must have inTime and outTime set.
     * @param discount If true, applies a 5% discount to the total fare.
     * @throws IllegalArgumentException if outTime is null or before inTime, or if parking type is unknown.
     */
    public void calculateFare(Ticket ticket, Boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime());
        }

        // Convert timestamps to milliseconds, then compute duration in hours
        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();
        double durationHours = (outHour - inHour) / (1000 * 60 * 60);

        // Free if parked for 30 minutes or less
        if (durationHours <= 0.5) {
            ticket.setPrice(0);
            return;
        }

        // Apply discount if applicable
        double reduction = Boolean.TRUE.equals(discount) ? 0.95 : 1.0;

        // Calculate fare based on parking type
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                ticket.setPrice(durationHours * Fare.CAR_RATE_PER_HOUR * reduction);
                break;
            case BIKE:
                ticket.setPrice(durationHours * Fare.BIKE_RATE_PER_HOUR * reduction);
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }

    /**
     * Calculates the fare for a given ticket without any discount.
     * If the parking duration is 30 minutes or less, parking is free.
     *
     * @param ticket The ticket for which to calculate the fare. Must have inTime and outTime set.
     * @throws IllegalArgumentException if outTime is null or before inTime, or if parking type is unknown.
     */
    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();
        double durationHours = (outHour - inHour) / (1000 * 60 * 60);

        // Free if parked for 30 minutes or less
        if (durationHours <= 0.5) {
            ticket.setPrice(0);
            return;
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                ticket.setPrice(durationHours * Fare.CAR_RATE_PER_HOUR);
                break;
            case BIKE:
                ticket.setPrice(durationHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}
