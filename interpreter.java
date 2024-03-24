package lox;

class Interpreter implements Expr.Visitor<Object> {

  /**
   * * Takes the syntax tree for an expression and evaluates it.
   * 
   * @param expression
   */
  void interpret(Expr expression) {
    try {
      Object value = evaluate(expression);
      System.out.println(Stringify(value));
    } catch (RuntimeError error) {
      Lox.RuntimeError(error);
    }
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
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
   * @return
   */
  private String Stringify(Object object) {
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

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case GREATER:
        // TODO compare two strings
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
          return (String) left + Stringify(right);
        } else if (left instanceof Double && right instanceof String) {
          return Stringify(left) + (String) right;
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
}
