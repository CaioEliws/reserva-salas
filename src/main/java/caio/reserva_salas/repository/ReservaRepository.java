package caio.reserva_salas.repository;

import caio.reserva_salas.model.Reserva;
import caio.reserva_salas.model.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsBySalaIdAndStatusReservaAndInicioLessThanAndFimGreaterThan(
      Long salaId,
      StatusReserva status,
      LocalDateTime fim,
      LocalDateTime inicio
    );
}
