package org.ispw.eventi.exception;

/**
 * Eccezione lanciata dal {@code PrenotaEventoAppController} quando
 * l'evento selezionato non ha posti disponibili (extension 4a).
 */
public class EventoNonDisponibileException extends Exception {
    public EventoNonDisponibileException(String message) {
        super(message);
    }
}