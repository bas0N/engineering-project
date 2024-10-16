package org.example.auth.entity;

public enum Code {
    SUCCESS("Operation successful"),
    PERMIT("Permission granted");

    public final String label;
    private Code(String label){
        this.label = label;
    }
}
