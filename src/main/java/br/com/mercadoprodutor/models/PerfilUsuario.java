package br.com.mercadoprodutor.models;

public enum PerfilUsuario {
    ADMINISTRADOR("admin"),
    OPERADOR("operd"),
    PRODUTOR("prodt"),
    COMPRADOR("compr");

    private String perfil;

    PerfilUsuario(String perfil){
        this.perfil = perfil;
    }

    public String getPerfil(){
        return perfil;
    }

}
