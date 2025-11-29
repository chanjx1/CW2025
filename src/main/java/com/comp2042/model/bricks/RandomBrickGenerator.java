package com.comp2042.model.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A standard random generator for Tetromino bricks.
 * <p>
 * This strategy selects bricks purely at random, without history or "bag" logic.
 * <b>Note:</b> This class is currently unused in favor of {@link Bag7BrickGenerator},
 * but is kept as an alternative strategy.
 * </p>
 */
public class RandomBrickGenerator implements BrickGenerator {

    /**
     * A list containing one instance of each available brick type.
     * Used as the source pool for random selection.
     */
    private final List<Brick> brickList;

    /**
     * A queue holding the upcoming bricks.
     * Ensures that the "Next Piece" preview is always populated.
     */
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /**
     * Initializes the random generator and pre-fills the queue with two random bricks.
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }

    /**
     * Returns the next brick from the queue and adds a new random one to the end.
     *
     * @return The next {@link Brick} to spawn.
     */
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll();
    }

    /**
     * Peeks at the next brick in the sequence without removing it.
     *
     * @return The {@link Brick} that will appear next.
     */
    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }
}
