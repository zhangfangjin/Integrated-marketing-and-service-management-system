package org.example.rootmanage.companyinfo;

import org.example.rootmanage.companyinfo.dto.CompanyInfoReleaseRequest;
import org.example.rootmanage.companyinfo.entity.CompanyInfoRelease;
import org.example.rootmanage.option.OptionItem;
import org.example.rootmanage.option.OptionItemRepository;
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
 * 公司信息发布服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("公司信息发布服务测试")
class CompanyInfoServiceTest {

    @Mock
    private CompanyInfoReleaseRepository companyInfoReleaseRepository;

    @Mock
    private OptionItemRepository optionItemRepository;

    @InjectMocks
    private CompanyInfoService companyInfoService;

    private UUID releaseId;
    private UUID typeId;
    private CompanyInfoRelease release;
    private CompanyInfoReleaseRequest request;
    private OptionItem type;

    @BeforeEach
    void setUp() {
        releaseId = UUID.randomUUID();
        typeId = UUID.randomUUID();

        type = new OptionItem();
        type.setId(typeId);
        type.setTitle("新产品资料");

        release = new CompanyInfoRelease();
        release.setId(releaseId);
        release.setTitle("测试标题");
        release.setType(type);
        release.setContent("测试内容");
        release.setAttachments("attachment1.pdf");
        release.setActive(true);

        request = new CompanyInfoReleaseRequest();
        request.setTitle("测试标题");
        request.setTypeId(typeId);
        request.setContent("测试内容");
        request.setAttachments("attachment1.pdf");
    }

    @Test
    @DisplayName("查询所有信息发布 - 成功")
    void testFindAllReleases_Success() {
        List<CompanyInfoRelease> releases = Arrays.asList(release);
        when(companyInfoReleaseRepository.findByActiveTrueOrderByCreateTimeDesc()).thenReturn(releases);

        List<CompanyInfoRelease> result = companyInfoService.findAllReleases(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(releaseId, result.get(0).getId());
        verify(companyInfoReleaseRepository, times(1)).findByActiveTrueOrderByCreateTimeDesc();
    }

    @Test
    @DisplayName("查询信息发布 - 带关键词")
    void testFindAllReleases_WithKeyword() {
        List<CompanyInfoRelease> releases = Arrays.asList(release);
        when(companyInfoReleaseRepository.searchByKeyword("测试")).thenReturn(releases);

        List<CompanyInfoRelease> result = companyInfoService.findAllReleases("测试");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(companyInfoReleaseRepository, times(1)).searchByKeyword("测试");
    }

    @Test
    @DisplayName("根据ID查询信息发布 - 成功")
    void testFindReleaseById_Success() {
        when(companyInfoReleaseRepository.findById(releaseId)).thenReturn(Optional.of(release));

        Optional<CompanyInfoRelease> result = companyInfoService.findReleaseById(releaseId);

        assertTrue(result.isPresent());
        assertEquals(releaseId, result.get().getId());
        verify(companyInfoReleaseRepository, times(1)).findById(releaseId);
    }

    @Test
    @DisplayName("根据ID查询信息发布 - 不存在")
    void testFindReleaseById_NotFound() {
        when(companyInfoReleaseRepository.findById(releaseId)).thenReturn(Optional.empty());

        Optional<CompanyInfoRelease> result = companyInfoService.findReleaseById(releaseId);

        assertFalse(result.isPresent());
        verify(companyInfoReleaseRepository, times(1)).findById(releaseId);
    }

    @Test
    @DisplayName("创建信息发布 - 成功")
    void testCreateRelease_Success() {
        when(optionItemRepository.findById(typeId)).thenReturn(Optional.of(type));
        when(companyInfoReleaseRepository.save(any(CompanyInfoRelease.class))).thenReturn(release);

        CompanyInfoRelease result = companyInfoService.createRelease(request);

        assertNotNull(result);
        assertEquals(releaseId, result.getId());
        verify(optionItemRepository, times(1)).findById(typeId);
        verify(companyInfoReleaseRepository, times(1)).save(any(CompanyInfoRelease.class));
    }

    @Test
    @DisplayName("创建信息发布 - 类型不存在")
    void testCreateRelease_TypeNotFound() {
        when(optionItemRepository.findById(typeId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            companyInfoService.createRelease(request);
        });

        verify(optionItemRepository, times(1)).findById(typeId);
        verify(companyInfoReleaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("更新信息发布 - 成功")
    void testUpdateRelease_Success() {
        when(companyInfoReleaseRepository.findById(releaseId)).thenReturn(Optional.of(release));
        when(optionItemRepository.findById(typeId)).thenReturn(Optional.of(type));
        when(companyInfoReleaseRepository.save(any(CompanyInfoRelease.class))).thenReturn(release);

        CompanyInfoRelease result = companyInfoService.updateRelease(releaseId, request);

        assertNotNull(result);
        verify(companyInfoReleaseRepository, times(1)).findById(releaseId);
        verify(optionItemRepository, times(1)).findById(typeId);
        verify(companyInfoReleaseRepository, times(1)).save(any(CompanyInfoRelease.class));
    }

    @Test
    @DisplayName("更新信息发布 - 不存在")
    void testUpdateRelease_NotFound() {
        when(companyInfoReleaseRepository.findById(releaseId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            companyInfoService.updateRelease(releaseId, request);
        });

        verify(companyInfoReleaseRepository, times(1)).findById(releaseId);
        verify(companyInfoReleaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("删除信息发布 - 成功")
    void testDeleteRelease_Success() {
        when(companyInfoReleaseRepository.findById(releaseId)).thenReturn(Optional.of(release));
        when(companyInfoReleaseRepository.save(any(CompanyInfoRelease.class))).thenReturn(release);

        companyInfoService.deleteRelease(releaseId);

        verify(companyInfoReleaseRepository, times(1)).findById(releaseId);
        verify(companyInfoReleaseRepository, times(1)).save(any(CompanyInfoRelease.class));
        assertFalse(release.getActive());
    }

    @Test
    @DisplayName("删除信息发布 - 不存在")
    void testDeleteRelease_NotFound() {
        when(companyInfoReleaseRepository.findById(releaseId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            companyInfoService.deleteRelease(releaseId);
        });

        verify(companyInfoReleaseRepository, times(1)).findById(releaseId);
        verify(companyInfoReleaseRepository, never()).save(any());
    }
}

