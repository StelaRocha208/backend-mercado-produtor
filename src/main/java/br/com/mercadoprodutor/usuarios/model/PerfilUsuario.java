package br.com.mercadoprodutor.usuarios.model;

public enum PerfilUsuario {
    ADMINISTRADOR("admin"),
    OPERADOR("operd"),
    PRODUTOR("prodt"),
    COMPRADOR("compr");

    private String sigla;

    PerfilUsuario(String sigla){
        this.sigla = sigla;
    }

    public String getSigla(){
        return sigla;
    }
}