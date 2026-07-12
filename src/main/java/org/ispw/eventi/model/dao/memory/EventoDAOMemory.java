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
        Evento e1 = Evento.builder().id("1").nome("Giro in Moto sulle Dolomiti")
                .descrizione("Un tour mozzafiato tra i passi più belli delle Dolomiti.")
                .data("15 giugno 2026").luogo("Bolzano, IT")
                .postiTotali(10).postiOccupati(4).prezzo(45.0).categoria(Categoria.MOTO)
                .build();
        Evento e2 = Evento.builder().id("2").nome("Giornata in Barca a Vela")
                .descrizione("Una giornata indimenticabile in barca a vela.")
                .data("20 luglio 2026").luogo("Portofino, IT")
                .postiTotali(6).postiOccupati(0).prezzo(120.0).categoria(Categoria.BOAT)
                .build();
        Evento e3 = Evento.builder().id("3").nome("Trekking Monte Bianco")
                .descrizione("Escursione guidata sul Monte Bianco.")
                .data("10 agosto 2026").luogo("Courmayeur, IT")
                .postiTotali(15).postiOccupati(8).prezzo(30.0).categoria(Categoria.TREKKING)
                .build();
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