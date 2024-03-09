package io.github.chaitanyabhardwaj.BlockchainSimulator.service;

import io.github.chaitanyabhardwaj.BlockchainSimulator.model.Block;
import io.github.chaitanyabhardwaj.BlockchainSimulator.util.Encoder;

import java.util.List;
import java.util.Optional;

public interface BlockChain {

    Block init(String data);

    boolean push(Block block);

    Block getTop();

    /*
    *Tries to create and add a new block in the current blockchain by providing PoW.
    *This does not guarantee the addition of block in the blockchain.
    */
    Optional<Block> mine(long prevIndex, String data);

    /*
     *Tries to create and add a new block in the current blockchain by providing PoW.
     *This does not guarantee the addition of block in the blockchain.
     */
    Optional<Block> mine(long prevIndex, String data, NonceGenerator nonceGenerator, long seed);

    /*
    *Validates a block. The block should fulfil all requirements
    *to be added to the current blockchain.
    */
    boolean isValid(Block block);

    void validateChain(long index);

    /*Return the block by index.*/
    Optional<Block> get(long index);

    /*Change the data of block by index*/
    boolean setData(long index, String data);

    List<Block> getAll();

    void setEncoder(Encoder encoder);

}
