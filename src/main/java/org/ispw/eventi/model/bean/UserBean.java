package org.ispw.eventi.model.bean;

/**
 * Bean di uscita restituito dagli AppController alla UI dopo
 * un'operazione andata a buon fine.
 *
 * <p>Contiene solo i dati che la UI ha bisogno di conoscere:
 * la UI non vede mai l'Entity {@code Utente} né le sue sottoclassi.</p>
 */
public class UserBean {

    private final String nome;
    private final String email;
    private final String ruolo;   // "CLIENTE" o "ORGANIZZATORE"

    public UserBean(String nome, String email, String ruolo) {
        this.nome  = nome;
        this.email = email;
        this.ruolo = ruolo;
    }

    public String getNome()  { return nome; }
    public String getEmail() { return email; }
    public String getRuolo() { return ruolo; }
}