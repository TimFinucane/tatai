package util;

import java.util.Random;

/**
 * Defines basic constraints on a randomly generatable number.
 */
public class NumberConstraint {
    /**
     * Indicates that the current line of questioning cannot produce a result.
     * The field maybefixable indicates whether there *may* be a configuration of the question that does not produce
     * an exception.
     */
    public class ConstraintException extends Exception {
        public ConstraintException(NumberConstraint constraint, boolean maybefixable) {
            this.constraint = constraint;
            this.maybefixable = maybefixable;
        }

        public final NumberConstraint   constraint;
        public final boolean maybefixable;
    }

    public NumberConstraint(int min, int max) {
        this(min, max, 1, 0);
    }
    public NumberConstraint(int min, int max, int mod, int eqClass) {
        this.min = min;
        this.max = max;
        this.mod = mod;
        this.eqClass = eqClass % mod;
    }

    /**
     * Generates a number following all constraints that fits within the given range
     */
    public int      generate(int min, int max) throws ConstraintException {
        int smallestMin = (int)(Math.ceil(Math.max(min, this.min)/mod) * mod);
        int largestMax = (int)(Math.floor(Math.min(max, this.max)-eqClass)/mod) * mod;

        if(largestMax - smallestMin < 0) {
            throw new ConstraintException(this, true);
        }

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
