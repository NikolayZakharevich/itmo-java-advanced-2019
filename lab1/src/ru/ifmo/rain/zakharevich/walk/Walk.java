package ru.ifmo.rain.zakharevich.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Walk {

    private final static int FNV_32_PRIME = 16777619;
    private final static int FNV_32_INITIAL_VALUE = (int) 2166136261L;
    private final static int FNV_32_BIT_MASK = 255;

    private final static int INPUT_BUFFER_BYTE_SIZE = 1024;

    private final static String BAD_FILE_HASH = "00000000";

    private static int findHash(int hashValue, byte[] buf, int length) {
        for (int i = 0; i < length; ++i) {
            hashValue *= FNV_32_PRIME;
            hashValue ^= buf[i] & FNV_32_BIT_MASK;
        }
        return hashValue;
    }

    private static String findFileHash(String fileName) {

        try (InputStream is = new FileInputStream(fileName)) {
            byte[] buffer = new byte[INPUT_BUFFER_BYTE_SIZE];
            int currentHash = FNV_32_INITIAL_VALUE;
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                currentHash = findHash(currentHash, buffer, length);
            }
            return String.format("%08x", currentHash);
        } catch (IOException e) {
            return BAD_FILE_HASH;
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Usage: java Walk <input file> <output file>");
            return;
        }

        File outputFile = new File(args[1]);
        File outputFileDirectory = outputFile.getParentFile();
        if (outputFileDirectory != null) {
            if (!outputFileDirectory.mkdirs()) {
                System.err.println("Unable to create file " + args[1]);
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]),
                StandardCharsets.UTF_8))) {

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),
                    StandardCharsets.UTF_8))) {
                String filename = null;
                while ((filename = reader.readLine()) != null) {
                    writer.write(findFileHash(filename) + " " + filename + "\n");
                }
            } catch (UnsupportedEncodingException e) {
                System.err.println("Unsupported encoding UTF-8 when using output file: " + e.getMessage());
            } catch (FileNotFoundException e) {
                System.err.println("Output file not found: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("An I/O error occurred with output file: " + e.getMessage());
            }

        } catch (UnsupportedEncodingException e) {
            System.err.println("Unsupported encoding UTF-8 when using input file: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An I/O error occurred with input file: " + e.getMessage());
        }


    }
}
