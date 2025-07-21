package io.github.easyspi.ext;

import io.github.easyspi.meta.TemplateExt;

@TemplateExt(bizCode = "modelA", scenario = "scenarioA")
public class ModelAWithScenario extends DefaultTestAbilityExt {

    @Override
    public String getName() {
        return "ModelAWithScenario";
    }

}
