package com.example.core;

import com.example.annotation.EnableFeignClient;
import com.example.annotation.FeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 定义注册类，启动时扫描包并注册到Spring中
 * @Author: whm
 * @CreateTime: 2023-04-18 14:46
 * @Version: 1.0
 */
@Slf4j
public class FeignClientRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanClassLoaderAware, ResourceLoaderAware {

    private Environment environment;

    private ClassLoader classLoader;

    private ResourceLoader resourceLoader;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(FeignClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableFeignClient.class.getCanonicalName());
        if (CollectionUtils.isEmpty(attributes)) {
            return;
        }
        Object basePackagesObj = attributes.get("basePackages");
        String[] basePackagesArr = (String[]) basePackagesObj;

        Set<String> basePackages;
        if(basePackagesArr.length == 0){
            basePackages = getBasePackages(metadata);
        }else{
            basePackages = new HashSet<>();
            for (String pkg : basePackagesArr) {
                if (StringUtils.hasText(pkg)) {
                    basePackages.add(pkg);
                }
            }
        }

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {

                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(), "@FeignClient can only be specified on an interface");

                    Map<String, Object> feignClientAttributeMap = annotationMetadata.getAnnotationAttributes(FeignClient.class.getCanonicalName());
                    if (CollectionUtils.isEmpty(feignClientAttributeMap)) {
                        return;
                    }

                    String className = annotationMetadata.getClassName();

                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        log.error("FeignClient start up fail:", e);
                    }

                    String beanName = className.substring(className.lastIndexOf(".") + 1);
                    String alias = beanName.substring(0, 1).toLowerCase().concat(beanName.substring(1)).concat("FeignClient");
                    String name = String.valueOf(feignClientAttributeMap.get("name"));
                    String url = String.valueOf(feignClientAttributeMap.get("url"));
                    if (StringUtils.hasText(name)) {
                        alias = name;
                    }
                    String readTimeout = String.valueOf(feignClientAttributeMap.get("readTimeout"));
                    String connectTimeout = String.valueOf(feignClientAttributeMap.get("connectTimeout"));

                    BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);
                    definition.addConstructorArgValue(clazz);
                    definition.addConstructorArgValue(url);
                    definition.addConstructorArgValue(readTimeout);
                    definition.addConstructorArgValue(connectTimeout);
                    definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

                    AbstractBeanDefinition handleDefinition = definition.getBeanDefinition();
                    handleDefinition.setPrimary(true);

                    // 向Spring的上下文中注册bean组件
                    BeanDefinitionHolder holder = new BeanDefinitionHolder(handleDefinition, className, new String[]{alias});
                    BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
                }
            }
        }
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableFeignClient.class.getCanonicalName());
        Set<String> basePackages = new HashSet();

        String[] var4 = (String[]) attributes.get("basePackages");
        int var5 = var4.length;
        int var6;
        String pkg;
        for(var6 = 0; var6 < var5; ++var6) {
            pkg = var4[var6];
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        if (basePackages.isEmpty()) {
            /**
             * 若没有配置包路径，则获取 @EnableFeignClient 所在包路径
             */
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        return basePackages;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition.getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), FeignClientRegistrar.this.classLoader);
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            this.logger.error("Could not load target class: " + beanDefinition.getMetadata().getClassName(), ex);
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

}
