package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * Represents a parking spot in the system.
 * A parking spot has an ID (number), a parking type (e.g., CAR or BIKE),
 * and a flag indicating whether it is currently available.
 */
public class ParkingSpot {

    private int number;
    private ParkingType parkingType;
    private boolean isAvailable;

    /**
     * Constructor for ParkingSpot.
     *
     * @param number      The unique identifier for the parking spot.
     * @param parkingType The type of the parking spot (e.g., CAR or BIKE).
     * @param isAvailable Indicates whether the spot is currently available.
     */
    public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
        this.number = number;
        this.parkingType = parkingType;
        this.isAvailable = isAvailable;
    }

    /**
     * Gets the ID (number) of the parking spot.
     *
     * @return The ID of the parking spot.
     */
    public int getId() {
        return number;
    }

    /**
     * Sets the ID (number) of the parking spot.
     *
     * @param number The new ID for the parking spot.
     */
    public void setId(int number) {
        this.number = number;
    }

    /**
     * Gets the type of parking spot (CAR or BIKE).
     *
     * @return The parking type.
     */
    public ParkingType getParkingType() {
        return parkingType;
    }

    /**
     * Sets the type of the parking spot.
     *
     * @param parkingType The parking type to set.
     */
    public void setParkingType(ParkingType parkingType) {
        this.parkingType = parkingType;
    }

    /**
     * Indicates whether the parking spot is available.
     *
     * @return true if the spot is available; false otherwise.
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Sets the availability of the parking spot.
     *
     * @param available true to mark the spot as available, false otherwise.
     */
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    /**
     * Compares this parking spot to another object for equality.
     * Two parking spots are considered equal if they have the same number.
     *
     * @param o The object to compare to.
     * @return true if the objects are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSpot that = (ParkingSpot) o;
        return number == that.number;
    }

    /**
     * Returns a hash code value for the object based on the parking spot number.
     *
     * @return The hash code for this parking spot.
     */
    @Override
    public int hashCode() {
        return number;
    }
}
