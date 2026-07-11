package org.ispw.eventi.model.dao;

import org.ispw.eventi.model.entity.Utente;

public interface UtenteDAO {
    void save(Utente utente);
    Utente findByEmail(String email);
    Utente findByEmailAndPassword(String email, String password);
}