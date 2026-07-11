package org.ispw.eventi.model.dao.memory;

import org.ispw.eventi.model.dao.PrenotazioneDAO;
import org.ispw.eventi.model.entity.Prenotazione;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrenotazioneDAOMemory implements PrenotazioneDAO {

    // LinkedHashMap: O(1) per ID, mantiene ordine di inserimento
    private static final Map<String, Prenotazione> PRENOTAZIONI = new LinkedHashMap<>();

    @Override
    public void save(Prenotazione p) {
        PRENOTAZIONI.put(p.getId(), p);
    }

    @Override
    public void update(Prenotazione p) {
        PRENOTAZIONI.put(p.getId(), p);   // put sovrascrive la chiave esistente
    }

    @Override
    public Prenotazione findById(String id) {
        return PRENOTAZIONI.get(id);
    }

    @Override
    public List<Prenotazione> findAll() {
        return new ArrayList<>(PRENOTAZIONI.values());
    }

    @Override
    public List<Prenotazione> findByClienteEmail(String clienteEmail) {
        List<Prenotazione> risultato = new ArrayList<>();
        for (Prenotazione p : PRENOTAZIONI.values()) {
            if (p.getClienteEmail().equalsIgnoreCase(clienteEmail)) risultato.add(p);
        }
        return risultato;
    }

    @Override
    public List<Prenotazione> findByEventoId(String eventoId) {
        List<Prenotazione> risultato = new ArrayList<>();
        for (Prenotazione p : PRENOTAZIONI.values()) {
            if (p.getEventoId().equals(eventoId)) risultato.add(p);
        }
        return risultato;
    }
}