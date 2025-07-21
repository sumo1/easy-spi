package com.easyspi.ability;

import com.easyspi.AbstractAbility;
import com.easyspi.meta.DefaultTemplateExt;
import com.easyspi.ext.DefaultTestAbilityExt;
import com.easyspi.meta.BaseModel;

@DefaultTemplateExt
public class TestAbility extends AbstractAbility<DefaultTestAbilityExt> {

    public String executorSPI(BaseModel model) {
        return execute(model, ext -> ext.getName());
    }
}
