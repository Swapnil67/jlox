package lox;

import java.util.List;
import java.util.ArrayList;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

  // private Environment environment = new Environment();
  final Environment globals = new Environment();
  private Environment environment = globals;

  Interpreter() {
    globals.define("clock", new LoxCallable() {
      @Override
      public int arity() { return 0; }

      @Override
      public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000.0;
      }
      
      @Override
      public String toString() { return "<native fn>"; }

    });
  }

  /**
   * * Takes the Statement syntax tree for an expression and evaluates it.
   * 
   * @param expression
   */
  void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        execute(statement);
      }
    } catch (RuntimeError error) {
      Lox.RuntimeError(error);
    }
  }
  
  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitLogicalExpr(Expr.Logical expr) {
    Object left = evaluate(expr.left);
    if(expr.operator.type == TokenType.OR) {
      if(isTruthy(left)) return left;
    } else {
      if(!isTruthy(left)) return left;
    }

    return evaluate(expr.right);
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return -(double) right;
      default:
        break;
    }

    // * Unreachable
    return null;
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return environment.get(expr.name);
  }

  /**
   * * Runtime check for Unary operand
   * 
   * @param operator
   * @param operand
   */
  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double)
      return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  /**
   * * Runtime check for binary operands
   * 
   * @param operator
   * @param left
   * @param right
   */
  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double)
      return;
    throw new RuntimeError(operator, "Operands must be numbers.");
  }

  /**
   * * false and nil are falsey and everything else is truthy
   * 
   * @param object
   * @return boolean
   */
  private boolean isTruthy(Object object) {
    if (object == null)
      return false;
    if (object instanceof Boolean)
      return (boolean) object;
    return true;
  }

  /**
   * * The equality operators support operands of any type, even mixed ones
   * 
   * @param a
   * @param b
   * @return boolean
   */
  private boolean isEqual(Object a, Object b) {
    if (a == null & b == null)
      return true;
    if (a == null)
      return false;

    return a.equals(b);
  }

  /**
   * * To convert a Lox value to a string.
   * 
   * @param object
   * @return String
   */
  private String stringify(Object object) {
    if (object == null)
      return "nil";
    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }

  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  /**
   * * We create new environment for the block scope
   * @param statements
   * @param environment
   */
  void executeBlock(List<Stmt> statements, Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;
      for(Stmt statement : statements) {
        execute(statement);
      }
    }
    finally {
      // * Restore the previous environment
      this.environment = previous;
    }
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    executeBlock(stmt.statements, new Environment(environment));
    return null;
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    // * Java requires that to satisfy the specical capitalized Void return type
    return null;
  }

  /**
   * * Interpreting function calls
   */
  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    LoxFunction function = new LoxFunction(stmt);
    environment.define(stmt.name.lexeme, function);
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    if(isTruthy(evaluate(stmt.condition))) {
      execute(stmt.thenBranch);
    }
    else if(stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }
    return null;
  }


  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    Object value = evaluate(stmt.expression);
    System.out.println(stringify(value));
    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    Object value = null;
    if (stmt.initializer != null) {
      value = evaluate(stmt.initializer);
    }

    environment.define(stmt.name.lexeme, value);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    while(isTruthy(evaluate(stmt.condtion))) {
      execute(stmt.body);
    }
    return null;
  }

  @Override
  public Object visitAssignExpr(Expr.Assign expr) {
    Object value = evaluate(expr.value);
    environment.assign(expr.name, value);
    return value;
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case GREATER:
        // compare two strings
        checkNumberOperands(expr.operator, left, right);
        return (double) left > (double) right;
      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double) left >= (double) right;
      case LESS:
        checkNumberOperands(expr.operator, left, right);
        return (double) left < (double) right;
      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double) left <= (double) right;
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return !isEqual(left, right);
      case MINUS:
        checkNumberOperands(expr.operator, left, right);
        return (double) left - (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }
        if (left instanceof String && right instanceof String) {
          return (String) left + (String) right;
        }
        if (left instanceof String && right instanceof Double) {
          return (String) left + stringify(right);
        } else if (left instanceof Double && right instanceof String) {
          return stringify(left) + (String) right;
        }
        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        return (double) left / (double) right;
      case STAR:
        checkNumberOperands(expr.operator, left, right);
        return (double) left * (double) right;
      default:
        break;

    }

    // * Unreachable null;
    return null;
  }

  @Override
  public Object visitCallExpr(Expr.Call expr) {
    Object callee = evaluate(expr.callee);

    List<Object> arguments = new ArrayList<>();
    for(Expr argument : expr.arguments) {
      arguments.add(evaluate(argument));
    }

    // * Check if valid callee
    if(!(callee instanceof LoxCallable)) {
      throw new RuntimeError(expr.paren, "Can only call functions and classes");
    }

    LoxCallable function = (LoxCallable)callee;
    
    // * Check 'Arity' of the function
    if(arguments.size() != function.arity()) {
      throw new RuntimeError(expr.paren,
          "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
    }

    return function.call(this, arguments);
  }
}
