package collections;

import java.io.*;
import java.util.*;

/**
 * Example for the {@link Collection} interfaces.
 * <p>
 * Wraps a collection and performs simple i/o.
 */
public class CollectionExample {
    /** Wrapped collection. */
    private final Collection<String> c;

    /**
     * Creates a new collection wrapper.
     *
     * @param c collection to wrap.
     */
    public CollectionExample(final Collection<String> c) {
        this.c = c;
    }


    /**
     * Reads specified file word-by-word into the collection.
     *
     * @param file file to read.
     *
     * @return number of words read.
     *
     * @throws IOException if an I/O error occurred.
     */
    public int read(final String file) throws IOException {
        final Scanner scanner = new Scanner(new File(file), "Cp1251");

        int read = 0;
        while (scanner.hasNext()) {
            read++;
            c.add(scanner.next());
        }

        return read;
    }

    /**
     * Dumps collection to the console.
     */
    public void dump() {
        for (final String word : c) {
            System.out.print(word + " ");
        }
        System.out.println();
    }

    /**
     * Returns wrapped collection.
     *
     * @return wrapped collection.
     */
    public Collection getCollection() {
        return c;
    }

    /**
     * Reads words from the file specified by the first command line argument 
     * and writes it to the console.
     */
    public static void main(final String[] args) {
        try {
            final CollectionExample c = new CollectionExample(new ArrayList<>());
            c.read(args[0]);
            c.dump();
        } catch (final IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        }
    }
}
