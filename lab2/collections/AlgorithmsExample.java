package collections;

import java.io.*;
import java.util.*;

/**
 * Example for {@link Collections} class.
 */
public class AlgorithmsExample {
    /*
     * Reads words from the file specified by the first command line argument 
     * and writes it to the console in the reverse order.
     */
    public static void main(final String... args) {
        try {
            final List<String> list = new ArrayList<>();
            final CollectionExample c = new CollectionExample(list);
            c.read(args[0]);

            System.out.println("\nNormal order:");
            c.dump();

            Collections.reverse(list);
            System.out.println("\nReversed order:");
            c.dump();

            Collections.shuffle(list);
            System.out.println("\nRandom order:");
            c.dump();

            Collections.sort(list);
            System.out.println("\nSorted order:");
            c.dump();

            list.sort(String.CASE_INSENSITIVE_ORDER);
            System.out.println("\nSorted order (case insensitive):");
            c.dump();

            Collections.fill(list, "temp");
            System.out.println("\nFilled:");
            c.dump();

            System.out.println("\nMin value: " + Collections.min(list));
            System.out.println("\nMin value (case insensitive): " + Collections.min(list, String.CASE_INSENSITIVE_ORDER));
        } catch (final IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        }
    }
}