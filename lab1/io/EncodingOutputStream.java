package io;

import java.io.*;

/**
 * Output stream with simple encoding capability.
 * Each by is xor'ed with secret key.
 *
 * @author Georgiy Korneev
 */
public class EncodingOutputStream extends FilterOutputStream {
    /** Secret key. */
    private final int key;

    /**
     * Creates a new <tt>EncodingOutputStream<tt>.
     *
     * @param os underlying output stream.
     * @param key secret key.
     */
    public EncodingOutputStream(OutputStream os, int key) {
        super(os);
        this.key = key;
    }

    public void write(int b) throws IOException {
        super.write(b ^ key);
    }

    /**
     * Encodes a file specified by the first argument by key '<tt>a</tt>'
     * and writes result to the file specified by the second argument.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        try {
            InputStream is = new FileInputStream(args[0]);
            try {
                OutputStream os = new EncodingOutputStream(new FileOutputStream(args[1]), 'a');
                try {
                    BlockCopy.copy(is, os);
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
