package org.ispw.eventi.model.entity;

public class Evento {

    public enum Categoria { MOTO, BOAT, TREKKING }

    private String    id;
    private String    nome;
    private String    descrizione;
    private String    data;
    private String    luogo;
    private int       postiTotali;
    private int       postiOccupati;
    private double    prezzo;
    private Categoria categoria;

    private Evento(Builder b) {
        this.id            = b.id;
        this.nome          = b.nome;
        this.descrizione   = b.descrizione;
        this.data          = b.data;
        this.luogo         = b.luogo;
        this.postiTotali   = b.postiTotali;
        this.postiOccupati = b.postiOccupati;
        this.prezzo        = b.prezzo;
        this.categoria     = b.categoria;
    }

    public String    getId()            { return id; }
    public String    getNome()          { return nome; }
    public String    getDescrizione()   { return descrizione; }
    public String    getData()          { return data; }
    public String    getLuogo()         { return luogo; }
    public int       getPostiTotali()   { return postiTotali; }
    public int       getPostiOccupati() { return postiOccupati; }
    public double    getPrezzo()        { return prezzo; }
    public Categoria getCategoria()     { return categoria; }

    public void setId(String id)                       { this.id = id; }
    public void setNome(String nome)                   { this.nome = nome; }
    public void setDescrizione(String descrizione)     { this.descrizione = descrizione; }
    public void setData(String data)                   { this.data = data; }
    public void setLuogo(String luogo)                 { this.luogo = luogo; }
    public void setPostiTotali(int postiTotali)        { this.postiTotali = postiTotali; }
    public void setPostiOccupati(int postiOccupati)    { this.postiOccupati = postiOccupati; }
    public void setPrezzo(double prezzo)               { this.prezzo = prezzo; }
    public void setCategoria(Categoria categoria)      { this.categoria = categoria; }

    public String getPostiLabel() {
        return postiOccupati + "/" + postiTotali + " posti occupati";
    }

    public String getPrezzoLabel() {
        return "€" + (int) prezzo;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Builder fluente per Evento. */
    public static final class Builder {
        private String    id;
        private String    nome;
        private String    descrizione;
        private String    data;
        private String    luogo;
        private int       postiTotali;
        private int       postiOccupati;
        private double    prezzo;
        private Categoria categoria;

        private Builder() { }

        public Builder id(String id)                       { this.id = id; return this; }
        public Builder nome(String nome)                   { this.nome = nome; return this; }
        public Builder descrizione(String descrizione)     { this.descrizione = descrizione; return this; }
        public Builder data(String data)                   { this.data = data; return this; }
        public Builder luogo(String luogo)                 { this.luogo = luogo; return this; }
        public Builder postiTotali(int postiTotali)        { this.postiTotali = postiTotali; return this; }
        public Builder postiOccupati(int postiOccupati)    { this.postiOccupati = postiOccupati; return this; }
        public Builder prezzo(double prezzo)                { this.prezzo = prezzo; return this; }
        public Builder categoria(Categoria categoria)       { this.categoria = categoria; return this; }

        public Evento build() {
            return new Evento(this);
        }
    }
}