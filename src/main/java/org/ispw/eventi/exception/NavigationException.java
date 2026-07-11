package org.ispw.eventi.exception;

/**
 * Eccezione sollevata quando la navigazione tra le view fallisce.
 *
 * Applica il pattern Exception Chaining:
 * - causa tecnica interna (IOException) mantenuta come cause
 * - messaggio comprensibile esposto allo strato superiore
 */
public class NavigationException extends Exception {

    public NavigationException(String message, Throwable cause) {
        super(message, cause);
    }
}
