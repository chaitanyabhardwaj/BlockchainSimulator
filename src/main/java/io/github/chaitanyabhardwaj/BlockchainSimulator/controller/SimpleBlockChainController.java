package io.github.chaitanyabhardwaj.BlockchainSimulator.controller;

import io.github.chaitanyabhardwaj.BlockchainSimulator.model.Block;
import io.github.chaitanyabhardwaj.BlockchainSimulator.service.BlockChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class SimpleBlockChainController {

    private final BlockChain blockChain;

    public SimpleBlockChainController(BlockChain blockChain) {
        this.blockChain = blockChain;
        System.out.println("Initializing Blockchain");
        Block block = blockChain.init("This is my first block");
        if(block == null) {
            System.out.println("Blockchain init failed!!!");
        }
        else {
            System.out.println("Initial block created.");
            System.out.println(block.toString());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "It's working!";
    }

    @PostMapping("/mine")
    public ResponseEntity<Block> mineBlock(@RequestBody Map<String, String> json) {
        System.out.println("Mining started!");
        if(!json.containsKey("data"))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        String data = json.get("data");
        Optional<Block> optionalBlock = blockChain.mine(blockChain.getTop().getHash(), data);
        if(optionalBlock.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        Block block = optionalBlock.get();
        System.out.println("Mining completed! Below is the mined block.");
        System.out.println(block.toString());
        System.out.println("Adding block to the Blockchain.");
        blockChain.push(block);
        return new ResponseEntity<>(block, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Block>> getAll() {
        List<Block> list = blockChain.getAll();
        if (list != null && !list.isEmpty())
            return new ResponseEntity<>(list, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
