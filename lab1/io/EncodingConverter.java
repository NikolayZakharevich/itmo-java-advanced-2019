package io;

import java.io.*;

/**
 * Converts <tt>input.txt</tt> from <em>Cp1251</em> to <em>Cp866</em>
 * encoding and writes result to the <tt>output.txt</tt> file.
 * <p>
 * <b>Warning: This code violates exception handling principles.</b>
 * For correct version see {@link FullEncodingConverter.java}.
 *
 * @author Georgiy Korneev
 */
public class EncodingConverter {
    /**
     * Converts <tt>input.txt</tt> from <em>Cp1251</em> to <em>Cp866</em>
     * encoding and writes result to the <tt>output.txt</tt> file.
     * <p>
     * <em>Warning: This code violates exception handling principles.</em>
     *
     * @param args ignored.
     *
     * @throws IOException if an I/O error uccurred.
     */
    public static void main(String[] args) throws IOException {
         Reader reader =
            new InputStreamReader(new FileInputStream("input.txt"), "Cp1251");
         Writer writer =
            new OutputStreamWriter(new FileOutputStream("output.txt"), "Cp866");
         int c = 0;
         while ((c = reader.read()) >= 0) writer.write(c);
         reader.close();
         writer.close();
    }
}
