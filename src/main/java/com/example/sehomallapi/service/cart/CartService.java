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
import com.example.sehomallapi.web.dto.cart.CartAllSearchResponse;
import com.example.sehomallapi.web.dto.cart.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public CartItemRequest addCartItem(Long userId , CartItemRequest cartItemRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("error"));
        Cart cart = cartRepository.findByUserId(userId);
        Long itemId = cartItemRequest.getItemId();
        System.out.println(itemId);
        Integer itemPrice = itemRepository.findById(itemId).orElseThrow().getPrice();
        System.out.println(itemPrice);
        Integer userItemCount = cartItemRequest.getCount();

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("error"));
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
            cartItemRepository.save(cartItem);
        }
        return cartItemRequest;
    }

    public List<CartAllSearchResponse> findCartItems(Long userId){
        Cart cart = cartRepository.findByUserId(userId);
        Long cartId = cart.getId();
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
        System.out.println(cartItems);

        return cartItems.stream()
                .map(cartItem -> CartAllSearchResponse.builder()
                        .itemId(cartItem.getItem().getId())
                        .count(cartItem.getCount())
                        .itemName(cartItem.getItem().getName())
                        .price(cartItem.getItem().getPrice())
                        // 이미지 url로 바꿔야함
                        .fileName(cartItem.getItem().getFiles().get(0).getFileName())
                        .build())
                .collect(Collectors.toList());
    }

    public CartItemRequest updateCartItem(Long userId, CartItemRequest cartItemRequest){
        Cart cart = cartRepository.findByUserId(userId);
        Long cartId = cart.getId();
        Long itemId = cartItemRequest.getItemId();
        System.out.println(itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("error"));
        CartItem cartItem = cartItemRepository.findByCartAndItem(cart, item);
        Long cartItemId = cartItem.getId();
        Integer itemQuantity = cartItemRequest.getCount();

        cartItem.setCount(cartItemRequest.getCount());
        cartItemRepository.save(cartItem);

        return cartItemRequest;
    }
}

