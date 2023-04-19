package com.example.annotation;

import com.example.core.FeignClientRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: TODO
 * @Author: whm
 * @CreateTime: 2023-04-18 14:44
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FeignClientRegistrar.class)
public @interface EnableFeignClient {

    String[] basePackages() default {};
}
