package dev.retreever.example.service;

import dev.retreever.example.dto.request.StoreWrite;
import dev.retreever.example.dto.response.StoreDetails;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.lang.Long;

@Service
public class StoreService {
    public Long createStore(@Valid StoreWrite request) {
        return  null;
    }

    public List<StoreDetails> getAllStores() {
        return null;
    }

    public StoreDetails getStoreDetails(Long storeId) {
        return null;
    }

    public StoreDetails updateStore(Long storeId, @Valid StoreWrite request) {
        return null;
    }

    public void deleteStore(Long storeId) {

    }
}
