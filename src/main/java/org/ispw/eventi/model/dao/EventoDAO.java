package org.ispw.eventi.model.dao;

import org.ispw.eventi.model.entity.Evento;
import java.util.List;

public interface EventoDAO {
    List<Evento> findAll();
    Evento findById(String id);
    void update(Evento evento);   // necessario per aggiornare i posti occupati
}