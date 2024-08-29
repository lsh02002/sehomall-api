package com.example.sehomallapi.web.dto.item;

import lombok.Builder;
import lombok.Getter;

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
    private List<FileResponse> files;
}