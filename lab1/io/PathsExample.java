// package io;

import java.io.*;
import java.nio.file.*;

/**
 * @author Georgiy Korneev
 */
public class PathsExample {
    /**
     * @param args ignored.
     *
     * @throws IOException if an I/O error uccurred.
     */
    public static void main(String[] args) throws IOException {
        final FileSystem fs = FileSystems.getDefault();
        System.out.println("Roots:");
        for (final Path root : fs.getRootDirectories()) {
            System.out.println("    " + root);
        }
        System.out.println("File stores:");
        for (final FileStore store : fs.getFileStores()) {
            System.out.println("    " + store + " " + store.type());
        }
    }
}