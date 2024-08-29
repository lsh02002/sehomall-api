package com.example.sehomallapi.service.item;

import com.example.sehomallapi.repository.item.File;
import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.item.ItemRepository;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.service.exceptions.NotAcceptableException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.web.dto.item.FileResponse;
import com.example.sehomallapi.web.dto.item.ItemRequest;
import com.example.sehomallapi.web.dto.item.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final FileService fileService;

    public Page<ItemResponse> getAllItems(Pageable pageable) {
        return itemRepository.findAllByCountGreaterThan(0, pageable)
                .map(this::convertToItemResponse);
    }

    public Page<ItemResponse> getAllItemsByUser(User user, Pageable pageable) {
        return itemRepository.findAllByUser(user, pageable)
                .map(this::convertToItemResponse);
    }

    public ItemResponse getItemById(Long id) {
        return itemRepository.findById(id)
                .map(this::convertToItemResponse)
                .orElseThrow(()->new NotFoundException("해당 아이템을 찾을 수 없습니다.", id));
    }

    public ItemResponse createItem(ItemRequest itemRequest, User user) {
        Item item = convertToItemEntity(itemRequest);

        // 이미지 저장
        updateFileFromRequest(item, itemRequest);

        // 연관관계 형성
        item.setUser(user);

        // item 저장
        itemRepository.save(item);

        return convertToItemResponse(item);
    }

    public ItemResponse updateItem(Long id, ItemRequest itemRequest, User user) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            // user 검증
            if (!user.getId().equals(item.getUser().getId())) {
                throw new NotAcceptableException("아이템 업데이트 실패: 유저가 아닙니다.", item.getUser().getNickname());
            }

            // item 수정
            updateItemFromRequest(item, itemRequest);
            return convertToItemResponse(item);
        } else {
            throw new NotFoundException("아이템 업데이트 실패: 아이템을 찾을 수 없습니다.", itemRequest.getName());
        }
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    private ItemResponse convertToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .count(item.getCount())
                .price(item.getPrice())
                .size(item.getSize())
                .careGuide(item.getCareGuide())
                .name(item.getName())
                .description(item.getDescription())
                .category(item.getCategory())
                .deliveryFee(item.getDeliveryFee())
                .files(item.getFiles().stream().map(this::convertToFileResponse).toList())
                .build();
    }

    private Item convertToItemEntity(ItemRequest itemRequest) {
        return Item.builder()
                .count(itemRequest.getCount())
                .price(itemRequest.getPrice())
                .size(itemRequest.getSize())
                .careGuide(itemRequest.getCareGuide())
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .category(itemRequest.getCategory())
                .deliveryFee(itemRequest.getDeliveryFee())
                .cartItems(new ArrayList<>())
                .paymentItems(new ArrayList<>())
                .files(new ArrayList<>())
                .build();
    }

    private void updateItemFromRequest(Item item, ItemRequest itemRequest) {
        item.setCount(itemRequest.getCount());
        item.setPrice(itemRequest.getPrice());
        item.setSize(itemRequest.getSize());
        item.setCareGuide(itemRequest.getCareGuide());
        item.setName(itemRequest.getName());
        item.setDescription(itemRequest.getDescription());
        item.setCategory(itemRequest.getCategory());
        item.setDeliveryFee(itemRequest.getDeliveryFee());

        // 기존 이미지 삭제 후 새 이미지 업로드
        fileService.deleteAllFiles(item);
        itemRepository.save(item);
        updateFileFromRequest(item, itemRequest);

        // item 저장
        itemRepository.save(item);
    }

    private FileResponse convertToFileResponse(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileSize(file.getFileSize())
                .fileExtension(file.getFileExtension())
                .fileUrl(file.getFileUrl())
                .build();
    }

    // 사진 업로드 및 연관관계
    private void updateFileFromRequest(Item item, ItemRequest itemRequest) {
        for (MultipartFile file : itemRequest.getFile()) {
            item.getFiles().add(fileService.createFile(file, item));
        }
    }
}