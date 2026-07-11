package org.ispw.eventi.model.dao.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.ispw.eventi.model.dao.PrenotazioneDAO;
import org.ispw.eventi.model.entity.Prenotazione;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrenotazioneDAOFileSystem implements PrenotazioneDAO {

    private static final Logger LOGGER    = Logger.getLogger(PrenotazioneDAOFileSystem.class.getName());
    private static final String FILE_PATH = "data/prenotazioni.json";
    private static final Gson   GSON      = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(Prenotazione prenotazione) {
        List<Prenotazione> prenotazioni = findAll();
        prenotazioni.add(prenotazione);
        writeFile(GSON.toJson(prenotazioni));
    }

    @Override
    public List<Prenotazione> findByClienteEmail(String clienteEmail) {
        List<Prenotazione> risultato = new ArrayList<>();
        for (Prenotazione p : findAll()) {
            if (p.getClienteEmail().equals(clienteEmail)) {
                risultato.add(p);
            }
        }
        return risultato;
    }

    @Override
    public List<Prenotazione> findByEventoId(String eventoId) {
        List<Prenotazione> risultato = new ArrayList<>();
        for (Prenotazione p : findAll()) {
            if (p.getEventoId().equals(eventoId)) {
                risultato.add(p);
            }
        }
        return risultato;
    }

    @Override
    public void update(Prenotazione prenotazione) {
        List<Prenotazione> prenotazioni = findAll();
        for (int i = 0; i < prenotazioni.size(); i++) {
            if (prenotazioni.get(i).getId().equals(prenotazione.getId())) {
                prenotazioni.set(i, prenotazione);
                break;
            }
        }
        writeFile(GSON.toJson(prenotazioni));
    }

    @Override
    public Prenotazione findById(String id) {
        for (Prenotazione p : findAll()) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    @Override
    public List<Prenotazione> findAll() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }
            String json = Files.readString(path, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Prenotazione>>(){}.getType();
            return GSON.fromJson(json, listType);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore lettura file prenotazioni: " + FILE_PATH);
            return new ArrayList<>();
        }
    }

    private void writeFile(String content) {
        try {
            Path path = Paths.get(FILE_PATH);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore scrittura file prenotazioni: " + FILE_PATH);
        }
    }
}