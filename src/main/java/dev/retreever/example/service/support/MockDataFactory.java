package dev.retreever.example.service.support;

import dev.retreever.example.dto.envelope.CustomPage;
import dev.retreever.example.dto.request.StoreWrite;
import dev.retreever.example.dto.response.*;
import dev.retreever.example.dto.shared.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MockDataFactory {

    private static final byte[] SAMPLE_IMAGE_BYTES = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Wn0P2kAAAAASUVORK5CYII="
    );

    private final AtomicLong idSequence = new AtomicLong(500L);

    public Long nextId() {
        return idSequence.incrementAndGet();
    }

    public CartResponse cartResponse(long cartId) {
        UUID cartUuid = uuidFromSeed("cart-" + cartId);
        return CartResponse.builder(cartUuid)
                .addItem(uuidFromSeed("cart-item-" + cartId), 2, productSnapshot(cartId))
                .addItem(uuidFromSeed("cart-item-" + (cartId + 1)), 1, productSnapshot(cartId + 1))
                .build();
    }

    public CategorySummary categorySummary(long categoryId, CategoryStatus status) {
        return new CategorySummary(
                uuidFromSeed("category-" + categoryId),
                "Category " + categoryId,
                status,
                (int) ((categoryId % 3) + 1),
                "https://cdn.quickcart.test/categories/" + categoryId + ".png"
        );
    }

    public CategoryDetail categoryDetail(long categoryId, CategoryStatus status, int depth) {
        List<CategoryDetail> children = depth <= 0
                ? List.of()
                : List.of(
                categoryDetail(categoryId * 10 + 1, CategoryStatus.ACTIVE, depth - 1),
                categoryDetail(categoryId * 10 + 2, CategoryStatus.DRAFT, depth - 1)
        );

        return new CategoryDetail(
                uuidFromSeed("category-detail-" + categoryId),
                "Category " + categoryId,
                status,
                depth + 1,
                "https://cdn.quickcart.test/categories/" + categoryId + ".png",
                children
        );
    }

    public StoreDetails storeDetails(long storeId, StoreWrite request) {
        String name = request != null && request.name() != null ? request.name() : "Store " + storeId;
        String location = request != null && request.location() != null ? request.location() : "Bengaluru";
        String contactNumber = request != null && request.contactNumber() != null ? request.contactNumber() : "+91-9876543210";
        String email = request != null && request.email() != null ? request.email() : "store" + storeId + "@quickcart.test";
        String about = request != null && request.about() != null ? request.about() : "Curated demo storefront for Retreever.";

        return new StoreDetails(
                uuidFromSeed("store-" + storeId),
                name,
                location,
                contactNumber,
                email,
                about,
                instantFromSeed(storeId),
                instantFromSeed(storeId + 10)
        );
    }

    public ProductVariantResponse productVariantResponse(long variantId) {
        return new ProductVariantResponse(
                uuidFromSeed("variant-" + variantId),
                "Variant " + variantId + " - Ceramic Mug",
                12.49 + (variantId % 5),
                20 + (int) (variantId % 10),
                "Variant " + variantId + " with polished finish and quick mock inventory.",
                instantFromSeed(variantId),
                instantFromSeed(variantId + 1),
                Map.of(
                        "color", variantId % 2 == 0 ? "Black" : "White",
                        "size", "M"
                ),
                Set.of(
                        "https://cdn.quickcart.test/products/" + variantId + "/1.png",
                        "https://cdn.quickcart.test/products/" + variantId + "/2.png"
                ),
                variantId % 4 != 0,
                variantId % 5 == 0
        );
    }

    public ProductResponse productResponse(long productId) {
        return new ProductResponse(
                uuidFromSeed("product-" + productId),
                "Product " + productId,
                "A mocked product used for Retreever schema and endpoint testing.",
                brands().get((int) (productId % brands().size())),
                "Home > Kitchen > Demo Product " + productId,
                4.4,
                128,
                instantFromSeed(productId),
                instantFromSeed(productId + 2),
                List.of(
                        productVariantResponse(productId * 10 + 1),
                        productVariantResponse(productId * 10 + 2)
                ),
                true,
                false
        );
    }

    public ProductSnapshot productSnapshot(long seed) {
        return new ProductSnapshot(
                uuidFromSeed("product-snapshot-product-" + seed),
                uuidFromSeed("product-snapshot-variant-" + seed),
                "Product " + seed,
                "Variant " + seed,
                19.99 + seed,
                "https://cdn.quickcart.test/snapshots/" + seed + ".png"
        );
    }

    public StoreSnapshot storeSnapshot(long seed) {
        return new StoreSnapshot(
                uuidFromSeed("store-snapshot-" + seed),
                "Store " + seed,
                "Bengaluru",
                "+91-98765000" + (seed % 10),
                "store" + seed + "@quickcart.test",
                uuidFromSeed("store-owner-" + seed)
        );
    }

    public UserSnapshot userSnapshot(long seed, String email) {
        return new UserSnapshot(
                uuidFromSeed("user-" + seed),
                "Demo",
                "User" + seed,
                email
        );
    }

    public OrderResponse orderResponse(long orderId) {
        return OrderResponse.builderInit(
                        uuidFromSeed("order-" + orderId),
                        instantFromSeed(orderId),
                        249.75,
                        OrderStatus.CONFIRMED,
                        PaymentStatus.PAID
                )
                .addItem(
                        uuidFromSeed("order-item-" + orderId),
                        2,
                        productSnapshot(orderId),
                        storeSnapshot(orderId)
                )
                .addItem(
                        uuidFromSeed("order-item-" + (orderId + 1)),
                        1,
                        productSnapshot(orderId + 1),
                        storeSnapshot(orderId + 1)
                )
                .build();
    }

    public OrderItemResponse orderItemResponse(long itemId, String email) {
        return new OrderItemResponse(
                uuidFromSeed("seller-order-item-" + itemId),
                1 + (int) (itemId % 3),
                instantFromSeed(itemId),
                productSnapshot(itemId),
                userSnapshot(itemId, email)
        );
    }

    public CustomPage<ProductResponse> productPage(int page, int size, long seed) {
        List<ProductResponse> products = List.of(
                productResponse(seed),
                productResponse(seed + 1),
                productResponse(seed + 2)
        );
        return CustomPage.create(page, size, 27, 9, products);
    }

    public CustomPage<OrderResponse> orderPage(int page, int size, long seed) {
        List<OrderResponse> orders = List.of(
                orderResponse(seed),
                orderResponse(seed + 1)
        );
        return CustomPage.create(page, size, 14, 7, orders);
    }

    public CustomPage<OrderItemResponse> sellerOrderPage(int page, int size, long seed, String email) {
        List<OrderItemResponse> items = List.of(
                orderItemResponse(seed, email),
                orderItemResponse(seed + 1, email)
        );
        return CustomPage.create(page, size, 18, 9, items);
    }

    public S3PresignedUpload presignedUpload(String prefix, String extension) {
        long next = nextId();
        return new S3PresignedUpload(
                prefix + "/" + next + extension,
                "https://uploads.quickcart.test/" + prefix + "/" + next + extension,
                900L
        );
    }

    public DownloadFile imageFile() {
        return DownloadFile.of("image/png", SAMPLE_IMAGE_BYTES);
    }

    public UserProfileResponse userProfile(String email, Set<String> authorities) {
        long seed = Math.abs(email.hashCode());
        return new UserProfileResponse(
                uuidFromSeed("user-profile-" + email),
                "Demo",
                "User",
                email,
                instantFromSeed(seed),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                authorities.stream()
                        .sorted()
                        .map(authority -> switch (authority) {
                            case "seller" -> (UserRoleProfile.Role) new UserRoleProfile.SellerRole(
                                    "Operating as a mocked seller profile.",
                                    instantFromSeed(seed + 1)
                            );
                            case "admin" -> new UserRoleProfile.AdminRole();
                            default -> new UserRoleProfile.CustomerRole();
                        })
                        .toList()
        );
    }

    public List<String> brands() {
        return List.of("Retreever Home", "QuickCart Labs", "Nimbus Kitchen", "Atlas Storefronts");
    }

    public UUID uuidFromSeed(String seed) {
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }

    public Instant instantFromSeed(long seed) {
        return Instant.parse("2026-01-01T00:00:00Z").plus(seed % 360, ChronoUnit.HOURS);
    }
}
