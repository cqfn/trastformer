![Build and test](https://github.com/cqfn/trastformer/workflows/Build%20and%20test/badge.svg)
[![Codecov](https://codecov.io/gh/cqfn/trastformer/branch/master/graph/badge.svg)](https://codecov.io/gh/cqfn/trastformer)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/cqfn/trastformer/blob/master/LICENSE.txt)
___


*TrASTformer* is a tool that performs rule-based transformation of source code.
Transformation rules are written in a special domain-specific language.

You can use TrASTformer for:
- mutation testing
- simple cases of library migration
- complex transformation of code, e.g. for differential testing or testing of static analyzers.

Suppose, you have a project, where you use the `commons-io` library of an old version.
Previously, the usage of `IOUtils.toInputStream` required a single string argument.
However, in the newest versions this method is deprecated, so you also need to specify a character encoding.

With our tool you can transform the code snippet:

```java
public String convert(final String source) {
    final InputStream stream = IOUtils.toInputStream(source);
    final String result = IOUtils.toString(stream);
    stream.close();
    return result;
}
```

into the following

```java
public String convert(final String source) {
    final InputStream stream = IOUtils.toInputStream(source, "UTF-8");
    final String result = IOUtils.toString(stream, "UTF-8");
    stream.close();
    return result;
}
```

With a single rule `Identifier<"val"> -> Identifier<"num">;`, you can change a variable name:

before

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

after

```java
class Program {
    public int count(final int num) {
        return num + 10;
    }

    public int calculateAbs(final int num) {
        return Math.abs(num);
    }
}
```

Or with `BinaryExpression(#1, #2) -> Subtraction(#1, #2);` transform all binary operators into one:

before:

```java
public class Calc {
    public double calc(final int alpha, final long beta, final float gamma, final double delta) {
        return (alpha + beta) * gamma / delta;
    }
}
```

after:

```java
public class Calc {
  public double calc(final int alpha, final long beta, final float gamma, final double delta) {
    return (alpha - beta) - gamma - delta;
  }
}
```



The list of **supported mutations**:

TODO


## How it works

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
