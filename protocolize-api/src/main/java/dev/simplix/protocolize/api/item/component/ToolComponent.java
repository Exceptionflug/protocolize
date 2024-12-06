package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ToolRule;

import java.util.List;

public interface ToolComponent extends StructuredComponent {

    List<ToolRule> getRules();

    void setRules(List<ToolRule> rules);

    void addRule(ToolRule rule);

    void removeRule(ToolRule rule);

    void removeAllRules();

    float getMiningSpeed();

    void setMiningSpeed(float miningSpeed);

    int getDamagePerBlock();

    void setDamagePerBlock(int damagePerBlock);

    static ToolComponent create(List<ToolRule> rules, float miningSpeed, int damagePerBlock) {
        return Protocolize.getService(Factory.class).create(rules, miningSpeed, damagePerBlock);
    }

    interface Factory {

        ToolComponent create(List<ToolRule> rules, float miningSpeed, int damagePerBlock);

    }

}
