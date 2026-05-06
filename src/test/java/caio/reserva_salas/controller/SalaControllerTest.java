package caio.reserva_salas.controller;

import caio.reserva_salas.model.Sala;
import caio.reserva_salas.service.SalaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(SalaController.class)
class SalaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaService salaService;

    @Test
    void deveRetornar400QuandoCapacidadeForInvalida() throws Exception {
        when(salaService.criar(any(Sala.class)))
                .thenThrow(new IllegalArgumentException("A capacidade da sala deve ser positiva."));

        mockMvc.perform(post("/api/v1/salas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Sala A",
                                  "capacidade": 0,
                                  "ativa": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A capacidade da sala deve ser positiva."));
    }
}