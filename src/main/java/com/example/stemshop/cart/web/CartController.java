package com.example.stemshop.cart.web;

import com.example.stemshop.cart.domain.Cart;
import com.example.stemshop.util.SecurityUtil;
import com.example.stemshop.cart.service.CartService;
import com.example.stemshop.cart.dto.AddItemRequest;
import com.example.stemshop.cart.dto.RemoveItemRequest;
import com.example.stemshop.cart.dto.UpdateQtyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static com.example.stemshop.util.CookieUtil.*;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name="Корзина", description = "Корзина пользователя")
public class CartController {

    private final CartService carts;

    public CartController(CartService carts) {
        this.carts = carts;
    }

    // уже была ручка получения /my
    @GetMapping("/my")
    @Operation(summary = "Просмотр корзины пользователя")
    public ResponseEntity<Cart> myCart(HttpServletRequest request) {
        var userIdOpt = SecurityUtil.currentUserId();
        var guestCartIdOpt = getCartIdCookie(request).map(UUID::fromString);
        Cart cart = carts.resolveMyCart(userIdOpt, guestCartIdOpt);

        if (userIdOpt.isEmpty()) {
            boolean needSetCookie = guestCartIdOpt.isEmpty()
                    || !guestCartIdOpt.get().equals(cart.getId());
            if (needSetCookie) {
                ResponseCookie cookie = buildCartCookie(cart.getId().toString(), request.isSecure());
                return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body(cart);
            }
        }
        return ResponseEntity.ok(cart);
    }

    // === добавить товар ===
    @PostMapping("/my/items")
    public ResponseEntity<Cart> addItem(@RequestBody @Valid AddItemRequest req, HttpServletRequest request) {
        Cart cart = resolveForRequest(request);
        Cart updated = carts.addItem(cart, req.productId(), req.qty());
        return maybeSetCookieForGuest(request, updated);
    }

    // === установить точное количество ===
    @PutMapping("/my/items")
    public ResponseEntity<Cart> setQty(@RequestBody @Valid UpdateQtyRequest req, HttpServletRequest request) {
        Cart cart = resolveForRequest(request);
        Cart updated = carts.setQty(cart, req.productId(), req.qty());
        return maybeSetCookieForGuest(request, updated);
    }

    // === уменьшить на 1 ===
    @PatchMapping("/my/items/{productId}/decrement")
    public ResponseEntity<Cart> decrement(@PathVariable Long productId, HttpServletRequest request) {
        Cart cart = resolveForRequest(request);
        Cart updated = carts.decrement(cart, productId);
        return maybeSetCookieForGuest(request, updated);
    }

    // === удалить позицию ===
    @DeleteMapping("/my/items/{productId}")
    public ResponseEntity<Cart> remove(@PathVariable Long productId, HttpServletRequest request) {
        Cart cart = resolveForRequest(request);
        Cart updated = carts.removeItem(cart, productId);
        return maybeSetCookieForGuest(request, updated);
    }

    // ====== вспомогалки ======

    private Cart resolveForRequest(HttpServletRequest request) {
        var userIdOpt = SecurityUtil.currentUserId();
        var guestCartIdOpt = getCartIdCookie(request).map(UUID::fromString);
        return carts.resolveMyCart(userIdOpt, guestCartIdOpt);
    }

    private ResponseEntity<Cart> maybeSetCookieForGuest(HttpServletRequest request, Cart cart) {
        var userIdOpt = SecurityUtil.currentUserId();
        if (userIdOpt.isEmpty()) {
            var guestCartIdOpt = getCartIdCookie(request).map(UUID::fromString);
            boolean needSetCookie = guestCartIdOpt.isEmpty()
                    || !guestCartIdOpt.get().equals(cart.getId());
            if (needSetCookie) {
                ResponseCookie cookie = buildCartCookie(cart.getId().toString(), request.isSecure());
                return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body(cart);
            }
        }
        return ResponseEntity.ok(cart);
    }
}
