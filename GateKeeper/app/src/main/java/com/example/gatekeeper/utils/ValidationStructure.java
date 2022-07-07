package com.example.gatekeeper.utils;

public class ValidationStructure {
    boolean isInside;
    boolean exists;
    String message;

    public boolean getIsInside() {
        return isInside;
    }
    public void setisInside(boolean isInside) {
        this.isInside = isInside;
    }

    public boolean getExists() { return exists; }
    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = message;
    }
}

