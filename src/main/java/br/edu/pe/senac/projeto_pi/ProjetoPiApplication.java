package br.edu.pe.senac.projeto_pi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjetoPiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoPiApplication.class, args);
		System.out.println("Projeto em execulção");
	}

}
