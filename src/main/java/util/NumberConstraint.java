package util;

import java.util.Random;

/**
 * Defines basic constraints on a randomly generatable number.
 */
public class NumberConstraint {
    public NumberConstraint() {
        mod = 1;
        eqClass = 0;

        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
    }
    public NumberConstraint(int min, int max, int mod, int eqClass) {
        this.min = min;
        this.max = max;
        this.mod = mod;
        this.eqClass = eqClass;
    }

    /**
     * Generates a number following all constraints that fits within the given range
     */
    public int      generate(int min, int max) {
        int smallestMin = (int)(Math.ceil(Math.max(min, this.min)/mod) * mod);
        int largestMax = (int)(Math.floor(Math.min(max, this.max)-eqClass)/mod) * mod;

        return smallestMin + _random.nextInt((largestMax - smallestMin)/ mod + 1) * mod + eqClass;
    }

    /**
     * The modulus on the constraint
     */
    public final int    mod;

    /**
     * The equivalence class in the modulus of the constraint
     */
    public final int    eqClass;

    /**
     * The minimum value this constraint can produce
     */
    public final int    min;

    /**
     * And the maximum value that can be produces
     */
    public final int    max;

    private static Random _random = new Random();
}
