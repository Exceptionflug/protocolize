package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;

public interface UseCooldownComponent extends StructuredComponent {

    float getCooldown();

    void setCooldown(float cooldown);

    String getCooldownGroup();

    void setCooldownGroup(String cooldownGroup);

    static UseCooldownComponent create(float cooldown, String cooldownGroup) {
        return Protocolize.getService(UseCooldownComponent.Factory.class).create(cooldown, cooldownGroup);
    }

    interface Factory {

        UseCooldownComponent create(float cooldown, String cooldownGroup);

    }

}
