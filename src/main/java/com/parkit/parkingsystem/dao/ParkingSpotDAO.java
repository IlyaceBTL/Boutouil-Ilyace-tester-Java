package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO class for interacting with the parking spot data in the database.
 */
public class ParkingSpotDAO {

    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    private final DataBaseConfig dataBaseConfig;

    /**
     * Constructor for ParkingSpotDAO.
     *
     * @param dataBaseConfig The database configuration object used for obtaining connections.
     */
    public ParkingSpotDAO(DataBaseConfig dataBaseConfig) {
        this.dataBaseConfig = dataBaseConfig;
    }

    /**
     * Fetches the next available parking slot for a given parking type (CAR or BIKE).
     *
     * @param parkingType The type of parking (e.g., CAR or BIKE).
     * @return The ID of the next available slot, or -1 if no slot is available or an error occurs.
     */
    public int getNextAvailableSlot(ParkingType parkingType) {
        int result = -1;
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)) {

            ps.setString(1, parkingType.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                result = rs.getInt(1);
            }

            // Clean up the result set and prepared statement
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);

        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        }
        return result;
    }

    /**
     * Updates the availability status of a parking spot in the database.
     *
     * @param parkingSpot The ParkingSpot object containing the updated availability and ID.
     * @return true if the update was successful (one row affected), false otherwise.
     */
    public boolean updateParking(ParkingSpot parkingSpot) {
        // Update the availability for the given parking spot
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)) {

            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            int updateRowCount = ps.executeUpdate();

            dataBaseConfig.closePreparedStatement(ps);

            return (updateRowCount == 1);

        } catch (Exception ex) {
            logger.error("Error updating parking info", ex);
            return false;
        }
    }

}
