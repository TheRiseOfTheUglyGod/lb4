package ru.kafpin.lb4;

public enum FileType {
    FILE("F"),
    DIRECTORY("D");

    private final String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}