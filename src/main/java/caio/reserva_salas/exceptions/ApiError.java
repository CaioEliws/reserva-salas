package caio.reserva_salas.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiError {

    private LocalDateTime dataHora;
    private int status;
    private String erro;
    private String mensagem;
    private String campo;

    public ApiError(int status, String erro, String mensagem, String campo) {
        this.dataHora = LocalDateTime.now();
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.campo = campo;
    }
}