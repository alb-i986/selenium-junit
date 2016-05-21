package me.alb_i986.junit;

/**
 * An exception which is expected to be thrown.
 * Useful in unit tests: it can be easily targeted in a catch block.
 */
public class ExpectedException extends RuntimeException {
    public ExpectedException(String message) {
        super(message);
    }
}
