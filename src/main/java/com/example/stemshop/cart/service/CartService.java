package com.example.stemshop.cart.service;

import com.example.stemshop.cart.domain.Cart;
import com.example.stemshop.cart.domain.CartItem;
import com.example.stemshop.cart.repo.CartItemRepository;
import com.example.stemshop.cart.repo.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;

    public CartService(CartRepository cartRepo, CartItemRepository cartItemRepo) {
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
    }

    // ==== уже был метод ====
    public Cart resolveMyCart(Optional<Long> userIdOpt, Optional<UUID> guestCartIdOpt) {
        if (userIdOpt.isPresent()) {
            Long userId = userIdOpt.get();
            return cartRepo.findByUserIdAndCheckedOutFalse(userId)
                    .orElseGet(() -> cartRepo.save(new Cart(null, userId, null, null, false, new HashSet<>())));
        }
        if (guestCartIdOpt.isPresent()) {
            UUID id = guestCartIdOpt.get();
            return cartRepo.findByIdAndCheckedOutFalse(id)
                    .orElseGet(() -> cartRepo.save(new Cart(null, null, null, null, false, new HashSet<>())));
        }
        return cartRepo.save(new Cart(null, null, null, null, false, new HashSet<>()));
    }

    // ==== добавить товар (или увеличить, если уже есть) ====
    public Cart addItem(Cart cart, Long productId, Integer qty) {
        int addQty = (qty == null || qty < 1) ? 1 : qty;
        CartItem item = cartItemRepo.findByCart_IdAndProductId(cart.getId(), productId)
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cart);
                    ci.setProductId(productId);
                    ci.setQty(0);
                    return ci;
                });
        item.setQty(item.getQty() + addQty);
        cartItemRepo.save(item);
        return refresh(cart);
    }

    // ==== установить точное количество (0 = удалить) ====
    public Cart setQty(Cart cart, Long productId, int qty) {
        CartItem item = cartItemRepo.findByCart_IdAndProductId(cart.getId(), productId).orElse(null);
        if (qty <= 0) {
            if (item != null) cartItemRepo.delete(item);
            return refresh(cart);
        }
        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProductId(productId);
        }
        item.setQty(qty);
        cartItemRepo.save(item);
        return refresh(cart);
    }

    // ==== уменьшить на 1 (если стало 0 — удалить) ====
    public Cart decrement(Cart cart, Long productId) {
        CartItem item = cartItemRepo.findByCart_IdAndProductId(cart.getId(), productId).orElse(null);
        if (item == null) return cart; // нечего уменьшать
        int next = item.getQty() - 1;
        if (next <= 0) cartItemRepo.delete(item);
        else { item.setQty(next); cartItemRepo.save(item); }
        return refresh(cart);
    }

    // ==== удалить позицию ====
    public Cart removeItem(Cart cart, Long productId) {
        cartItemRepo.deleteByCart_IdAndProductId(cart.getId(), productId);
        return refresh(cart);
    }

    // ==== (опционально) очистить корзину ====
    public Cart clear(Cart cart) {
        cart.getItems().clear();
        // удобнее через репозиторий, если items LAZY:
        // cartItemRepo.deleteAll(cartItemRepo.findByCart_Id(cart.getId()));
        return refresh(cart);
    }

    // перезагрузить корзину с актуальными items (если нужно вернуть клиенту обновлённые данные)
    private Cart refresh(Cart cart) {
        return cartRepo.findById(cart.getId()).orElse(cart);
    }
}
