package org.example.rootmanage.option;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OptionItemRepository extends JpaRepository<OptionItem, UUID> {

    List<OptionItem> findByGroupCodeOrderByOrderNoAsc(String groupCode);
}

