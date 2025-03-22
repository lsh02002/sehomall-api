package com.example.sehomallapi.web.dto.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequest {
    private int count;
    private int price;
    private String size;
    private String careGuide;
    private String name;
    private String description;
    private String category;
    private int deliveryFee;
}