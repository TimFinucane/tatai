package tatai.model.question;

import util.NumberConstraint;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This represents a variety of mathematical operations, all of which accept two arguments
 */
public enum Operator {
    ADD         ("+",       1) {
        @Override
        int                 apply(int left, int right) {
            return left + right;
        }

        @Override
        NumberConstraint    chooseLeft(NumberConstraint original, int right) {
            return original.applyConstraint(original.mod, original.eqClass - right);
        }
    },
    SUBTRACT    ("\u2212",  1) {
        @Override
        int                 apply(int left, int right) {
            return left - right;
        }

        @Override
        NumberConstraint    chooseLeft(NumberConstraint original, int right) {
            return original.applyConstraint(original.mod, original.eqClass + right);
        }
    },
    MULTIPLY    ("\u00D7",  2) {
        @Override
        int                 apply(int left, int right) {
            return left * right;
        }

        @Override
        NumberConstraint    chooseRight(NumberConstraint original) {
            // Choose two numbers that form the Eq. Find two roots of the Eq
            // TODO: Find solution which accesses more options. Currently using roots: Eq and 1
            return original;
        }

        @Override
        NumberConstraint    chooseLeft(NumberConstraint original, int right) {
            // As right side has Eq of original Eq, this has to have Eq of 1
            return original.applyConstraint(original.mod, 1);
        }
    },
    DIVIDE      ("\u00F7",  2) {
        @Override
        int                 apply(int left, int right) {
            return left / right;
        }

        // TODO: ChooseRight that ensures small enough number?

        @Override
        NumberConstraint    chooseLeft(NumberConstraint original, int right) {
            // Both are multiplied as the number will be divided to produce the requested equivalence class
            return original.applyConstraint(original.mod * right, original.eqClass * right);
        }
    };

    Operator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    /**
     * Creates an array containing all operators that have been found in the symbols string
     */
    @Nonnull
    public static Operator[]            createOperators(String symbols) {
        ArrayList<Operator> operators = new ArrayList<>();
        for(Operator value : Operator.values()) {
            if(symbols.contains(value.symbol)) {
                operators.add(value);
            }
        }
        return operators.toArray(new Operator[0]);
    }

    /**
     * Returns the list of operator groups in DESCENDING order of precedence
     */
    @Nonnull
    static Collection<List<Operator>>   getOperatorPrecedences() {
        TreeMap<Integer, List<Operator>> map = new TreeMap<>(Collections.reverseOrder());
        for(Operator value : Operator.values()) {
            if(map.containsKey(value.precedence))
                map.get(value.precedence).add(value);
            else
                map.put(value.precedence, new ArrayList<>(Arrays.asList(value)));
        }

        return map.values();
    }

    /**
     * Apply the operation on the two numbers
     */
    abstract int                        apply(int left, int right);

    /**
     * This is called first, to let the operator decide constraints on the right hand side
     */
    NumberConstraint                    chooseRight(NumberConstraint original) {
        return new NumberConstraint(); // No constraint
    }

    /**
     * This then lets the operator decide constraints on the left hand side based on the right
     */
    abstract NumberConstraint           chooseLeft(NumberConstraint original, int right);

    public final String symbol;
    public final int    precedence;
}