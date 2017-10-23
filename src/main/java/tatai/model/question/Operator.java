package tatai.model.question;

import util.NumberConstraint;

import java.util.*;

/**
 * This represents a variety of mathematical operations, all of which accept two arguments
 */
public abstract class Operator {
    static class Add extends Operator {
        int                 precedence() { return 1; }

        int                 apply(int left, int right) {
            return left + right;
        }
        NumberConstraint    chooseLeft(NumberConstraint original, int right) {
            int eq = Math.floorMod(original.eqClass() - right, original.mod());

            return new NumberConstraint(original.mod(), eq);
        }
    }
    static class Subtract extends Operator {
        int                 precedence() { return 1; }

        int                 apply(int left, int right) {
            return left - right;
        }
        NumberConstraint    chooseLeft(NumberConstraint original, int right) {
            int eq = Math.floorMod(original.eqClass() + right, original.mod());

            return new NumberConstraint(original.mod(), eq);
        }
    }
    static class Multiply extends Operator {
        int                 precedence() { return 2; }

        int                 apply(int left, int right) {
            return left * right;
        }

        NumberConstraint    chooseRight(NumberConstraint original) {
            // Choose two numbers that form the Eq. Find two roots of the Eq
            // TODO: Find solution which accesses more options. Currently using roots: Eq and 1
            return original;
        }
        NumberConstraint    chooseLeft(NumberConstraint original, int right) {
            // As right side has Eq of original Eq, this has to have Eq of 1
            return new NumberConstraint(original.mod(), 1);
        }
    }
    static class Divide extends Operator {
        int                 precedence() { return 2; }

        int                 apply(int left, int right) {
            return left / right;
        }

        // TODO: ChooseRight that ensures small enough number?

        NumberConstraint    chooseLeft(NumberConstraint original, int right) {
            // Both are multiplied as the number will be divided to produce the requested equivalence class
            return new NumberConstraint(original.mod() * right, original.eqClass() * right);
        }
    }

    /**
     * The main way of accessing the operators. Has extra info as well
     */
    public enum Type {
        ADD("+", 1, Add.class),
        SUBTRACT("\u2212", 1, Subtract.class),
        MULTIPLY("\u00D7", 2, Multiply.class),
        DIVIDE("\u00F7", 2, Divide.class);

        Type(String symbol, int precedence, Class<? extends Operator> cl) {
            _symbol = symbol;
            _precedence = precedence;
            _class = cl;
        }

        /**
         * Creates an array containing all operators that have been found in the symbols string
         */
        public static Operator[]        createOperators(String symbols) {
            ArrayList<Operator> operators = new ArrayList<>();
            for(Type value : Type.values()) {
                if(symbols.contains(value._symbol)) {
                    operators.add(value.create());
                }
            }
            return operators.toArray(new Operator[0]);
        }
        static String                   getSymbol(Class<? extends Operator> cl) {
            for(Type value : Type.values()) {
                if(value._class == cl)
                    return value._symbol;
            }
            throw new RuntimeException("Operator does not exist in Operator.Type: " + cl.getName());
        }
        static int                      getPrecedence(Class<? extends Operator> cl) {
            for(Type value : Type.values()) {
                if(value._class == cl)
                    return value._precedence;
            }
            throw new RuntimeException("Operator does not exist in Operator.Type: " + cl.getName());
        }

        // Returns the list of operator groups in DESCENDING order of precedence
        static Collection<List<Type>>   getOperatorPrecedences() {
            TreeMap<Integer, List<Type>> map = new TreeMap<>(Collections.reverseOrder());
            for(Type value : Type.values()) {
                if(map.containsKey(value.precedence()))
                    map.get(value.precedence()).add(value);
                else
                    map.put(value.precedence(), new ArrayList<>(Arrays.asList(value)));
            }

            return map.values();
        }

        public String   symbol() {
            return _symbol;
        }
        public int      precedence() {
            return _precedence;
        }

        /**
         * Creates the class of the given type
         */
        public Operator create() {
            try {
                return _class.newInstance();
            } catch(Exception e) {
                throw new RuntimeException("Somehow we weren't able to construct an operator: " + e.getCause());
            }
        }

        private String                      _symbol;
        private int                         _precedence;
        private Class<? extends Operator>   _class;
    }

    public String               symbol() {
        // Sneaky sneaky
        return Type.getSymbol(this.getClass());
    }
    int                         precedence() {
        return Type.getPrecedence(this.getClass());
    }

    /**
     * Apply the operation on the two numbers
     */
    abstract int                apply(int left, int right);

    /**
     * This is called first, to let the operator decide constraints on the right hand side
     */
    NumberConstraint            chooseRight(NumberConstraint original) {
        return new NumberConstraint(); // No constraint
    }

    /**
     * This then lets the operator decide constraints on the left hand side based on the right
     */
    abstract NumberConstraint   chooseLeft(NumberConstraint original, int right);
}
