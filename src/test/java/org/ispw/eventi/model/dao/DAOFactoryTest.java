package org.ispw.eventi.model.dao;

import org.ispw.eventi.model.dao.dbms.DbmsDAOFactory;
import org.ispw.eventi.model.dao.filesystem.FileSystemDAOFactory;
import org.ispw.eventi.model.dao.memory.MemoryDAOFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Verifica che DAOFactory selezioni correttamente l'implementazione
 * (MEMORY / DBMS / FILESYSTEM) in base al tipo impostato a runtime
 * tramite setTipo(), come richiesto dal nuovo menu di selezione in Main.
 */
class DAOFactoryTest {

    @AfterEach
    void resetToDefault() {
        // Riporta la factory allo stato di default dopo ogni test,
        // così i test restano indipendenti tra loro.
        DAOFactory.setTipo(DAOFactory.Tipo.MEMORY);
    }

    @Test
    void setTipoMemory_creaMemoryDAOFactory() {
        DAOFactory.setTipo(DAOFactory.Tipo.MEMORY);

        DAOFactory factory = DAOFactory.getInstance();

        assertEquals(DAOFactory.Tipo.MEMORY, DAOFactory.getTipo());
        assertInstanceOf(MemoryDAOFactory.class, factory);
    }

    @Test
    void setTipoDbms_creaDbmsDAOFactory() {
        DAOFactory.setTipo(DAOFactory.Tipo.DBMS);

        DAOFactory factory = DAOFactory.getInstance();

        assertEquals(DAOFactory.Tipo.DBMS, DAOFactory.getTipo());
        assertInstanceOf(DbmsDAOFactory.class, factory);
    }

    @Test
    void setTipoFilesystem_creaFileSystemDAOFactory() {
        DAOFactory.setTipo(DAOFactory.Tipo.FILESYSTEM);

        DAOFactory factory = DAOFactory.getInstance();

        assertEquals(DAOFactory.Tipo.FILESYSTEM, DAOFactory.getTipo());
        assertInstanceOf(FileSystemDAOFactory.class, factory);
    }

    @Test
    void getInstance_restituisceSempreLaStessaIstanzaFinoAlCambioTipo() {
        DAOFactory.setTipo(DAOFactory.Tipo.MEMORY);

        DAOFactory prima = DAOFactory.getInstance();
        DAOFactory seconda = DAOFactory.getInstance();

        assertSame(prima, seconda,
                "getInstance() deve restituire lo stesso singleton finché il tipo non cambia");
    }

    @Test
    void setTipo_forzaLaRicreazioneDellIstanza() {
        DAOFactory.setTipo(DAOFactory.Tipo.MEMORY);
        DAOFactory memoryInstance = DAOFactory.getInstance();

        DAOFactory.setTipo(DAOFactory.Tipo.FILESYSTEM);
        DAOFactory filesystemInstance = DAOFactory.getInstance();

        assertNotSame(memoryInstance, filesystemInstance,
                "Cambiando tipo, getInstance() deve restituire una nuova istanza");
    }

    @Test
    void memoryFactory_fornisceDaoNonNulli() {
        DAOFactory.setTipo(DAOFactory.Tipo.MEMORY);
        DAOFactory factory = DAOFactory.getInstance();

        assertNotNull(factory.getEventoDAO());
        assertNotNull(factory.getPrenotazioneDAO());
        assertNotNull(factory.getUtenteDAO());
    }
}