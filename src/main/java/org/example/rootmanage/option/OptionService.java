package org.example.rootmanage.option;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.option.dto.OptionItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionItemRepository optionItemRepository;

    public List<OptionItem> findAll() {
        return optionItemRepository.findAll();
    }

    public List<OptionItem> findByGroup(String groupCode) {
        return optionItemRepository.findByGroupCodeOrderByOrderNoAsc(groupCode);
    }

    @Transactional
    public OptionItem create(OptionItemRequest request) {
        OptionItem item = new OptionItem();
        item.setGroupCode(request.getGroupCode());
        item.setTitle(request.getTitle());
        item.setValue(request.getValue());
        item.setOrderNo(request.getOrderNo());
        return optionItemRepository.save(item);
    }

    @Transactional
    public OptionItem update(UUID id, OptionItemRequest request) {
        OptionItem item = optionItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("选项不存在"));
        item.setGroupCode(request.getGroupCode());
        item.setTitle(request.getTitle());
        item.setValue(request.getValue());
        item.setOrderNo(request.getOrderNo());
        return optionItemRepository.save(item);
    }

    @Transactional
    public void delete(UUID id) {
        optionItemRepository.deleteById(id);
    }
}

