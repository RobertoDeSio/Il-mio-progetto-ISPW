package org.ispw.eventi.model.dao.dbms;

import org.ispw.eventi.model.dao.PrenotazioneDAO;
import org.ispw.eventi.model.entity.Prenotazione;
import org.ispw.eventi.model.entity.Prenotazione.Stato;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrenotazioneDAODBMS implements PrenotazioneDAO {

    private static final Logger LOGGER     = Logger.getLogger(PrenotazioneDAODBMS.class.getName());
    private static final String SELECT_ALL = "SELECT id, evento_id, cliente_email, numero_partecipanti, note, stato, data_approvazione FROM prenotazioni";

    @Override
    public void save(Prenotazione p) {
        String sql = "INSERT INTO prenotazioni (id, evento_id, cliente_email, numero_partecipanti, note, stato, data_approvazione) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, p.getId());
            stmt.setString(2, p.getEventoId());
            stmt.setString(3, p.getClienteEmail());
            stmt.setInt(4, p.getNumeroPartecipanti());
            stmt.setString(5, p.getNote());
            stmt.setString(6, p.getStato().name());
            stmt.setString(7, p.getDataApprovazione() != null ? p.getDataApprovazione().toString() : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore save prenotazione: " + p.getId());
        }
    }

    @Override
    public void update(Prenotazione p) {
        String sql = "UPDATE prenotazioni SET stato = ?, data_approvazione = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, p.getStato().name());
            stmt.setString(2, p.getDataApprovazione() != null ? p.getDataApprovazione().toString() : null);
            stmt.setString(3, p.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore update prenotazione: " + p.getId());
        }
    }

    @Override
    public Prenotazione findById(String id) {
        String sql = SELECT_ALL + " WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore findById prenotazione: " + id);
        }
        return null;
    }

    @Override
    public List<Prenotazione> findAll() {
        List<Prenotazione> risultato = new ArrayList<>();
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) risultato.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore findAll prenotazioni");
        }
        return risultato;
    }

    @Override
    public List<Prenotazione> findByClienteEmail(String clienteEmail) {
        List<Prenotazione> risultato = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE cliente_email = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, clienteEmail);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) risultato.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore findByClienteEmail: " + clienteEmail);
        }
        return risultato;
    }

    @Override
    public List<Prenotazione> findByEventoId(String eventoId) {
        List<Prenotazione> risultato = new ArrayList<>();
        String sql = SELECT_ALL + " WHERE evento_id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, eventoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) risultato.add(mapRow(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore findByEventoId: " + eventoId);
        }
        return risultato;
    }

    private Prenotazione mapRow(ResultSet rs) throws SQLException {
        Prenotazione p = new Prenotazione(
                rs.getString("id"),
                rs.getString("evento_id"),
                rs.getString("cliente_email"),
                rs.getInt("numero_partecipanti"),
                rs.getString("note")
        );
        p.setStato(Stato.valueOf(rs.getString("stato")));
        String dataAppr = rs.getString("data_approvazione");
        if (dataAppr != null) {
            p.setDataApprovazione(java.time.LocalDateTime.parse(dataAppr));
        }
        return p;
    }
}