package org.ispw.eventi.model.dao.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ispw.eventi.model.dao.DAOFactory;
import org.ispw.eventi.model.dao.EventoDAO;
import org.ispw.eventi.model.dao.PrenotazioneDAO;
import org.ispw.eventi.model.dao.UtenteDAO;
import org.ispw.eventi.model.entity.Evento;
import org.ispw.eventi.model.entity.Evento.Categoria;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemDAOFactory extends DAOFactory {

    private static final Logger LOGGER    = Logger.getLogger(FileSystemDAOFactory.class.getName());
    private static final Gson   GSON      = new GsonBuilder().setPrettyPrinting().create();
    private static final String DIR_DATA  = "data";

    /**
     * Al momento della creazione della factory inizializza i file JSON
     * con i dati dummy se non esistono ancora.
     * Equivalente del blocco static dei DAOMemory e degli INSERT OR IGNORE dello schema SQL.
     */
    public FileSystemDAOFactory() {
        inizializzaEventi();
        inizializzaUtenti();
    }

    @Override
    public EventoDAO getEventoDAO()             { return new EventoDAOFileSystem(); }

    @Override
    public PrenotazioneDAO getPrenotazioneDAO() { return new PrenotazioneDAOFileSystem(); }

    @Override
    public UtenteDAO getUtenteDAO()             { return new UtenteDAOFileSystem(); }

    // -------------------------------------------------------------------------
    // Inizializzazione file JSON al primo avvio
    // -------------------------------------------------------------------------

    private void inizializzaEventi() {
        Path path = Paths.get(DIR_DATA, "eventi.json");
        if (Files.exists(path)) return; // già inizializzato

        List<Evento> eventi = List.of(
                Evento.builder().id("1").nome("Giro in Moto sulle Dolomiti")
                        .descrizione("Un tour mozzafiato tra i passi più belli delle Dolomiti.")
                        .data("15 giugno 2026").luogo("Bolzano, IT")
                        .postiTotali(10).postiOccupati(4).prezzo(45.0).categoria(Categoria.MOTO)
                        .build(),
                Evento.builder().id("2").nome("Giornata in Barca a Vela")
                        .descrizione("Una giornata indimenticabile in barca a vela.")
                        .data("20 luglio 2026").luogo("Portofino, IT")
                        .postiTotali(6).postiOccupati(0).prezzo(120.0).categoria(Categoria.BOAT)
                        .build(),
                Evento.builder().id("3").nome("Trekking Monte Bianco")
                        .descrizione("Escursione guidata sul Monte Bianco.")
                        .data("10 agosto 2026").luogo("Courmayeur, IT")
                        .postiTotali(15).postiOccupati(8).prezzo(30.0).categoria(Categoria.TREKKING)
                        .build()
        );
        scriviFile(path, GSON.toJson(eventi));
    }

    private void inizializzaUtenti() {
        Path path = Paths.get(DIR_DATA, "utenti.json");
        if (Files.exists(path)) return; // già inizializzato

        // Usiamo una struttura raw per evitare il problema della
        // deserializzazione di Utente (classe astratta) con Gson.
        // Ogni utente ha un campo "tipo" che UtenteDAOFileSystem usa
        // per ricostruire il tipo concreto (Cliente o Organizzatore).
        String json = """
                [
                  {
                    "tipo": "CLIENTE",
                    "nome": "Mario Rossi",
                    "email": "cliente@test.it",
                    "password": "cliente123"
                  },
                  {
                    "tipo": "ORGANIZZATORE",
                    "nome": "Laura Bianchi",
                    "email": "org@test.it",
                    "password": "org123",
                    "nomeAzienda": "Weekender Srl",
                    "descrizione": "Organizzatore di eventi outdoor"
                  }
                ]
                """;
        scriviFile(path, json);
    }

    private void scriviFile(Path path, String content) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8);
            LOGGER.info(() -> "File inizializzato: " + path);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore inizializzazione file: " + path);
        }
    }
}