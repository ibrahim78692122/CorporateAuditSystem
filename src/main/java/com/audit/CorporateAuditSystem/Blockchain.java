
package com.audit.CorporateAuditSystem;

import java.util.ArrayList;

public class Blockchain {

    private ArrayList<Block> chain;

    public Blockchain() {
        chain = new ArrayList<>();
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        return new Block(
                0,
                "Genesis Block",
                "0"
        );
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addBlock(String data) {

        Block latestBlock = getLatestBlock();

        Block newBlock = new Block(
                latestBlock.getIndex() + 1,
                data,
                latestBlock.getHash()
        );

        chain.add(newBlock);
    }

    public void addAccountingEntry(AccountingEntry entry) {
        addBlock(entry.toString());
    }

    public boolean isChainValid() {

        for (int i = 1; i < chain.size(); i++) {

            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Current block hash check
            if (!currentBlock.getHash()
                    .equals(currentBlock.calculateHash())) {

                System.out.println();
                System.out.println("=================================");
                System.out.println("       TAMPERING DETECTED 🚨");
                System.out.println("=================================");

                System.out.println(
                        "Block Index: " + currentBlock.getIndex()
                );

                System.out.println(
                        "Data: " + currentBlock.getData()
                );

                System.out.println(
                        "Stored Hash: " + currentBlock.getHash()
                );

                System.out.println(
                        "Calculated Hash: "
                                + currentBlock.calculateHash()
                );

                System.out.println(
                        "Blockchain Status: INVALID ❌"
                );

                return false;
            }

            // Previous hash check
            if (!currentBlock.getPreviousHash()
                    .equals(previousBlock.getHash())) {

                System.out.println();
                System.out.println("=================================");
                System.out.println("       TAMPERING DETECTED 🚨");
                System.out.println("=================================");

                System.out.println(
                        "Block Index: " + currentBlock.getIndex()
                );

                System.out.println(
                        "Reason: Previous hash mismatch."
                );

                System.out.println(
                        "Blockchain Status: INVALID ❌"
                );

                return false;
            }
        }

        return true;
    }

    public void printBlockchain() {

        System.out.println();
        System.out.println("=================================");
        System.out.println("          BLOCKCHAIN");
        System.out.println("=================================");

        for (Block block : chain) {

            System.out.println("----------------------------");

            System.out.println(
                    "Block Index: " + block.getIndex()
            );

            System.out.println(
                    "Data: " + block.getData()
            );

            System.out.println(
                    "Previous Hash: "
                            + block.getPreviousHash()
            );

            System.out.println(
                    "Current Hash: "
                            + block.getHash()
            );
        }

        System.out.println("----------------------------");
    }

    public ArrayList<Block> getChain() {
        return chain;
    }

    // Database ke saath blockchain synchronize karne ke liye
    public void rebuildFromDatabase() {

        chain.clear();

        chain.add(createGenesisBlock());

        ArrayList<AccountingEntry> entries =
                DatabaseConnection.getAllAccountingEntries();

        for (AccountingEntry entry : entries) {
            addAccountingEntry(entry);
        }

        System.out.println(
                "Blockchain Rebuilt From Database ✅"
        );
    }
}