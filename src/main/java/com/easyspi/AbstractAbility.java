package com.easyspi;

import com.easyspi.factory.ExtBeanFactory;
import com.easyspi.meta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static com.easyspi.factory.ExtBeanFactory.BIZCODE_SPLITTER;

public abstract class AbstractAbility<BusinessExt extends IBusinessExt> implements IAbility<BusinessExt> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAbility.class);

    @Autowired
    @Qualifier("extBeanFactory")
    ExtBeanFactory extBeanFactory;

    public <T> T execute(BaseModel baseModel, ExtensionCallback<BusinessExt, T> callback) {
        Class<BusinessExt> defaultTemplateExtClass = getDefaultTemplateExtClass();

        if (null == defaultTemplateExtClass) {
            String errorMsg = "错误扩展点类格式定义" + getClass().getSimpleName() + ",没有声明默认扩展点。";
            LOGGER.error(errorMsg);
            throw new EasySpiException(errorMsg);
        }
        if (baseModel == null || baseModel.getBizCode() == null) {
            BusinessExt defaultBusinessExt = extBeanFactory.getBusinessExt(ExtBeanFactory.DEFAULT_TEMPLATE_NAME,
                    defaultTemplateExtClass);
            return callback.apply(defaultBusinessExt);
        }
        BusinessExt actualBusinessExt = getExtendByCode(baseModel, defaultTemplateExtClass);
        return callback.apply(actualBusinessExt);
    }

    public void executeVoid(BaseModel baseModel, ExtensionConsumer<BusinessExt> consumer) {
        Class<BusinessExt> defaultTemplateExtClass = getDefaultTemplateExtClass();

        if (null == defaultTemplateExtClass) {
            String errorMsg = "错误扩展点类格式定义：" + getClass().getSimpleName() + ",没有声明默认扩展点。";
            LOGGER.error(errorMsg);
            throw new EasySpiException(errorMsg);
        }
        if (baseModel == null || baseModel.getBizCode() == null) {
            BusinessExt defaultBusinessExt = extBeanFactory.getBusinessExt(ExtBeanFactory.DEFAULT_TEMPLATE_NAME,
                    defaultTemplateExtClass);
            consumer.accept(defaultBusinessExt);
            return;
        }
        BusinessExt actualBusinessExt = getExtendByCode(baseModel, defaultTemplateExtClass);
        consumer.accept(actualBusinessExt);
    }

    @SuppressWarnings("unchecked")
    private Class<BusinessExt> getDefaultTemplateExtClass() {
        return (Class<BusinessExt>) extBeanFactory.getDefaultTemplateExtClassByGeneric(getClass());
    }

    private BusinessExt getExtendByCode(BaseModel baseModel, Class<BusinessExt> businessExtClass) {
        String code = baseModel.getBizCode();
        BusinessExt result = null;
        if (baseModel.getScenario() != null) {
            String codeWithScenario = code + BIZCODE_SPLITTER + baseModel.getScenario();
            result = extBeanFactory.getBusinessExt(codeWithScenario, businessExtClass);
        }
        if (null == result) {
            result = extBeanFactory.getBusinessExt(code, businessExtClass);
        }
        if (null == result) {
            result = extBeanFactory.getBusinessExt(ExtBeanFactory.DEFAULT_TEMPLATE_NAME, businessExtClass);
        }
        return result;
    }

}
