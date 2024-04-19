## A program is a list of statements.

### A statement can be an Expression Statement or Print statement.

```
program        → statement* EOF ;

statement      → exprStmt
               | printStmt ;

exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
```

---------------------------------------------------------------------------------

## A program is made up of declarations

### A declaration can be an variable declaration or Statement.
### A statement can be an Expression Statement or Print statement or a new block.

```
program        → declaration* EOF ;

declaration    → varDecl
               | statement ;

statement      → exprStmt
               | printStmt ;
               | block ;

block          → "{" declaration* "}" ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
```
