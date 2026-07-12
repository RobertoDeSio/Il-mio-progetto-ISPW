package org.ispw.eventi.model.bean;

import org.ispw.eventi.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrenotazioneRequestBeanTest {

    @Test
    void validate_datiCorretti_nonLanciaEccezioni() {
        PrenotazioneRequestBean bean = new PrenotazioneRequestBean(
                "1", "cliente@test.it", 2, "3331234567", "nessuna nota");

        assertDoesNotThrow(bean::validate);
    }

    @Test
    void validate_eventoIdMancante_lanciaValidationException() {
        PrenotazioneRequestBean bean = new PrenotazioneRequestBean(
                "", "cliente@test.it", 2, "3331234567", "");

        ValidationException ex = assertThrows(ValidationException.class, bean::validate);
        assertTrue(ex.getMessage().toLowerCase().contains("evento"));
    }

    @Test
    void validate_emailMancante_lanciaValidationException() {
        PrenotazioneRequestBean bean = new PrenotazioneRequestBean(
                "1", " ", 2, "3331234567", "");

        assertThrows(ValidationException.class, bean::validate);
    }

    @Test
    void validate_numeroPartecipantiNonPositivo_lanciaValidationException() {
        PrenotazioneRequestBean bean = new PrenotazioneRequestBean(
                "1", "cliente@test.it", 0, "3331234567", "");

        assertThrows(ValidationException.class, bean::validate);
    }

    @Test
    void validate_telefonoMancante_lanciaValidationException() {
        PrenotazioneRequestBean bean = new PrenotazioneRequestBean(
                "1", "cliente@test.it", 2, "", "");

        assertThrows(ValidationException.class, bean::validate);
    }

    @Test
    void validate_telefonoNonNumerico_lanciaValidationException() {
        PrenotazioneRequestBean bean = new PrenotazioneRequestBean(
                "1", "cliente@test.it", 2, "abc123", "");

        assertThrows(ValidationException.class, bean::validate);
    }

    @Test
    void getNote_seNoteNull_restituisceStringaVuota() {
        PrenotazioneRequestBean bean = new PrenotazioneRequestBean(
                "1", "cliente@test.it", 2, "3331234567", null);

        assertEquals("", bean.getNote());
    }
}