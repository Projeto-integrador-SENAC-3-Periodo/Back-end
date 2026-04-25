package br.edu.pe.senac.projeto_pi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class FileStorageService {

    private static final List<String> CONTENT_TYPES_ACEITOS = List.of(
        "application/pdf",
        "image/jpeg",
        "image/jpg",
        "image/png"
    );

    private static final long TAMANHO_MAXIMO_BYTES = 10 * 1024 * 1024L;

    private final Cloudinary cloudinary;

    public FileStorageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key",    apiKey,
            "api_secret", apiSecret,
            "secure",     true
        ));
    }

    /**
     * Faz upload do comprovante para o Cloudinary e retorna a URL pública.
     * PDFs são enviados como resource_type "raw"; imagens como "image".
     */
    public String storeFile(MultipartFile file, Long atividadeId) {
        if (file == null || file.isEmpty())
            throw new RuntimeException("Nenhum arquivo foi enviado.");

        if (file.getSize() > TAMANHO_MAXIMO_BYTES)
            throw new RuntimeException("Arquivo muito grande. Máximo permitido: 10 MB.");

        String contentType = file.getContentType();
        if (contentType == null || !CONTENT_TYPES_ACEITOS.contains(contentType.toLowerCase()))
            throw new RuntimeException("Tipo não permitido: " + contentType + ". Aceitos: PDF, JPG, PNG.");

        try {
            boolean isPdf = contentType.equalsIgnoreCase("application/pdf");

            Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder",          "atividades/" + atividadeId,
                    "resource_type",   isPdf ? "raw" : "image",
                    "use_filename",    false,
                    "unique_filename", true
                )
            );

            String url = (String) result.get("secure_url");

            // Para PDFs, substituir /raw/upload/ por /raw/upload/fl_attachment:false/
            // Isso força o Cloudinary a servir com Content-Type: application/pdf
            if (isPdf) {
                url = url.replace("/raw/upload/", "/raw/upload/fl_attachment:false/");
            }

            return url;

        } catch (IOException ex) {
            throw new RuntimeException("Falha ao enviar arquivo para o Cloudinary.", ex);
        }
    }
}
