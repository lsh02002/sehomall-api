package com.example.sehomallapi.web.dto.item;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ItemResponse {
    private Long id;
    private int count;
    private int price;
    private String size;
    private String careGuide;
    private String name;
    private String description;
    private String category;
    private int deliveryFee;
    private String userNickname;
    private Long views;
    private Long heartCount;
    private String createAt;
    private List<FileResponse> files;
    private Long reviewCount;
}