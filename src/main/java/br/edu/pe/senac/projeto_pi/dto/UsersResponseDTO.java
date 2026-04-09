package br.edu.pe.senac.projeto_pi.dto;

public class UsersResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String perfil;

    public UsersResponseDTO(Long id, String nome, String email, String perfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
    }

}