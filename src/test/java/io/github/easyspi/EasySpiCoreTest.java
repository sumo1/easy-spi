package io.github.easyspi;

import io.github.easyspi.ability.TestAbility;
import io.github.easyspi.meta.BaseModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {EasySpiBeanAutoConfig.class})
public class EasySpiCoreTest {

    @Autowired
    private TestAbility ability;

    @Test
    public void testDefaultExtensionRegistrationAndExecution() {
        String result = ability.execute(null, ext -> ext.getName());
        Assert.assertEquals("DefaultTestAbilityExt", result);
    }

    @Test
    public void testCustomExtensionWithBizCode() {
        BaseModel model = BaseModel.valueOf("modelA");
        String result = ability.execute(model, ext -> ext.getName());
        Assert.assertEquals("ModelAExt", result);
    }

    @Test
    public void testCustomExtensionWithBizCode2() {
        BaseModel model = BaseModel.valueOf("modelB");
        String result = ability.execute(model, ext -> ext.getName());
        Assert.assertEquals("ModelBExt", result);
    }

    @Test
    public void testCustomExtensionWithScenario() {
        BaseModel model = BaseModel.valueOf("modelA", "scenarioA");
        String result = ability.execute(model, ext -> ext.getName());
        Assert.assertEquals("ModelAWithScenario", result);
    }

    @Test
    public void testCustomExtensionWithScenarioFallback() {
        BaseModel model = BaseModel.valueOf("modelA", "scenarioB");
        String result = ability.execute(model, ext -> ext.getName());
        Assert.assertEquals("ModelAExt", result);
    }

    @Test
    public void testFallbackToDefault() {
        BaseModel model = BaseModel.valueOf("nonExistentBiz");
        String result = ability.execute(model, ext -> ext.getName());
        Assert.assertEquals("DefaultTestAbilityExt", result);
    }

    @Test
    public void testExecuteVoid() {
        final String[] result = new String[1];
        ability.executeVoid(null, ext -> result[0] = ext.getName());
        Assert.assertEquals("DefaultTestAbilityExt", result[0]);
    }
} 