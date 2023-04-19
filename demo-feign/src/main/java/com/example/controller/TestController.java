package com.example.controller;

import com.example.model.StockUpdateRequest;
import com.example.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description: TODO
 * @Author: whm
 * @CreateTime: 2023-04-14 15:08
 * @Version: 1.0
 */
@Slf4j
@RestController
public class TestController {

    @Resource
    StockService stockService;

    @RequestMapping("/r")
    public String r(String id, String name){
        return stockService.r(id, name);
    }
    @RequestMapping("/test")
    public String test(String id, String name){
        return stockService.getStock(id, name);
    }

    @RequestMapping("/update")
    public String update(@RequestBody StockUpdateRequest request){
        log.info("{}", request);
        return stockService.update(request);
    }
    @RequestMapping("/delete")
    public String delete(@RequestBody StockUpdateRequest request){
        log.info("{}", request);
        return stockService.delete(request);
    }

}
