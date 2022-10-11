# TrASTformer

![Build and test](https://github.com/unified-ast/trastformer/workflows/Build%20and%20test/badge.svg)
[![Codecov](https://codecov.io/gh/unified-ast/trastformer/branch/master/graph/badge.svg)](https://codecov.io/gh/unified-ast/unified-ast)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/unified-ast/trastformer/blob/master/LICENSE.txt)
___

## Brief

*TrASTformer* is a tool that performs transformation of source code with the usage of rules written 
in a special domain-specific language.

The main steps:

- Loads [DSL rules](https://github.com/cqfn/astranaut#domain-specific-language) and source code in one general-purpose programming language from the list:
    - Java
    - JavaScript
    - Python
- Parses source code and creates an abstract syntax tree (AST):
    - Java - from [JavaParser](https://javaparser.org/) AST
    - JavaScript - from [ANTLR](https://github.com/antlr/grammars-v4/tree/master/javascript/javascript) AST
    - Python - from [ANTLR](https://github.com/antlr/grammars-v4/tree/master/python/python) AST
- Unifies a third-party AST model into our custom structure, called [UAST](https://github.com/cqfn/uast) (unified AST);
- Transforms the UAST with the provided DSL rules;
- Generates source code from the modified tree.

> The last step has not been implemented yet. For now, the intermediate output (a transformed UAST) is saved in JSON format.

## Requirements

* Java 1.8
* Maven 3.6.3+ (to build)

## How to use

### Command line interface

> Here and below, it is assumed that the name of the executable file is `trastformer.jar`.

Syntax:

```
java -jar trastformer.jar --code <path to source file> --rules <path to DSL file> --output <path to generated file> [optional arguments] 
```

Required arguments:

* `--code` (short: `-c`), the path to a file that contains source code in some supported programming languages.
  Expected file extensions are `.txt`, `.java`, `.js`, `.py`.
* `--rules` (short: `--dsl`, `-r`), the path to a file that contains rules described using the DSL
  syntax, expected file extensions are `.dsl` or `.txt`.
* `--output` (short: `-o`), the path to the generated source file where the result generated code will be saved.
  Supported file extensions: `.java`.
* `--json` (short: `-j`) [*optional*], the path to the `JSON` file where the result syntax tree will be saved
  in a serialized format, file extension is `.json`. The `JSON` format is described [here](https://github.com/cqfn/astranaut#syntax-tree-representation).
* `--image` (short: `-i`) [*optional*], the path to the image file where the result syntax tree will be saved
  in a graphical format. Supported image extensions are `.png` and`.svg`.
* `--lang` (short: `-l`), the name of the source file language. For Java, it should be `java`,
  for JavaScript - `js` or `javascript`, for Python - `python`.
  This option is *required* if the file with the source code has a `.txt` format, otherwise you can omit it.

Examples:

```
java -jar trastformer.jar --code D:\sources\MyClass.java --rules D:\dsl\rules.dsl -o D:\storage\ResultCode.java
```

See real examples of sources, files with rules and results of transformations [here](src/main/examples).

For instance, this is how we ran one of these cases:

```
java -jar trastformer.jar
--code
src/main/examples/field-injector/sources/Program.java
--rules
src/main/examples/field-injector/rules.dsl
--json
src/main/examples/field-injector/results/java_ast.json
--output
src/main/examples/field-injector/results/Program_gen.java
--image
src/main/examples/field-injector/results/java_ast.png
```

This example would transform the code snippet
```java
class Program {
    public int count(final int val) {
        return val + 10;
    }

    public int calculateAbs(final int val) {
        return Math.abs(val);
    }
}
```

into the following

```java
class Program {
  private int result;

  public int count() {
    return this.result + 10;
  }

  public int calculateAbs(final int val) {
    return Math.abs(val);
  }
}
```

with 2 DSL rules:

```
ClassBody(FunctionDeclaration#1) ->
    ClassBody(
        FieldDeclaration(ModifierBlock(Modifier<"private">), PrimitiveType<"int">, DeclaratorList(Declarator(Identifier<"result">))),
        FunctionDeclaration#1
    );

FunctionDeclaration(#1, #2, Identifier<"count">, #3, #4) ->
    FunctionDeclaration(
        #1, #2, Identifier<"count">, ParameterBlock(),
        StatementBlock(Return(Addition(PropertyAccess(This, Identifier<"result">), IntegerLiteral<"10">)))
    );
```