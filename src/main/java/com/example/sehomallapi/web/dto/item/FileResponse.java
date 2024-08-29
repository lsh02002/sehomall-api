package com.example.superproject1.web.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이미지 파일 응답")
public class FileResponse {
    @Schema(description = "이미지 파일의 고유한 id", example = "1")
    private Long id;

    @Schema(description = "이미지 파일 이름", example = "bag_image1.jpg")
    private String fileName;

    @Schema(description = "이미지 파일 사이즈(바이트)", example = "241309")
    private long fileSize;

    @Schema(description = "이미지 파일 확장명", example = ".jpg")
    private String fileExtension;

    @Schema(description = "이미지 파일 링크, 'http://서버주소:8080/images/bag_image1.jpg' 와 같이 선언해서 이미지 사용 가능", example = "/images/bag_image1.jpg")
    private String fileUrl;
}