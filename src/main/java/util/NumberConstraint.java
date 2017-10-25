package util;

import java.util.Random;

/**
 * Defines basic constraints on a randomly generatable number.
 */
public class NumberConstraint {
    public NumberConstraint() {
        this(1, 0);
    }
    public NumberConstraint(int mod, int eqClass) {
        this.mod = mod;
        this.eqClass = Math.floorMod(eqClass, mod);
    }

    /**
     * Generates a number following all constraints that fits within the given range
     */
    public int      generate(int min, int max) {
        int smallestMin = (int)(Math.ceil(min/mod) * mod);
        int largestMax = (int)(Math.floor(max-eqClass)/mod) * mod;

        return smallestMin + _random.nextInt((largestMax - smallestMin)/ mod + 1) * mod + eqClass;
    }

    public NumberConstraint     applyConstraint(int mod, int eqClass) {
        return new NumberConstraint(mod, eqClass);
    }

    /**
     * The modulus on the constraint
     */
    public final int    mod;

    /**
     * The equivalence class in the modulus of the constraint
     */
    public final int    eqClass;

    private static Random _random = new Random();
}
