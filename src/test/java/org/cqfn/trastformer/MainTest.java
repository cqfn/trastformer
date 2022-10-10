/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.trastformer;

import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.cqfn.astranaut.exceptions.ProcessorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link Main} class.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
class MainTest {
    /**
     * The "--code" option.
     */
    private static final String CODE = "--code";

    /**
     * The "--rules" option.
     */
    private static final String RULES = "--rules";

    /**
     * The "--output" option.
     */
    private static final String OUTPUT = "--output";

    /**
     * The "--lang" option.
     */
    private static final String LANG = "--lang";

    /**
     * The "--json" option.
     */
    private static final String JSON = "--json";

    /**
     * The "--image" option.
     */
    private static final String IMAGE = "--image";

    /**
     * The Java language name.
     */
    private static final String JAVA = "java";

    /**
     * Test passing required options to main().
     * @param source A temporary directory
     */
    @Test
    void testNoException(@TempDir final Path source) throws IOException {
        final Path code = this.createTempSourceFile(source, MainTest.JAVA);
        final Path dsl = this.createTempDslFile(source);
        final String[] example = {
            MainTest.CODE,
            code.toString(),
            MainTest.RULES,
            dsl.toString(),
            MainTest.OUTPUT,
            source.resolve("example_gen.java").toString(),
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final  ParameterException | IOException | ProcessorException exc) {
            caught = true;
        }
        Assertions.assertFalse(caught);
    }

    /**
     * Test passing options that contain "--lang" to main().
     * @param source A temporary directory
     */
    @Test
    void testWithLangSpecified(@TempDir final Path source) throws IOException {
        final Path code = this.createTempSourceFile(source, "txt");
        final Path dsl = this.createTempDslFile(source);
        final String[] example = {
            MainTest.CODE,
            code.toString(),
            MainTest.LANG,
            MainTest.JAVA,
            MainTest.RULES,
            dsl.toString(),
            MainTest.OUTPUT,
            source.resolve("example_gen.java").toString(),
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final  ParameterException | IOException | ProcessorException exc) {
            caught = true;
        }
        Assertions.assertFalse(caught);
    }

    /**
     * Test passing options that contain "--json" to main().
     * @param source A temporary directory
     */
    @Test
    void testWithJson(@TempDir final Path source) throws IOException {
        final Path code = this.createTempSourceFile(source, MainTest.JAVA);
        final Path dsl = this.createTempDslFile(source);
        final String[] example = {
            MainTest.CODE,
            code.toString(),
            MainTest.RULES,
            dsl.toString(),
            MainTest.OUTPUT,
            source.resolve("example_gen.java").toString(),
            MainTest.JSON,
            source.resolve("tree_gen.json").toString(),
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final  ParameterException | IOException | ProcessorException exc) {
            caught = true;
        }
        Assertions.assertFalse(caught);
    }

    /**
     * Test passing options that contain "--image" to main().
     * @param source A temporary directory
     */
    @Test
    void testWithImage(@TempDir final Path source) throws IOException {
        final Path code = this.createTempSourceFile(source, MainTest.JAVA);
        final Path dsl = this.createTempDslFile(source);
        final String[] example = {
            MainTest.CODE,
            code.toString(),
            MainTest.RULES,
            dsl.toString(),
            MainTest.OUTPUT,
            source.resolve("example_gen.java").toString(),
            MainTest.IMAGE,
            source.resolve("tree_gen.png").toString(),
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final  ParameterException | IOException | ProcessorException exc) {
            caught = true;
        }
        Assertions.assertFalse(caught);
    }

    /**
     * Test passing no option to main().
     */
    @Test
    void testWithException() {
        final String[] example = {
        };
        boolean caught = false;
        try {
            Main.main(example);
        } catch (final  ParameterException | IOException | ProcessorException exc) {
            caught = true;
        }
        Assertions.assertTrue(caught);
    }

    /**
     * Test passing the {@code --code} option with no parameters to main().
     */
    @Test
    void testGenerateWithoutParameters() {
        final String[] example = {
            MainTest.CODE,
        };
        boolean caught = false;
        String message = "";
        try {
            Main.main(example);
        } catch (final ParameterException | IOException | ProcessorException exc) {
            caught = true;
            message = exc.getMessage();
        }
        Assertions.assertTrue(caught);
        Assertions.assertEquals("Expected a value after parameter --code", message);
    }

    /**
     * Test passing the {@code --output} option with wrong extension to main().
     * @param source A temporary directory
     */
    @Test
    void testOutputOptionWithWrongExtension(@TempDir final Path source) throws IOException {
        final Path code = this.createTempSourceFile(source, MainTest.JAVA);
        final Path dsl = this.createTempDslFile(source);
        final String output = source.resolve("example_gen.go").toString();
        final String[] example = {
            MainTest.CODE,
            code.toString(),
            MainTest.RULES,
            dsl.toString(),
            MainTest.OUTPUT,
            output,
        };
        boolean caught = false;
        String message = "";
        try {
            Main.main(example);
        } catch (final ParameterException | IOException | ProcessorException exc) {
            caught = true;
            message = exc.getMessage();
        }
        Assertions.assertTrue(caught);
        Assertions.assertEquals(
            String.format("The parameter [%s] should be a valid source file", output),
            message
        );
    }

    /**
     * Test passing the {@code --output} option with no parameter to main().
     * @param source A temporary directory
     */
    @Test
    void testOutputOptionWithoutParameter(@TempDir final Path source) throws IOException {
        final Path code = this.createTempSourceFile(source, MainTest.JAVA);
        final Path dsl = this.createTempDslFile(source);
        final String[] example = {
            MainTest.CODE,
            code.toString(),
            MainTest.OUTPUT,
            MainTest.RULES,
            dsl.toString(),
        };
        boolean caught = false;
        String message = "";
        try {
            Main.main(example);
        } catch (final ParameterException | IOException | ProcessorException exc) {
            caught = true;
            message = exc.getMessage();
        }
        Assertions.assertTrue(caught);
        final String expectedmsg = "Missed parameter for the option [--output]";
        Assertions.assertEquals(expectedmsg, message);
    }

    /**
     * Creates a temporary file with source code to test passing files in CLI options.
     * @param source A temporary directory
     * @param extension A file extension
     * @return The path to a temporary file
     * @throws IOException If fails to create a temporary file
     */
    private Path createTempSourceFile(
        @TempDir final Path source,
        final String extension) throws IOException {
        final Path file = source.resolve("source_example.".concat(extension));
        final List<String> lines = Collections.singletonList(
            "class X{public int calc(){return 2 + 3;}}"
        );
        Files.write(file, lines);
        return file;
    }

    /**
     * Creates a temporary file with DSL rules to test passing files in CLI options.
     * @param source A temporary directory
     * @return The path to a temporary file
     * @throws IOException If fails to create a temporary file
     */
    private Path createTempDslFile(@TempDir final Path source) throws IOException {
        final Path file = source.resolve("dsl_example.txt");
        final List<String> lines = Collections.singletonList(
            "Addition(#1, #2) -> Subtraction(#1, #2);"
        );
        Files.write(file, lines);
        return file;
    }
}
