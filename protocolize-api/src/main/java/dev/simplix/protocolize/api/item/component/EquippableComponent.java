package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.EquipmentSlot;
import dev.simplix.protocolize.api.item.SoundEvent;
import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.data.EntityType;

import java.util.List;

public interface EquippableComponent extends StructuredComponent {

    EquipmentSlot getSlot();

    void setSlot(EquipmentSlot slot);

    SoundEvent getEquipSound();

    void setEquipSound(SoundEvent equipSound);

    String getModel();

    void setModel(String model);

    String getCameraOverlay();

    void setCameraOverlay(String cameraOverlay);

    Either<String, List<EntityType>> getAllowedEntities();

    void setAllowedEntities(Either<String, List<EntityType>> allowedEntities);

    boolean isDispensable();

    void setDispensable(boolean dispensable);

    boolean isSwappable();

    void setSwappable(boolean swappable);

    boolean isDamageOnHurt();

    void setDamageOnHurt(boolean damageOnHurt);

    static EquippableComponent create(EquipmentSlot equipmentSlot, SoundEvent equipSound, String model, String cameraOverlay, Either<String, List<EntityType>> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt) {
        return Protocolize.getService(EquippableComponent.Factory.class).create(equipmentSlot, equipSound, model, cameraOverlay, allowedEntities, dispensable, swappable, damageOnHurt);
    }

    interface Factory {

        EquippableComponent create(EquipmentSlot equipmentSlot, SoundEvent equipSound, String model, String cameraOverlay, Either<String, List<EntityType>> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt);

    }
}
