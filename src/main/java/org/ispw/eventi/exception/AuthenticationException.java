package org.ispw.eventi.exception;

/**
 * Eccezione lanciata dal {@code LoginAppController} quando le credenziali
 * sono sintatticamente corrette ma non trovano riscontro nella persistenza.
 *
 * <p>Distinta da {@link ValidationException} perché l'errore non è
 * sintattico ma semantico: i campi sono ben formati, ma l'utente
 * non esiste o la password è sbagliata.</p>
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }
}