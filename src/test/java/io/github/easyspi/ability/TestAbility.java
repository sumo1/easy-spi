package io.github.easyspi.ability;

import io.github.easyspi.AbstractAbility;
import io.github.easyspi.meta.DefaultTemplateExt;
import io.github.easyspi.ext.DefaultTestAbilityExt;
import io.github.easyspi.meta.BaseModel;

@DefaultTemplateExt
public class TestAbility extends AbstractAbility<DefaultTestAbilityExt> {

    public String executorSPI(BaseModel model) {
        return execute(model, ext -> ext.getName());
    }
}
