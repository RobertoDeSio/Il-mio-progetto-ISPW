package org.ispw.eventi.controller.appcontroller;

import org.ispw.eventi.exception.EventoNonDisponibileException;
import org.ispw.eventi.exception.PagamentoFallitoException;
import org.ispw.eventi.exception.PrenotazioneNotFoundException;
import org.ispw.eventi.exception.ValidationException;
import org.ispw.eventi.model.bean.EventoBean;
import org.ispw.eventi.model.bean.PrenotazioneBean;
import org.ispw.eventi.model.bean.PrenotazioneRequestBean;
import org.ispw.eventi.model.dao.EventoDAO;
import org.ispw.eventi.model.dao.PrenotazioneDAO;
import org.ispw.eventi.model.dao.memory.EventoDAOMemory;
import org.ispw.eventi.model.dao.memory.PrenotazioneDAOMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test del caso d'uso "Prenota Evento" usando i DAO in memoria
 * (nessuna dipendenza da file o database esterni).
 *
 * NB: EventoDAOMemory e PrenotazioneDAOMemory conservano i dati in mappe
 * statiche condivise tra le istanze create nella stessa JVM. Per questo
 * ogni test evita di assumere valori assoluti fissi e usa eventi diversi
 * (Barca, Trekking, Moto) per non interferire con gli altri test.
 */
class PrenotaEventoAppControllerTest {

    private static final String EVENTO_BARCA_ID    = "2"; // 6 posti totali, 0 occupati
    private static final String EVENTO_TREKKING_ID = "3"; // 15 posti totali, 8 occupati
    private static final String EVENTO_MOTO_ID     = "1"; // 10 posti totali, 4 occupati

    private PrenotaEventoAppController controller;

    @BeforeEach
    void setUp() {
        EventoDAO eventoDAO = new EventoDAOMemory();
        PrenotazioneDAO prenotazioneDAO = new PrenotazioneDAOMemory();
        controller = new PrenotaEventoAppController(eventoDAO, prenotazioneDAO);
    }

    private int postiDisponibiliDi(String eventoId) {
        return controller.getEventiDisponibili().stream()
                .filter(e -> e.getId().equals(eventoId))
                .findFirst()
                .map(EventoBean::getPostiDisponibili)
                .orElseThrow();
    }

    @Test
    void getEventiDisponibili_restituisceCatalogoNonVuoto() {
        List<EventoBean> eventi = controller.getEventiDisponibili();

        assertFalse(eventi.isEmpty());
        assertTrue(eventi.stream().anyMatch(e -> e.getId().equals(EVENTO_BARCA_ID)));
    }

    @Test
    void richiediPrenotazione_eventoInesistente_lanciaValidationException() {
        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                "id-inesistente", "cliente@test.it", 1, "3331234567", "");

        assertThrows(ValidationException.class, () -> controller.richiediPrenotazione(richiesta));
    }

    @Test
    void richiediPrenotazione_datiNonValidi_lanciaValidationException() {
        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                EVENTO_MOTO_ID, "", 1, "3331234567", "");

        assertThrows(ValidationException.class, () -> controller.richiediPrenotazione(richiesta));
    }

    @Test
    void richiediPrenotazione_troppiPartecipanti_lanciaEventoNonDisponibileException() {
        int disponibili = postiDisponibiliDi(EVENTO_MOTO_ID);
        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                EVENTO_MOTO_ID, "cliente@test.it", disponibili + 1, "3331234567", "");

        assertThrows(EventoNonDisponibileException.class,
                () -> controller.richiediPrenotazione(richiesta));
    }

    @Test
    void richiediPrenotazione_datiValidi_creaPrenotazioneInAttesa() throws Exception {
        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                EVENTO_BARCA_ID, "cliente@test.it", 2, "3331234567", "finestrino");

        PrenotazioneBean risultato = controller.richiediPrenotazione(richiesta);

        assertNotNull(risultato.getId());
        assertEquals("IN_ATTESA", risultato.getStato());
        assertEquals(2, risultato.getNumeroPartecipanti());
        assertFalse(risultato.isPuoiPagare(), "in attesa non si può ancora pagare");
    }

    @Test
    void flussoCompleto_richiediApprovaPaga_confermaPrenotazioneEAggiornaPosti() throws Exception {
        int disponibiliPrima = postiDisponibiliDi(EVENTO_BARCA_ID);

        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                EVENTO_BARCA_ID, "mario@test.it", 2, "3331234567", "");
        PrenotazioneBean creata = controller.richiediPrenotazione(richiesta);
        assertEquals("IN_ATTESA", creata.getStato());

        PrenotazioneBean approvata = controller.approvaRichiesta(creata.getId());
        assertEquals("APPROVATA", approvata.getStato());
        assertTrue(approvata.isPuoiPagare());

        PrenotazioneBean pagata = controller.effettuaPagamento(creata.getId(), "1234567890123456");
        assertEquals("CONFERMATA", pagata.getStato());
        assertTrue(pagata.getTotale() > 0);

        int disponibiliDopo = postiDisponibiliDi(EVENTO_BARCA_ID);
        assertEquals(disponibiliPrima - 2, disponibiliDopo,
                "dopo il pagamento i posti disponibili devono diminuire di 2");
    }

    @Test
    void rifiutaRichiesta_cambiaStatoInRifiutataENonPermettePagamento() throws Exception {
        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                EVENTO_BARCA_ID, "luca@test.it", 1, "3339876543", "");
        PrenotazioneBean creata = controller.richiediPrenotazione(richiesta);

        PrenotazioneBean rifiutata = controller.rifiutaRichiesta(creata.getId());

        assertEquals("RIFIUTATA", rifiutata.getStato());
        assertFalse(rifiutata.isPuoiPagare());
    }

    @Test
    void effettuaPagamento_cartaFallimento_lanciaPagamentoFallitoENonCambiaLoStato() throws Exception {
        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                EVENTO_TREKKING_ID, "anna@test.it", 1, "3335551122", "");
        PrenotazioneBean creata = controller.richiediPrenotazione(richiesta);
        controller.approvaRichiesta(creata.getId());

        assertThrows(PagamentoFallitoException.class, () -> controller.effettuaPagamento(
                creata.getId(), PrenotazioneRequestBean.CARTA_TEST_FALLIMENTO));

        // Lo stato resta APPROVATA: il cliente può riprovare con un'altra carta
        PrenotazioneBean daListaCliente = controller.getPrenotazioniCliente("anna@test.it").stream()
                .filter(p -> p.getId().equals(creata.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals("APPROVATA", daListaCliente.getStato());
    }

    @Test
    void approvaRichiesta_idInesistente_lanciaPrenotazioneNotFoundException() {
        assertThrows(PrenotazioneNotFoundException.class,
                () -> controller.approvaRichiesta("id-che-non-esiste"));
    }

    @Test
    void effettuaPagamento_idInesistente_lanciaPrenotazioneNotFoundException() {
        assertThrows(PrenotazioneNotFoundException.class,
                () -> controller.effettuaPagamento("id-che-non-esiste", "1234"));
    }

    @Test
    void simulaScadenza_dopo25oreDaApprovazione_cambiaStatoInScaduta() throws Exception {
        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                EVENTO_TREKKING_ID, "giorgio@test.it", 1, "3337778899", "");
        PrenotazioneBean creata = controller.richiediPrenotazione(richiesta);
        controller.approvaRichiesta(creata.getId());

        PrenotazioneBean scaduta = controller.simulaScadenza(creata.getId());

        assertEquals("SCADUTA", scaduta.getStato());
    }

    @Test
    void getPrenotazioniCliente_restituisceSoloQuelleDelCliente() throws Exception {
        String email = "esclusivo@test.it";
        PrenotazioneRequestBean richiesta = new PrenotazioneRequestBean(
                EVENTO_BARCA_ID, email, 1, "3330001122", "");
        controller.richiediPrenotazione(richiesta);

        List<PrenotazioneBean> prenotazioni = controller.getPrenotazioniCliente(email);

        assertFalse(prenotazioni.isEmpty());
        assertTrue(prenotazioni.stream().allMatch(p -> p.getClienteEmail().equalsIgnoreCase(email)));
    }
}