package dev.retreever.example.controller;

import dev.retreever.example.dto.envelope.ApiAck;
import dev.retreever.example.dto.envelope.ApiResponse;
import dev.retreever.example.dto.request.StoreWrite;
import dev.retreever.example.dto.response.StoreDetails;
import dev.retreever.example.service.StoreService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/stores")
    public ResponseEntity<ApiAck> createStore(@RequestBody @Valid StoreWrite request) {
        UUID id = storeService.createStore(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/api/v1/public/stores/" + id))
                .body(ApiAck.success("Store Created."));
    }

    @GetMapping("/stores")
    @PreAuthorize("hasAuthority('seller')")
    public ResponseEntity<ApiResponse<List<StoreDetails>>> getAllStores() {
        List<StoreDetails> storeDetails = storeService.getAllStores();
        return ResponseEntity.ok(ApiResponse.success(
                "Stores Found",
                storeDetails
        ));
    }

    @GetMapping("/public/stores/{storeId}")
    public ResponseEntity<ApiResponse<StoreDetails>> getStoreByIDForPublic(@PathVariable UUID storeId) {
        StoreDetails storeDetails = storeService.getStoreDetails(storeId);
        return ResponseEntity.ok(ApiResponse.success(
                "Store Found",
                storeDetails
        ));
    }

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<ApiResponse<StoreDetails>> getStoreByID(@PathVariable UUID storeId) {
        StoreDetails storeDetails = storeService.getStoreDetails(storeId);
        return ResponseEntity.ok(ApiResponse.success(
                "Store Found",
                storeDetails
        ));
    }

    @PutMapping("/stores/{storeId}")
    public ResponseEntity<ApiResponse<StoreDetails>> updateStore(
            @PathVariable UUID storeId,
            @RequestBody @Valid StoreWrite request
    ) {
        StoreDetails storeDetails = storeService.updateStore(storeId, request);
        return ResponseEntity.ok(ApiResponse.success(
                "Store Updated",
                storeDetails
        ));
    }

    @DeleteMapping("/stores/{storeId}")
    public ResponseEntity<ApiAck> deleteStore(@PathVariable UUID storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiAck.success("Store Deleted."));
    }
}
