package lox;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import static lox.TokenType.*;

class Parser {

  private static class ParseError extends RuntimeException {
  }

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   * * This parses the series of statements as many as it can find until it hits
   * * the end of the input.
   * @return
   */
  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }

    return statements;
  }

  private Expr expression() {
    // return equality();
    return assignment();
  }

  /**
   * * This can be a declaration or a statement
   * @return statement
   */
  private Stmt declaration() {
    try {
      if (match(CLASS))
        return classDeclaration();
      if (match(FUN))
        return function("function");
      if (match(VAR))
        return varDeclaration();
      return statement();
    } catch (ParseError e) {
      synchronize();
      return null;
    }
  }

  /**
   * * Parsing the class
   * @return class statement
   */
  private Stmt classDeclaration() {
    Token name = consume(IDENTIFIER, "Expect class name.");
    consume(LEFT_BRACE, "Expect '{' before class body.");

    List<Stmt.Function> methods = new ArrayList<>();
    while(!check(RIGHT_BRACE) && !isAtEnd()) {
      methods.add(function("method"));
    }

    consume(RIGHT_BRACE, "Expect '}' after class body.");
    return new Stmt.Class(name, methods);
  }

  /**
   * * Program is a list of statements we parse one of those statements using this
   * * method
   * * Parse the statement
   * * [PRINT]
   * @return statement
   */
  private Stmt statement() {
    if (match(FOR))
      return forStatement();
    if (match(IF))
      return ifStatement();
    if (match(PRINT))
      return printStatement();
    if(match(RETURN)) 
      return returnStatement();
    if (match(WHILE))
      return whileStatement();
    if (match(LEFT_BRACE))
      return new Stmt.Block(block());

    return expressionStatement();
  }

  private Stmt.Function function(String kind) {
    Token name = consume(IDENTIFIER, "Expect " + kind + " name.");

    consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");

    // * This is like the code for handling arguments in a call
    List<Token> parameters = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        if (parameters.size() >= 255) {
          error(peek(), "Can't have more than 255 parameters.");
        }
        parameters.add(consume(IDENTIFIER, "Expect parameter name."));
      } while (match(COMMA));
    }

    consume(RIGHT_PAREN, "Expect ')' after parameters.");
    consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
    List<Stmt> body = block();
    return new Stmt.Function(name, parameters, body);
  }

  private Stmt forStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'for'.");

    Stmt initializer;
    if (match(SEMICOLON)) {
      initializer = null;
    } else if (match(VAR)) {
      initializer = varDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expr condition = null;
    if (!check(SEMICOLON)) {
      condition = expression();
    }
    consume(SEMICOLON, "Expect ';' after loop condition.");

    Expr increment = null;
    if (!check(RIGHT_PAREN)) {
      increment = expression();
    }
    consume(RIGHT_PAREN, "Expect ')' after for clauses.");

    Stmt body = statement();

    if (increment != null) {
      body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
    }

    // * If no condition then make it an infinite loop
    if (condition == null)
      condition = new Expr.Literal(true);

    // * Desugaring for loops
    body = new Stmt.While(condition, body);

    if (initializer != null) {
      body = new Stmt.Block(Arrays.asList(initializer, body));
    }

    return body;
  }

  /**
   * * Parses If Statements
   * 
   * @return Instance of Stmt.If class
   */
  private Stmt ifStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after if condition.");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  /**
   * * Parses Print Statements
   * 
   * @return Instance of Stmt.Print class
   */
  private Stmt printStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }


  /**
   * * Parses Return Statements
   * 
   * @return Instance of Stmt.Return class
   */
  private Stmt returnStatement() {
    Token keyword = previous();
    Expr value = null;
    if(!check(SEMICOLON)) {
      value = expression();
    }

    consume(SEMICOLON, "Expect ';', after return value.");
    return new Stmt.Return(keyword, value);
  }

  /**
   * * Parses Var Statements
   * 
   * @return Instance of Stmt.Var class
   */
  private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new Stmt.Var(name, initializer);
  }

  /**
   * * Parses While Statements
   * 
   * @return Instance of Stmt.While class
   */
  private Stmt whileStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'while'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after condition.");

    Stmt body = statement();

    return new Stmt.While(condition, body);
  }

  /**
   * * Parses Expressions Statments
   * 
   * @return Instance of Stmt.Expression class
   */
  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(SEMICOLON, "Expect ';' after expression.");
    return new Stmt.Expression(expr);
  }

  /**
   * * Parses the block "{" declaration* "}"
   * 
   * @return List of statements
   */
  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  private Expr assignment() {
    Expr expr = or();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable) expr).name;
        return new Expr.Assign(name, value);
      } else if(expr instanceof Expr.Get) {
        Expr.Get get = (Expr.Get)expr;
        return new Expr.Set(get.object, get.name, value);
      }

      error(equals, "Invalid assignment target.");
    }
    return expr;
  }

  private Expr or() {
    Expr expr = and();

    while (match(OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr and() {
    Expr expr = equality();

    while (match(AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  /**
   * * [BANG_EQUAL, EQUAL_EQUAL]
   * 
   * @return expression
   */
  private Expr equality() {
    Expr expr = comparision();
    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparision();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  /**
   * * [GREATER, GREATER_EQUAL, LESS, LESS_EQUAL]
   * 
   * @return expression
   */
  private Expr comparision() {
    Expr expr = term();
    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * * [MINUS, PLUS]
   * 
   * @return expression
   */
  private Expr term() {
    Expr expr = factor();
    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  /**
   * * [STAR, SLASH]
   * 
   * @return expression
   */
  private Expr factor() {
    Expr expr = unary();
    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  /**
   * * [BANG, MINUS]
   * 
   * @return expression
   */
  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }
    // return primary();
    return call();
  }

  /**
   * Parses single function call
   * 
   * @return expression
   */
  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        if (arguments.size() >= 255) {
          error(peek(), "Can't have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match(COMMA));
    }
    Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
    return new Expr.Call(callee, paren, arguments);
  }

  /**
   * * Parses function call
   * @return expression
   */
  private Expr call() {
    Expr expr = primary();

    while (true) {
      if (match(LEFT_PAREN)) {
        expr = finishCall(expr);
      } else if(match(DOT)) {
        Token name = consume(IDENTIFIER, "Expect property name after '.'.");
        expr = new Expr.Get(expr, name);
      } else {
        break;
      }
    }
    return expr;
  }

  private Expr primary() {
    if (match(FALSE))
      return new Expr.Literal(false);
    if (match(TRUE))
      return new Expr.Literal(true);
    if (match(NIL))
      return new Expr.Literal(null);

    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }

    if(match(THIS)) {
      return new Expr.This(previous());
    }

    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  /**
   * * Similar to match in that it checks if the next token is of the expected
   * * type.
   * 
   * @param type
   * @param message
   * @return
   */
  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();
    throw error(peek(), message);
  }

  /**
   * * Returns true if the current token is of the given type.
   * 
   * @param type
   * @return boolean
   */
  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  /**
   * * Consumes the current token and returns it.
   * 
   * @return Token
   */
  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  /**
   * * Checks if we've run out of tokens to parse.
   * 
   * @return boolean
   */
  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  /**
   * * Returns the current token we have yet to consume
   * 
   * @return Token
   */
  private Token peek() {
    return tokens.get(current);
  }

  /**
   * * Returns most recently consumed token
   * 
   * @return Token
   */
  private Token previous() {
    return tokens.get(current - 1);
  }

  private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
  }

  /**
   * * It discards tokens until it thinks it has found a statement boundary
   */
  private void synchronize() {
    advance();
    while (!isAtEnd()) {
      if (previous().type == SEMICOLON)
        return;
      switch (peek().type) {
        case IF:
        case FOR:
        case FUN:
        case VAR:
        case CLASS:
        case PRINT:
        case WHILE:
        case RETURN:
          return;
        default:
          break;
      }
      advance();
    }
  }

}
