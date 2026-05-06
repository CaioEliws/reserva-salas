package caio.reserva_salas.service;

import caio.reserva_salas.exceptions.RecursoNaoEncontradoException;
import caio.reserva_salas.model.Sala;
import caio.reserva_salas.repository.SalaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    @Test
    void deveCriarSalaComSucesso() {
        Sala sala = new Sala();
        sala.setNome("Sala Azul");
        sala.setCapacidade(20);

        when(salaRepository.save(any(Sala.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sala resultado = salaService.criar(sala);

        assertEquals("Sala Azul", resultado.getNome());
        assertEquals(20, resultado.getCapacidade());
        assertTrue(resultado.getAtiva());
    }

    @Test
    void deveLancarErroQuandoCapacidadeForInvalida() {
        Sala sala = new Sala();
        sala.setNome("Sala Azul");
        sala.setCapacidade(0);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> salaService.criar(sala)
        );

        assertEquals("A capacidade da sala deve ser positiva.", ex.getMessage());
        verify(salaRepository, never()).save(any());
    }

    @Test
    void deveLancarErroQuandoNomeDaSalaForVazio() {
        Sala sala = new Sala();
        sala.setNome("");
        sala.setCapacidade(10);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> salaService.criar(sala)
        );

        assertEquals("O nome da sala é obrigatório.", ex.getMessage());
    }

    @Test
    void deveBuscarSalaPorId() {
        Sala sala = new Sala();
        sala.setId(1L);
        sala.setNome("Sala 1");

        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));

        Sala resultado = salaService.buscarPorId(1L);

        assertEquals("Sala 1", resultado.getNome());
    }

    @Test
    void deveLancarErroQuandoSalaNaoEncontrada() {
        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> salaService.buscarPorId(1L)
        );

        assertEquals("Sala não encontrada.", ex.getMessage());
    }
}