package collections;

import java.io.*;
import java.util.*;

/**
 * Example for {@link Map} interface and {@link LinkedHashMap} class.
 *
 * @see HashMapExample
 */
public class LinkedHashMapExample {
    /**
     * Count the number of occurrences of the words in the file specified
     * by the first command line argument.
     */
    public static void main(final String... args) {
        try {
            final MapExample map = new MapExample(new LinkedHashMap<>());

            map.read(args[0]);
            map.dump();
        } catch (final IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        }
    }
}