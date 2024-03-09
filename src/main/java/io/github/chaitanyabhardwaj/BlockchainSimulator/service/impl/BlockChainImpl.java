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

    final private Set<String> CHAIN_HASH = new HashSet<>();
    final private Map<Long, Block> CHAIN_INDEX = new LinkedHashMap<>();

    private Encoder encoder;

    final private Predicate<String> VALID_HASH;

    final public static int REQUIRED_TRAILING_ZEROS = 5;

    private Block topBlock;

    //create dummy
    final private Block DUMMY_BLOCK = new Block(-1, 0, "0", "0", false, null);

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
        Optional<Block> optionalBlock = mine(-1, data);
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
            if(CHAIN_HASH.contains(block.getHash())) {
                System.out.println("COLLISION!!!! AT hash: " + block.getHash());
                return false;
            }
            if(CHAIN_INDEX.containsKey(block.getIndex())) {
                System.out.println("COLLISION!!!! AT index: " + block.getIndex());
                return false;
            }
            CHAIN_HASH.add(block.getHash());
            CHAIN_INDEX.put(block.getIndex(), block);
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
    public Optional<Block> mine(long prevIndex, String data) {
        NonceGenerator nonceGenerator = (seed -> seed + 1);
        return mine(prevIndex, data, nonceGenerator, 0);
    }

    @Override
    public Optional<Block> mine(long prevIndex, String data, NonceGenerator nonceGenerator, long seed) {
        System.out.println("Mining with input[" + prevIndex + ", " + data + ", " + seed);
        long nonce = seed;
        String hash;
        Block prevBlock;
        //hash function = prevNonce + (prevIndex * 7 - currentIndex * 7) - currentNonce * 2
        //check if block is initial node
        if(prevIndex < 0) {
            //create dummy prevBlock
            prevBlock = DUMMY_BLOCK;
        }
        else {
            Optional<Block> prevBlockOptional = get(prevIndex);
            if(prevBlockOptional.isEmpty()) {
                return Optional.empty();
            }
            prevBlock = prevBlockOptional.get();
        }
        long currentIndex = prevIndex + 1;
        LOG.append(currentIndex, "Mining with input[Prev Index = " + prevIndex + ", Data = " + data + ", Initial nonce = " + seed + "]\n");
        while(!VALID_HASH.test(hash = getHash(prevBlock.getNonce(), prevIndex, prevBlock.getHash(), nonce, currentIndex, data)))
            nonce = nonceGenerator.get(nonce);
        LOG.append(currentIndex, "Found valid hash at nonce = " + nonce + "\n");
        Block block = new Block(currentIndex, nonce, prevBlock.getHash(), hash, true, data);
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
        Optional<Block> prevBlockOptional = get(block.getIndex() - 1);
        if(prevBlockOptional.isEmpty()) return false;
        Block prevBlock = prevBlockOptional.get();
        String currHash = getHash(prevBlock.getNonce(), prevBlock.getIndex(), prevBlock.getHash(), block.getNonce(), block.getIndex(), block.getData());
        return currHash.equalsIgnoreCase(block.getHash());
    }

    @Override
    public void validateChain(long index) {
        Optional<Block> optBlock = get(index);
        if(optBlock.isPresent()) {
            Block currBlock = optBlock.get();
            Optional<Block> prevBlockOptional = get(index - 1);
            Block prevBlock;
            if(prevBlockOptional.isEmpty()) prevBlock = DUMMY_BLOCK;
            prevBlock = prevBlockOptional.get();
            String newHash = getHash(prevBlock.getNonce(), prevBlock.getIndex(), prevBlock.getHash(), currBlock.getNonce(), currBlock.getIndex(), currBlock.getData());
            CHAIN_HASH.remove(currBlock.getHash());
            CHAIN_HASH.add(newHash);
            currBlock.setPrevHash(prevBlock.getHash());
            currBlock.setHash(newHash);
            currBlock.setValid(isValid(currBlock));
            CHAIN_INDEX.put(index, currBlock);
            validateChain(index + 1);
        }
    }

    @Override
    public Optional<Block> get(long index) {
        if(CHAIN_INDEX.containsKey(index))
            return Optional.of(CHAIN_INDEX.get(index));
        return Optional.empty();
    }

    @Override
    public List<Block> getAll() {
        List<Block> list = new ArrayList<>();
        for (long index: CHAIN_INDEX.keySet()) {
            list.add(CHAIN_INDEX.get(index));
        }
        return list;
    }

    @Override
    public boolean setData(long index, String data) {
        Optional<Block> blockOptional = get(index);
        if(blockOptional.isEmpty()) return false;
        Block block = blockOptional.get();
        block.setData(data);
        //calculate new hash
        Block prevBlock;
        if(index <= 0)
            prevBlock = DUMMY_BLOCK;
        else {
            Optional<Block> prevBlockOptional = get(block.getIndex() - 1);
            if(prevBlockOptional.isEmpty()) return false;
            prevBlock = prevBlockOptional.get();
        }
        String newHash = getHash(prevBlock.getNonce(), prevBlock.getIndex(), prevBlock.getHash(), block.getNonce(), block.getIndex(), data);
        CHAIN_HASH.remove(block.getHash());
        CHAIN_HASH.add(newHash);
        block.setHash(newHash);
        block.setValid(isValid(block));
        CHAIN_INDEX.put(index, block);
        validateChain(index + 1);
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
