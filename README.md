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

Or with `ArithmeticExpression(#1, #2) -> Subtraction(#1, #2);` transform all binary arithmetic operators into one:

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

Now suppose that you want to test some static analyzer.
Assume it has a set of source code programs for testing.
Running the analyzer on each program should detect one vulnerability.

For example, the program contains a function:

```java
double count(double value) {
    return 10 / value;    
}
```

After the processing of this program, the analyzer detects a *division by zero* vulnerability. 

But will the analyzer find this error in other code with similar semantics?
Our tool can make complex mutations of programs to check this, and subsequently extend the testing coverage of analyzers.

For instance, it can transform the provided function into the following:

```java
class Program { 
    public double count(double value) {
        return 10 / value;
    }
}
```

or this one:

```java
class Program {
    private double copy;

    public double count(double value) {
        this.copy = value;
        return 10 / this.copy;
    }
}
```

We divide possible changes of code into *simple* and *complex*:

- A simple change is a single transformation inside an abstract syntax tree (AST) of initial code, e.g., changing a node value,
replacing a node type, deleting a subtree, or inserting a subtree.
- A complex change is a combination of different transformations inside an AST, e.g., inserting and deletion of subtrees
in various places of the initial AST. In most cases, such transformations are achievable by making several rules.

The list of **simple supported transformations**:

| Type        | Rule example| Before     | After
| :---        | :---        | :---       | :---
| Variable renaming      | Identifier<"n"> -> Identifier<"num">;               | `int  n = 5;`  | `int  num = 5;`
| Literal replacement    | IntegerLiteral<"5"> -> IntegerLiteral<"10">;             | `int  n = 5;`  | `int  n = 10;`
| Inversion              | IntegerLiteral<#1> -> Negative(IntegerLiteral<#1>);  | `int  n = 5;`  | `int  n = -5;`
| Arithmetic operands replacement | Addition(#1, #2) -> Multiplication(#1, #2);  | `int  n =  x + y;`  | `int  n =  x * y;`
| Unary operands replacement | PreIncrement(#1) -> PostIncrement(#1);  | `++x;`  | `x++;`
| Conditionals replacement | GreaterThan(#1, #2) -> GreaterThanOrEqualTo(#1, #2);  | `return x > y;`  | `return x >= y;`
| Parameters swapping | ParameterBlock(#1, #2) -> ParameterBlock(#2, #1);  | `int calc(float x, double y){}`  | `int calc(double y, float x){}`
| Content removal | ParameterBlock(#1...) -> ParameterBlock();  | `int calc(float x, double y){}`  | `int calc(){}`
| Statement insertion | StatementBlock() -> StatementBlock(Return(IntegerLiteral<"10">));  | `int getval(){}`  | `int getval(){return 10;}`
| Statement replacement | FunctionCall(#1, #2, #3) -> FunctionCall(Identifier<"create">, ExpressionList(Variable(Name(Identifier<"a">)))); | `A.build();`  | `create(a);`
| Expression insertion | Return(#1) -> Return(FunctionCall(Name(Identifier<"Math">), Identifier<"abs">), ExpressionList(#1));  | `int getval(return 10;){}`  | `int getval(){return Math.abs(10);}`
| Expression replacement | VariableDeclaration(#1, DeclaratorList(Declarator(#2, #3))) -> VariableDeclaration(#1, DeclaratorList(Declarator(#2, NullLiteral))); | `Object obj = new Object();`  | `Object obj = null;}`

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

> For now, generation supports only Java language

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
