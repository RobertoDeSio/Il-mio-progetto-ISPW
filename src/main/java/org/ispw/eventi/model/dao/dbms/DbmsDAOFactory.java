package org.ispw.eventi.model.dao.dbms;

import org.ispw.eventi.model.dao.DAOFactory;
import org.ispw.eventi.model.dao.EventoDAO;
import org.ispw.eventi.model.dao.PrenotazioneDAO;
import org.ispw.eventi.model.dao.UtenteDAO;

public class DbmsDAOFactory extends DAOFactory {

    @Override
    public EventoDAO getEventoDAO()             { return new EventoDAODBMS(); }

    @Override
    public PrenotazioneDAO getPrenotazioneDAO() { return new PrenotazioneDAODBMS(); }

    @Override
    public UtenteDAO getUtenteDAO()             { return new UtenteDAODBMS(); }
}