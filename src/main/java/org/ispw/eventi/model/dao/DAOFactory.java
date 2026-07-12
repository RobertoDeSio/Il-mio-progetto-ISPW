package org.ispw.eventi.model.dao;

import org.ispw.eventi.model.dao.dbms.DbmsDAOFactory;
import org.ispw.eventi.model.dao.filesystem.FileSystemDAOFactory;
import org.ispw.eventi.model.dao.memory.MemoryDAOFactory;

public abstract class DAOFactory {

    public enum Tipo { MEMORY, FILESYSTEM, DBMS }

    private static Tipo tipoAttivo = Tipo.MEMORY;
    private static DAOFactory instance;

    /**
     * Imposta il tipo di persistenza da usare. Va chiamato PRIMA della prima
     * invocazione di {@link #getInstance()} (tipicamente all'avvio dell'app,
     * dopo la scelta dell'utente). Se chiamato dopo che l'istanza è già
     * stata creata, ricrea l'istanza con il nuovo tipo.
     */
    public static void setTipo(Tipo tipo) {
        tipoAttivo = tipo;
        instance = null; // forza la ricreazione con il nuovo tipo
    }

    public static Tipo getTipo() {
        return tipoAttivo;
    }

    public static DAOFactory getInstance() {
        if (instance == null) {
            instance = switch (tipoAttivo) {
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