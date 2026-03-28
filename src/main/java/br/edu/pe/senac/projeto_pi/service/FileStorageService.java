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
import java.util.Objects;
@Service
public class FileStorageService{

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório onde os arquivos serão armazenados.", ex);
        }
    }

    public String storeFile(MultipartFile file, Long atividadeId) {
        // Normaliza o nome do arquivo
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Verifica se o nome do arquivo contém caracteres inválidos
            if (fileName.contains("..")) {
                throw new RuntimeException("Nome de arquivo inválido: " + fileName);
            }

            // Cria um subdiretório para cada atividade
            Path atividadeStorageLocation = this.fileStorageLocation.resolve(String.valueOf(atividadeId));
            Files.createDirectories(atividadeStorageLocation);

            // Copia o arquivo para o local de destino (substituindo se já existir)
            Path targetLocation = atividadeStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(atividadeId + "/")
                    .path(fileName)
                    .toUriString();
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível armazenar o arquivo " + fileName + ". Por favor, tente novamente!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName, Long atividadeId) {
        try {
            Path filePath = this.fileStorageLocation.resolve(String.valueOf(atividadeId)).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Arquivo não encontrado " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Arquivo não encontrado " + fileName, ex);
        }
    }
}