package org.ispw.eventi.model;

import org.ispw.eventi.model.bean.EventoBean;
import org.ispw.eventi.model.bean.UserBean;

/**
 * Contenitore dello stato della sessione corrente.
 *
 * <p>Memorizza esclusivamente Bean — mai Entity di dominio.
 * La UI e i controller grafici non devono vedere le classi di dominio.</p>
 *
 * <ul>
 *   <li>{@link UserBean} per l'utente autenticato</li>
 *   <li>{@link EventoBean} per l'evento selezionato nel catalogo</li>
 * </ul>
 */
public class SessioneUtente {

    private UserBean   utenteLoggato;
    private EventoBean eventoSelezionato;

    /**
     * Destinazione a cui navigare dopo il login riuscito.
     * Impostata da chi chiama goToLogin() con un contesto specifico.
     * Valori possibili: "ESPLORA" (cliente) | "GESTIONE" (organizzatore) | null (default → esplora)
     */
    private String destinazionePostLogin;

    public UserBean    getUtenteLoggato()                   { return utenteLoggato; }
    public void        setUtenteLoggato(UserBean u)         { this.utenteLoggato = u; }
    public EventoBean  getEventoSelezionato()               { return eventoSelezionato; }
    public void        setEventoSelezionato(EventoBean e)   { this.eventoSelezionato = e; }
    public boolean     isLoggato()                          { return utenteLoggato != null; }
    public String      getRuolo()                           { return isLoggato() ? utenteLoggato.getRuolo() : null; }
    public String      getDestinazionePostLogin()           { return destinazionePostLogin; }
    public void        setDestinazionePostLogin(String d)   { this.destinazionePostLogin = d; }
    public void        logout()                             { utenteLoggato = null; eventoSelezionato = null; destinazionePostLogin = null; }
}