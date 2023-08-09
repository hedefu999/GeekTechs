package com.drools;

public enum CustomerType {
    LOYAL,NEW,DISSATISFIED;

    public String getValue(){
        return this.name();
    }
}
