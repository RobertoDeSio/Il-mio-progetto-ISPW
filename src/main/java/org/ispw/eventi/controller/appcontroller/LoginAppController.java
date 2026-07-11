package org.ispw.eventi.controller.appcontroller;

import org.ispw.eventi.exception.AuthenticationException;
import org.ispw.eventi.exception.ValidationException;
import org.ispw.eventi.model.bean.LoginBean;
import org.ispw.eventi.model.bean.UserBean;
import org.ispw.eventi.model.dao.UtenteDAO;
import org.ispw.eventi.model.entity.Utente;

/**
 * Controller Applicativo per il caso d'uso di <strong>Login</strong>.
 *
 * <h3>Caratteristiche architetturali</h3>
 * <ul>
 *   <li><b>Stateless</b>: nessun attributo di istanza variabile.
 *       L'unico campo è il DAO iniettato a costruzione (immutabile).
 *       Più thread possono condividere la stessa istanza senza rischi.</li>
 *   <li><b>Disaccoppiato dalla UI</b>: non importa nulla di JavaFX.
 *       Comunica con la UI esclusivamente tramite Bean.</li>
 *   <li><b>Disaccoppiato dalla persistenza</b>: riceve {@link UtenteDAO}
 *       tramite Dependency Injection; funziona con qualsiasi implementazione
 *       (Memory, FileSystem, DBMS).</li>
 * </ul>
 *
 * <h3>Flusso</h3>
 * <pre>
 * UI  →  LoginBean  →  LoginAppController  →  UtenteDAO  →  Utente (Entity)
 *                            ↓
 *                        UserBean  →  UI
 * </pre>
 */
public class LoginAppController {

    private final UtenteDAO utenteDAO;

    /**
     * Costruisce il controller con il DAO iniettato.
     * Il chiamante (tipicamente il ViewController) ottiene il DAO
     * tramite la {@code DAOFactory} appropriata.
     *
     * @param utenteDAO implementazione del DAO da utilizzare
     */
    public LoginAppController(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    /**
     * Esegue il caso d'uso di login.
     *
     * <ol>
     *   <li>Delega la validazione sintattica al Bean stesso.</li>
     *   <li>Interroga il DAO con le credenziali.</li>
     *   <li>Converte l'Entity risultante in un {@link UserBean} da restituire alla UI.</li>
     * </ol>
     *
     * @param loginBean bean popolato dalla UI con email e password
     * @return {@link UserBean} con i dati dell'utente autenticato
     * @throws ValidationException     se i campi non superano la validazione sintattica
     * @throws AuthenticationException se le credenziali non trovano riscontro nella persistenza
     */
    public UserBean login(LoginBean loginBean)
            throws ValidationException, AuthenticationException {

        // 1. Validazione sintattica — fail-fast, prima di toccare il DAO
        loginBean.validate();

        // 2. Interrogazione della persistenza
        Utente utente = utenteDAO.findByEmailAndPassword(
                loginBean.getEmail(),
                loginBean.getPassword()
        );

        // 3. Controllo semantico
        if (utente == null) {
            throw new AuthenticationException("Email o password non corretti.");
        }

        // 4. Conversione Entity → Bean (la UI non vede mai Utente)
        return toUserBean(utente);
    }

    // -------------------------------------------------------------------------
    // Metodi privati di conversione (potrebbe essere estratto in UserConverter)
    // -------------------------------------------------------------------------

    /**
     * Converte un'Entity {@link Utente} nel corrispondente {@link UserBean}.
     * La UI riceverà solo questo Bean, senza mai toccare le classi di dominio.
     *
     * @param utente entity da convertire
     * @return bean pronto per la UI
     */
    private UserBean toUserBean(Utente utente) {
        String ruolo = utente.getClass().getSimpleName().toUpperCase();
        return new UserBean(utente.getNome(), utente.getEmail(), ruolo);
    }
}