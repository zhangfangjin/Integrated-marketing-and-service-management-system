package org.example.rootmanage.companyinfo;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.companyinfo.dto.CompanyInfoReleaseRequest;
import org.example.rootmanage.companyinfo.entity.CompanyInfoRelease;
import org.example.rootmanage.option.OptionItem;
import org.example.rootmanage.option.OptionItemRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyInfoService {

    private final CompanyInfoReleaseRepository companyInfoReleaseRepository;
    private final OptionItemRepository optionItemRepository;

    /**
     * 查询所有启用的信息发布
     */
    @Transactional(readOnly = true)
    public List<CompanyInfoRelease> findAllReleases(String keyword) {
        List<CompanyInfoRelease> releases;
        if (keyword != null && !keyword.trim().isEmpty()) {
            releases = companyInfoReleaseRepository.searchByKeyword(keyword.trim());
        } else {
            releases = companyInfoReleaseRepository.findByActiveTrueOrderByCreateTimeDesc();
        }
        // 初始化类型关联
        releases.forEach(release -> {
            if (release.getType() != null) {
                Hibernate.initialize(release.getType());
            }
        });
        return releases;
    }

    /**
     * 根据ID查询信息发布
     */
    @Transactional(readOnly = true)
    public Optional<CompanyInfoRelease> findReleaseById(UUID id) {
        Optional<CompanyInfoRelease> release = companyInfoReleaseRepository.findById(id);
        release.ifPresent(r -> {
            if (r.getType() != null) {
                Hibernate.initialize(r.getType());
            }
        });
        return release;
    }

    /**
     * 创建信息发布
     */
    @Transactional
    public CompanyInfoRelease createRelease(CompanyInfoReleaseRequest request) {
        CompanyInfoRelease release = new CompanyInfoRelease();
        release.setTitle(request.getTitle());
        release.setContent(request.getContent());
        release.setAttachments(request.getAttachments());
        release.setActive(true);

        // 设置类型
        if (request.getTypeId() != null) {
            OptionItem type = optionItemRepository.findById(request.getTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("类型不存在"));
            release.setType(type);
        }

        return companyInfoReleaseRepository.save(release);
    }

    /**
     * 更新信息发布
     */
    @Transactional
    public CompanyInfoRelease updateRelease(UUID id, CompanyInfoReleaseRequest request) {
        CompanyInfoRelease release = companyInfoReleaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("信息发布不存在"));

        release.setTitle(request.getTitle());
        release.setContent(request.getContent());
        release.setAttachments(request.getAttachments());

        // 更新类型
        if (request.getTypeId() != null) {
            OptionItem type = optionItemRepository.findById(request.getTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("类型不存在"));
            release.setType(type);
        }

        return companyInfoReleaseRepository.save(release);
    }

    /**
     * 删除信息发布（软删除）
     */
    @Transactional
    public void deleteRelease(UUID id) {
        CompanyInfoRelease release = companyInfoReleaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("信息发布不存在"));
        release.setActive(false);
        companyInfoReleaseRepository.save(release);
    }
}

