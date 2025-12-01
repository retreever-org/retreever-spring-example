package dev.retreever.example.service;

import dev.retreever.example.dto.request.StoreWrite;
import dev.retreever.example.dto.response.StoreDetails;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StoreService {
    public UUID createStore(@Valid StoreWrite request) {
        return  null;
    }

    public List<StoreDetails> getAllStores() {
        return null;
    }

    public StoreDetails getStoreDetails(UUID storeId) {
        return null;
    }

    public StoreDetails updateStore(UUID storeId, @Valid StoreWrite request) {
        return null;
    }

    public void deleteStore(UUID storeId) {

    }
}
