package com.example.sehomallapi.web.dto.heart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeartRequest {
    private Long userId;
    private Long itemId;
}
