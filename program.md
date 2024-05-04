## A program is a list of statements.

### A statement can be an Expression Statement or Print statement.

```
program        → statement* EOF ;

statement      → exprStmt
               | printStmt ;

exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;

expression     → literal
               | unary
               | binary
               | grouping ;

literal        → NUMBER | STRING | "true" | "false" | "nil" ;
grouping       → "(" expression ")" ;
unary          → ( "-" | "!" ) expression ;
binary         → expression operator expression ;
operator       → "==" | "!=" | "<" | "<=" | ">" | ">="
               | "+"  | "-"  | "*" | "/" ;
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
               | printStmt 
               | block ;

block          → "{" declaration* "}" ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;

expression     → literal
               | unary
               | binary
               | grouping ;

literal        → NUMBER | STRING | "true" | "false" | "nil" ;
grouping       → "(" expression ")" ;
unary          → ( "-" | "!" ) expression ;
binary         → expression operator expression ;
operator       → "==" | "!=" | "<" | "<=" | ">" | ">="
               | "+"  | "-"  | "*" | "/" ;
```


---------------------------------------------------------------------------------

# Control Flow
### Conditional Execution

```
program        → declaration* EOF ;

declaration    → varDecl
               | statement ;

statement      → exprStmt
               | ifStmt 
               | printStmt 
               | block ;

exprStmt       → expression ";" ;
ifStmt         → "if" "(" expression ")" statement
( "else" statement )? ;
block          → "{" declaration* "}" ;
printStmt      → "print" expression ";" ;

expression     → literal
               | unary
               | binary
               | grouping ;

literal        → NUMBER | STRING | "true" | "false" | "nil" ;
grouping       → "(" expression ")" ;
unary          → ( "-" | "!" ) expression ;
binary         → expression operator expression ;
operator       → "==" | "!=" | "<" | "<=" | ">" | ">="
               | "+"  | "-"  | "*" | "/" ;
```
---------------------------------------------------------------------------------

# Control Flow
### Logical Operators

```
program        → declaration* EOF ;

declaration    → varDecl
               | statement ;

statement      → exprStmt
               | ifStmt 
               | printStmt 
               | block ;

exprStmt       → expression ";" ;
ifStmt         → "if" "(" expression ")" statement
( "else" statement )? ;
block          → "{" declaration* "}" ;
printStmt      → "print" expression ";" ;

expression     → assignment ;
assignment     → IDENTIFIER "=" assignment
               | logic_or ;

logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
```
---------------------------------------------------------------------------------

# Control Flow
### While Loops

```
program        → declaration* EOF ;

declaration    → varDecl
               | statement ;

statement      → exprStmt
               | ifStmt 
               | printStmt 
               | whileStmt 
               | block ;

exprStmt       → expression ";" ;
ifStmt         → "if" "(" expression ")" statement
( "else" statement )? ;
block          → "{" declaration* "}" ;
printStmt      → "print" expression ";" ;
whileStmt      → "while" "(" expression ")" statement ;

expression     → assignment ;
assignment     → IDENTIFIER "=" assignment
               | logic_or ;

logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
```

---------------------------------------------------------------------------------

# Control Flow
### For Loops

```
program        → declaration* EOF ;

declaration    → varDecl
               | statement ;

statement      → exprStmt
               | forStmt
               | ifStmt 
               | printStmt 
               | whileStmt  
               | block ;

exprStmt       → expression ";" ;
forStmt        → "for" "(" ( varDecl | exprStmt | ";" )
                 expression? ";"
                 expression? ")" statement ;
ifStmt         → "if" "(" expression ")" statement
( "else" statement )? ;
block          → "{" declaration* "}" ;
printStmt      → "print" expression ";" ;
whileStmt      → "while" "(" expression ")" statement ;

expression     → assignment ;
assignment     → IDENTIFIER "=" assignment
               | logic_or ;

logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
```

---------------------------------------------------------------------------------

# Functions
### function declaration

```
program        → declaration* EOF ;

declaration    → funDecl
               | varDecl
               | statement ;

funDecl        → "fun" function ;
function       → IDENTIFIER "(" parameters? ")" block ;

parameters     → IDENTIFIER ( "," IDENTIFIER )* ;

statement      → exprStmt
               | forStmt
               | ifStmt 
               | printStmt 
               | whileStmt  
               | block ;

exprStmt       → expression ";" ;
forStmt        → "for" "(" ( varDecl | exprStmt | ";" )
                 expression? ";"
                 expression? ")" statement ;
ifStmt         → "if" "(" expression ")" statement
( "else" statement )? ;
block          → "{" declaration* "}" ;
printStmt      → "print" expression ";" ;
whileStmt      → "while" "(" expression ")" statement ;

expression     → assignment ;
assignment     → IDENTIFIER "=" assignment
               | logic_or ;

logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
```

### return statements

```
program        → declaration* EOF ;

declaration    → funDecl
               | varDecl
               | statement ;

funDecl        → "fun" function ;
function       → IDENTIFIER "(" parameters? ")" block ;

parameters     → IDENTIFIER ( "," IDENTIFIER )* ;

statement      → exprStmt
               | forStmt
               | ifStmt
               | printStmt
               | returnStmt
               | whileStmt
               | block ;

returnStmt     → "return" expression? ";" ;

expression     → assignment ;
assignment     → IDENTIFIER "=" assignment
               | logic_or ;

logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
```
