package org.example.model;


import lombok.*;


public class NodeDto implements Cloneable {

    private String id;
    private String name;
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return super.toString();
    }



    @Override
    public NodeDto clone() {
        try {
            return (NodeDto) super.clone(); // shallow copy
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // should never happen since we implement Cloneable
        }
    }
}
