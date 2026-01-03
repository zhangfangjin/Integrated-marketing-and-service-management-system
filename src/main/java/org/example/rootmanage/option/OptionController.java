package org.example.rootmanage.option;

import lombok.RequiredArgsConstructor;
import org.example.rootmanage.option.dto.OptionItemRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    @GetMapping
    public List<OptionItem> list(@RequestParam(required = false) String group) {
        if (group != null) {
            return optionService.findByGroup(group);
        }
        return optionService.findAll();
    }

    @PostMapping
    public OptionItem create(@RequestBody OptionItemRequest request) {
        return optionService.create(request);
    }

    @PutMapping("/{id}")
    public OptionItem update(@PathVariable UUID id, @RequestBody OptionItemRequest request) {
        return optionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        optionService.delete(id);
    }
}

