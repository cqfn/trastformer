FunctionCall(Name(Identifier<"IOUtils">), Identifier<"toInputStream">, ExpressionList(#1)) ->
    FunctionCall(Name(Identifier<"IOUtils">), Identifier<"toInputStream">, ExpressionList(#1, StringLiteral<"UTF-8">));


FunctionCall(Name(Identifier<"IOUtils">), Identifier<"toString">, ExpressionList(#1)) ->
    FunctionCall(Name(Identifier<"IOUtils">), Identifier<"toString">, ExpressionList(#1, StringLiteral<"UTF-8">));
