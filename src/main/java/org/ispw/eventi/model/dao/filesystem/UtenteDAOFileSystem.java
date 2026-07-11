package org.ispw.eventi.model.dao.filesystem;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.ispw.eventi.model.dao.UtenteDAO;
import org.ispw.eventi.model.entity.Cliente;
import org.ispw.eventi.model.entity.Organizzatore;
import org.ispw.eventi.model.entity.Utente;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione FileSystem di UtenteDAO.
 *
 * Ogni utente nel JSON ha un campo "tipo" ("CLIENTE" o "ORGANIZZATORE")
 * che permette a Gson di ricostruire il tipo concreto corretto.
 * Senza questo campo Gson non saprebbe se creare un Cliente o un Organizzatore
 * poiché Utente è una classe astratta.
 */
public class UtenteDAOFileSystem implements UtenteDAO {

    private static final Logger LOGGER    = Logger.getLogger(UtenteDAOFileSystem.class.getName());
    private static final String FILE_PATH = "data/utenti.json";
    private static final Gson   GSON      = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(Utente utente) {
        // Legge il JSON grezzo come lista di JsonObject
        List<JsonObject> utenti = findAllRaw();

        // Crea il JsonObject con il campo "tipo"
        JsonObject obj = utenteToJson(utente);
        utenti.add(obj);
        writeFile(GSON.toJson(utenti));
    }

    @Override
    public Utente findByEmail(String email) {
        for (JsonObject obj : findAllRaw()) {
            if (email.equalsIgnoreCase(obj.get("email").getAsString())) {
                return jsonToUtente(obj);
            }
        }
        return null;
    }

    @Override
    public Utente findByEmailAndPassword(String email, String password) {
        for (JsonObject obj : findAllRaw()) {
            if (email.equalsIgnoreCase(obj.get("email").getAsString())
                    && password.equals(obj.get("password").getAsString())) {
                return jsonToUtente(obj);
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Legge il file JSON come lista di JsonObject grezzi. */
    private List<JsonObject> findAllRaw() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) return new ArrayList<>();
            String json = Files.readString(path, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<JsonObject>>(){}.getType();
            List<JsonObject> result = GSON.fromJson(json, listType);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore lettura file utenti: " + FILE_PATH);
            return new ArrayList<>();
        }
    }

    /** Converte un JsonObject nel tipo concreto corretto usando il campo "tipo". */
    private Utente jsonToUtente(JsonObject obj) {
        String tipo = obj.get("tipo").getAsString();
        if ("ORGANIZZATORE".equals(tipo)) {
            return new Organizzatore(
                    obj.get("nome").getAsString(),
                    obj.get("email").getAsString(),
                    obj.get("password").getAsString(),
                    obj.has("nomeAzienda") ? obj.get("nomeAzienda").getAsString() : "",
                    obj.has("descrizione") ? obj.get("descrizione").getAsString() : ""
            );
        }
        return new Cliente(
                obj.get("nome").getAsString(),
                obj.get("email").getAsString(),
                obj.get("password").getAsString()
        );
    }

    /** Converte un Utente in JsonObject con il campo "tipo". */
    private JsonObject utenteToJson(Utente utente) {
        JsonObject obj = new JsonObject();
        if (utente instanceof Organizzatore org) {
            obj.addProperty("tipo", "ORGANIZZATORE");
            obj.addProperty("nome", org.getNome());
            obj.addProperty("email", org.getEmail());
            obj.addProperty("password", org.getPassword());
            obj.addProperty("nomeAzienda", org.getNomeAzienda());
            obj.addProperty("descrizione", org.getDescrizione());
        } else {
            obj.addProperty("tipo", "CLIENTE");
            obj.addProperty("nome", utente.getNome());
            obj.addProperty("email", utente.getEmail());
            obj.addProperty("password", utente.getPassword());
        }
        return obj;
    }

    private void writeFile(String content) {
        try {
            Path path = Paths.get(FILE_PATH);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore scrittura file utenti: " + FILE_PATH);
        }
    }
}