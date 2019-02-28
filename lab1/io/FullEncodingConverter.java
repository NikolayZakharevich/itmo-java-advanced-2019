package io;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Converts <tt>input.txt</tt> from <em>Cp1251</em> to <em>Cp866</em>
 * encoding and writes result to the <tt>output.txt</tt> file.
 *
 * @author Georgiy Korneev
 */
public class FullEncodingConverter {
    /**
     * Converts file to different encoding.
     *
     * @param inputFilename name of the file to convert.
     * @param inputCharset charset of the file to convert.
     * @param outputFilename name of the file to save result to.
     * @param outputCharset charset of the result file.
     *
     * @throws IOException if an I/O error occurred.
     */
    public static void convert(
        final String inputFilename,
        final Charset inputCharset,
        final String outputFilename,
        final Charset outputCharset
    ) throws IOException {
         final Reader reader = new InputStreamReader(
            new FileInputStream(inputFilename),
            inputCharset
         );
         try {
            final Writer writer = new OutputStreamWriter(
                new FileOutputStream(outputFilename),
                outputCharset
            );
            try {
               int c = 0;
               while ((c = reader.read()) >= 0) writer.write(c);
            } finally {
                writer.close();
            }
         } finally {
            reader.close();
         }
    }

    /**
     * Converts <tt>input.txt</tt> from <em>Cp1251</em> to <em>Cp866</em>
     * encoding and writes result to the <tt>output.txt</tt> file.
     *
     * @param args ignored.
     */
    public static void main(final String[] args) {
        try {
            convert(
                "input.txt",
                Charset.forName("Cp1251"),
                "output.txt",
                Charset.forName("Cp866")
            );
        } catch (IOException e) {
            System.err.println("An I/O error occurred:" + e.getMessage());
        }
    }
}
