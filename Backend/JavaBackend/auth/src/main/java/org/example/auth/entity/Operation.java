package org.example.auth.entity;

public enum Operation {
    UPDATE("Update"),
    DELETE("Delete"),
    CREATE("Create");

    private final String displayName;

    Operation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
