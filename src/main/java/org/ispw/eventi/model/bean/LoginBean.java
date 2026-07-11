package org.ispw.eventi.model.bean;

import org.ispw.eventi.exception.ValidationException;

/**
 * Bean di ingresso per il caso d'uso di Login.
 *
 * <p>Trasporta le credenziali dalla UI verso il {@code LoginAppController}.
 * Espone il metodo {@link #validate()} che il controller chiama come
 * prima operazione: se i dati sono sintatticamente invalidi l'eccezione
 * viene lanciata prima ancora di toccare il DAO.</p>
 *
 * <p>La UI costruisce questo Bean e non vede mai le Entity di dominio.</p>
 */
public class LoginBean {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final String email;
    private final String password;

    public LoginBean(String email, String password) {
        this.email    = email;
        this.password = password;
    }

    /**
     * Valida sintatticamente i campi del bean.
     *
     * @throws ValidationException se un campo è vuoto, l'email è malformata
     *                             o la password è troppo corta
     */
    public void validate() throws ValidationException {
        if (email == null || email.isBlank()) {
            throw new ValidationException("L'email non può essere vuota.");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new ValidationException("Formato email non valido.");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("La password non può essere vuota.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException(
                    "La password deve contenere almeno " + MIN_PASSWORD_LENGTH + " caratteri.");
        }
    }

    public String getEmail()    { return email.trim(); }
    public String getPassword() { return password; }
}