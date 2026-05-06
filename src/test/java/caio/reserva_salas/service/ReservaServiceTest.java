package caio.reserva_salas.service;

import caio.reserva_salas.exceptions.RecursoNaoEncontradoException;
import caio.reserva_salas.model.Reserva;
import caio.reserva_salas.model.Sala;
import caio.reserva_salas.model.StatusReserva;
import caio.reserva_salas.model.Usuario;
import caio.reserva_salas.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SalaService salaService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void deveCriarReservaQuandoNaoHouverConflito() {
        Long salaId = 1L;
        Long usuarioId = 1L;

        Sala sala = new Sala();
        sala.setId(salaId);
        sala.setNome("Sala 1");
        sala.setCapacidade(10);
        sala.setAtiva(true);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Caio");
        usuario.setEmail("caio@email.com");
        usuario.setAtivo(true);

        Reserva reserva = new Reserva();
        reserva.setTitulo("Reunião");
        reserva.setInicio(LocalDateTime.of(2026, 5, 6, 10, 0));
        reserva.setFim(LocalDateTime.of(2026, 5, 6, 11, 0));

        when(salaService.buscarPorId(salaId)).thenReturn(sala);
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(usuario);
        when(reservaRepository.existsBySalaIdAndStatusReservaAndInicioLessThanAndFimGreaterThan(
                eq(salaId),
                eq(StatusReserva.ATIVA),
                eq(reserva.getFim()),
                eq(reserva.getInicio())
        )).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva resultado = reservaService.criarReserva(salaId, usuarioId, reserva);

        assertNotNull(resultado);
        assertEquals(StatusReserva.ATIVA, resultado.getStatusReserva());
        assertEquals(sala, resultado.getSala());
        assertEquals(usuario, resultado.getUsuario());

        verify(reservaRepository).save(reserva);
    }

    @Test
    void deveLancarErroQuandoHouverConflitoDeHorario() {
        Long salaId = 1L;
        Long usuarioId = 1L;

        Sala sala = new Sala();
        sala.setId(salaId);
        sala.setAtiva(true);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Reserva reserva = new Reserva();
        reserva.setTitulo("Reunião");
        reserva.setInicio(LocalDateTime.of(2026, 5, 6, 10, 0));
        reserva.setFim(LocalDateTime.of(2026, 5, 6, 11, 0));

        when(salaService.buscarPorId(salaId)).thenReturn(sala);
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(usuario);
        when(reservaRepository.existsBySalaIdAndStatusReservaAndInicioLessThanAndFimGreaterThan(
                eq(salaId),
                eq(StatusReserva.ATIVA),
                eq(reserva.getFim()),
                eq(reserva.getInicio())
        )).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.criarReserva(salaId, usuarioId, reserva)
        );

        assertEquals("Já existe uma reserva ativa para essa sala nesse horário.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void devePermitirReservaQuandoFimDaAnteriorForIgualAoInicioDaNova() {
        Long salaId = 1L;
        Long usuarioId = 1L;

        Sala sala = new Sala();
        sala.setId(salaId);
        sala.setAtiva(true);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Reserva reserva = new Reserva();
        reserva.setTitulo("Reunião 2");
        reserva.setInicio(LocalDateTime.of(2026, 5, 6, 11, 0));
        reserva.setFim(LocalDateTime.of(2026, 5, 6, 12, 0));

        when(salaService.buscarPorId(salaId)).thenReturn(sala);
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(usuario);
        when(reservaRepository.existsBySalaIdAndStatusReservaAndInicioLessThanAndFimGreaterThan(
                eq(salaId),
                eq(StatusReserva.ATIVA),
                eq(reserva.getFim()),
                eq(reserva.getInicio())
        )).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva resultado = reservaService.criarReserva(salaId, usuarioId, reserva);

        assertEquals(StatusReserva.ATIVA, resultado.getStatusReserva());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void deveIgnorarReservasCanceladasNaChecagemDeConflito() {
        Long salaId = 1L;
        Long usuarioId = 1L;

        Sala sala = new Sala();
        sala.setId(salaId);
        sala.setAtiva(true);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Reserva reserva = new Reserva();
        reserva.setTitulo("Reunião");
        reserva.setInicio(LocalDateTime.of(2026, 5, 6, 10, 0));
        reserva.setFim(LocalDateTime.of(2026, 5, 6, 11, 0));

        when(salaService.buscarPorId(salaId)).thenReturn(sala);
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(usuario);
        when(reservaRepository.existsBySalaIdAndStatusReservaAndInicioLessThanAndFimGreaterThan(
                eq(salaId),
                eq(StatusReserva.ATIVA),
                eq(reserva.getFim()),
                eq(reserva.getInicio())
        )).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva resultado = reservaService.criarReserva(salaId, usuarioId, reserva);

        assertNotNull(resultado);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void deveCancelarReservaComSucesso() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setStatusReserva(StatusReserva.ATIVA);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reserva resultado = reservaService.cancelarReserva(1L);

        assertEquals(StatusReserva.CANCELADA, resultado.getStatusReserva());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void deveLancarErroAoCancelarReservaJaCancelada() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setStatusReserva(StatusReserva.CANCELADA);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reservaService.cancelarReserva(1L)
        );

        assertEquals("Essa reserva já está cancelada.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarErroQuandoReservaNaoForEncontrada() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> reservaService.buscarPorId(99L)
        );

        assertEquals("Reserva não encontrada.", ex.getMessage());
    }
}