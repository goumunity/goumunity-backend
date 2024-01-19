package com.ssafy.goumunity.util;

import com.ssafy.goumunity.image.Image;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultiImageHandler {
    public List<Image> parseFileInfo(Long writerId, List<MultipartFile> multipartFiles)
            throws IOException {
        List<Image> fileList = new ArrayList<>();

        if (multipartFiles == null || multipartFiles.isEmpty()) return fileList;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = simpleDateFormat.format(Date.from(Instant.now()));

        String absolutePath = new File("").getAbsolutePath() + "\\";

        // 저장 path 설정 --> 추후 게시판 기능 구현시 활용
        String path = "src/main/resources/images/board/" + currentDate;
        String simplePath = "/images/board/" + currentDate;
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                String contentType = multipartFile.getContentType();
                String originalFileExtension;

                if (ObjectUtils.isEmpty(contentType)) {
                    break;
                } else {
                    if (contentType.contains("image/jpeg")) {
                        originalFileExtension = ".jpg";
                    } else if (contentType.contains("image/png")) {
                        originalFileExtension = ".png";
                    } else if (contentType.contains("image/gif")) {
                        originalFileExtension = ".gif";
                    } else {
                        break;
                    }
                }

                String newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
                Image img =
                        Image.builder()
                                .originalFileName(multipartFile.getOriginalFilename())
                                .storedFilePath(simplePath + "/" + newFileName)
                                .fileSize(multipartFile.getSize())
                                .build();
                fileList.add(img);

                file = new File(absolutePath + path + "/" + newFileName);
                multipartFile.transferTo(file);
            }
        }

        return fileList;
    }
}