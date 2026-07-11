package org.ispw.eventi.model.dao.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.ispw.eventi.model.dao.EventoDAO;
import org.ispw.eventi.model.entity.Evento;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventoDAOFileSystem implements EventoDAO {

    private static final Logger LOGGER    = Logger.getLogger(EventoDAOFileSystem.class.getName());
    private static final String FILE_PATH = "data/eventi.json";
    private static final Gson   GSON      = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<Evento> findAll() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }
            String json = Files.readString(path, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Evento>>(){}.getType();
            return GSON.fromJson(json, listType);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore lettura file eventi: " + FILE_PATH);
            return new ArrayList<>();
        }
    }

    @Override
    public Evento findById(String id) {
        for (Evento e : findAll()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public void update(Evento evento) {
        List<Evento> eventi = findAll();
        for (int i = 0; i < eventi.size(); i++) {
            if (eventi.get(i).getId().equals(evento.getId())) {
                eventi.set(i, evento);
                break;
            }
        }
        try {
            Path path = Paths.get(FILE_PATH);
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(eventi), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Errore update file eventi: " + FILE_PATH);
        }
    }
}