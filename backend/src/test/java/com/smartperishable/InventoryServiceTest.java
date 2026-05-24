package com.smartperishable;

import com.smartperishable.model.InventoryBatch;
import com.smartperishable.model.Product;
import com.smartperishable.repository.BatchRepository;
import com.smartperishable.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class InventoryServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void testFifoLogic() {
        Long productId = 1L;
        Product p = new Product();
        p.setId(productId);
        p.setName("Test Product");

        List<InventoryBatch> batches = new ArrayList<>();
        batches.add(new InventoryBatch(p, "B1", 10, LocalDate.now().plusDays(5), "R1", "L1")); // Oldest
        batches.add(new InventoryBatch(p, "B2", 10, LocalDate.now().plusDays(10), "R2", "L2")); // Newest

        when(batchRepository.findFifoBatches(productId)).thenReturn(batches);

        // Sell 15 units
        Map<String, Object> result = inventoryService.sellStock(productId, 15, "test");

        assertTrue((Boolean) result.get("success"));
        assertEquals(15, (int) result.get("quantitySold"));
        
        // Verify batch 1 is sold out (0) and batch 2 has 5 left
        assertEquals(0, batches.get(0).getQuantity());
        assertEquals(5, batches.get(1).getQuantity());
        
        verify(batchRepository, times(2)).save(any(InventoryBatch.class));
    }
}
