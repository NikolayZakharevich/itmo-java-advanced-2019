package collections;

import java.io.*;
import java.util.*;

/**
 * Example for {@link Map} interface.
 * <p>
 * Wraps a {@link Map} instance and performs a simple i/o.
 *
 * @see HashMapExample
 * @see LinkedHashMapExample
 */
public class MapExample {
    /** Wrapped map. */
    private final Map<String, Integer> map;

    /**
     * Creates a new map wrapper.
     *
     * @param map map to wrap.
     */
    public MapExample(final Map<String, Integer> map) {
        this.map = map;
    }


    /**
     * Count number of words in the specified file.
     *
     * @param file file to read.
     *
     * @throws IOException if an I/O error occurred.
     */
    public void read(final String file) throws IOException {
        final Scanner scanner = new Scanner(new File(file), "Cp1251");

        while (scanner.hasNext()) {
            final String word = scanner.next();
            final Integer count = map.get(word);
            map.put(word, (count == null ? 0 : count) + 1);
        }
    }

    /**
     * Dumps map to the console.
     */
    public void dump() {
        for (final Map.Entry entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    /**
     * Returns wrapped map.
     *
     * @return wrapped map.
     */
    public Map getMap() {
        return map;
    }
}