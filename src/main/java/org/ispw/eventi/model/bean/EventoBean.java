package org.ispw.eventi.model.bean;

/**
 * Bean di uscita che rappresenta un evento per la UI.
 *
 * <p>La UI (EsploraController, DettaglioEventoController) non vede mai
 * l'Entity {@code Evento}: riceve esclusivamente questo Bean.
 * Prodotto da {@code PrenotaEventoAppController.getEventiDisponibili()}.</p>
 *
 * Costruito tramite Builder per evitare un costruttore con troppi
 * parametri (SonarQube S107 — max 7 parametri autorizzati).
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

    private EventoBean(Builder b) {
        this.id               = b.id;
        this.nome             = b.nome;
        this.descrizione      = b.descrizione;
        this.data             = b.data;
        this.luogo            = b.luogo;
        this.categoria        = b.categoria;
        this.postiDisponibili = b.postiDisponibili;
        this.prezzo           = b.prezzo;
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

    public static Builder builder() {
        return new Builder();
    }

    /** Builder fluente per EventoBean. */
    public static final class Builder {
        private String id;
        private String nome;
        private String descrizione;
        private String data;
        private String luogo;
        private String categoria;
        private int    postiDisponibili;
        private double prezzo;

        private Builder() { }

        public Builder id(String id)                          { this.id = id; return this; }
        public Builder nome(String nome)                      { this.nome = nome; return this; }
        public Builder descrizione(String descrizione)        { this.descrizione = descrizione; return this; }
        public Builder data(String data)                      { this.data = data; return this; }
        public Builder luogo(String luogo)                    { this.luogo = luogo; return this; }
        public Builder categoria(String categoria)            { this.categoria = categoria; return this; }
        public Builder postiDisponibili(int postiDisponibili) { this.postiDisponibili = postiDisponibili; return this; }
        public Builder prezzo(double prezzo)                  { this.prezzo = prezzo; return this; }

        public EventoBean build() {
            return new EventoBean(this);
        }
    }
}