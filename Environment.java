package lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private final Map<String, Object> values = new HashMap<>();

  Object get(Token name) {
    if(values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  /**
   * * A variable definition binds a new name to a value
   * @param name
   * @param value
   */
  void define(String name, Object value) {
    values.put(name, value);
  }



}
