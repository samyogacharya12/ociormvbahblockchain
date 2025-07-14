package org.example.model;


import lombok.*;


public class NodeDto implements Cloneable {

    private String id;
    private String name;
    private String message;

   private byte[] ri;
   private  byte[] commitment;


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


    public byte[] getRi() {
        return ri;
    }

    public void setRi(byte[] ri) {
        this.ri = ri;
    }

    public byte[] getCommitment() {
        return commitment;
    }

    public void setCommitment(byte[] commitment) {
        this.commitment = commitment;
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
