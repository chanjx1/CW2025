package com.comp2042.model.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Implements the "7-Bag" Random Generator system.
 * <p>
 * This strategy guarantees that the player receives a sequence containing exactly
 * one of every tetromino type (I, J, L, O, S, T, Z) in a random order before the set repeats.
 * This prevents long droughts of specific pieces (e.g., waiting forever for an I-piece).
 * </p>
 */
public class Bag7BrickGenerator implements BrickGenerator {

    private final Deque<Brick> brickQueue = new ArrayDeque<>();

    public Bag7BrickGenerator() {
        // Fill the queue initially with 2 bags so we always have a "next" piece ready
        refillBag();
        refillBag();
    }

    private void refillBag() {
        List<Brick> newBag = new ArrayList<>();
        newBag.add(new IBrick());
        newBag.add(new JBrick());
        newBag.add(new LBrick());
        newBag.add(new OBrick());
        newBag.add(new SBrick());
        newBag.add(new TBrick());
        newBag.add(new ZBrick());

        // Randomize the order of these 7 bricks
        Collections.shuffle(newBag);

        brickQueue.addAll(newBag);
    }

    @Override
    public Brick getBrick() {
        // If we are running low (fewer than 7 pieces), add another bag to the end
        if (brickQueue.size() <= 7) {
            refillBag();
        }
        return brickQueue.poll();
    }

    @Override
    public Brick getNextBrick() {
        // Peek at the next piece without removing it
        return brickQueue.peek();
    }
}