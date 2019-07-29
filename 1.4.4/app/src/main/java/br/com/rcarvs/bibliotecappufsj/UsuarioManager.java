package br.com.rcarvs.bibliotecappufsj;


import java.lang.reflect.Array;

public class UsuarioManager {

    private String login;
    private String senha;
    private String tokenBefore;
    private String tokenAfter;
    private String nome;
    private String livros;


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTokenBefore() {
        return tokenBefore;
    }

    public void setTokenBefore(String tokenBefore) {
        this.tokenBefore = tokenBefore;
    }

    public String getTokenAfter() {
        return tokenAfter;
    }

    public void setTokenAfter(String tokenAfter) {
        this.tokenAfter = tokenAfter;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLivros() {
        return livros;
    }

    public void setLivros(String livros) {
        this.livros = livros;
    }
}
