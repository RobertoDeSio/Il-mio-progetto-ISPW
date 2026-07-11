package org.ispw.eventi.model.bean;

import org.ispw.eventi.exception.ValidationException;

/**
 * Bean di ingresso per il caso d'uso "Prenota Evento" — step 5.
 * Raccoglie i dati del modulo di prenotazione.
 */
public class PrenotazioneRequestBean {

    /** Stringa di test per simulare il fallimento del pagamento. */
    public static final String CARTA_TEST_FALLIMENTO = "FAIL";

    private final String eventoId;
    private final String clienteEmail;
    private final int    numeroPartecipanti;
    private final String numeroDiTelefono;
    private final String note;

    public PrenotazioneRequestBean(String eventoId, String clienteEmail,
                                   int numeroPartecipanti,
                                   String numeroDiTelefono, String note) {
        this.eventoId           = eventoId;
        this.clienteEmail       = clienteEmail;
        this.numeroPartecipanti = numeroPartecipanti;
        this.numeroDiTelefono   = numeroDiTelefono;
        this.note               = note;
    }

    /**
     * Valida sintatticamente i campi obbligatori.
     *
     * @throws ValidationException se un campo non supera i controlli
     */
    public void validate() throws ValidationException {
        if (eventoId == null || eventoId.isBlank())
            throw new ValidationException("Nessun evento selezionato.");
        if (clienteEmail == null || clienteEmail.isBlank())
            throw new ValidationException("Sessione non valida. Esegui di nuovo il login.");
        if (numeroPartecipanti <= 0)
            throw new ValidationException("Il numero di partecipanti deve essere almeno 1.");
        if (numeroDiTelefono == null || numeroDiTelefono.isBlank())
            throw new ValidationException("Inserisci il numero di telefono.");
        if (!numeroDiTelefono.matches("\\d+"))
            throw new ValidationException("Il numero di telefono deve contenere solo cifre.");
    }

    public String getEventoId()           { return eventoId; }
    public String getClienteEmail()       { return clienteEmail; }
    public int    getNumeroPartecipanti() { return numeroPartecipanti; }
    public String getNumeroDiTelefono()   { return numeroDiTelefono; }
    public String getNote()               { return note != null ? note : ""; }
}