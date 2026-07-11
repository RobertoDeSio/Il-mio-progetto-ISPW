package org.ispw.eventi.model.bean;

/**
 * Bean di uscita che rappresenta una prenotazione per la UI.
 *
 * Porta con sé anche le informazioni di presentazione dello State
 * (etichetta e stile badge) in modo che la UI non debba fare switch
 * sulla stringa dello stato.
 */
public class PrenotazioneBean {

    private final String  id;
    private final String  nomeEvento;
    private final String  clienteEmail;
    private final int     numeroPartecipanti;
    private final String  luogoDiIncontro;
    private final String  note;
    private final String  stato;           // enum name — per logica UI (puoiPagare, ecc.)
    private final double  totale;
    private final boolean puoiPagare;      // dal pattern State
    private final boolean puoiApprovare;   // dal pattern State
    private final boolean puoiRifiutare;   // dal pattern State
    private final String  etichettaStato;  // dal pattern State
    private final String  badgeStile;      // dal pattern State

    public PrenotazioneBean(String id, String nomeEvento, String clienteEmail,
                            int numeroPartecipanti, String luogoDiIncontro, String note,
                            String stato, double totale,
                            boolean puoiPagare, boolean puoiApprovare, boolean puoiRifiutare,
                            String etichettaStato, String badgeStile) {
        this.id                 = id;
        this.nomeEvento         = nomeEvento;
        this.clienteEmail       = clienteEmail;
        this.numeroPartecipanti = numeroPartecipanti;
        this.luogoDiIncontro    = luogoDiIncontro;
        this.note               = note;
        this.stato              = stato;
        this.totale             = totale;
        this.puoiPagare         = puoiPagare;
        this.puoiApprovare      = puoiApprovare;
        this.puoiRifiutare      = puoiRifiutare;
        this.etichettaStato     = etichettaStato;
        this.badgeStile         = badgeStile;
    }

    public String  getId()                  { return id; }
    public String  getNomeEvento()          { return nomeEvento; }
    public String  getClienteEmail()        { return clienteEmail; }
    public int     getNumeroPartecipanti()  { return numeroPartecipanti; }
    public String  getLuogoDiIncontro()     { return luogoDiIncontro; }
    public String  getNote()                { return note; }
    public String  getStato()               { return stato; }
    public double  getTotale()              { return totale; }
    public String  getTotaleLabel()         { return totale > 0 ? "€" + (int) totale : "—"; }
    public boolean isPuoiPagare()           { return puoiPagare; }
    public boolean isPuoiApprovare()        { return puoiApprovare; }
    public boolean isPuoiRifiutare()        { return puoiRifiutare; }
    public String  getEtichettaStato()      { return etichettaStato; }
    public String  getBadgeStile()          { return badgeStile; }
}