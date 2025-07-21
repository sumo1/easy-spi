package com.easyspi;

import com.easyspi.factory.ExtBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.easyspi")
public class EasySpiBeanAutoConfig {

    @Bean
    @ConditionalOnMissingBean(ExtBeanFactory.class)
    public ExtBeanFactory extBeanFactory() {
        return new ExtBeanFactory();
    }
}
