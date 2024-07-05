package dev.simplix.protocolize.api.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.Bee;

import java.util.List;

public interface BeesComponent extends StructuredComponent {

    List<Bee> getBees();

    void setBees(List<Bee> bees);

    void addBee(Bee bee);

    void removeBee(Bee bee);

    void removeAllBees();

    static BeesComponent create(List<Bee> bees) {
        return Protocolize.getService(Factory.class).create(bees);
    }

    interface Factory {

        BeesComponent create(List<Bee> bees);

    }

}
