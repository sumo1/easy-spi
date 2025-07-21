package com.easyspi.meta;

import java.io.Serializable;

public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1;

    protected String bizCode;

    protected String scenario;

    public static BaseModel valueOf(String bizCode) {
        BaseModel baseModel = new BaseModel();
        baseModel.setBizCode(bizCode);
        return baseModel;
    }

    public static BaseModel valueOf(String bizCode, String scenario) {
        BaseModel baseModel = new BaseModel();
        baseModel.setBizCode(bizCode);
        baseModel.setScenario(scenario);
        return baseModel;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }
}
