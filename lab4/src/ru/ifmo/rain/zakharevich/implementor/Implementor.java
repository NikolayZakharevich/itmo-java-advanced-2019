package ru.ifmo.rain.zakharevich.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Implementor implements Impler {

    private static final String CLASS_NAME_SUFFIX = "Impl";
    private static final String FILE_NAME_SUFFIX = CLASS_NAME_SUFFIX + ".java";

    private static final String NEW_LINE = System.lineSeparator();
    private static final String SPACE = " ";
    private static final String TAB = "    ";
    private static final String COMMA = ",";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String OPEN_CURLY_BRACKET = "{";
    private static final String CLOSE_CURLY_BRACKET = "}";
    private static final String SEMICOLON = ";";

    private static final String RETURN_WORD = "return";
    private static final String CLASS_WORD = "class";
    private static final String IMPORT_WORD = "import";
    private static final String IMPLEMENTS_WORD = "implements";
    private static final String EXTENDS_WORD = "extends";
    private static final String OVERRIDE_WORD = "@Override";

    private static final String DEFAULT_BOOLEAN_VALUE = "false";
    private static final String DEFAULT_PRIMITIVE_VALUE = "0";
    private static final String DEFAULT_OBJECT_VALUE = "null";
    private static final String DEFAULT_VOID_VALUE = "";


    public static void main(String[] args) {

        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Usage: java Implementor <class name> <path to root>");
            return;
        }

        try {
            Implementor implementor = new Implementor();
            implementor.implement(Class.forName(args[0]), Path.of(args[1]));
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid class name: " + e.getMessage());
        } catch (ImplerException e) {
            System.err.println("Error occurred with class implementation: " + e.getMessage());
        }

    }

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {

        if (token == null || root == null) {
            throw new ImplerException("Invalid arguments: " + ((token == null) ? "class definition " : "root path ") + "is null");
        }

        if (token.isPrimitive() || token == Enum.class || token.isArray()
                || Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Invalid token was passed");
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(getFileName(token, root).toFile()), StandardCharsets.UTF_8))) {

            generatePackages(token, writer);
            generateClassDeclaration(token, writer);
            generateConstructors(token, writer);
            generateMethods(token, writer);

        } catch (UnsupportedEncodingException e) {
            System.err.println("Unsupported encoding UTF-8: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("Output file not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An I/O error occurred with output file: " + e.getMessage());
        }
    }

    private void generatePackages(Class<?> token, BufferedWriter writer) throws IOException {
        if (!token.getPackage().getName().isEmpty()) {
            writer.write(token.getPackage().toString()
                    + SEMICOLON
                    + NEW_LINE
                    + NEW_LINE);
        }
    }

    private void generateConstructors(Class<?> token, BufferedWriter writer) throws IOException {
        Constructor[] constructors = token.getConstructors();

        for (var constructor : constructors) {
            writer.write(Modifier.toString(constructor.getModifiers())
                    + SPACE
                    + constructor.getName()
                    + OPEN_BRACKET);

            Parameter[] parameters = constructor.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];

                if (i > 0) {
                    writer.write(COMMA + SPACE);
                }
                writer.write(parameter.getType().getSimpleName() + SPACE + parameter.getName());
            }
            writer.write(CLOSE_BRACKET + SPACE);
        }
        writer.write(OPEN_CURLY_BRACKET  + NEW_LINE);
    }

    private Path getFileName(Class<?> token, Path folder) throws IOException {
        if (token.getPackage() != null) {
            folder = folder.resolve(token.getPackage().getName().replace('.', File.separatorChar));
        }
        folder = Files.createDirectories(folder);
        return folder.resolve(token.getSimpleName() + FILE_NAME_SUFFIX);
    }

    private void generateClassDeclaration(Class<?> token, BufferedWriter writer) throws IOException {

        writer.write(getClassModifiers(token)
                + SPACE
                + CLASS_WORD
                + SPACE
                + token.getSimpleName() + CLASS_NAME_SUFFIX
                + SPACE
                + (token.isInterface() ? IMPLEMENTS_WORD : EXTENDS_WORD)
                + SPACE
                + token.getSimpleName()
                + SPACE
                + OPEN_CURLY_BRACKET
                + NEW_LINE
                + NEW_LINE);
    }

    private void generateMethods(Class<?> token, BufferedWriter writer) throws IOException {

        Method[] methods = token.getMethods();
        for (Method method : methods) {

            int modifiers = method.getModifiers();

            if (method.isDefault() || (modifiers & Modifier.ABSTRACT) == 0) {
                continue;
            }

            if ((modifiers & Modifier.STATIC) == 0) {
                writer.write(OVERRIDE_WORD + NEW_LINE);
            }

            generateMethodAnnotations(method, writer);
            generateMethodDeclaration(method, writer);
            writer.write(OPEN_CURLY_BRACKET + NEW_LINE);
            generateMethodImplementation(method, writer);
            writer.write(CLOSE_CURLY_BRACKET + NEW_LINE + NEW_LINE);
        }

        writer.write(NEW_LINE
                + CLOSE_CURLY_BRACKET);
    }

    private void generateMethodAnnotations(Method method, BufferedWriter writer) throws IOException {

        Annotation[] annotations = method.getDeclaredAnnotations();
        for (var annotation : annotations) {
            writer.write(annotation.toString() + NEW_LINE);
        }

    }

    private void generateMethodDeclaration(Method method, BufferedWriter writer) throws IOException {

        writer.write(getMethodModifiers(method)
                + SPACE
                + method.getReturnType().getCanonicalName()
                + SPACE
                + method.getName()
                + OPEN_BRACKET);

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (i > 0) {
                writer.write(COMMA + SPACE);
            }
            writer.write(parameter.getType().getSimpleName() + SPACE + parameter.getName());
        }
        writer.write(CLOSE_BRACKET + SPACE);
    }

    private void generateMethodImplementation(Method method, BufferedWriter writer) throws IOException {

        String returnValue = getDefaultValue(method.getReturnType());

        writer.write(TAB
                + RETURN_WORD
                + (!returnValue.isEmpty() ? SPACE : "")
                + returnValue
                + SEMICOLON
                + NEW_LINE);
    }

    private String getClassModifiers(Class<?> token) {
        return Modifier.toString(token.getModifiers() & ~Modifier.INTERFACE & ~Modifier.ABSTRACT);
    }

    private String getMethodModifiers(Method method) {
        return Modifier.toString(method.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT);
    }

    private String getDefaultValue(Class<?> token) {
        if (token.equals(boolean.class)) {
            return DEFAULT_BOOLEAN_VALUE;
        }
        if (token.equals(void.class)) {
            return DEFAULT_VOID_VALUE;
        }
        if (token.isPrimitive()) {
            return DEFAULT_PRIMITIVE_VALUE;
        }
        return DEFAULT_OBJECT_VALUE;
    }

}
