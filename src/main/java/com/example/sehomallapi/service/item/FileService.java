package com.example.sehomallapi.service.item;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.sehomallapi.repository.item.File;
import com.example.sehomallapi.repository.item.FileRepository;
import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.review.Review;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class FileService {
    FileRepository fileRepository;
    private final AmazonS3Client amazonS3Client;

    // 파일 디렉터리
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드 및 DB 생성
    public File createFile(MultipartFile file, Item item) {
        // 이미지 파일이 없을 경을 경우
        if (file.isEmpty()) throw new NotFoundException("File is empty", file.getName());

        try {
            String fileName = file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            return File.builder()
                    .fileName(fileName)
                    .fileSize(file.getSize())
                    .fileExtension(fileName != null ? getFileExtension(fileName) : null)
                    .fileUrl(fileUrl)
                    .item(item)
                    .review(null)
                    .build();
        } catch (IOException e) {
            throw new NotFoundException("파일이 존재하지 않습니다.", file.getName());
        }
    }

    public File createReviewFile(MultipartFile file, Review review) {
        // 이미지 파일이 없을 경을 경우
        if (file.isEmpty()) throw new NotFoundException("File is empty", file.getName());

        try {
            String fileName = file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            return File.builder()
                    .fileName(fileName)
                    .fileSize(file.getSize())
                    .fileExtension(fileName != null ? getFileExtension(fileName) : null)
                    .fileUrl(fileUrl)
                    .item(null)
                    .review(review)
                    .build();
        } catch (IOException e) {
            throw new NotFoundException("파일이 존재하지 않습니다.", file.getName());
        }
    }

    // 파일 확장자
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // 파일 삭제
    public void deleteFile(Long id) {
        File file = fileRepository.findById(id).orElseThrow(()->new NotFoundException("삭제할 파일을 찾을 수 없습니다.", id));
        fileRepository.delete(file);
    }
}
