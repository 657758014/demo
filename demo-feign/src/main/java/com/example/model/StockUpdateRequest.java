package com.example.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: TODO
 * @Author: whm
 * @CreateTime: 2023-04-18 20:27
 * @Version: 1.0
 */
@Getter
@Setter
public class StockUpdateRequest {

    private String id;
    private String name;

    @Override
    public String toString() {
        return "StockUpdateRequest{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
