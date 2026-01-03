package org.example.rootmanage.sales;

import org.example.rootmanage.basicinfo.ProductRepository;
import org.example.rootmanage.basicinfo.entity.Product;
import org.example.rootmanage.option.OptionItemRepository;
import org.example.rootmanage.sales.dto.SalesInventoryRequest;
import org.example.rootmanage.sales.entity.SalesInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 销售库存服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("销售库存服务测试")
class SalesServiceInventoryTest {

    @Mock
    private SalesInventoryRepository salesInventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OptionItemRepository optionItemRepository;

    @InjectMocks
    private SalesService salesService;

    private UUID inventoryId;
    private UUID productId;
    private SalesInventory inventory;
    private Product product;
    private SalesInventoryRequest request;

    @BeforeEach
    void setUp() {
        inventoryId = UUID.randomUUID();
        productId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setProductName("测试产品");

        inventory = new SalesInventory();
        inventory.setId(inventoryId);
        inventory.setProduct(product);
        inventory.setQuantity(100);
        inventory.setAvailableQuantity(80);
        inventory.setDrawingNo("DWG001");
        inventory.setMaterial("钢材");
        inventory.setIsStagnant(false);

        request = new SalesInventoryRequest();
        request.setProductId(productId);
        request.setQuantity(100);
        request.setAvailableQuantity(80);
        request.setDrawingNo("DWG001");
        request.setMaterial("钢材");
        request.setIsStagnant(false);
    }

    @Test
    @DisplayName("查询所有库存 - 无关键词")
    void testFindAllSalesInventories_NoKeyword() {
        List<SalesInventory> expectedInventories = Arrays.asList(inventory);
        when(salesInventoryRepository.findAll()).thenReturn(expectedInventories);

        List<SalesInventory> actualInventories = salesService.findAllSalesInventories(null);

        assertNotNull(actualInventories);
        assertEquals(1, actualInventories.size());
        assertEquals(inventory.getDrawingNo(), actualInventories.get(0).getDrawingNo());
        verify(salesInventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("查询所有库存 - 带关键词")
    void testFindAllSalesInventories_WithKeyword() {
        List<SalesInventory> expectedInventories = Arrays.asList(inventory);
        when(salesInventoryRepository.searchInventories("DWG001")).thenReturn(expectedInventories);

        List<SalesInventory> actualInventories = salesService.findAllSalesInventories("DWG001");

        assertNotNull(actualInventories);
        assertEquals(1, actualInventories.size());
        verify(salesInventoryRepository, times(1)).searchInventories("DWG001");
    }

    @Test
    @DisplayName("创建库存记录 - 成功")
    void testCreateSalesInventory_Success() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(salesInventoryRepository.save(any(SalesInventory.class))).thenReturn(inventory);

        SalesInventory created = salesService.createSalesInventory(request);

        assertNotNull(created);
        assertEquals(inventory.getDrawingNo(), created.getDrawingNo());
        verify(productRepository, times(1)).findById(productId);
        verify(salesInventoryRepository, times(1)).save(any(SalesInventory.class));
    }

    @Test
    @DisplayName("创建库存记录 - 产品不存在")
    void testCreateSalesInventory_ProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            salesService.createSalesInventory(request);
        });

        assertEquals("产品不存在", exception.getMessage());
        verify(salesInventoryRepository, never()).save(any(SalesInventory.class));
    }

    @Test
    @DisplayName("更新库存记录 - 成功")
    void testUpdateSalesInventory_Success() {
        when(salesInventoryRepository.findById(inventoryId)).thenReturn(Optional.of(inventory));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(salesInventoryRepository.save(any(SalesInventory.class))).thenReturn(inventory);

        request.setQuantity(150);
        request.setDrawingNo("DWG002");
        SalesInventory updated = salesService.updateSalesInventory(inventoryId, request);

        assertNotNull(updated);
        verify(salesInventoryRepository, times(1)).findById(inventoryId);
        verify(salesInventoryRepository, times(1)).save(any(SalesInventory.class));
    }

    @Test
    @DisplayName("删除库存记录 - 成功")
    void testDeleteSalesInventory_Success() {
        doNothing().when(salesInventoryRepository).deleteById(inventoryId);

        assertDoesNotThrow(() -> salesService.deleteSalesInventory(inventoryId));

        verify(salesInventoryRepository, times(1)).deleteById(inventoryId);
    }
}

