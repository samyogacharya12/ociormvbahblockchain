package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Builder
public class Share {

    String commitment;
    byte[] share;
    List<byte[]> proof;

    @Override
    public String toString() {
        return "Share{" +
                "commitment='" + commitment + '\'' +
                ", share=" + Arrays.toString(share) +
                ", proof=" + proof +
                '}';
    }

    public Share() {
    }

    public Share(String commitment, byte[] share, List<byte[]> proof) {
        this.commitment = commitment;
        this.share = share;
        this.proof = proof;
    }

    public String getCommitment() {
        return commitment;
    }

    public void setCommitment(String commitment) {
        this.commitment = commitment;
    }

    public byte[] getShare() {
        return share;
    }

    public void setShare(byte[] share) {
        this.share = share;
    }

    public List<byte[]> getProof() {
        return proof;
    }

    public void setProof(List<byte[]> proof) {
        this.proof = proof;
    }
}
