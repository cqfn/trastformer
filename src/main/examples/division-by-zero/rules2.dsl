
StatementBlock(ExpressionStatement#3, Return(Division(#1, #2)))  ->
    StatementBlock(
        ExpressionStatement(SimpleAssignment(PropertyAccess(This, Identifier<"copy">), #2)),
        Return(Division(#1, PropertyAccess(This, Identifier<"copy">)))
    );


ClassBody(FunctionDeclaration#1) ->
    ClassBody(
        FieldDeclaration(ModifierBlock(Modifier<"private">), PrimitiveType<"double">, DeclaratorList(Declarator(Identifier<"copy">))),
        #1
    );