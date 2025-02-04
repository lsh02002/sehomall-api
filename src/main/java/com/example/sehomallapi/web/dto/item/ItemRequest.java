package com.example.sehomallapi.web.dto.item;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ItemRequest {
    private int quantity;
    private int price;
    private String size;
    private String careGuide;
    private String name;
    private String description;
    private String category;
    private int deliveryFee;
}