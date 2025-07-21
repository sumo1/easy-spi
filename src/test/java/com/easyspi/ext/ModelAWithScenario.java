package com.easyspi.ext;

import com.easyspi.meta.TemplateExt;

@TemplateExt(bizCode = "modelA", scenario = "scenarioA")
public class ModelAWithScenario extends DefaultTestAbilityExt {

    @Override
    public String getName() {
        return "ModelAWithScenario";
    }

}
