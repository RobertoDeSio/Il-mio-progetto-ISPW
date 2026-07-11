package org.ispw.eventi.exception;

/**
 * Eccezione lanciata dal {@code PrenotaEventoAppController} quando
 * il pagamento non va a buon fine (extension 9a).
 *
 * <p>Quando viene lanciata, la prenotazione esiste già nel sistema
 * in stato {@code IN_ATTESA}: il cliente ha 24 ore per riprovare.</p>
 *
 * <p><b>Simulazione:</b> il pagamento fallisce se il campo
 * "numero carta" contiene la stringa {@code "FAIL"}.</p>
 */
public class PagamentoFallitoException extends Exception {

    /** Id della prenotazione rimasta in stato IN_ATTESA. */
    private final String prenotazioneId;

    public PagamentoFallitoException(String message, String prenotazioneId) {
        super(message);
        this.prenotazioneId = prenotazioneId;
    }

    public String getPrenotazioneId() { return prenotazioneId; }
}