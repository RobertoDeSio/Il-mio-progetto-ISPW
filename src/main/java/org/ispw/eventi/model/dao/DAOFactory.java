package org.ispw.eventi.model.dao;

import org.ispw.eventi.model.dao.dbms.DbmsDAOFactory;
import org.ispw.eventi.model.dao.filesystem.FileSystemDAOFactory;
import org.ispw.eventi.model.dao.memory.MemoryDAOFactory;

public abstract class DAOFactory {

    public enum Tipo { MEMORY, FILESYSTEM, DBMS }

    private static final Tipo TIPO_ATTIVO = Tipo.DBMS;
    private static DAOFactory instance;

    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = switch (TIPO_ATTIVO) {
                case MEMORY     -> new MemoryDAOFactory();
                case FILESYSTEM -> new FileSystemDAOFactory();
                case DBMS       -> new DbmsDAOFactory();
            };
        }
        return instance;
    }

    public abstract EventoDAO getEventoDAO();
    public abstract PrenotazioneDAO getPrenotazioneDAO();
    public abstract UtenteDAO getUtenteDAO();
}