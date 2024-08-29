package com.example.superproject1.web.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "물품 응답")
public class ItemResponse {
    @Schema(description = "물품의 고유한 id")
    private Long id;

    @Schema(description = "물품 개수", example = "10")
    private int count;

    @Schema(description = "물품 가격", example = "119000")
    private int price;

    @Schema(description = "물품 크기", example = "가로 45 x 높이 23 x 깊이 8cm")
    private String size;

    @Schema(description = "케어 가이드", example = "- 일상적인 관리: 가방의 겉면을 부드럽게 천을 사용해 닦아주세요. 거친 소재는 표면을 손상시킬 수 있습니다. 먼지가 쌓이지 않도록 정기적으로 건조한 천으로 가방을 닦아주세요.")
    private String careGuide;

    @Schema(description = "물품명", example = "Split Half Moon Messenger Bag_Dark brown")
    private String name;

    @Schema(description = "물품 설명", example = "핸즈프리로 가볍고 편안하게 착용할 수 있는 스플릿 하프문 메신저백 입니다. 크로스 바디 스타일에 완벽한 핏이 되도록 일반 가죽보다 더 부드럽고 유연한 신세틱 레더를 사용하였고, 조절 가능한 스트랩과 지퍼 디테일로 이루어져 있습니다.")
    private String description;

    @Schema(description = "물품 카테고리", example = "BAGS")
    private String category;

    @Schema(description = "물품 배송비", example = "3500")
    private int deliveryFee;

    @Schema(description = "물품 이미지 파일")
    private List<FileResponse> files;
}