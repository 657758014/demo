package com.example.annotation;

import java.lang.annotation.*;

/**
 * @Description: TODO
 * @Author: whm
 * @CreateTime: 2023-04-18 14:45
 * @Version: 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {

    /**
     * 注册bean名称，默认类名
     *
     * @return
     */
    String name() default "";

    /**
     * 调用url
     *
     * @return
     */
    String url();

    /**
     * 读超时时间
     *
     * @return
     */
    String readTimeout() default "5000";

    /**
     * 连接超时时间
     *
     * @return
     */
    String connectTimeout() default "5000";
}
