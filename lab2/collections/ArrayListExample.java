package collections;

import java.io.*;
import java.util.*;

/**
 * Example for {@link List} interface and {@link ArrayList} class.
 *
 * @see LinkedListExample
 * @see CollectionExample
 */
public class ArrayListExample {
    /*
     * Reads words from the file specified by the first command line argument 
     * and writes it to the console in the reverse order.
     */
    public static void main(final String... args) {
        try {
            final List<String> list = new ArrayList<>();
            final CollectionExample c = new CollectionExample(list);
            c.read(args[0]);

            for (int i = list.size() - 1; i >= 0; i--) {
                System.out.println(list.get(i));
            }
        } catch (final IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        }
    }
}