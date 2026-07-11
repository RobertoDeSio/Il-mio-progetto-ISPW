package org.ispw.eventi.model.dao.memory;

import org.ispw.eventi.model.dao.DAOFactory;
import org.ispw.eventi.model.dao.EventoDAO;
import org.ispw.eventi.model.dao.PrenotazioneDAO;
import org.ispw.eventi.model.dao.UtenteDAO;

public class MemoryDAOFactory extends DAOFactory {

    @Override
    public EventoDAO getEventoDAO()           { return new EventoDAOMemory(); }

    @Override
    public PrenotazioneDAO getPrenotazioneDAO() { return new PrenotazioneDAOMemory(); }

    @Override
    public UtenteDAO getUtenteDAO()           { return new UtenteDAOMemory(); }
}