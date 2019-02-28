package io;

import java.io.*;

/**
 * Copies <tt>input.txt</tt> to <tt>output.txt</tt> capitalizing all letters.
 * <p>
 * <em>Warning: This code violates exception handling principles.</em>
 *
 * @author Georgiy Korneev
 */
public class ToUpperCase {
    /**
     * Copies <tt>input.txt</tt> to <tt>output.txt</tt> capitalizing all letters.
     * <p>
     * <em>Warning: This code violates exception handling principles.</em>
     *
     * @param args ignored.
     *
     * @throws IOException if an I/O error uccurred.
     */
    public static void main(String[] args) throws IOException {
        Reader reader = new FileReader("input.txt");
        Writer writer = new FileWriter("output.txt");
        int c = 0;
        while ((c = reader.read()) >= 0) {
            writer.write(Character.toUpperCase((char) c));
        }
        reader.close();
        writer.close();
    }
}