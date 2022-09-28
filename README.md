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
java -jar trastformer.jar --parse <path to source file> --rules <path to DSL file> --json <path to JSON file> [optional arguments] 
```

Required arguments:

* `--parse` (short: `-p`), the path to a file that contains source code in some supported programming languages.
  Expected file extensions are `.txt`, `.java`, `.js`, `.py`.
* `--rules` (short: `--dsl`, `-r`), the path to a file that contains rules described using the DSL
  syntax, expected file extensions are `.dsl` or `.txt`.
* `--json` (short: `-j`), the path to the `JSON` file where the result syntax tree will be saved
  in a serialized format, file extension is `.json`. The `JSON` format is described [here](https://github.com/cqfn/astranaut#syntax-tree-representation).
* `--lang` (short: `-l`), the name of the source file language. For Java, it should be `java`,
  for JavaScript - `js` or `javascript`, for Python - `python`.
  This option is required if the file with the source code has a `.txt` format, otherwise you can omit it.

Example:

```
java -jar trastformer.jar --parse D:\sources\MyClass.java --rules D:\dsl\rules.dsl -j D:\storage\ResultAst.json
```
