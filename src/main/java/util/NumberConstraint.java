package util;

import java.util.Random;

/**
 * Defines basic constraints on a randomly generatable number.
 *
 */
public class NumberConstraint {
    public NumberConstraint() {
        this(1);
    }
    public NumberConstraint(int mod) {
        this(mod, 0);
    }
    public NumberConstraint(int mod, int eqClass) {
        assert(eqClass < mod);

        this._mod = mod;
        this._eqClass = eqClass % mod;
    }

    public int      generate(int min, int max) {
        int smallestMin = (int)(Math.ceil(min/_mod) * _mod);
        int largestMax = (int)(Math.floor(max-_eqClass)/_mod) * _mod;

        return smallestMin + _random.nextInt((largestMax - smallestMin)/ _mod + 1) * _mod + _eqClass;
    }

    public int      mod() {
        return _mod;
    }
    public int      eqClass() {
        return _eqClass;
    }

    private int  _mod = 1;
    private int  _eqClass = 0;

    private static Random _random = new Random();
}
