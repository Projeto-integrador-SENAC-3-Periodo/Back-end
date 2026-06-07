package br.edu.pe.senac.projeto_pi.controllers;

import br.edu.pe.senac.projeto_pi.service.OcrService;
import br.edu.pe.senac.projeto_pi.service.OcrService.OcrResultado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint para pré-visualização do OCR sem submeter a atividade.
 * Usado pelo app mobile para preencher campos automaticamente
 * assim que o aluno seleciona o comprovante.
 *
 * POST /ocr/processar  — autenticado, multipart/form-data
 */
@RestController
@RequestMapping("/ocr")
public class OCRController {

    @Autowired
    private OcrService ocrService;

    @PostMapping(value = "/processar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> processar(
            @RequestParam("arquivo") MultipartFile arquivo) {

        OcrResultado resultado = ocrService.processar(arquivo);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("ocrProcessado",   resultado.sucesso());
        resposta.put("horasDetectadas", resultado.horasDetectadas());
        resposta.put("dataDetectada",   resultado.dataDetectada());
        resposta.put("textoCompleto",   resultado.textoCompleto());

        return ResponseEntity.ok(resposta);
    }
}