package caio.reserva_salas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError tratarErroDeValidacao(IllegalArgumentException ex) {
        return new ApiError(
                400,
                "Bad Request",
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return new ApiError(
                404,
                "Not Found",
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError tratarErroGenerico(Exception ex) {
        return new ApiError(
                500,
                "Internal Server Error",
                "Erro inesperado na aplicação.",
                null
        );
    }
}