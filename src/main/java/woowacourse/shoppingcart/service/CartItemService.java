package woowacourse.shoppingcart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.dao.ProductDao;
import woowacourse.shoppingcart.domain.Quantity;
import woowacourse.shoppingcart.domain.cart.CartItem;
import woowacourse.shoppingcart.domain.cart.CartItemRepository;
import woowacourse.shoppingcart.domain.cart.CartItems;
import woowacourse.shoppingcart.domain.customer.Customer;
import woowacourse.shoppingcart.domain.product.Product;
import woowacourse.shoppingcart.dto.CartItemDeletionRequest;
import woowacourse.shoppingcart.dto.CartItemRequest;
import woowacourse.shoppingcart.dto.CartItemResponse;
import woowacourse.shoppingcart.dto.UpdateCartItemRequest;

@Service
@Transactional(rollbackFor = Exception.class)
public class CartItemService {

    private final CustomerDao customerDao;
    private final ProductDao productDao;
    private final CartItemRepository cartItemRepository;

    public CartItemService(CustomerDao customerDao, ProductDao productDao,
        CartItemRepository cartItemRepository) {
        this.customerDao = customerDao;
        this.productDao = productDao;
        this.cartItemRepository = cartItemRepository;
    }

    public CartItemResponse addCart(final String email, final CartItemRequest cartItemRequest) {
        long customerId = findCustomerIdByEmail(email);
        Product productById = productDao.findProductById(cartItemRequest.getProductId());
        CartItems cartItems = cartItemRepository.findByCustomer(customerId);
        CartItem newCartItem = new CartItem(productById, new Quantity(cartItemRequest.getQuantity()));
        cartItems.add(newCartItem);
        long addedCartItemID = cartItemRepository.addCartItem(customerId, newCartItem);
        return CartItemResponse.from(cartItemRepository.findById(addedCartItemID));
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(String email) {
        long customerId = findCustomerIdByEmail(email);
        CartItems cartItems = cartItemRepository.findByCustomer(customerId);
        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        cartItems.forEach(cartItem -> cartItemResponses.add(CartItemResponse.from(cartItem)));
        return cartItemResponses;
    }

    @Transactional(readOnly = true)
    public CartItemResponse getCartItem(String email, long id) {
        long customerId = findCustomerIdByEmail(email);
        CartItems cartItems = cartItemRepository.findByCustomer(customerId);
        CartItem cartItem = cartItemRepository.findById(id);

        cartItems.checkContain(cartItem);

        return CartItemResponse.from(cartItem);
    }

    public void update(String email, UpdateCartItemRequest cartItemRequest) {
        long customerId = findCustomerIdByEmail(email);
        CartItems cartItems = cartItemRepository.findByCustomer(customerId);
        CartItem cartItem = cartItemRepository.findById(cartItemRequest.getCartItemId());

        cartItems.checkContain(cartItem);

        CartItem updateCartItem = new CartItem(cartItem.getId(), cartItem.getProduct(),
            new Quantity(cartItemRequest.getQuantity()));
        cartItemRepository.update(updateCartItem);
    }

    public void delete(String email, CartItemDeletionRequest cartItemDeletionRequest) {
        long customerId = findCustomerIdByEmail(email);
        CartItems cartItems = cartItemRepository.findByCustomer(customerId);

        List<CartItem> deleteCartItems = cartItemDeletionRequest.getCartItemIds().stream()
            .map(cartItemRepository::findById)
            .collect(Collectors.toList());

        for (CartItem deleteCartItem : deleteCartItems) {
            cartItems.checkContain(deleteCartItem);
            cartItemRepository.delete(deleteCartItem);
        }
    }

    private long findCustomerIdByEmail(String email) {
        Customer customer = customerDao.findByEmail(email);
        return customer.getId();
    }
}
