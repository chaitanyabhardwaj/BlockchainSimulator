package io.github.chaitanyabhardwaj.BlockchainSimulator.model;

import java.util.Objects;

public class Data {

    private Block block;

    private String logs;

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return Objects.equals(block, data.block) && Objects.equals(logs, data.logs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, logs);
    }

    @Override
    public String toString() {
        return "Data{" +
                "block=" + block +
                ", logs='" + logs + '\'' +
                '}';
    }

}
