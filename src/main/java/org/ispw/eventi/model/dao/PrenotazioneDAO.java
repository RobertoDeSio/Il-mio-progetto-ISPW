package org.ispw.eventi.model.dao;

import org.ispw.eventi.model.entity.Prenotazione;
import java.util.List;

public interface PrenotazioneDAO {
    void save(Prenotazione prenotazione);
    void update(Prenotazione prenotazione);
    Prenotazione findById(String id);
    List<Prenotazione> findByClienteEmail(String clienteEmail);
    List<Prenotazione> findByEventoId(String eventoId);
    List<Prenotazione> findAll();
}