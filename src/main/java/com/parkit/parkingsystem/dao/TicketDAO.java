package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");
    private final DataBaseConfig dataBaseConfig;

    public TicketDAO(DataBaseConfig dataBaseConfig) {
        this.dataBaseConfig = dataBaseConfig;
    }

    public boolean saveTicket(Ticket ticket) {
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)) {

            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : new Timestamp(ticket.getOutTime().getTime()));

            return ps.executeUpdate() == 1;

        } catch (Exception ex) {
            logger.error("Error saving ticket", ex);
            return false;
        }
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Ticket ticket = null;

        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {

            ps.setString(1, vehicleRegNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ticket = new Ticket();
                    ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
                    ticket.setParkingSpot(parkingSpot);
                    ticket.setId(rs.getInt(2));
                    ticket.setVehicleRegNumber(vehicleRegNumber);
                    ticket.setPrice(rs.getDouble(3));
                    ticket.setInTime(rs.getTimestamp(4));
                    ticket.setOutTime(rs.getTimestamp(5));
                }
            }

        } catch (Exception ex) {
            logger.error("Error fetching ticket", ex);
        }

        return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)) {

            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3, ticket.getId());

            return ps.executeUpdate() == 1;

        } catch (Exception ex) {
            logger.error("Error updating ticket", ex);
            return false;
        }
    }

    public int getNbTicket(String vehicleRegNumber) {
        int nbTicket = 0;

        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.GET_NB_TICKET)) {

            ps.setString(1, vehicleRegNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nbTicket = rs.getInt(1);
                }
            }

        } catch (Exception ex) {
            logger.error("Error fetching ticket count", ex);
        }

        return nbTicket;
    }
}
