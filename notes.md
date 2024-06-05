# Notes
- java is statically typed language but it also does some runtime error handling
- Some languages have operator for string concatenation
  - perl(.)
  - lua(..)
  - Smalltalk(,)
  - Haskell(++)
- In java, javascript, python the '+' operator is overloaded to support both adding numbers and concatenating strings.
- Function return values, but procedures cannot.
- There is no place in grammer where both an expression and statement are allowed. The operands of say, '+' are  
  always expressions, never statements. The body of while look is always statement.
- Scheme allows redifining variables at the top level. 
- Using a variable isn't same as referring to it. You can refer to a variable in a chunk of code without         
  immediately evaluating it if that chunk of code is wrapped inside a function.
- It’s OK to refer to a variable before it’s defined as long as you don’t evaluate the reference.
- In Pascal, Go, Python assignment is a statement
- Most C derived languages assignement is a expression and not a statement
- Scope and environments are close cousins, The former is the theoretical concept, and the latter is the machinery 
  that implements.
- Condition is an expression and the body is a statement.
- C language has maximum 127 agruments to a function & java has a limit of 255 arguments
- The normal way of defining a function that takes multiple arguments is as a series of nested functions. Each function takes 
  one argument and return a new function. That function consumes the next argument, returns yet another function, and so on. Eventually
  once all the arguments are consumed, the last function completes the operation. This style is called "Currying" ```[Haskell Curry]```

## Token 
- A token represents a unit of code at a specific place in the source text.

## Program
- A program is a list of statements followed by a special "end of file" [EOF] token.

## Side Effects
- State and statements go hand in hand. Since statements, by definition, don't evaluate to a value, they need to do something else to be useful. That something is called a "side effect"

## Expression Statements
- Lets you place an expression where a statement is expected.
- Anytime you see a function or method call followed by ;, you're looking at a expression statement

## Scope
- A scope defines a region where a name maps to a certain entity.
### Lexical Scope / Static Scope
- It is a specific style of scoping where the text of the program itself shows where a scope begins and ends.
```js
var a = "outer";
{
  var a = "inner";
  print a;
}
```

## Hoisting In JS
- "var" are implicitly "hoisted" to the begining of the block.
```js
{
  conole.log(a);
  var a = 10;
}
// It behaves like
{
  var a; // Hoist
  conole.log(a);
  var a = 10;
}
```
- That means that in some cases you can read a variable before its initializer has run—an annoying source of bugs. 
  The alternate "let" syntax for declaring variables was added later to address this problem.

## Control Flow

### Conditional or Branching control flow
- Is used to not execute some piece of code. Imperatively you can think of it as jumping ahead over a region of code.

### Looping control flow
- Executes a chunk of code more than once. It jumps back so that you can do something again. Since you don’t usually want infinite loops, it typically has some conditional logic to know when to stop looping as well.
- Condition is an expression and the body is a statement.

### Desugaring
- That funny word describes a process where the front end takes code using syntax sugar and translates it to a more primitive form that the back end already knows how to execute.

# ---------- Functions ----------

## Callee
- Can be any expression that evaluates to a function

## Arity
- Arity is fancy term for the number of arguments a function or operation expects.
   - unary operators have 1 arity
   - binary operators have 2 arity
  With functions arity is determined by number of parameters it declares

## Native functions
- These are functions that the interpreter exposes to user code but that are implemented in the host language (in our case java), not the 
  language being implemented (Lox)

## Lisp-1
- Refers to languages like scheme that puts functions and variables in same namespace

## Lisp-2
- Refers to languages like Common Lisp that puts functions and variables in different namespace

## Closure
- It "closes over" and holds on to the surrounding variables where the function is declared.

## Persistent Data Structures
- Unlike the squishy data structures you’re familiar with in imperative programming, a persistent data structure can never be directly modified.
- Instead, any “modification” to an existing structure produces a brand new object that contains all of the original data and the new modification. The original is left unchanged.

## A variable resolution pass
- After the parser produces the syntax tree, but before the interpreter starts executing it, we’ll do a single walk over the tree to resolve all of the variables it contains. 
- Additional passes between parsing and execution are common. If Lox had static types, we could slide a type checker in there. 
- Optimizations are often implemented in separate passes like this too.
- Our variable resolution pass works like a sort of "mini-interpreter". It walks the tree, visiting each node, but a static analysis is different from a dynamic execution:

# ---------- OOPs & Classes ----------

