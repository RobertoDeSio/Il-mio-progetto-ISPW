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

    public Evento(String id, String nome, String descrizione, String data,
                  String luogo, int postiTotali, int postiOccupati,
                  double prezzo, Categoria categoria) {
        this.id            = id;
        this.nome          = nome;
        this.descrizione   = descrizione;
        this.data          = data;
        this.luogo         = luogo;
        this.postiTotali   = postiTotali;
        this.postiOccupati = postiOccupati;
        this.prezzo        = prezzo;
        this.categoria     = categoria;
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
}