package ru.ifmo.rain.zakharevich.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Generates implementation of classes
 *
 * @author Nikolay Zakharevich (nikolayzakharevich@gmail.com)
 */
public class Implementor implements Impler, JarImpler {

    /**
     * Suffix for generated file
     */
    private static final String CLASS_NAME_SUFFIX = "Impl";

    /**
     * Java file extension
     */
    private static final String JAVA_EXTENSION = ".java";

    /**
     * Class file extension
     */
    private static final String CLASS_EXTENSION = ".class";

    /**
     * Jar option for console usage
     */
    private static final String JAR_OPTION = "-jar";

    /**
     * System dependent new line symbol for writer
     */
    private static final String NEW_LINE = System.lineSeparator();

    /**
     * Space symbol
     */
    private static final String SPACE = " ";

    /**
     * Tab symbol
     */
    private static final String TAB = "    ";

    /**
     * Comma symbol
     */
    private static final String COMMA = ",";

    /**
     * Left bracket symbol
     */
    private static final String OPEN_BRACKET = "(";

    /**
     * Right bracket symbol
     */
    private static final String CLOSE_BRACKET = ")";

    /**
     * Left curly bracket symbol
     */
    private static final String OPEN_CURLY_BRACKET = "{";

    /**
     * Right curly bracket symbol
     */
    private static final String CLOSE_CURLY_BRACKET = "}";

    /**
     * Left bracket symbol
     */
    private static final String SEMICOLON = ";";

    /**
     * Return word in methods
     */
    private static final String RETURN_WORD = "return";

    /**
     * Class declaration word
     */
    private static final String CLASS_WORD = "class";

    /**
     * Super word for parent class constructors
     */
    private static final String SUPER_WORD = "super";

    /**
     * Throws word for exception declarations
     */
    private static final String THROWS_WORD = "throws";

    /**
     * Implements word for class declaration
     */
    private static final String IMPLEMENTS_WORD = "implements";

    /**
     * Extends word for class declaration
     */
    private static final String EXTENDS_WORD = "extends";

    /**
     * Override word for method annotation
     */
    private static final String OVERRIDE_WORD = "@Override";

    /**
     * Default value for methods with boolean return type
     */
    private static final String DEFAULT_BOOLEAN_VALUE = "false";

    /**
     * Default value for methods with primitive return type except boolean
     */
    private static final String DEFAULT_PRIMITIVE_VALUE = "0";

    /**
     * Default value for methods with object return type
     */
    private static final String DEFAULT_OBJECT_VALUE = "null";

    /**
     * Default value for methods with void return type
     */
    private static final String DEFAULT_VOID_VALUE = "";

    /**
     * Entry point of the program. Command line arguments are processed here
     * <p>
     * Usage:
     * <ul>
     * <li>{@code java -jar Implementor.jar -jar class-to-implement path-to-jar}</li>
     * <li>{@code java -jar Implementor.jar class-to-implement path-to-class}</li>
     * </ul>
     *
     * @param args command line arguments.
     * @see Implementor
     */
    public static void main(String[] args) {

        if (args == null || args.length < 2 || args[0] == null || args[1] == null
                || args[0].equals(JAR_OPTION) && (args.length < 3 || args[2] == null)) {
            System.err.println("Usage: java Implementor <class name> <root path> | -jar <class name> <root path>");
            return;
        }

        try {
            Implementor implementor = new Implementor();

            if (args[0].equals(JAR_OPTION)) {
                implementor.implementJar(Class.forName(args[1]), Path.of(args[2]));
            } else {
                implementor.implement(Class.forName(args[0]), Path.of(args[1]));
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Invalid class name: " + e.getMessage());
        } catch (ImplerException e) {
            System.err.println("Error occurred with class implementation: " + e.getMessage());
        }

    }

    /**
     * Default constructor with no arguments
     */
    public Implementor() {
    }

    /**
     * Produces code implementing class or interface specified by provided <code>token</code>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <code>Impl</code> suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * <code>root</code> directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to <code>$root/java/util/ListImpl.java</code>
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws ImplerException when implementation cannot be
     *                         generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {

        if (token == null || root == null) {
            throw new ImplerException("Invalid arguments: " + ((token == null) ? "class token " : "root path ") + "is null");
        }

        if (token.isPrimitive() || token.isEnum() || token == Enum.class || token.isArray()
                || Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Invalid token was passed");
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(getFileName(token, root, JAVA_EXTENSION, false).toFile()), StandardCharsets.UTF_8))) {

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

    /**
     * Produces <code>.jar</code> file implementing class or interface specified by provided <code>token</code>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <code>Impl</code> suffix
     * added.
     *
     * @param token   type token to create implementation for.
     * @param jarPath target <code>.jar</code> file.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implementJar(Class<?> token, Path jarPath) throws ImplerException {
        try {
            Path temporaryDirectory = Paths.get(System.getProperty("java.io.tmpdir"));

            implement(token, temporaryDirectory);
            compile(getFileName(token, temporaryDirectory, JAVA_EXTENSION, false));
            makeJar(getFileName(token, Paths.get("."), CLASS_EXTENSION, true), temporaryDirectory, jarPath);
        } catch (IOException e) {
            throw new ImplerException("Error occurred while implementing jar: " + e.getMessage());
        }
    }

    /**
     * Returns full path where implementation class of <code>token</code> with extension <code>extension</code> will be generated
     *
     * @param token     class type
     * @param folder    root folder
     * @param extension file extension
     * @param isJar     flag which is true when file is jar and false otherwise
     * @return {@link Path} - file path
     * @throws IOException if error while creating directories
     */
    private Path getFileName(Class<?> token, Path folder, String extension, boolean isJar) throws IOException {
        if (token.getPackage() != null) {
            folder = folder.resolve(token.getPackage().getName().replace('.', (
                    isJar ? File.separatorChar : '/')));
        }
        folder = Files.createDirectories(folder);
        return folder.resolve(token.getSimpleName() + CLASS_NAME_SUFFIX + extension);
    }

    /**
     * Creates manifest
     *
     * @return {@link Manifest} manifest
     */
    private Manifest createManifest() {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        return manifest;
    }

    /**
     * Compiles class
     *
     * @param classPath path to class
     * @throws ImplerException if fail
     * @see ImplerException
     */
    private void compile(Path classPath) throws ImplerException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new ImplerException("Java compiler not found");
        }
        int returnCode = javaCompiler.run(null, null, null, classPath.toString(),
                "-encoding", "cp866");
        if (returnCode != 0) {
            throw new ImplerException("Error with code: " + returnCode + " occurred during compilation");
        }
    }

    /**
     * Makes jar
     *
     * @param className class name
     * @param root      path root
     * @param jarPath   jar path
     * @throws ImplerException if error occurred while creating jar
     */
    private void makeJar(Path className, Path root, Path jarPath) throws ImplerException {
        className = className.normalize();
        Path classFile = root.resolve(className);
        Manifest manifest = createManifest();

        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jarPath), manifest)) {
            out.putNextEntry(new ZipEntry(className.toString()));
            Files.copy(classFile, out);
            out.closeEntry();
        } catch (IOException e) {
            throw new ImplerException("Error while creating jar file: " + e.getMessage());
        }
    }

    /**
     * Escapes text
     *
     * @param text text to escape
     * @return {@link java.lang.String} escaped string
     */
    private String escape(String text) {
        StringBuilder result = new StringBuilder();

        int textLength = text.length();
        for (int index = 0; index < textLength; index++) {
            char code = text.charAt(index);
            if ((int) code <= 127) {
                result.append(code);
            } else {
                result.append(String.format("\\u%04x", (int) code));
            }
        }
        return result.toString();
    }


    /**
     * Prints escaped text
     *
     * @param writer writer to print result
     * @param text   text to escape
     * @throws IOException if error occurred with IO
     */
    private void print(BufferedWriter writer, String text) throws IOException {
        writer.write(escape(text));
    }

    /**
     * Prints packages of class implementation
     *
     * @param token  class token
     * @param writer writer to print result
     * @throws IOException if error occurred with IO
     */
    private void generatePackages(Class<?> token, BufferedWriter writer) throws IOException {
        if (!token.getPackage().getName().isEmpty()) {
            print(writer, token.getPackage().toString()
                    + SEMICOLON
                    + NEW_LINE.repeat(2));
        }
    }

    /**
     * Prints declaration of class implementation
     *
     * @param token  class token
     * @param writer writer to print result
     * @throws IOException if error occurred with IO
     */
    private void generateClassDeclaration(Class<?> token, BufferedWriter writer) throws IOException {

        print(writer, getClassModifiers(token)
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
                + NEW_LINE.repeat(2));
    }

    /**
     * Prints declaration of class implementation
     *
     * @param token  class token
     * @param writer writer to print result
     * @throws IOException     if error occurred with IO
     * @throws ImplerException if implemented class has only private constructors
     */
    private void generateConstructors(Class<?> token, BufferedWriter writer) throws IOException, ImplerException {

        if (token.isInterface()) {
            return;
        }

        Constructor[] constructors = token.getDeclaredConstructors();

        boolean onlyPrivateConstructors = true;

        for (var constructor : constructors) {

            if (Modifier.isPrivate(constructor.getModifiers())) {
                continue;
            }

            onlyPrivateConstructors = false;

            generateMethodAnnotations(constructor, writer);
            generateMethodDeclaration(constructor, writer, false);
            generateMethodExceptions(constructor, writer);

            print(writer, SPACE
                    + OPEN_CURLY_BRACKET
                    + NEW_LINE
                    + TAB.repeat(2)
                    + SUPER_WORD);
            generateParameters(constructor, writer, false);
            print(writer, SEMICOLON
                    + NEW_LINE
                    + TAB
                    + CLOSE_CURLY_BRACKET
                    + NEW_LINE.repeat(2));
        }

        if (onlyPrivateConstructors) {
            throw new ImplerException("No available constructors");
        }

    }

    /**
     * Prints all methods of class implementation
     *
     * @param token  class token
     * @param writer writer to print result
     * @throws IOException if error occurred with IO
     */
    private void generateMethods(Class<?> token, BufferedWriter writer) throws IOException {

        Class<?> ancestor = token;
        Set<MethodComparingWrapper> set = Arrays.stream(token.getMethods())
                .map(MethodComparingWrapper::new)
                .collect(Collectors.toSet());

        while (ancestor != null && !ancestor.equals(Object.class)) {
            for (Method method : ancestor.getDeclaredMethods()) {
                set.add(new MethodComparingWrapper(method));
            }
            ancestor = ancestor.getSuperclass();
        }

        for (MethodComparingWrapper wrapper : set) {
            Method method = wrapper.getMethod();
            int modifiers = method.getModifiers();

            if (method.isDefault() || !Modifier.isAbstract(modifiers)) {
                continue;
            }

            print(writer, TAB
                    + OVERRIDE_WORD
                    + NEW_LINE);

            generateMethodAnnotations(method, writer);
            generateMethodDeclaration(method, writer, true);
            generateMethodExceptions(method, writer);
            print(writer, SPACE + OPEN_CURLY_BRACKET + NEW_LINE);
            generateMethodImplementation(method, writer);
            print(writer, TAB + CLOSE_CURLY_BRACKET + NEW_LINE.repeat(2));
        }

        print(writer, NEW_LINE
                + CLOSE_CURLY_BRACKET);
    }

    /**
     * Prints annotations of method or constructor
     *
     * @param executable method or constructor
     * @param writer     writer to print result
     * @throws IOException if error occurred with IO
     */
    private void generateMethodAnnotations(Executable executable, BufferedWriter writer) throws IOException {

        Annotation[] annotations = executable.getDeclaredAnnotations();
        for (var annotation : annotations) {
            print(writer, TAB + annotation.toString() + NEW_LINE);
        }

    }

    /**
     * Prints declaration of method or constructor
     *
     * @param executable     method or constructor
     * @param writer         writer to print result
     * @param withReturnType flag to print return type
     * @throws IOException if error occurred with IO
     */
    private void generateMethodDeclaration(Executable executable, BufferedWriter writer, boolean withReturnType) throws IOException {

        print(writer, TAB
                + getMethodModifiers(executable)
                + SPACE
                + (withReturnType ? ((Method) executable).getReturnType().getCanonicalName() + SPACE : "")
                + (withReturnType ? executable.getName() : executable.getName()
                .substring(executable.getName()
                        .lastIndexOf('.') + 1)
                + CLASS_NAME_SUFFIX));

        generateParameters(executable, writer, true);
    }

    /**
     * Prints exceptions of method or constructor
     *
     * @param executable method or constructor
     * @param writer     writer to print result
     * @throws IOException if error occurred with IO
     */
    private void generateMethodExceptions(Executable executable, BufferedWriter writer) throws IOException {

        Class<?>[] exceptions = executable.getExceptionTypes();
        if (exceptions.length > 0) {
            print(writer, SPACE
                    + THROWS_WORD
                    + SPACE);
        }

        for (int i = 0; i < exceptions.length; i++) {
            if (i > 0) {
                print(writer, COMMA
                        + SPACE);
            }
            print(writer, exceptions[i].getCanonicalName());
        }
    }

    /**
     * Prints method implementation
     *
     * @param method method to implement
     * @param writer writer to print result
     * @throws IOException if error occurred with IO
     */
    private void generateMethodImplementation(Method method, BufferedWriter writer) throws IOException {

        String returnValue = getDefaultValue(method.getReturnType());

        print(writer, TAB.repeat(2)
                + RETURN_WORD
                + (!returnValue.isEmpty() ? SPACE : "")
                + returnValue
                + SEMICOLON
                + NEW_LINE);
    }

    /**
     * Prints parameters of method or constructor
     *
     * @param executable      method or constructor
     * @param writer          writer to print result
     * @param withReturnTypes flag to print return types
     * @throws IOException if error occurred with IO
     */
    private void generateParameters(Executable executable, BufferedWriter writer, boolean withReturnTypes) throws IOException {
        Parameter[] parameters = executable.getParameters();
        print(writer, OPEN_BRACKET);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (i > 0) {
                print(writer, COMMA + SPACE);
            }
            print(writer, (withReturnTypes ? parameter.getType().getCanonicalName() + SPACE : "")
                    + parameter.getName());
        }
        print(writer, CLOSE_BRACKET);
    }

    /**
     * Returns class modifiers of class implementation  which are allowed in implemented class
     *
     * @param token class token
     * @return {@link String} modifiers
     */
    private String getClassModifiers(Class<?> token) {
        return Modifier.toString(token.getModifiers() & ~Modifier.INTERFACE & ~Modifier.ABSTRACT);
    }

    /**
     * Returns class modifiers of method or constructor which are allowed in implemented method
     *
     * @param executable method or constructor
     * @return {@link String} modifiers
     */
    private String getMethodModifiers(Executable executable) {
        return Modifier.toString(executable.getModifiers() & ~Modifier.ABSTRACT
                & ~Modifier.TRANSIENT & ~Modifier.NATIVE);
    }

    /**
     * Returns default value of token's type
     *
     * @param token class token
     * @return {@link String} default value of method
     */
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

    /**
     * Helper class which provides correct comparison of methods to store them in {@link java.util.Set}
     */
    private class MethodComparingWrapper {

        /**
         * Method which is wrapped
         */
        private final Method method;

        /**
         * String identifier for method
         */
        private final String methodDefinition;

        /**
         * Constructor from method
         *
         * @param method method to wrap
         */
        MethodComparingWrapper(Method method) {
            this.method = method;
            StringBuilder builder = new StringBuilder();
            builder.append(method.getReturnType().getCanonicalName())
                    .append(COMMA)
                    .append(method.getName())
                    .append(COMMA);
            for (Parameter parameter : method.getParameters()) {
                builder.append(parameter.getType().getCanonicalName())
                        .append(COMMA);
            }
            methodDefinition = builder.toString();
        }

        /**
         * Returns wrapped method
         *
         * @return {@link Method} - wrapped method
         */
        Method getMethod() {
            return method;
        }

        /**
         * Returns method hash
         *
         * @return hash code value for this object.
         */
        @Override
        public int hashCode() {
            return methodDefinition.hashCode();
        }

        /**
         * Check if objects are equal
         *
         * @param object object to compare
         */
        @Override
        public boolean equals(Object object) {
            if (!(object instanceof MethodComparingWrapper)) {
                return false;
            }
            return methodDefinition.equals(((MethodComparingWrapper) object).methodDefinition);
        }
    }

}
