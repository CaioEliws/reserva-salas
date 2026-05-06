package caio.reserva_salas.repository;

import caio.reserva_salas.model.Reserva;
import caio.reserva_salas.model.StatusReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsBySalaIdAndStatusReservaAndInicioLessThanAndFimGreaterThan(
      Long salaId,
      StatusReserva status,
      LocalDateTime fim,
      LocalDateTime inicio
    );

    List<Reserva> findBySalaIdAndInicioBetween(
        Long salaId,
        LocalDateTime inicio,
        LocalDateTime fim
    );

    Page<Reserva> findBySalaId(Long salaId, Pageable pageable);

    Page<Reserva> findByStatusReserva(StatusReserva statusReserva, Pageable pageable);

}
