package br.com.mercadoprodutor.core.exception;

public class ErroInternoException extends RuntimeException {
    public ErroInternoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
