package io.github.chaitanyabhardwaj.BlockchainSimulator.service.impl;

import io.github.chaitanyabhardwaj.BlockchainSimulator.model.Block;
import io.github.chaitanyabhardwaj.BlockchainSimulator.service.BlockChain;
import io.github.chaitanyabhardwaj.BlockchainSimulator.service.NonceGenerator;
import io.github.chaitanyabhardwaj.BlockchainSimulator.util.Encoder;
import io.github.chaitanyabhardwaj.BlockchainSimulator.util.LOG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

/*
*Manages blocks and create a structure that represents a blockchain.
*The blocks are stored in a Map of SHA256, block pair.
*/
@Service
@Primary
public class BlockChainImpl implements BlockChain {

    final private Map<String, Block> CHAIN = new HashMap<>();

    private Encoder encoder;

    final private Predicate<String> VALID_HASH;

    final public static int REQUIRED_TRAILING_ZEROS = 5;

    final public static String PREVIOUS_BLOCK_NULL = "0";

    private Block topBlock;

    public BlockChainImpl() {
        VALID_HASH = (hash -> {
            int l = REQUIRED_TRAILING_ZEROS;
            while(l-- != 0 ) {
                if(hash.charAt(l) != '0') return false;
            }
            return true;
        });
    }

    @Override
    public Block init(String data) {
        Optional<Block> optionalBlock = mine(PREVIOUS_BLOCK_NULL, data);
        if(optionalBlock.isPresent()) {
            Block b = optionalBlock.get();
            if(push(b))
                topBlock = b;
        }
        return topBlock;
    }

    @Override
    public boolean push(Block block) {
        if(isValid(block)) {
            if(CHAIN.containsKey(block.getHash())) {
                System.out.println("COLLISION!!!! AT: " + block.getHash());
                return false;
            }
            CHAIN.put(block.getHash(), block);
            topBlock = block;
            return true;
        }
        return false;
    }

    @Override
    public Block getTop() {
        return topBlock;
    }

    @Override
    public Optional<Block> mine(String prevHash, String data) {
        NonceGenerator nonceGenerator = (seed -> seed + 1);
        return mine(prevHash, data, nonceGenerator, 0);
    }

    @Override
    public Optional<Block> mine(String prevHash, String data, NonceGenerator nonceGenerator, long seed) {
        System.out.println("Mining with input[" + prevHash + ", " + data + ", " + seed);
        long nonce = seed;
        String hash;
        Block prevBlock;
        //hash function = prevNonce + (prevIndex * 7 - currentIndex * 7) - currentNonce * 2
        //check if block is initial node
        if(prevHash.equalsIgnoreCase(PREVIOUS_BLOCK_NULL)) {
            //create dummy prevBlock
            prevBlock = new Block(-1, 0, "0", PREVIOUS_BLOCK_NULL, null);
        }
        else {
            Optional<Block> prevBlockOptional = get(prevHash);
            if(prevBlockOptional.isEmpty()) {
                return Optional.empty();
            }
            prevBlock = prevBlockOptional.get();
        }
        long currentIndex = prevBlock.getIndex() + 1;
        LOG.append(currentIndex, "Mining with input[Prev Hash = " + prevHash + ", Data = " + data + ", Initial nonce = " + seed + "]\n");
        while(!VALID_HASH.test(hash = getHash(prevBlock.getNonce(), prevBlock.getIndex(), prevHash, nonce, currentIndex, prevBlock.getData())))
            nonce = nonceGenerator.get(nonce);
        LOG.append(currentIndex, "Valid nonce mapped for hash\n");
        Block block = new Block(currentIndex, nonce, prevHash, hash, data);
        LOG.append(currentIndex, "Mined: " + block + "\n");
        System.out.println("Mined : \n" + block);
        return Optional.of(block);
    }

    @Override
    public boolean isValid(Block block) {
        if (block.getIndex() == 0) {
            return VALID_HASH.test(block.getHash());
        }
        if(block.getHash().length() != 64) return false;
        if(!VALID_HASH.test(block.getHash())) return false;
        Optional<Block> prevBlockOptional = get(block.getPrevHash());
        if(prevBlockOptional.isEmpty()) return false;
        Block prevBlock = prevBlockOptional.get();
        String currHash = getHash(prevBlock.getNonce(), prevBlock.getIndex(), prevBlock.getHash(), block.getNonce(), block.getIndex(), prevBlock.getData());
        return currHash.equalsIgnoreCase(block.getHash());
    }

    @Override
    public Optional<Block> validateChain() {
        return null;
    }

    @Override
    public Optional<Block> get(long index) {
        return Optional.empty();
    }

    @Override
    public Optional<Block> get(String hash) {
        if(CHAIN.containsKey(hash))
            return Optional.of(CHAIN.get(hash));
        return Optional.empty();
    }

    @Override
    public List<Block> getAll() {
        List<Block> list = new ArrayList<>();
        for (String hash: CHAIN.keySet()) {
            list.add(CHAIN.get(hash));
        }
        return list;
    }

    @Override
    public boolean setData(long index, String data) {
        return false;
    }

    @Override
    public boolean setData(String hash, String data) {
        Optional<Block> blockOptional = get(hash);
        if(blockOptional.isEmpty()) return false;
        Block block = blockOptional.get();
        block.setData(data);
        return true;
    }

    @Override
    @Autowired
    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    private String getHash(long prevNonce, long prevIndex, String prevHash, long currentNonce, long currentIndex, String data) {
        return encoder.encode(prevHash + (prevNonce + (prevIndex * 3 - currentIndex * 3) - currentNonce) + data);
    }
}
