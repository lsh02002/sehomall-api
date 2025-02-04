package com.example.sehomallapi.service.item;

import com.example.sehomallapi.repository.item.File;
import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.item.ItemRepository;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.service.exceptions.BadRequestException;
import com.example.sehomallapi.service.exceptions.ConflictException;
import com.example.sehomallapi.service.exceptions.NotAcceptableException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.web.dto.item.FileResponse;
import com.example.sehomallapi.web.dto.item.ItemRequest;
import com.example.sehomallapi.web.dto.item.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final FileService fileService;

    @CachePut(key = "'all'", value = "item")
    public Page<ItemResponse> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(this::convertToItemResponse);
    }

    @Transactional
    @CachePut(key = "#user.id", value = "item")
    public Page<ItemResponse> getAllItemsByUser(User user, Pageable pageable) {
        return itemRepository.findAllByUser(user, pageable)
                .map(this::convertToItemResponse);
    }

    @Transactional
    @CachePut(key = "#id", value = "item")
    public ItemResponse getItemById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        item.get().setViews(item.get().getViews()+1);

        return item.map(this::convertToItemResponse)
                .orElseThrow(()->new NotFoundException("해당 아이템을 찾을 수 없습니다.", id));

    }

    @Transactional
    @CachePut(key = "'all'", value = "item")
    public Page<ItemResponse> getAllItemsByCategory(String category, Pageable pageable) {
        return itemRepository.findByCategory(category, pageable)
                .map(this::convertToItemResponse);
    }

    @Transactional
    @CachePut(key = "#user.id", value = "item")
    public ItemResponse createItem(ItemRequest itemRequest, List<MultipartFile> files, User user) {

        if(itemRequest.getName().trim().isEmpty()){
            throw new BadRequestException("상품명이 비어있습니다", null);
        } else if(itemRepository.existsByName(itemRequest.getName().trim())){
            throw new ConflictException("이미 같은 이름의 상품이 있습니다.", itemRequest.getName());
        } else if(files == null || files.isEmpty()) {
            throw new BadRequestException("상품 사진을 입력하세요", null);
        } else if(itemRequest.getPrice() == 0) {
            throw new BadRequestException("상품 가격을 입력하세요", null);
        } else if(itemRequest.getSize().trim().isEmpty()){
            throw new BadRequestException("상품 크기를 입력하세요", null);
        } else if(itemRequest.getQuantity() == 0) {
            throw new BadRequestException("상품 제고량을 입력하세요", null);
        } else if(itemRequest.getDeliveryFee() == 0){
            throw new BadRequestException("배송비를 입력하세요", null);
        }

        Item item = convertToItemEntity(itemRequest);

        // 이미지 저장
        updateFileFromRequest(item, files);

        // 연관관계 형성
        item.setUser(user);

        // item 저장
        itemRepository.save(item);

        return convertToItemResponse(item);
    }

    @Transactional
    @CachePut(key = "#user.id", value = "item")
    public ItemResponse updateItem(Long id, ItemRequest itemRequest, List<MultipartFile> files, User user) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            // user 검증
            if (!user.getId().equals(item.getUser().getId())) {
                throw new NotAcceptableException("아이템 업데이트 실패: 유저가 아닙니다.", item.getUser().getNickname());
            }

            // item 수정
            updateItemFromRequest(item, itemRequest, files);
            return convertToItemResponse(item);
        } else {
            throw new NotFoundException("아이템 업데이트 실패: 아이템을 찾을 수 없습니다.", itemRequest.getName());
        }
    }

    @Transactional
    @CacheEvict(key = "#userId", value = "item")
    public void deleteItem(Long id, Long userId) {
        Item item = itemRepository.findByIdAndUserId(id, userId)
                        .orElseThrow(()-> new NotFoundException("상품을 찾을 수 없습니다.", id));

        fileService.deleteAllFiles(item);
        itemRepository.deleteById(id);
    }

    @CachePut(key = "#itemId", value = "item")
    public Long getItemHeartCount(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()-> new NotFoundException("상품을 찾을 수 없습니다.", itemId));

        return item.getHeartCount();
    }

    public Page<ItemResponse> getItemsByKeyword(String keyword, Pageable pageable) {
        return itemRepository.findByKeyword(keyword, pageable)
                .map(this::convertToItemResponse);
    }

    private ItemResponse convertToItemResponse(Item item) {
        String createAt = item.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        return ItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .size(item.getSize())
                .careGuide(item.getCareGuide())
                .name(item.getName())
                .description(item.getDescription())
                .category(item.getCategory())
                .deliveryFee(item.getDeliveryFee())
                .userNickname(item.getUser().getNickname())
                .views(item.getViews())
                .heartCount(item.getHeartCount())
                .createAt(createAt)
                .files(item.getFiles().stream().map(this::convertToFileResponse).toList())
                .reviewCount((long) item.getReviews().size())
                .build();
    }

    private Item convertToItemEntity(ItemRequest itemRequest) {
        return Item.builder()
                .quantity(itemRequest.getQuantity())
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
                .reviews(new ArrayList<>())
                .build();
    }

    private void updateItemFromRequest(Item item, ItemRequest itemRequest, List<MultipartFile> files) {
        item.setQuantity(itemRequest.getQuantity());
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
        updateFileFromRequest(item, files);

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
    private void updateFileFromRequest(Item item, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            item.getFiles().add(fileService.createFile(file, item));
        }
    }
}