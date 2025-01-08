package com.example.sehomallapi.service.cart;

import com.example.sehomallapi.repository.cart.Cart;
import com.example.sehomallapi.repository.cart.CartItem;
import com.example.sehomallapi.repository.cart.CartItemRepository;
import com.example.sehomallapi.repository.cart.CartRepository;
import com.example.sehomallapi.repository.item.Item;
import com.example.sehomallapi.repository.item.ItemRepository;
import com.example.sehomallapi.repository.users.User;
import com.example.sehomallapi.repository.users.UserRepository;
import com.example.sehomallapi.service.exceptions.NotAcceptableException;
import com.example.sehomallapi.service.exceptions.NotFoundException;
import com.example.sehomallapi.web.dto.cart.CartAllResponse;
import com.example.sehomallapi.web.dto.cart.CartAllSearchResponse;
import com.example.sehomallapi.web.dto.cart.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

//    @Cacheable(key="#userId", value = "cart")
    public CartItemRequest addCartItem(Long userId , CartItemRequest cartItemRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("error"));
        Cart cart = cartRepository.findByUserId(userId);
        Long itemId = cartItemRequest.getItemId();
        System.out.println(itemId);
        Integer itemPrice = itemRepository.findById(itemId).orElseThrow().getPrice();
        System.out.println(itemPrice);
        Integer userItemCount = cartItemRequest.getCount();
        Boolean itemChecked = cartItemRequest.getChecked();

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다.", itemId));
        Integer itemCount = item.getCount();

        CartItem cartItem = cartItemRepository.findByCartAndItem(cart, item);

        if (cart == null){
            cart = Cart.builder().user(user).build();
            cartRepository.save(cart);
        }

        if(userItemCount > itemCount){
            throw new NotAcceptableException("물건 최대 개수는 " + itemCount + " 입니다.",itemCount.toString());
        }
        if(cartItem == null) {
            CartItem isNullCartItem = CartItem.builder().item(item).cart(cart).count(userItemCount).build();
            cartItemRepository.save(isNullCartItem);
        }else{
            cartItem.setCount(userItemCount);
            cartItem.setChecked(itemChecked);
            cartItemRepository.save(cartItem);
        }
        return cartItemRequest;
    }

//    @Cacheable(key = "#userId", value = "cart")
    public CartAllResponse findCartItems(Long userId){
        Cart cart = cartRepository.findByUserId(userId);
        Long cartId = cart.getId();
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
        System.out.println(cartItems);

        List<CartAllSearchResponse> searchResponse = cartItems.stream()
                .map(cartItem -> CartAllSearchResponse.builder()
                        .itemId(cartItem.getItem().getId())
                        .count(cartItem.getCount())
                        .itemName(cartItem.getItem().getName())
                        .price(cartItem.getItem().getPrice())
                        // 이미지 url로 바꿔야함
                        .fileUrl(cartItem.getItem().getFiles().get(0).getFileUrl())
                        .checked(cartItem.getChecked())
                        .build())
                .collect(Collectors.toList());

        return CartAllResponse.builder()
                .cartAllSearchResponses(searchResponse)
                .build();
    }

    @CachePut(key = "#userId", value = "cart")
    public CartItemRequest updateCartItem(Long userId, CartItemRequest cartItemRequest){
        Cart cart = cartRepository.findByUserId(userId);
        Long itemId = cartItemRequest.getItemId();
        System.out.println(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다.", itemId));
        CartItem cartItem = cartItemRepository.findByCartAndItem(cart, item);

        cartItem.setCount(cartItemRequest.getCount());
        cartItem.setChecked(cartItemRequest.getChecked());
        cartItemRepository.save(cartItem);

        return cartItemRequest;
    }

//    @Cacheable(key = "#userId", value = "cart")
    public Integer getItemCount(Long userId){
        Cart cart = cartRepository.findByUserId(userId);
        return cartItemRepository.countByCart(cart);
    }

    @Transactional
//    @CacheEvict(key = "#userId", value = "cart")
    public void deleteCartItem(Long userId, Long itemId){
        Cart cart = cartRepository.findByUserId(userId);
        cartItemRepository.deleteByCartAndItemId(cart, itemId);
    }
}
