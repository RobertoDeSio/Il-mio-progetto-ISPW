package org.ispw.eventi.model.bean;

/**
 * Bean di uscita che rappresenta una prenotazione per la UI.
 *
 * Porta con sé anche le informazioni di presentazione dello State
 * (etichetta e stile badge) in modo che la UI non debba fare switch
 * sulla stringa dello stato.
 *
 * Costruito tramite Builder per evitare un costruttore con troppi
 * parametri (SonarQube S107 — max 7 parametri autorizzati).
 */
public class PrenotazioneBean {

    private final String  id;
    private final String  nomeEvento;
    private final String  clienteEmail;
    private final int     numeroPartecipanti;
    private final String  note;
    private final String  stato;
    private final double  totale;
    private final boolean puoiPagare;
    private final boolean puoiApprovare;
    private final boolean puoiRifiutare;
    private final String  etichettaStato;
    private final String  badgeStile;
    private final String  dataApprovazione;   // ISO string, null se non ancora approvata

    private PrenotazioneBean(Builder b) {
        this.id                 = b.id;
        this.nomeEvento         = b.nomeEvento;
        this.clienteEmail       = b.clienteEmail;
        this.numeroPartecipanti = b.numeroPartecipanti;
        this.note               = b.note;
        this.stato              = b.stato;
        this.totale             = b.totale;
        this.puoiPagare         = b.puoiPagare;
        this.puoiApprovare      = b.puoiApprovare;
        this.puoiRifiutare      = b.puoiRifiutare;
        this.etichettaStato     = b.etichettaStato;
        this.badgeStile         = b.badgeStile;
        this.dataApprovazione   = b.dataApprovazione;
    }

    public String  getId()                  { return id; }
    public String  getNomeEvento()          { return nomeEvento; }
    public String  getClienteEmail()        { return clienteEmail; }
    public int     getNumeroPartecipanti()  { return numeroPartecipanti; }
    public String  getNote()                { return note; }
    public String  getStato()               { return stato; }
    public double  getTotale()              { return totale; }
    public String  getTotaleLabel()         { return totale > 0 ? "€" + (int) totale : "—"; }
    public boolean isPuoiPagare()           { return puoiPagare; }
    public boolean isPuoiApprovare()        { return puoiApprovare; }
    public boolean isPuoiRifiutare()        { return puoiRifiutare; }
    public String  getEtichettaStato()      { return etichettaStato; }
    public String  getBadgeStile()          { return badgeStile; }
    public String  getDataApprovazione()    { return dataApprovazione; }

    public static Builder builder() {
        return new Builder();
    }

    /** Builder fluente per PrenotazioneBean. */
    public static final class Builder {
        private String  id;
        private String  nomeEvento;
        private String  clienteEmail;
        private int     numeroPartecipanti;
        private String  note;
        private String  stato;
        private double  totale;
        private boolean puoiPagare;
        private boolean puoiApprovare;
        private boolean puoiRifiutare;
        private String  etichettaStato;
        private String  badgeStile;
        private String  dataApprovazione;

        private Builder() { }

        public Builder id(String id)                             { this.id = id; return this; }
        public Builder nomeEvento(String nomeEvento)              { this.nomeEvento = nomeEvento; return this; }
        public Builder clienteEmail(String clienteEmail)          { this.clienteEmail = clienteEmail; return this; }
        public Builder numeroPartecipanti(int numeroPartecipanti) { this.numeroPartecipanti = numeroPartecipanti; return this; }
        public Builder note(String note)                          { this.note = note; return this; }
        public Builder stato(String stato)                        { this.stato = stato; return this; }
        public Builder totale(double totale)                      { this.totale = totale; return this; }
        public Builder puoiPagare(boolean puoiPagare)             { this.puoiPagare = puoiPagare; return this; }
        public Builder puoiApprovare(boolean puoiApprovare)       { this.puoiApprovare = puoiApprovare; return this; }
        public Builder puoiRifiutare(boolean puoiRifiutare)       { this.puoiRifiutare = puoiRifiutare; return this; }
        public Builder etichettaStato(String etichettaStato)      { this.etichettaStato = etichettaStato; return this; }
        public Builder badgeStile(String badgeStile)              { this.badgeStile = badgeStile; return this; }
        public Builder dataApprovazione(String dataApprovazione)  { this.dataApprovazione = dataApprovazione; return this; }

        public PrenotazioneBean build() {
            return new PrenotazioneBean(this);
        }
    }
}