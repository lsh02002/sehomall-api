package com.example.sehomallapi.web.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileRequest {
    private String fileName;
    private int fileSize;
    private String fileExtension;
}