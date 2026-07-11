package org.ispw.eventi.model.dao.dbms;

import org.ispw.eventi.model.dao.UtenteDAO;
import org.ispw.eventi.model.entity.Cliente;
import org.ispw.eventi.model.entity.Organizzatore;
import org.ispw.eventi.model.entity.Utente;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtenteDAODBMS implements UtenteDAO {

    private static final Logger LOGGER     = Logger.getLogger(UtenteDAODBMS.class.getName());
    private static final String SELECT_ALL = "SELECT nome, email, password, ruolo, nome_azienda, descrizione FROM utenti";

    @Override
    public void save(Utente utente) {
        String sql = "INSERT INTO utenti (nome, email, password, ruolo, nome_azienda, descrizione) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getEmail());
            stmt.setString(3, utente.getPassword());
            if (utente instanceof Organizzatore org) {
                stmt.setString(4, "ORGANIZZATORE");
                stmt.setString(5, org.getNomeAzienda());
                stmt.setString(6, org.getDescrizione());
            } else {
                stmt.setString(4, "CLIENTE");
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore salvataggio utente: " + utente.getEmail());
        }
    }

    @Override
    public Utente findByEmail(String email) {
        String sql = SELECT_ALL + " WHERE email = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore findByEmail: " + email);
        }
        return null;
    }

    @Override
    public Utente findByEmailAndPassword(String email, String password) {
        String sql = SELECT_ALL + " WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore findByEmailAndPassword: " + email);
        }
        return null;
    }

    private Utente mapRow(ResultSet rs) throws SQLException {
        String ruolo = rs.getString("ruolo");
        if ("ORGANIZZATORE".equals(ruolo)) {
            return new Organizzatore(
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("nome_azienda"),
                    rs.getString("descrizione")
            );
        }
        return new Cliente(
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}