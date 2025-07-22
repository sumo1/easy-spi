package io.github.easyspi.ability;

import org.springframework.stereotype.Service;

import io.github.easyspi.AbstractAbility;
import io.github.easyspi.meta.DefaultTemplateExt;
import io.github.easyspi.ext.DefaultTestAbilityExt;
import io.github.easyspi.meta.BaseModel;

@Service
public class TestAbility extends AbstractAbility<DefaultTestAbilityExt> {

    public String executorSPI(BaseModel model) {
        return execute(model, ext -> ext.getName());
    }
}
