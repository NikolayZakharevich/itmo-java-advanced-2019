package io;

import java.io.*;

/**
 * Stream block copy example.
 *
 * @author Georgiy Korneev
 */
public class BlockCopy {
    /**
     * Performs a block copy of the input stream to the output stream.
     *
     * @param is input stream to copy.
     * @param os output stream to copy to.
     *
     * @throws IOException if an I/O error occurred.
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] b = new byte[1024];
        int c = 0;
        while ((c = is.read(b)) >= 0) {
            os.write(b, 0, c);
        }
    }

    /**
     * Copies a file specified by the first argument to the file specified by
     * the second argument.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        try {
            InputStream is = new FileInputStream(args[0]);
            try {
                OutputStream os = new FileOutputStream(args[1]);
                try {
                    copy(is, os);
                } finally {
                    os.close();
                }
            } finally {
                is.close();
            }

            System.out.println("OK");
        } catch (IOException e) {
            System.err.println("An I/O error occurred:" + e.getMessage());
        }
    }
}
