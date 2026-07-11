package org.ispw.eventi.exception;

/**
 * Eccezione lanciata quando i dati forniti dalla UI non superano
 * la validazione sintattica (campi vuoti, formato email non valido,
 * password troppo corta, ecc.).
 *
 * <p>Viene scatenata dal Bean stesso, prima che i dati raggiungano
 * l'AppController, garantendo il fail-fast.</p>
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}