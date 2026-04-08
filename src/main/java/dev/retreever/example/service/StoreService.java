package dev.retreever.example.service;

import dev.retreever.example.dto.request.StoreWrite;
import dev.retreever.example.dto.response.StoreDetails;
import dev.retreever.example.service.support.MockDataFactory;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Long;

@Service
public class StoreService {
    private final MockDataFactory mockDataFactory;

    public StoreService(MockDataFactory mockDataFactory) {
        this.mockDataFactory = mockDataFactory;
    }

    public Long createStore(@Valid StoreWrite request) {
        return mockDataFactory.nextId();
    }

    public List<StoreDetails> getAllStores() {
        return List.of(
                mockDataFactory.storeDetails(1L, null),
                mockDataFactory.storeDetails(2L, null)
        );
    }

    public StoreDetails getStoreDetails(Long storeId) {
        return mockDataFactory.storeDetails(storeId == null ? 1L : storeId, null);
    }

    public StoreDetails updateStore(Long storeId, @Valid StoreWrite request) {
        return mockDataFactory.storeDetails(storeId == null ? 1L : storeId, request);
    }

    public void deleteStore(Long storeId) {

    }
}
