package org.ispw.eventi.model.dao.memory;

import org.ispw.eventi.model.dao.UtenteDAO;
import org.ispw.eventi.model.entity.Cliente;
import org.ispw.eventi.model.entity.Organizzatore;
import org.ispw.eventi.model.entity.Utente;

import java.util.LinkedHashMap;
import java.util.Map;

public class UtenteDAOMemory implements UtenteDAO {

    // LinkedHashMap: O(1) per email, mantiene ordine di inserimento
    private static final Map<String, Utente> UTENTI = new LinkedHashMap<>();

    static {
        Utente cliente = new Cliente(
                "Mario Rossi", "cliente@test.it", "cliente123");
        Utente org = new Organizzatore(
                "Laura Bianchi", "org@test.it", "org123",
                "Weekender Srl", "Organizzatore di eventi outdoor");
        UTENTI.put(cliente.getEmail().toLowerCase(), cliente);
        UTENTI.put(org.getEmail().toLowerCase(), org);
    }

    @Override
    public void save(Utente utente) {
        UTENTI.put(utente.getEmail().toLowerCase(), utente);
    }

    @Override
    public Utente findByEmail(String email) {
        return UTENTI.get(email.toLowerCase());
    }

    @Override
    public Utente findByEmailAndPassword(String email, String password) {
        Utente utente = UTENTI.get(email.toLowerCase());
        if (utente != null && utente.getPassword().equals(password)) {
            return utente;
        }
        return null;
    }
}