package de.exceptionflug.protocolize.protogen;

public class Field {

    private final String name;
    private final Class type;
    private final int modifier;

    public Field(String name, Class type, int modifier) {
        this.name = name;
        this.type = type;
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public int getModifier() {
        return modifier;
    }

    public Class getType() {
        return type;
    }
}
