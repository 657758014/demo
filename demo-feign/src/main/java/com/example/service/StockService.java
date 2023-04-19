package com.example.service;

import com.example.annotation.FeignClient;
import com.example.model.StockUpdateRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: TODO
 * @Author: whm
 * @CreateTime: 2023-04-14 14:41
 * @Version: 1.0
 */
@FeignClient(url = "http://127.0.0.1:8080/stock")
public interface StockService {

    @GetMapping(value = "/r")
    String r(@RequestParam String id, @RequestParam String name);

    @GetMapping(value = "/get")
    String getStock(@RequestParam String id, @RequestParam String name);

    @PostMapping(value = "/update")
    String update(@RequestBody StockUpdateRequest request);

    @DeleteMapping(value = "/delete")
    String delete(@RequestBody StockUpdateRequest request);
}
