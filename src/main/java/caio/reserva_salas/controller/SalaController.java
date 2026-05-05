package caio.reserva_salas.controller;

import caio.reserva_salas.model.Sala;
import caio.reserva_salas.service.SalaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public List<Sala> listar() {
        return salaService.listar();
    }

    @GetMapping("/{id}")
    public Sala buscarPorId(@PathVariable Long id) {
        return salaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sala criar(@RequestBody Sala sala) {
        return salaService.criar(sala);
    }

    @PutMapping("/{id}")
    public Sala atualizar(@PathVariable Long id, @RequestBody Sala sala) {
        return salaService.atualizar(id, sala);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        salaService.remover(id);
    }
}
