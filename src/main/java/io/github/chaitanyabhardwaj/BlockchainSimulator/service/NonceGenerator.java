package io.github.chaitanyabhardwaj.BlockchainSimulator.service;

@FunctionalInterface
public interface NonceGenerator {

    long get(long seed);

}
