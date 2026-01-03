package org.example.rootmanage.mall;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 商城管理Controller
 * 用于权限验证测试，提供基本的CRUD接口
 */
@RestController
@RequestMapping("/api/mall")
@RequiredArgsConstructor
public class MallController {

    // 简单的内存存储（仅用于演示）
    private final Map<String, List<Map<String, Object>>> dataStore = new ConcurrentHashMap<>();

    // ========== 广告设置 ==========
    @GetMapping("/ad-settings")
    public List<Map<String, Object>> getAdSettings() {
        return dataStore.getOrDefault("ad-settings", new ArrayList<>());
    }

    @PostMapping("/ad-settings")
    public Map<String, Object> createAdSetting(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.computeIfAbsent("ad-settings", k -> new ArrayList<>());
        request.put("id", UUID.randomUUID().toString());
        list.add(request);
        return request;
    }

    @PutMapping("/ad-settings/{id}")
    public Map<String, Object> updateAdSetting(@PathVariable String id, @RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.getOrDefault("ad-settings", new ArrayList<>());
        for (Map<String, Object> item : list) {
            if (id.equals(item.get("id"))) {
                item.putAll(request);
                item.put("id", id);
                return item;
            }
        }
        throw new IllegalArgumentException("广告不存在");
    }

    @DeleteMapping("/ad-settings/{id}")
    public void deleteAdSetting(@PathVariable String id) {
        List<Map<String, Object>> list = dataStore.getOrDefault("ad-settings", new ArrayList<>());
        list.removeIf(item -> id.equals(item.get("id")));
    }

    // ========== 商场类型 ==========
    @GetMapping("/mall-type")
    public List<Map<String, Object>> getMallTypes() {
        return dataStore.getOrDefault("mall-type", new ArrayList<>());
    }

    @PostMapping("/mall-type")
    public Map<String, Object> createMallType(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.computeIfAbsent("mall-type", k -> new ArrayList<>());
        request.put("id", UUID.randomUUID().toString());
        list.add(request);
        return request;
    }

    @PutMapping("/mall-type/{id}")
    public Map<String, Object> updateMallType(@PathVariable String id, @RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.getOrDefault("mall-type", new ArrayList<>());
        for (Map<String, Object> item : list) {
            if (id.equals(item.get("id"))) {
                item.putAll(request);
                item.put("id", id);
                return item;
            }
        }
        throw new IllegalArgumentException("类型不存在");
    }

    @DeleteMapping("/mall-type/{id}")
    public void deleteMallType(@PathVariable String id) {
        List<Map<String, Object>> list = dataStore.getOrDefault("mall-type", new ArrayList<>());
        list.removeIf(item -> id.equals(item.get("id")));
    }

    // ========== 商店管理 ==========
    @GetMapping("/stores")
    public List<Map<String, Object>> getStores() {
        return dataStore.getOrDefault("stores", new ArrayList<>());
    }

    @PostMapping("/stores")
    public Map<String, Object> createStore(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.computeIfAbsent("stores", k -> new ArrayList<>());
        request.put("id", UUID.randomUUID().toString());
        list.add(request);
        return request;
    }

    @PutMapping("/stores/{id}")
    public Map<String, Object> updateStore(@PathVariable String id, @RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.getOrDefault("stores", new ArrayList<>());
        for (Map<String, Object> item : list) {
            if (id.equals(item.get("id"))) {
                item.putAll(request);
                item.put("id", id);
                return item;
            }
        }
        throw new IllegalArgumentException("商店不存在");
    }

    @DeleteMapping("/stores/{id}")
    public void deleteStore(@PathVariable String id) {
        List<Map<String, Object>> list = dataStore.getOrDefault("stores", new ArrayList<>());
        list.removeIf(item -> id.equals(item.get("id")));
    }

    // ========== 商品管理 ==========
    @GetMapping("/products")
    public List<Map<String, Object>> getProducts() {
        return dataStore.getOrDefault("products", new ArrayList<>());
    }

    @PostMapping("/products")
    public Map<String, Object> createProduct(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.computeIfAbsent("products", k -> new ArrayList<>());
        request.put("id", UUID.randomUUID().toString());
        list.add(request);
        return request;
    }

    @PutMapping("/products/{id}")
    public Map<String, Object> updateProduct(@PathVariable String id, @RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.getOrDefault("products", new ArrayList<>());
        for (Map<String, Object> item : list) {
            if (id.equals(item.get("id"))) {
                item.putAll(request);
                item.put("id", id);
                return item;
            }
        }
        throw new IllegalArgumentException("商品不存在");
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable String id) {
        List<Map<String, Object>> list = dataStore.getOrDefault("products", new ArrayList<>());
        list.removeIf(item -> id.equals(item.get("id")));
    }

    // ========== 订单管理 ==========
    @GetMapping("/orders")
    public List<Map<String, Object>> getOrders() {
        return dataStore.getOrDefault("orders", new ArrayList<>());
    }

    @PostMapping("/orders")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.computeIfAbsent("orders", k -> new ArrayList<>());
        request.put("id", UUID.randomUUID().toString());
        list.add(request);
        return request;
    }

    @PutMapping("/orders/{id}")
    public Map<String, Object> updateOrder(@PathVariable String id, @RequestBody Map<String, Object> request) {
        List<Map<String, Object>> list = dataStore.getOrDefault("orders", new ArrayList<>());
        for (Map<String, Object> item : list) {
            if (id.equals(item.get("id"))) {
                item.putAll(request);
                item.put("id", id);
                return item;
            }
        }
        throw new IllegalArgumentException("订单不存在");
    }

    @DeleteMapping("/orders/{id}")
    public void deleteOrder(@PathVariable String id) {
        List<Map<String, Object>> list = dataStore.getOrDefault("orders", new ArrayList<>());
        list.removeIf(item -> id.equals(item.get("id")));
    }
}

