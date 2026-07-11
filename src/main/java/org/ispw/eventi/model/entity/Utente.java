package org.ispw.eventi.model.entity;

public abstract class Utente {

    private String nome;
    private String email;
    private String password;

    protected Utente(String nome, String email, String password) {
        this.nome     = nome;
        this.email    = email;
        this.password = password;
    }

    public String getNome()     { return nome; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    public void setNome(String nome)         { this.nome = nome; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}