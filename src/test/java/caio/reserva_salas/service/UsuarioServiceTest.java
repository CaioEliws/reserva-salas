package caio.reserva_salas.service;

import caio.reserva_salas.exceptions.RecursoNaoEncontradoException;
import caio.reserva_salas.model.Usuario;
import caio.reserva_salas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setNome("Caio");
        usuario.setEmail("caio@email.com");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.criar(usuario);

        assertEquals("Caio", resultado.getNome());
        assertEquals("caio@email.com", resultado.getEmail());
        assertTrue(resultado.getAtivo());
    }

    @Test
    void deveLancarErroQuandoNomeDoUsuarioForVazio() {
        Usuario usuario = new Usuario();
        usuario.setNome("");
        usuario.setEmail("caio@email.com");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.criar(usuario)
        );

        assertEquals("O nome do usuário é obrigatório.", ex.getMessage());
    }

    @Test
    void deveLancarErroQuandoEmailDoUsuarioForVazio() {
        Usuario usuario = new Usuario();
        usuario.setNome("Caio");
        usuario.setEmail("");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.criar(usuario)
        );

        assertEquals("O e-mail do usuário é obrigatório.", ex.getMessage());
    }

    @Test
    void deveBuscarUsuarioPorId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Caio");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1L);

        assertEquals("Caio", resultado.getNome());
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> usuarioService.buscarPorId(1L)
        );

        assertEquals("Usuário não encontrado.", ex.getMessage());
    }
}