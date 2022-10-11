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