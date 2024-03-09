package io.github.chaitanyabhardwaj.BlockchainSimulator.model;

import java.time.Instant;
import java.util.Objects;

/*Represents properties of a block in a blockchain*/

public class Block {

    private long index;
    private long nonce;
    private String prevHash;
    private String hash;
    private String data;

    private boolean isValid;
    private final Instant CREATED_AT;

    //no args constructor
    public Block() {
        CREATED_AT = Instant.now();
    }

    public Block(long index, long nonce, String prevHash, String hash, boolean isValid, String data) {
        this.index = index;
        this.nonce = nonce;
        this.prevHash = prevHash;
        this.hash = hash;
        this.data = data;
        this.isValid = isValid;
        CREATED_AT = Instant.now();
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Instant getCREATED_AT() {
        return CREATED_AT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return index == block.index && nonce == block.nonce && isValid == block.isValid && Objects.equals(prevHash, block.prevHash) && Objects.equals(hash, block.hash) && Objects.equals(data, block.data) && Objects.equals(CREATED_AT, block.CREATED_AT);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, nonce, prevHash, hash, data, isValid, CREATED_AT);
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", nonce=" + nonce +
                ", prevHash='" + prevHash + '\'' +
                ", hash='" + hash + '\'' +
                ", data='" + data + '\'' +
                ", isValid=" + isValid +
                ", CREATED_AT=" + CREATED_AT +
                '}';
    }
}
