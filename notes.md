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


# Program
- A program is a list of statements followed by a special "end of file" [EOF] token.

# Side Effects
- State and statements go hand in hand. Since statements, by definition, don't evaluate to a value, they need to  do something else to be useful. That something is called a "side effect"

# Scope
- A scope defines a region where a name maps to a certain entity.

# Lexical Scope / Static Scope
- It is a specific style of scoping where the text of the program itself shows where a scope begins and ends.