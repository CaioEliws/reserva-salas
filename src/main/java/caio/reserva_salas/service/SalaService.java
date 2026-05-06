package caio.reserva_salas.service;

import caio.reserva_salas.exceptions.RecursoNaoEncontradoException;
import caio.reserva_salas.model.Sala;
import caio.reserva_salas.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Transactional
    public Sala criar(Sala sala) {
        validar(sala);

        sala.setAtiva(true);
        return salaRepository.save(sala);
    }

    @Transactional(readOnly = true)
    public Sala buscarPorId(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala não encontrada."));
    }

    @Transactional
    public List<Sala> listar() {
        return salaRepository.findByAtivaTrue();
    }

    @Transactional
    public Sala atualizar(Long id, Sala dados) {
        Sala sala = buscarPorId(id);

        validar(dados);

        sala.setNome(dados.getNome());
        sala.setCapacidade(dados.getCapacidade());
        sala.setAtiva(dados.getAtiva());

        return salaRepository.save(sala);
    }

    @Transactional
    public void remover(Long id) {
        Sala sala = buscarPorId(id);
        salaRepository.delete(sala);
    }

    private void validar(Sala sala) {
        if (sala.getNome() == null || sala.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da sala é obrigatório.");
        }

        if (sala.getCapacidade() == null || sala.getCapacidade() <= 0) {
            throw new IllegalArgumentException("A capacidade da sala deve ser positiva.");
        }
    }
}