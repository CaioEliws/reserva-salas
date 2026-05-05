package caio.reserva_salas.service;

import caio.reserva_salas.exceptions.RecursoNaoEncontradoException;
import caio.reserva_salas.model.Reserva;
import caio.reserva_salas.model.Sala;
import caio.reserva_salas.model.StatusReserva;
import caio.reserva_salas.model.Usuario;
import caio.reserva_salas.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaService salaService;
    private final UsuarioService usuarioService;

    public ReservaService(
            ReservaRepository reservaRepository,
            SalaService salaService,
            UsuarioService usuarioService
    ) {
        this.reservaRepository = reservaRepository;
        this.salaService = salaService;
        this.usuarioService = usuarioService;
    }

    public Reserva criarReserva(Long salaId, Long usuarioId, Reserva reserva) {
        Sala sala = salaService.buscarPorId(salaId);
        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        validar(reserva);
        validarSalaAtiva(sala);
        validarConflitoHorario(salaId, reserva);

        reserva.setSala(sala);
        reserva.setUsuario(usuario);
        reserva.setStatusReserva(StatusReserva.ATIVA);

        return reservaRepository.save(reserva);
    }

    public Reserva cancelarReserva(Long reservaId) {
        Reserva reserva = buscarPorId(reservaId);

        if (reserva.getStatusReserva() == StatusReserva.CANCELADA) {
            throw new IllegalArgumentException("Essa reserva já está cancelada.");
        }

        reserva.setStatusReserva(StatusReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada."));
    }

    public Reserva atualizar(Long id, Reserva dados) {
        Reserva reserva = buscarPorId(id);

        validar(dados);

        reserva.setTitulo(dados.getTitulo());
        reserva.setInicio(dados.getInicio());
        reserva.setFim(dados.getFim());

        return reservaRepository.save(reserva);
    }

    public void remover(Long id) {
        Reserva reserva = buscarPorId(id);
        reservaRepository.delete(reserva);
    }

    private void validar(Reserva reserva) {
        if (reserva.getTitulo() == null || reserva.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O título da reserva é obrigatório.");
        }

        if (reserva.getInicio() == null || reserva.getFim() == null) {
            throw new IllegalArgumentException("Data de início e fim são obrigatórias.");
        }

        if (!reserva.getInicio().isBefore(reserva.getFim())) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de fim.");
        }
    }

    private void validarSalaAtiva(Sala sala) {
        if (Boolean.FALSE.equals(sala.getAtiva())) {
            throw new IllegalArgumentException("Não é possível reservar uma sala inativa.");
        }
    }

    private void validarConflitoHorario(Long salaId, Reserva reserva) {
        boolean existeConflito = reservaRepository.existsBySalaIdAndStatusReservaAndInicioLessThanAndFimGreaterThan(
                salaId,
                StatusReserva.ATIVA,
                reserva.getFim(),
                reserva.getInicio()
        );

        if (existeConflito) {
            throw new IllegalArgumentException("Já existe uma reserva ativa para essa sala nesse horário.");
        }
    }
}