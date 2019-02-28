package collections;

import java.io.*;
import java.util.*;

/**
 * Example for {@link List} interface and {@link LinkedList} class.
 *
 * @see ArrayListExample
 * @see CollectionExample
 */
public class LinkedListExample {
    /**
     * Reads word from the file specified by the first command line argument 
     * and writes it to the console in the reverse order.
     */
    public static void main(final String... args) {
        try {
            final List<String> list = new LinkedList<>();
            final CollectionExample c = new CollectionExample(list);
            c.read(args[0]);

            for (final ListIterator li = list.listIterator(list.size()); li.hasPrevious(); ) {
                System.out.println(li.previous());
            }
        } catch (final IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        }
    }
}