package lox;

import java.util.HashMap;
import java.util.Map;

/**
 * * The bindings that associates variables to values needs to be stored somewhere.
 * * Ever since the lisp folks invented parentheses, this data structure has
 * * been called an "environment"
 */

class Environment {

  final Environment enclosing;

  // * Used to store the bindings
  private final Map<String, Object> values = new HashMap<>();

  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosising) {
    this.enclosing = enclosising;
  }

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    // * If varialbe isn't found in this envrionment, we simply try the enclosing one.
    if(enclosing != null) {
      return enclosing.get(name);
    }

    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  /**
   * * A variable definition binds a new name to a value
   * 
   * @param name
   * @param value
   */
  void define(String name, Object value) {
    values.put(name, value);
  }

  /*
   * This walks a fixed number of hops up the parent chain and returns the environment there
   */
  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }
    return environment;
  }

  /*
   * It walks a fixed number of environments, and then get the value from that map.
   */
  Object getAt(int distance, String name) {
    return ancestor(distance).values.get(name);
  }

  /*
   * It walks a fixed number of environments, and then stuffs the new value in that map.
   */
  void assignAt(int distance, Token name, Object value) {
    ancestor(distance).values.put(name.lexeme, value);
  }

  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }

    // * If varialbe isn't found in this envrionment, we simply try the enclosing one.
    if(enclosing != null) {
      enclosing.assign(name, value);;
      return;
    }

    //* If varible doesn't exitst while assigning its a runtime error */
    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

}
