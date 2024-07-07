package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BlockSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface ToolComponent extends StructuredComponent {

    List<Rule> getRules();

    void setRules(List<Rule> rules);

    void addRule(Rule rule);

    void removeRule(Rule rule);

    void removeAllRules();

    float getMiningSpeed();

    void setMiningSpeed(float miningSpeed);

    int getDamagePerBlock();

    void setDamagePerBlock(int damagePerBlock);

    static ToolComponent create(List<Rule> rules, float miningSpeed, int damagePerBlock) {
        return Protocolize.getService(Factory.class).create(rules, miningSpeed, damagePerBlock);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Rule {
        BlockSet blockSet;
        Float speed;
        Boolean correctToolForDrops;
    }

    interface Factory {

        ToolComponent create(List<Rule> rules, float miningSpeed, int damagePerBlock);

    }

}
