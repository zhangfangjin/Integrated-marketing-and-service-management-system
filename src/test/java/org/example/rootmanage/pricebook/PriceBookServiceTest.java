package org.example.rootmanage.pricebook;

import org.example.rootmanage.basicinfo.ProductRepository;
import org.example.rootmanage.basicinfo.entity.Product;
import org.example.rootmanage.pricebook.dto.PriceBookRequest;
import org.example.rootmanage.pricebook.entity.PriceBook;
import org.example.rootmanage.pricebook.repository.PriceBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 价格本服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("价格本服务测试")
class PriceBookServiceTest {

    @Mock
    private PriceBookRepository priceBookRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PriceBookService priceBookService;

    private UUID priceBookId;
    private UUID productId;
    private PriceBook priceBook;
    private Product product;
    private PriceBookRequest request;

    @BeforeEach
    void setUp() {
        priceBookId = UUID.randomUUID();
        productId = UUID.randomUUID();

        // 创建测试产品
        product = new Product();
        product.setId(productId);
        product.setProductCode("P001");
        product.setProductName("测试产品");
        product.setActive(true);

        // 创建测试价格本
        priceBook = new PriceBook();
        priceBook.setId(priceBookId);
        priceBook.setProduct(product);
        priceBook.setVersionNumber("V1.0");
        priceBook.setPriceType("标准价格");
        priceBook.setUnitPrice(new BigDecimal("100.00"));
        priceBook.setCurrency("CNY");
        priceBook.setEffectiveDate(Date.valueOf("2024-01-01"));
        priceBook.setExpiryDate(Date.valueOf("2024-12-31"));
        priceBook.setActive(true);
        priceBook.setRemark("测试备注");

        // 创建测试请求
        request = new PriceBookRequest();
        request.setProductId(productId);
        request.setVersionNumber("V1.0");
        request.setPriceType("标准价格");
        request.setUnitPrice(new BigDecimal("100.00"));
        request.setCurrency("CNY");
        request.setEffectiveDate(Date.valueOf("2024-01-01"));
        request.setExpiryDate(Date.valueOf("2024-12-31"));
        request.setActive(true);
        request.setRemark("测试备注");
    }

    @Test
    @DisplayName("查询所有价格记录 - 成功")
    void testFindAll_Success() {
        // Given
        List<PriceBook> priceBooks = Arrays.asList(priceBook);
        when(priceBookRepository.findAll()).thenReturn(priceBooks);

        // When
        List<PriceBook> result = priceBookService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(priceBookId, result.get(0).getId());
        verify(priceBookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("根据ID查找价格记录 - 存在")
    void testFindById_Success() {
        // Given
        when(priceBookRepository.findById(priceBookId)).thenReturn(Optional.of(priceBook));

        // When
        PriceBook result = priceBookService.findById(priceBookId);

        // Then
        assertNotNull(result);
        assertEquals(priceBookId, result.getId());
        assertEquals("V1.0", result.getVersionNumber());
        verify(priceBookRepository, times(1)).findById(priceBookId);
    }

    @Test
    @DisplayName("根据ID查找价格记录 - 不存在，抛出异常")
    void testFindById_NotFound() {
        // Given
        when(priceBookRepository.findById(priceBookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            priceBookService.findById(priceBookId);
        });
        verify(priceBookRepository, times(1)).findById(priceBookId);
    }

    @Test
    @DisplayName("根据产品ID查找价格记录 - 成功")
    void testFindByProductId_Success() {
        // Given
        List<PriceBook> priceBooks = Arrays.asList(priceBook);
        when(priceBookRepository.findByProductId(productId)).thenReturn(priceBooks);

        // When
        List<PriceBook> result = priceBookService.findByProductId(productId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getProduct().getId());
        verify(priceBookRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("创建价格记录 - 成功")
    void testCreate_Success() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(priceBookRepository.save(any(PriceBook.class))).thenReturn(priceBook);

        // When
        PriceBook result = priceBookService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(priceBookId, result.getId());
        verify(productRepository, times(1)).findById(productId);
        verify(priceBookRepository, times(1)).save(any(PriceBook.class));
    }

    @Test
    @DisplayName("创建价格记录 - 产品不存在，抛出异常")
    void testCreate_ProductNotFound() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            priceBookService.create(request);
        });
        verify(productRepository, times(1)).findById(productId);
        verify(priceBookRepository, never()).save(any(PriceBook.class));
    }

    @Test
    @DisplayName("更新价格记录 - 成功")
    void testUpdate_Success() {
        // Given
        request.setVersionNumber("V2.0");
        request.setUnitPrice(new BigDecimal("150.00"));
        PriceBook updatedPriceBook = new PriceBook();
        updatedPriceBook.setId(priceBookId);
        updatedPriceBook.setProduct(product);
        updatedPriceBook.setVersionNumber("V2.0");
        updatedPriceBook.setPriceType("标准价格");
        updatedPriceBook.setUnitPrice(new BigDecimal("150.00"));

        when(priceBookRepository.findById(priceBookId)).thenReturn(Optional.of(priceBook));
        when(priceBookRepository.save(any(PriceBook.class))).thenReturn(updatedPriceBook);

        // When
        PriceBook result = priceBookService.update(priceBookId, request);

        // Then
        assertNotNull(result);
        verify(priceBookRepository, times(1)).findById(priceBookId);
        verify(priceBookRepository, times(1)).save(any(PriceBook.class));
    }

    @Test
    @DisplayName("更新价格记录 - 价格记录不存在，抛出异常")
    void testUpdate_NotFound() {
        // Given
        when(priceBookRepository.findById(priceBookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            priceBookService.update(priceBookId, request);
        });
        verify(priceBookRepository, times(1)).findById(priceBookId);
        verify(priceBookRepository, never()).save(any(PriceBook.class));
    }

    @Test
    @DisplayName("删除价格记录 - 成功")
    void testDelete_Success() {
        // Given
        when(priceBookRepository.existsById(priceBookId)).thenReturn(true);
        doNothing().when(priceBookRepository).deleteById(priceBookId);

        // When
        priceBookService.delete(priceBookId);

        // Then
        verify(priceBookRepository, times(1)).existsById(priceBookId);
        verify(priceBookRepository, times(1)).deleteById(priceBookId);
    }

    @Test
    @DisplayName("删除价格记录 - 价格记录不存在，抛出异常")
    void testDelete_NotFound() {
        // Given
        when(priceBookRepository.existsById(priceBookId)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            priceBookService.delete(priceBookId);
        });
        verify(priceBookRepository, times(1)).existsById(priceBookId);
        verify(priceBookRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("根据关键词搜索价格记录 - 成功")
    void testSearchByKeyword_Success() {
        // Given
        List<PriceBook> priceBooks = Arrays.asList(priceBook);
        when(priceBookRepository.searchByKeyword("%测试%")).thenReturn(priceBooks);

        // When
        List<PriceBook> result = priceBookService.searchByKeyword("测试");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(priceBookRepository, times(1)).searchByKeyword("%测试%");
    }

    @Test
    @DisplayName("根据关键词搜索价格记录 - 关键词为空，返回所有启用记录")
    void testSearchByKeyword_EmptyKeyword() {
        // Given
        List<PriceBook> priceBooks = Arrays.asList(priceBook);
        when(priceBookRepository.findAllActive()).thenReturn(priceBooks);

        // When
        List<PriceBook> result = priceBookService.searchByKeyword(null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(priceBookRepository, times(1)).findAllActive();
        verify(priceBookRepository, never()).searchByKeyword(any());
    }
}

