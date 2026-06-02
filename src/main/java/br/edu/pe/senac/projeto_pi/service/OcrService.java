package br.edu.pe.senac.projeto_pi.service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OcrService {

    /**
     * Liga/desliga o OCR
     * O padrão dele é false e mude para true quando o Docker estiver em uso.
     */
    @Value("${ocr.enabled:false}")
    private boolean enabled;

    // Caminho dos dados de idioma do Tesseract dentro do container Docker.
    @Value("${ocr.tessdata-path:/usr/share/tesseract-ocr/4.00/tessdata}")
    private String tessdataPath;


    /**
     * Processa o comprovante e retorna texto/horas/data detectados.
     * NUNCA lança exceção — em caso de falha retorna OcrResultado.vazio().
     */
    public OcrResultado processar(MultipartFile comprovante) {
        if (!enabled) {
            log.debug("OCR desativado (ocr.enabled=false)");
            return OcrResultado.vazio();
        }

        Path tempFile = null;
        try {
            // Salva em arquivo temporário)
            tempFile = criarArquivoTemporario(comprovante);
            String textoCompleto = extrairTexto(tempFile.toFile(), comprovante.getContentType());

            log.debug("OCR extraiu {} caracteres de '{}'",
                textoCompleto.length(), comprovante.getOriginalFilename());

            return OcrResultado.builder()
                    .textoCompleto(textoCompleto)
                    .horasDetectadas(detectarHoras(textoCompleto))
                    .dataDetectada(detectarData(textoCompleto))
                    .build();

        } catch (Exception e) {
            log.warn("OCR falhou para '{}': {}", comprovante.getOriginalFilename(), e.getMessage());
            return OcrResultado.vazio();
        } finally {
            // Limpa o arquivo temporário em qualquer caso
            if (tempFile != null) {
                try { Files.deleteIfExists(tempFile); } catch (IOException ignored) {}
            }
        }
    }

    // Extração de texto

    private String extrairTexto(File file, String contentType) throws TesseractException, IOException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        // Português como idioma principal, inglês como fallback
        tesseract.setLanguage("por+eng");
        tesseract.setOcrEngineMode(1);
        tesseract.setPageSegMode(3);

        if (contentType != null && contentType.equalsIgnoreCase("application/pdf")) {
            // PDFs: converte primeira página para imagem antes do OCR
            return extrairTextoDePdf(file, tesseract);
        }

        // Imagens (JPG, PNG): processa direto
        return tesseract.doOCR(file);
    }

    /**
     * Para PDFs - BufferedImage usando ImageIO
     */
    private String extrairTextoDePdf(File pdfFile, Tesseract tesseract) throws TesseractException {
        // Tess4J consegue processar PDFs diretamente quando o Tesseract está instalado
        return tesseract.doOCR(pdfFile);
    }

    private Path criarArquivoTemporario(MultipartFile comprovante) throws IOException {
        String ext = obterExtensao(comprovante);
        Path temp = Files.createTempFile("ocr_", ext);
        try (InputStream in = comprovante.getInputStream()) {
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
        }
        return temp;
    }

    private String obterExtensao(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            return "." + original.substring(original.lastIndexOf('.') + 1);
        }
        String ct = file.getContentType();
        if (ct == null) return ".tmp";
        return switch (ct.toLowerCase()) {
            case "application/pdf" -> ".pdf";
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png"  -> ".png";
            default -> ".tmp";
        };
    }

    // Detectores de padrões

    private Integer detectarHoras(String texto) {
        if (texto == null || texto.isBlank()) return null;

        String[] padroes = {
            "(?i)carga\\s*hor[aá]ria[^\\d]*(\\d+)",       
            "(?i)(\\d+)\\s*(?:horas?|h\\b)",                
            "(?i)dura[çc][aã]o[^\\d]*(\\d+)\\s*h",       
            "(?i)total[^\\d]*(\\d+)\\s*(?:horas?|h\\b)",  
            "(?i)carga[^\\d]*(\\d+)\\s*h",                  
        };

        for (String padrao : padroes) {
            Matcher m = Pattern.compile(padrao).matcher(texto);
            if (m.find()) {
                try {
                    int h = Integer.parseInt(m.group(1));
                    if (h > 0 && h <= 1000) return h;
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }

    // Formatos: "15/03/2025", "15-03-2025", "15 de março de 2025"
    private String detectarData(String texto) {
        if (texto == null || texto.isBlank()) return null;

        String[] padroes = {
            "(\\d{2}/\\d{2}/\\d{4})",
            "(\\d{2}-\\d{2}-\\d{4})",
            "(?i)(\\d{1,2}\\s+de\\s+\\w+\\s+de\\s+\\d{4})",
        };

        for (String padrao : padroes) {
            Matcher m = Pattern.compile(padrao).matcher(texto);
            if (m.find()) return m.group(1);
        }
        return null;
    }

    //  DTO de resultado

    public record OcrResultado(
        String textoCompleto,
        Integer horasDetectadas,
        String dataDetectada,
        boolean sucesso
    ) {
        public static OcrResultado vazio() {
            return new OcrResultado(null, null, null, false);
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String textoCompleto;
            private Integer horasDetectadas;
            private String dataDetectada;

            public Builder textoCompleto(String v)    { textoCompleto   = v; return this; }
            public Builder horasDetectadas(Integer v) { horasDetectadas = v; return this; }
            public Builder dataDetectada(String v)    { dataDetectada   = v; return this; }

            public OcrResultado build() {
                return new OcrResultado(textoCompleto, horasDetectadas, dataDetectada, true);
            }
        }
    }
}
