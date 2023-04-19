package com.example.controller;

import com.example.model.StockUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: TODO
 * @Author: whm
 * @CreateTime: 2023-04-14 14:45
 * @Version: 1.0
 */
@Slf4j
@RequestMapping("/stock")
@RestController
public class StockController {

    @RequestMapping("/r")
    public String r(@RequestParam String id){
        return id;
    }
    @RequestMapping("/get")
    public String get( String id){
        return id;
    }

    @PostMapping("/update")
    public String update(@RequestBody StockUpdateRequest request){
        log.info("{}", request);
        return "update";
    }
    @PostMapping("/delete")
    public String delete(@RequestBody StockUpdateRequest request){
        log.info("{}", request);
        return "delete";
    }
}
