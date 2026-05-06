package caio.reserva_salas.repository;

import caio.reserva_salas.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    List<Sala> findByAtivaTrue();

    Optional<Sala> findByNome(String nome);

    boolean existsByNome(String nome);

}
