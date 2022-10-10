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
class MainTest {
    /**
     * The "--parse" option as an argument example.
     */
    private static final String PARSE = "--parse";

    /**
     * The "--rules" option as an argument example.
     */
    private static final String RULES = "--dsl";

    /**
     * The "--json" option as an argument example.
     */
    private static final String OUTPUT = "--output";

    /**
     * Test passing options to main().
     * @param source A temporary directory
     */
    @Test
    void testNoException(@TempDir final Path source) throws IOException {
        final Path code = this.createTempSourceFile(source);
        final Path dsl = this.createTempDslFile(source);
        final String[] example = {
            MainTest.PARSE,
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
     * Test passing the {@code --parse} option with no parameters to main().
     */
    @Test
    void testGenerateWithoutParameters() {
        final String[] example = {
            MainTest.PARSE,
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
        Assertions.assertEquals("Expected a value after parameter --parse", message);
    }

    /**
     * Creates a temporary file with source code to test passing files in CLI options.
     * @param source A temporary directory
     * @return The path to a temporary file
     * @throws IOException If fails to create a temporary file
     */
    private Path createTempSourceFile(@TempDir final Path source) throws IOException {
        final Path file = source.resolve("example.java");
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
        final Path file = source.resolve("example.txt");
        final List<String> lines = Collections.singletonList(
            "Addition(#1, #2) -> Subtraction(#1, #2);"
        );
        Files.write(file, lines);
        return file;
    }
}
