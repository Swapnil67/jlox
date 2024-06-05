package lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
  private final Interpreter interpreter;
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();
  private FunctionType currentFunction = FunctionType.NONE;

  Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  private enum FunctionType {
    NONE,
    FUNCTION;
  }
  
  /*
  * Begins a new scope, traverses into the statements inside the block, and then
  * discards the scope.
  */
  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  /*
   * Resolving the Class srtatement
   */
  @Override
  public Void visitClassStmt(Stmt.Class stmt) {
    declare(stmt.name);
    define(stmt.name);
    return null;
  }

  /**
   * * Resolving Expression statement
   * * An expression statement contains a single expression to traverse.
   */
  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    resolve(stmt.expression);
    return null;
  }

  /**
   * * Resolving If statement
   * * An if statement has an expression for its condition and one or two statements for the branches.
   */
  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    declare(stmt.name);
    define(stmt.name);
    resolveFunction(stmt, FunctionType.FUNCTION);
    return null;
  }

  /**
   * * Resolving Print statement
   * * An print statement contains a single expression to traverse.
   */
  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    resolve(stmt.expression);
    return null;
  }
  
  /**
   * * Resolving Return statement
   * * An return statement contains a single expression to traverse.
   */
  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    if(currentFunction == FunctionType.NONE) {
      Lox.error(stmt.keyword, "Can't return from top-level code.");
    }
    if(stmt.value != null) {
      resolve(stmt.value);
    }
    return null;
  }

  /**
   * * Resolving While statement
   * * while statement, we resolve its condition and resolve the body exactly once.
   */
  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    resolve(stmt.condtion);
    resolve(stmt.body);
    return null;
  }


  /**
   * * Resolving If statement
   * * An if statement has an expression for its condition and one or two statements for the branches.
   */
  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if(stmt.elseBranch != null) resolve(stmt.elseBranch);
    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    declare(stmt.name);
    if(stmt.initializer != null) {
      resolve(stmt.initializer);
    }
    define(stmt.name);
    return null;
  }

  /**
   * * Resolving Assign (=) expression
   */
  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }
  
  /**
   * * Resolving Binary expression
   * * We traverse into and resolve both operands.
   */
  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  /**
   * * Resolving Call expression
   */
  @Override
  public Void visitCallExpr(Expr.Call expr) {
    resolve(expr.callee);
    for(Expr argument : expr.arguments) {
      resolve(argument);
    }
    return null;
  }

  /**
   * * Resolving Grouping (Parentheses) expression
   */
  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  /**
   * * Resolving Literals expression
   * * A literal expression doesn’t mention any variables and doesn’t contain any subexpressions so there is no work to do.
   */
  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }
  
  /**
   * * Resolving Logical expression
   */
  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }
  
  /**
   * * Resolving Unary expression
   */
  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    if(!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
      Lox.error(expr.name, "Can't read local variable in its own initializer.");
    }
    resolveLocal(expr, expr.name);
    return null;
  }

  /**
   * * Resolves single statement
   * * they turn around and apply the Visitor pattern to the given syntax tree node.
   * @param stmt
   */
  private void resolve(Stmt stmt) {
    stmt.accept(this);
  }

  /**
   * * they turn around and apply the Visitor pattern to the given syntax tree node.
   * @param expr
   */
  private void resolve(Expr expr) {
    expr.accept(this);
  }

  /**
   * * This walks a list of statements and resolves each one. It in turn calls:
   * @param statements
   */
  void resolve(List<Stmt> statements) {
    for(Stmt statement: statements) {
      resolve(statement);
    }
    // for (int i = scopes.size() - 1; i >= 0; i--) {
    //   if(scopes.get(i).containsKey(name.lexeme)) {
    //     // * The resolver hands that number of environments to the interpreter by calling this:
    //     interpreter.resolve(expr, scopes.size() - 1 - i);
    //     return;
    //   }
    // }
  }

  /**
   * * Resolving the function
   * @param function
   */
  private void resolveFunction(Stmt.Function function, FunctionType type) {
    FunctionType enclosingFunction = currentFunction; // * Default NONE
    currentFunction = type;
    beginScope();
    for(Token param: function.params) {
      declare(param);
      define(param);
    }
    resolve(function.body);
    endScope();
    currentFunction = enclosingFunction;
  }

  private void beginScope() {
    scopes.push(new HashMap<String, Boolean>());
  }

  private void endScope() {
    // System.out.println("------- Resolver Scopes -------");
    // for (int i = scopes.size() - 1; i >= 0; i--) {
    //   System.out.println(scopes.get(i));
    // }
    // System.out.println("------- Resolver Scopes -------");
    scopes.pop();
  }

  // * We mark it as “not ready yet” by binding its name to false in the scope map.
  private void declare(Token name) {
    if(scopes.isEmpty()) return;

    Map<String, Boolean> scope = scopes.peek();
    if(scope.containsKey(name.lexeme)) {
      // * When we declare a variable in a local scope, we already know the names of
      // * every variable previously declared in that same scope. If we see a collision,
      // * we report an error.
      Lox.error(name, "Already a variable with this name in this scope.");
    }
    scope.put(name.lexeme, false);
  }

  // * Mark the varible value in scope map to true to mark it as fully initialized
  // * and available for use.
  private void define(Token name) {
    if(scopes.isEmpty()) return;

    scopes.peek().put(name.lexeme, true);
  }

  private void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if(scopes.get(i).containsKey(name.lexeme)) {
        // * The resolver hands that number of environments to the interpreter by calling this:
        interpreter.resolve(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }
}
