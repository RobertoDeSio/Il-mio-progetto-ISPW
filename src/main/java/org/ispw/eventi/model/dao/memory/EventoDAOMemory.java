package org.ispw.eventi.model.dao.memory;

import org.ispw.eventi.model.dao.EventoDAO;
import org.ispw.eventi.model.entity.Evento;
import org.ispw.eventi.model.entity.Evento.Categoria;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventoDAOMemory implements EventoDAO {

    // LinkedHashMap: O(1) per ID, mantiene ordine di inserimento
    private static final Map<String, Evento> EVENTI = new LinkedHashMap<>();

    static {
        Evento e1 = new Evento("1", "Giro in Moto sulle Dolomiti",
                "Un tour mozzafiato tra i passi più belli delle Dolomiti.",
                "15 giugno 2026", "Bolzano, IT", 10, 4, 45.0, Categoria.MOTO);
        Evento e2 = new Evento("2", "Giornata in Barca a Vela",
                "Una giornata indimenticabile in barca a vela.",
                "20 luglio 2026", "Portofino, IT", 6, 0, 120.0, Categoria.BOAT);
        Evento e3 = new Evento("3", "Trekking Monte Bianco",
                "Escursione guidata sul Monte Bianco.",
                "10 agosto 2026", "Courmayeur, IT", 15, 8, 30.0, Categoria.TREKKING);
        EVENTI.put(e1.getId(), e1);
        EVENTI.put(e2.getId(), e2);
        EVENTI.put(e3.getId(), e3);
    }

    @Override
    public List<Evento> findAll() {
        return new ArrayList<>(EVENTI.values());
    }

    @Override
    public Evento findById(String id) {
        return EVENTI.get(id);
    }

    @Override
    public void update(Evento evento) {
        EVENTI.put(evento.getId(), evento);  // put sovrascrive la chiave esistente
    }
}