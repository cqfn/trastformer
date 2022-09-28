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
- Unifies a third-party AST model into our custom structure, called UAST (unified AST);
- Transforms the UAST with the provided DSL rules;
- Generated source code from the modified tree.