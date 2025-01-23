package com.example.sehomallapi.web.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class ReviewedItemResponse {
    private Long id;
    private String name;

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // 아이디가 같은 경우 중복 처리
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReviewedItemResponse other = (ReviewedItemResponse) obj;
        return Objects.equals(id, other.getId());
    }
}
