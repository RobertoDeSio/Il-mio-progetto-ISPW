package org.ispw.eventi.model.dao.dbms;

import org.ispw.eventi.model.dao.EventoDAO;
import org.ispw.eventi.model.entity.Evento;
import org.ispw.eventi.model.entity.Evento.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventoDAODBMS implements EventoDAO {

    private static final Logger LOGGER     = Logger.getLogger(EventoDAODBMS.class.getName());
    private static final String SELECT_ALL = "SELECT id, nome, descrizione, data, luogo, posti_totali, posti_occupati, prezzo, categoria FROM eventi";

    @Override
    public List<Evento> findAll() {
        List<Evento> eventi = new ArrayList<>();
        try (Statement stmt = DatabaseConnection.getInstance().createStatement();
             ResultSet rs   = stmt.executeQuery(SELECT_ALL)) {
            while (rs.next()) {
                eventi.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore findAll eventi");
        }
        return eventi;
    }

    @Override
    public Evento findById(String id) {
        String sql = SELECT_ALL + " WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore findById evento: " + id);
        }
        return null;
    }

    @Override
    public void update(Evento evento) {
        String sql = "UPDATE eventi SET posti_occupati = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().prepareStatement(sql)) {
            stmt.setInt(1, evento.getPostiOccupati());
            stmt.setString(2, evento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore update evento: " + evento.getId());
        }
    }

    private Evento mapRow(ResultSet rs) throws SQLException {
        return Evento.builder()
                .id(rs.getString("id"))
                .nome(rs.getString("nome"))
                .descrizione(rs.getString("descrizione"))
                .data(rs.getString("data"))
                .luogo(rs.getString("luogo"))
                .postiTotali(rs.getInt("posti_totali"))
                .postiOccupati(rs.getInt("posti_occupati"))
                .prezzo(rs.getDouble("prezzo"))
                .categoria(Categoria.valueOf(rs.getString("categoria")))
                .build();
    }
}