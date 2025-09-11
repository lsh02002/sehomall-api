package com.example.sehomallapi.service.heart;

import com.example.sehomallapi.config.RestPage;
import com.example.sehomallapi.repository.heart.Heart;
import com.example.sehomallapi.repository.heart.HeartRepository;
import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.item.ItemRepository;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.service.exceptions.ConflictException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.service.item.ItemService;
import com.example.sehomallapi.web.dto.item.ItemResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Transactional
    @CachePut(key = "#itemId", value = "item")
    public ItemResponse insert(Long userId, Long itemId) {

        User user =userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다. : " + userId, null));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("해당 아이템을 찾을 수 없습니다. : " + itemId, null));

        // 이미 좋아요되어있으면 에러 반환
        if (heartRepository.findByUserAndItem(user, item).isPresent()){
            //TODO 409에러로 변경
            throw new ConflictException("이미 좋아요가 되어있습니다. ", null);
        }

        item.setHeartCount(item.getHeartCount() + 1);
        itemRepository.save(item);

        Heart heart = Heart.builder()
                .item(item)
                .user(user)
                .build();

        heartRepository.save(heart);

        return itemService.convertToItemResponse(item);
    }

    @Transactional
//    @CachePut(key = "#itemId", value = "item")
    public void delete(Long userId, Long itemId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다. : " + userId, null));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("해당 아이템을 찾을 수 없습니다. : " + itemId, null));

        Heart heart = heartRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new NotFoundException("좋아요가 되어있지 않았습니다. ", null));

        item.setHeartCount(item.getHeartCount() - 1);
        itemRepository.save(item);

        heartRepository.delete(heart);
    }

    public RestPage<ItemResponse> getMyHeartedItems(Long userId, Pageable pageable) {
        Page<Heart> hearts = heartRepository.findAllByUserId(userId, pageable);
        return new RestPage<>(hearts.map(heart -> itemService._getItem(heart.getItem())));
    }
}
