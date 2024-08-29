package com.example.superproject1.web.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이미지 파일 요청")
public class FileRequest {
    @Schema(description = "이미지 파일 이름")
    private String fileName;

    @Schema(description = "이미지 파일 사이즈(바이트)")
    private int fileSize;

    @Schema(description = "이미지 파일 확장명")
    private String fileExtension;
}