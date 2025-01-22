package com.example.cleonoraadmin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UploadFile {

    @Value("${upload.folder.path}")
    private String projectPath;



    public String uploadFile(MultipartFile file, String OldPath) {
        createFolder(projectPath);
        deleteFile(OldPath);
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            file.transferTo(new File(Paths.get(projectPath).toAbsolutePath() + "/" + fileName));
        } catch (IOException e) {
            log.error("Ошибка при загрузке файла: {}", e.getMessage(), e);
        }
        return "" + Paths.get(projectPath).subpath(Paths.get(projectPath).getNameCount() - 1,
                Paths.get(projectPath).getNameCount()) + "/" + fileName;
    }

    public void deleteFile(String deletingFilePath) {
        if (deletingFilePath != null) {
            String nameFile = Arrays.stream(deletingFilePath.split("/"))
                    .skip(2)
                    .collect(Collectors.joining("/"));

            File oldFile = Paths.get(projectPath + "/" + nameFile).toAbsolutePath().toFile();
            oldFile.delete();
        }
    }


    public void createFolder(String path) {
        Path currentPath = Paths.get(path).toAbsolutePath();

        File folder = new File(currentPath.toString());

        if (!folder.exists()) {
            boolean created = folder.mkdir();
            if (created) {
                System.out.println("Папка успешно создана: " + folder.getAbsolutePath());
            } else {
                System.out.println("Не удалось создать папку: " + folder.getAbsolutePath());
            }
        } else {
            System.out.println("Папка уже существует: " + folder.getAbsolutePath());
        }
    }

    public boolean isAllowedImageTypeAndSize(MultipartFile file) {
        if (file.getContentType() == null) {
            return true;
        }
        if (file.isEmpty()) {
            return true;
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return true;
        }

        return StringUtils.startsWithIgnoreCase(file.getContentType(), "image/");
    }
}
