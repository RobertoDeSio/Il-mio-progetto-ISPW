package org.ispw.eventi.model.entity;

public class Organizzatore extends Cliente {

    private String nomeAzienda;
    private String descrizione;

    public Organizzatore(String nome, String email, String password,
                         String nomeAzienda, String descrizione) {
        super(nome, email, password);
        this.nomeAzienda  = nomeAzienda;
        this.descrizione  = descrizione;
    }

    public String getNomeAzienda()  { return nomeAzienda; }
    public String getDescrizione()  { return descrizione; }

    public void setNomeAzienda(String nomeAzienda) { this.nomeAzienda = nomeAzienda; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}