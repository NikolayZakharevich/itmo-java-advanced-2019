package collections;

import java.io.*;
import java.util.*;

/**
 * Example for {@link Set} interface and {@link TreeSet} class.
 *
 * @see HashSetExample
 * @see LinkedHashSetExample
 * @see CollectionExample
 */
public class TreeSetComparatorExample {
    /**
     * Filters duplicate words from the file specified by the first command 
     * line argument and writes result to the console.
     */
    public static void main(final String... args) {
        try {
            final CollectionExample c = new CollectionExample(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));

            final int words = c.read(args[0]);
            System.out.println("Words total: " + words);
            System.out.println("Unique words: " + c.getCollection().size());

            c.dump();
        } catch (final IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        }
    }
}