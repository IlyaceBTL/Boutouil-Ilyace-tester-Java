package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        double durationHours = (outHour - inHour) / (1000 * 60 * 60);
        if(durationHours<=0.5){
            ticket.setPrice(0);
            return;
        }
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(durationHours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(durationHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}