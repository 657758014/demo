package com.example.core;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Description: 定义代理类，接口实现逻辑全在这里。
 * @Author: whm
 * @CreateTime: 2023-04-18 14:51
 * @Version: 1.0
 *
 */
@Slf4j
public class FeignClientHandler implements InvocationHandler {

    private final String url;

    private final Integer readTimeout;

    private final Integer connectTimeout;

    public FeignClientHandler(String url, Integer readTimeout, Integer connectTimeout) {
        this.url = url;
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        RequestMethod[] requestMethods = requestMapping.method();
        if(requestMethods.length == 0){
            throw new RuntimeException("@RequestMapping is not allowed");
        }
//        log.info("{}", requestMapping);
        switch (requestMapping.method()[0].name()){
            case "GET":
                String queryString = getQueryString(method, args);
                String reqUrl = url;
                if (method.getAnnotation(GetMapping.class).value().length > 0) {
                    reqUrl += method.getAnnotation(GetMapping.class).value()[0];
                }
                if(StringUtils.hasText(queryString)){
                    reqUrl += queryString;
                }
                return HttpUtil.get(reqUrl, connectTimeout);
            case "POST":
                String postUrl = url;
                if (method.getAnnotation(PostMapping.class).value().length > 0) {
                    postUrl += method.getAnnotation(PostMapping.class).value()[0];
                }
                return HttpUtil.post(postUrl, JSON.toJSONString(args[0]), connectTimeout);
            case "DELETE":
                String deleteUrl = url;
                if (method.getAnnotation(DeleteMapping.class).value().length > 0) {
                    deleteUrl += method.getAnnotation(DeleteMapping.class).value()[0];
                }
                return HttpUtil.post(deleteUrl, JSON.toJSONString(args[0]), connectTimeout);
            case "PUT":
                String putUrl = url;
                if (method.getAnnotation(PutMapping.class).value().length > 0) {
                    putUrl += method.getAnnotation(PutMapping.class).value()[0];
                }
                return HttpUtil.post(putUrl, JSON.toJSONString(args[0]), connectTimeout);
            default:
                throw new RuntimeException("暂不支持的请求方式：" + requestMapping.method()[0].name());
        }
    }

    private String getQueryString(Method method, Object[] args){
        // 获取方法参数的数据
        int count = method.getParameterCount();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        StringBuilder queryString = new StringBuilder();

        for (int i = 0; i < count; i++) {
            Class<?> parameterType = parameterTypes[i];
            //获取方法参数上面的注解
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Annotation annotation = parameterAnnotations[i][0];
            RequestParam requestParam = (RequestParam) annotation;
            if (requestParam != null) {
                String value = requestParam.value();
                String requestParamName = StringUtils.hasText(value) ? value : parameters[i].getName();
                String requestParamValue = String.class.equals(parameterType) ? (String) args[i] : String.valueOf(args[i]);
                if(StringUtils.hasText(requestParamValue)){
                    queryString.append("&").append(requestParamName).append("=").append(requestParamValue);
                }
            }
        }
        return queryString.toString().replaceFirst("&", "?");
    }

}
