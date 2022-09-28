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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.cqfn.astranaut.api.TreeProcessor;
import org.cqfn.astranaut.core.Node;
import org.cqfn.astranaut.core.exceptions.BaseException;
import org.cqfn.astranaut.core.utils.JsonSerializer;
import org.cqfn.astranaut.exceptions.ProcessorException;
import org.cqfn.astranaut.utils.cli.RulesFileConverter;
import org.cqfn.uast.cli.JsonPathValidator;
import org.cqfn.uast.cli.LanguageConverter;
import org.cqfn.uast.lang.SourceCodeParser;

/**
 * Main class.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.ImmutableField")
public final class Main {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /**
     * The source file.
     */
    @Parameter(
        names = { "--parse", "-p" },
        converter = FileConverter.class,
        required = true,
        arity = 1,
        description = "The file with source code"
    )
    private File source;

    /**
     * The file that contains DSL rules.
     */
    @Parameter(
        names = { "--rules", "--dsl", "-r" },
        converter = RulesFileConverter.class,
        required = true,
        description = "Text file with DSL descriptions"
    )
    private File dsl;

    /**
     * The json file to store result ASTs.
     */
    @Parameter(
        names = { "--json", "-j" },
        validateWith = JsonPathValidator.class,
        required = true,
        arity = 1,
        description = "The name (possibly path) of the json file with extension"
    )
    private File json;

    /**
     * The programming language for which the analysis is performed.
     */
    @Parameter(
        names = { "--lang", "-l" },
        converter = LanguageConverter.class,
        arity = 1,
        description = "The programming language of the source file"
    )
    private String language;

    /**
     * The help option.
     */
    @Parameter(names = "--help", help = true)
    private boolean help;

    /**
     * Private constructor with default values.
     */
    private Main() {
        this.help = false;
        this.language = "";
    }

    /**
     * The main function. Parses the command line and runs actions.
     * @param args The command-line arguments
     * @throws IOException If an error during input or output actions occurs
     * @throws ProcessorException If DSL processing fails
     */
    public static void main(final String... args) throws IOException, ProcessorException {
        final Main main = new Main();
        final JCommander jcr = JCommander.newBuilder()
            .addObject(main)
            .build();
        jcr.parse(args);
        if (main.help) {
            jcr.usage();
            return;
        }
        main.run();
    }

    /**
     * Runs actions.
     * @throws IOException If an error during input or output actions occurs
     * @throws ProcessorException If DSL processing fails
     */
    private void run() throws IOException, ProcessorException {
        final String ext = this.source.getName()
            .substring(this.source.getName().lastIndexOf('.') + 1);
        String lang = ext;
        if ("txt".equals(ext)) {
            if (this.language.isEmpty()) {
                throw new ParameterException(
                    "The option [--lang] should be specified with txt source file"
                );
            } else {
                lang = this.language;
            }
        }
        final Node node = new SourceCodeParser(this.source.getPath())
            .parse(lang, true, false);
        Node result = node;
        final String rules = this.dsl.getPath();
        final TreeProcessor processor = new TreeProcessor();
        try {
            processor.loadRules(rules);
            result = processor.transform(node);
        } catch (final BaseException exc) {
            LOG.severe(String.format("%s, %s", exc.getInitiator(), exc.getErrorMessage()));
            throw exc;
        }
        final JsonSerializer serializer = new JsonSerializer(result);
        serializer.serializeToFile(this.json.getPath());
    }
}
