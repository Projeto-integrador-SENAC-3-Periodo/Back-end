package br.edu.pe.senac.projeto_pi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import br.edu.pe.senac.projeto_pi.entity.LogSistema;
import br.edu.pe.senac.projeto_pi.repositories.LogSistemaRepository;

@RestController
@RequestMapping("/logs")
public class LogSistemaController {

    @Autowired
    private LogSistemaRepository logRepository;

    @GetMapping
    public List<LogSistema> listarLogs() {
        return logRepository.findAll();
    }
}