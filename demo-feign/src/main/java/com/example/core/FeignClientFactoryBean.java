package com.example.core;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @Description: 定义工厂类，生成动态代理
 * @Author: whm
 * @CreateTime: 2023-04-18 14:48
 * @Version: 1.0
 */
public class FeignClientFactoryBean implements FactoryBean<Object>, InitializingBean {

    private Class<?> clazz;

    private String url;

    private Integer readTimeout;

    private Integer connectTimeout;

    public FeignClientFactoryBean(Class<?> clazz, String url, Integer readTimeout, Integer connectTimeout) {
        this.clazz = clazz;
        this.url = url;
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                new FeignClientHandler(url, readTimeout, connectTimeout));
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.isNull(clazz)) {
            throw new RuntimeException("FeignClient start up param error, class is null");
        }
        if (!StringUtils.hasText(url)) {
            throw new RuntimeException("FeignClient start up param error, url is null");
        }
        if (Objects.isNull(readTimeout)) {
            throw new RuntimeException("FeignClient start up param error, readTimeout is null");
        }
        if (Objects.isNull(connectTimeout)) {
            throw new RuntimeException("FeignClient start up param error, connectTimeout is null");
        }
    }
}
