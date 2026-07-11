package org.ispw.eventi.exception;

/**
 * Eccezione lanciata quando si tenta di operare su una prenotazione
 * che non esiste nel sistema.
 */
public class PrenotazioneNotFoundException extends Exception {
    public PrenotazioneNotFoundException(String message) {
        super(message);
    }
}