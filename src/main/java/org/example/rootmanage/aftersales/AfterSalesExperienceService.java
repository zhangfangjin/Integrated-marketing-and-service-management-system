package org.example.rootmanage.aftersales;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.aftersales.dto.AfterSalesExperienceRequest;
import org.example.rootmanage.aftersales.entity.AfterSalesExperience;
import org.example.rootmanage.aftersales.repository.AfterSalesExperienceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 售后经验服务类
 */
@Service
@RequiredArgsConstructor
public class AfterSalesExperienceService {

    private final AfterSalesExperienceRepository experienceRepository;

    /**
     * 查询所有售后经验
     */
    @Transactional(readOnly = true)
    public List<AfterSalesExperience> findAll() {
        return experienceRepository.findAll();
    }

    /**
     * 根据关键词搜索售后经验
     */
    @Transactional(readOnly = true)
    public List<AfterSalesExperience> searchAfterSalesExperiences(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return experienceRepository.searchAfterSalesExperiences(keyword.trim());
        }
        return experienceRepository.findAll();
    }

    /**
     * 根据设备类型查询售后经验
     */
    @Transactional(readOnly = true)
    public List<AfterSalesExperience> findByDeviceType(String deviceType) {
        return experienceRepository.findByDeviceTypeOrderByCaseRegistrationDateDesc(deviceType);
    }

    /**
     * 根据案例类型查询售后经验
     */
    @Transactional(readOnly = true)
    public List<AfterSalesExperience> findByCaseType(String caseType) {
        return experienceRepository.findByCaseTypeOrderByCaseRegistrationDateDesc(caseType);
    }

    /**
     * 根据ID查找售后经验
     */
    @Transactional(readOnly = true)
    public AfterSalesExperience findById(UUID id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后经验不存在"));
    }

    /**
     * 创建售后经验
     */
    @Transactional
    public AfterSalesExperience create(AfterSalesExperienceRequest request) {
        AfterSalesExperience experience = new AfterSalesExperience();
        experience.setCustomerName(request.getCustomerName());
        experience.setDeviceType(request.getDeviceType());
        experience.setCaseRegistrationDate(request.getCaseRegistrationDate());
        experience.setCaseType(request.getCaseType());
        experience.setExperienceSummary(request.getExperienceSummary());
        experience.setCaseRemark(request.getCaseRemark());
        experience.setAttachment(request.getAttachment());
        experience.setRegistrantId(request.getRegistrantId());
        experience.setRegistrantName(request.getRegistrantName());

        return experienceRepository.save(experience);
    }

    /**
     * 更新售后经验
     */
    @Transactional
    public AfterSalesExperience update(UUID id, AfterSalesExperienceRequest request) {
        AfterSalesExperience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后经验不存在"));

        experience.setCustomerName(request.getCustomerName());
        experience.setDeviceType(request.getDeviceType());
        experience.setCaseRegistrationDate(request.getCaseRegistrationDate());
        experience.setCaseType(request.getCaseType());
        experience.setExperienceSummary(request.getExperienceSummary());
        experience.setCaseRemark(request.getCaseRemark());
        experience.setAttachment(request.getAttachment());
        experience.setRegistrantId(request.getRegistrantId());
        experience.setRegistrantName(request.getRegistrantName());

        return experienceRepository.save(experience);
    }

    /**
     * 删除售后经验
     */
    @Transactional
    public void delete(UUID id) {
        AfterSalesExperience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("售后经验不存在"));
        experienceRepository.delete(experience);
    }
}

















