package com.easyspi.factory;

import com.easyspi.meta.DefaultTemplateExt;
import com.easyspi.meta.EasySpiException;
import com.easyspi.meta.IBusinessExt;
import com.easyspi.meta.TemplateExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExtBeanFactory implements BeanFactoryAware, ApplicationContextAware, BeanPostProcessor, EnvironmentAware {

    public static final String DEFAULT_OBJECT_CLASS_NAME = "java.lang.Object";
    public static final String BIZCODE_SPLITTER = "#";
    public static final String DEFAULT_TEMPLATE_NAME = "EASY-SPI-DEFAULT";
    public static final String DEFAULT_OBJECT_NAME = "Object";
    public static final Logger LOGGER = LoggerFactory.getLogger(ExtBeanFactory.class);
    private static Map<String, Object> extMap = new ConcurrentHashMap<>();
    private BeanFactory beanFactory;
    private ApplicationContext applicationContext;
    private ConfigurableEnvironment environment;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        initTemplateExtBean(bean, beanName);
        initDefaultTemplateExtBean(bean, beanName);
        return bean;
    }

    private void initTemplateExtBean(Object bean, String beanName) {

        TemplateExt templateExt = AnnotationUtils.findAnnotation(AopUtils.getTargetClass(bean), TemplateExt.class);
        if (templateExt == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        LOGGER.info("class name with TemplateExt registered:" + beanName);

        String templateCode = resolveBizCode(templateExt.bizCode());
        if (!"".equals(templateExt.scenario())) {
            templateCode += BIZCODE_SPLITTER + templateExt.scenario();
        }
        String defaultTemplateExtName = "";
        try {
            Class<?> templateExtClass = applicationContext.getBean(beanName).getClass();
            Class<? extends IBusinessExt> defaultTemplateExtClass = getDefaultTemplateExtClass(templateExtClass);
            if (Objects.nonNull(defaultTemplateExtClass)) {
                defaultTemplateExtName = defaultTemplateExtClass.getSimpleName();
            }
        } catch (Exception e) {
            LOGGER.error("find default template ext class error", e);
            return;
        }
        if (Objects.equals(defaultTemplateExtName, "") || Objects.equals(defaultTemplateExtName, DEFAULT_OBJECT_NAME)) {
            throw new EasySpiException("错误扩展点类格式定义: " + beanName);
        }

        String key = defaultTemplateExtName + BIZCODE_SPLITTER + templateCode;
        if (extMap.containsKey(key)) {
            throw new EasySpiException("重复扩展点定义: " + key);
        }
        extMap.put(key, beanFactory.getBean(beanName));
        sb.append("key:").append(key).append(",value:").append(beanName);

        LOGGER.info("扩展点定义map:" + sb.toString());
    }

    private void initDefaultTemplateExtBean(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        if (!isDefaultTemplateExtBean(beanClass)) {
            return;
        }
        Object defaultTemplateExt = applicationContext.getBean(beanName);
        String key = defaultTemplateExt.getClass().getSimpleName() + BIZCODE_SPLITTER + DEFAULT_TEMPLATE_NAME;
        extMap.put(key, defaultTemplateExt);

        LOGGER.info("register default extension success, key: {}, bean: {}", key, beanName);
    }

    public boolean isDefaultTemplateExtBean(Class<?> clazz) {
        DefaultTemplateExt defaultTemplateExt = AnnotationUtils.findAnnotation(clazz, DefaultTemplateExt.class);
        if (Objects.isNull(defaultTemplateExt)) {
            return false;
        }
        ResolvableType[] interfaces = ResolvableType.forClass(clazz).getInterfaces();
        if (interfaces.length == 0) {
            return false;
        }
        if (!Objects.equals(interfaces[0].resolve(), IBusinessExt.class)) {
            LOGGER.error("class {} annotation with @DefaultTemplateExt must implement IBusinessExt, please check", clazz.getSimpleName());
            return false;
        }
        return true;
    }

    private Class<? extends IBusinessExt> getDefaultTemplateExtClass(Class<?> clazz) {
        if (isDefaultTemplateExtBean(clazz)) {
            return (Class<? extends IBusinessExt>) clazz;
        }
        ResolvableType businessExtType = ResolvableType.forType(clazz).getSuperType();
        while (true) {
            if (DEFAULT_OBJECT_CLASS_NAME.equals(businessExtType.getType().getTypeName())) {
                break;
            }
            if (isDefaultTemplateExtBean(businessExtType.resolve())) {
                return (Class<? extends IBusinessExt>) businessExtType.resolve();
            }
            businessExtType = businessExtType.getSuperType();
        }
        return null;
    }

    public Class<? extends IBusinessExt> getDefaultTemplateExtClassByGeneric(Class<?> clazz) {
        ResolvableType businessExtType = ResolvableType.forType(clazz).getSuperType();
        while (true) {
            for (ResolvableType resolvableType : businessExtType.getGenerics()) {
                if (isDefaultTemplateExtBean(resolvableType.resolve())) {
                    return (Class<? extends IBusinessExt>) resolvableType.resolve();
                }
            }
            businessExtType = businessExtType.getSuperType();
            if (DEFAULT_OBJECT_CLASS_NAME.equals(businessExtType.getType().getTypeName())) {
                return null;
            }
        }
    }

    private String resolveBizCode(String bizCode) {
        return environment.resolveRequiredPlaceholders(bizCode);
    }

    public <BusinessExt extends IBusinessExt> BusinessExt getBusinessExt(String bizCode, Class<BusinessExt> businessExtClass) {
        String key = businessExtClass.getSimpleName() + BIZCODE_SPLITTER + bizCode;
        Object result = extMap.get(key);
        if (null == result) {
            return null;
        }
        return (BusinessExt) result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}
