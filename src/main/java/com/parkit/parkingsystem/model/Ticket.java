package com.parkit.parkingsystem.model;

import java.util.Date;

/**
 * Represents a parking ticket associated with a parked vehicle.
 * A ticket includes information such as the ticket ID, parking spot,
 * vehicle registration number, price, and entry/exit timestamps.
 */
public class Ticket {

    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;

    /**
     * Gets the unique identifier for the ticket.
     *
     * @return The ticket ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the ticket.
     *
     * @param id The ticket ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the parking spot associated with this ticket.
     *
     * @return The ParkingSpot object.
     */
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    /**
     * Sets the parking spot associated with this ticket.
     *
     * @param parkingSpot The ParkingSpot to assign to the ticket.
     */
    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    /**
     * Gets the vehicle registration number.
     *
     * @return The vehicle's registration number.
     */
    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    /**
     * Sets the vehicle registration number.
     *
     * @param vehicleRegNumber The registration number of the vehicle.
     */
    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    /**
     * Gets the total price for the parking duration.
     *
     * @return The ticket price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the total price for the parking duration.
     *
     * @param price The price to set.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the entry time when the vehicle entered the parking.
     *
     * @return The entry timestamp.
     */
    public Date getInTime() {
        return inTime;
    }

    /**
     * Sets the entry time when the vehicle entered the parking.
     *
     * @param inTime The entry timestamp to set.
     */
    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    /**
     * Gets the exit time when the vehicle left the parking.
     *
     * @return The exit timestamp.
     */
    public Date getOutTime() {
        return outTime;
    }

    /**
     * Sets the exit time when the vehicle left the parking.
     *
     * @param outTime The exit timestamp to set.
     */
    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }
}
