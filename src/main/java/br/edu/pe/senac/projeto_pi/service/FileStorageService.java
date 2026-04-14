package br.edu.pe.senac.projeto_pi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    /** Tipos MIME aceitos para comprovantes/certificados. */
    private static final List<String> CONTENT_TYPES_ACEITOS = List.of(
        "application/pdf",
        "image/jpeg",
        "image/jpg",
        "image/png"
    );

    /** Extensões aceitas (validação extra além do content-type). */
    private static final List<String> EXTENSOES_ACEITAS = List.of(
        ".pdf", ".jpg", ".jpeg", ".png"
    );

    /** Tamanho máximo por arquivo: 10 MB. */
    private static final long TAMANHO_MAXIMO_BYTES = 10 * 1024 * 1024L;

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório de upload.", ex);
        }
    }

    /**
     * Armazena o comprovante/certificado do aluno.
     *
     * Aceita: PDF, JPG, JPEG, PNG — inclusive fotos tiradas pelo celular.
     * Tamanho máximo: 10 MB.
     * O nome do arquivo é substituído por um UUID para evitar colisões e
     * caracteres especiais vindos do dispositivo do aluno.
     *
     * @param file        arquivo enviado pelo aluno
     * @param atividadeId ID da atividade (usado como subdiretório)
     * @return URL pública do arquivo armazenado
     */
    public String storeFile(MultipartFile file, Long atividadeId) {
        // 1. Valida se o arquivo foi enviado
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Nenhum arquivo foi enviado.");
        }

        // 2. Valida tamanho (10 MB)
        if (file.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new RuntimeException(
                "Arquivo muito grande. O tamanho máximo permitido é 10 MB. " +
                "Tamanho recebido: " + (file.getSize() / (1024 * 1024)) + " MB.");
        }

        // 3. Valida content-type
        String contentType = file.getContentType();
        if (contentType == null || !CONTENT_TYPES_ACEITOS.contains(contentType.toLowerCase())) {
            throw new RuntimeException(
                "Tipo de arquivo não permitido: '" + contentType + "'. " +
                "Aceitos: PDF, JPG, JPEG, PNG.");
        }

        // 4. Valida extensão do nome original
        String nomeOriginal = StringUtils.cleanPath(
            Objects.requireNonNull(file.getOriginalFilename(), "Nome do arquivo não informado."));

        if (nomeOriginal.contains("..")) {
            throw new RuntimeException("Nome de arquivo inválido: " + nomeOriginal);
        }

        String extensao = extrairExtensao(nomeOriginal);
        if (!EXTENSOES_ACEITAS.contains(extensao.toLowerCase())) {
            throw new RuntimeException(
                "Extensão não permitida: '" + extensao + "'. Aceitas: .pdf, .jpg, .jpeg, .png.");
        }

        // 5. Gera nome seguro com UUID para evitar colisões e caracteres especiais
        // (importante para fotos de celular com nomes como "IMG_20240101_120000.jpg")
        String nomeSeguro = UUID.randomUUID() + extensao.toLowerCase();

        try {
            // 6. Cria subdiretório por atividade
            Path atividadeDir = this.fileStorageLocation.resolve(String.valueOf(atividadeId));
            Files.createDirectories(atividadeDir);

            // 7. Salva o arquivo
            Path destino = atividadeDir.resolve(nomeSeguro);
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // 8. Retorna a URL pública
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(atividadeId + "/")
                .path(nomeSeguro)
                .toUriString();

        } catch (IOException ex) {
            throw new RuntimeException(
                "Falha ao salvar o arquivo. Tente novamente.", ex);
        }
    }

    public Resource loadFileAsResource(String fileName, Long atividadeId) {
        try {
            Path filePath = this.fileStorageLocation
                .resolve(String.valueOf(atividadeId))
                .resolve(fileName)
                .normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) return resource;
            throw new RuntimeException("Arquivo não encontrado: " + fileName);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Arquivo não encontrado: " + fileName, ex);
        }
    }

    // ─── Helper ───────────────────────────────────────────────────

    private String extrairExtensao(String nomeArquivo) {
        int dot = nomeArquivo.lastIndexOf('.');
        return (dot >= 0) ? nomeArquivo.substring(dot) : "";
    }
}
