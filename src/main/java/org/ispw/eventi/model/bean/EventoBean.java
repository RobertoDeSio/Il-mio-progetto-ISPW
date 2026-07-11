package org.ispw.eventi.model.bean;

/**
 * Bean di uscita che rappresenta un evento per la UI.
 *
 * <p>La UI (EsploraController, DettaglioEventoController) non vede mai
 * l'Entity {@code Evento}: riceve esclusivamente questo Bean.
 * Prodotto da {@code PrenotaEventoAppController.getEventiDisponibili()}.</p>
 */
public class EventoBean {

    private final String id;
    private final String nome;
    private final String descrizione;
    private final String data;
    private final String luogo;
    private final String categoria;      // "MOTO", "BOAT", "TREKKING"
    private final int    postiDisponibili;
    private final double prezzo;

    public EventoBean(String id, String nome, String descrizione,
                      String data, String luogo, String categoria,
                      int postiDisponibili, double prezzo) {
        this.id               = id;
        this.nome             = nome;
        this.descrizione      = descrizione;
        this.data             = data;
        this.luogo            = luogo;
        this.categoria        = categoria;
        this.postiDisponibili = postiDisponibili;
        this.prezzo           = prezzo;
    }

    public String getId()                { return id; }
    public String getNome()              { return nome; }
    public String getDescrizione()       { return descrizione; }
    public String getData()              { return data; }
    public String getLuogo()             { return luogo; }
    public String getCategoria()         { return categoria; }
    public int    getPostiDisponibili()  { return postiDisponibili; }
    public double getPrezzo()            { return prezzo; }

    /** Etichetta formattata per la UI. */
    public String getPrezzoLabel()       { return "€" + (int) prezzo; }

    /** Etichetta posti formattata per la UI. */
    public String getPostiLabel()        { return postiDisponibili + " posti disponibili"; }
}