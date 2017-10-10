package util;

import java.util.Random;

public class NumberGenerator {
    public NumberGenerator  lessThanOrEqualTo(int max) {
        _max = Math.min(_max, max);
        return this;
    }
    public NumberGenerator  greaterThanOrEqualTo(int min) {
        _min = Math.max(_min, min);
        return this;
    }
    public NumberGenerator  divisibleBy(int lcm) {
        _lcm *= lcm;
        return this;
    }

    public int              generate() {
        return _min + _random.nextInt((_max - _min)/_lcm) * _lcm;
    }

    private int _max = 100;
    private int _min = 0;
    private int _lcm = 1;

    private Random _random = new Random();
}
