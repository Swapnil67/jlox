package lox;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
  private LoxClass klass;
  private final Map<String, Object> fields = new HashMap<>();

  LoxInstance(LoxClass klass) {
    this.klass = klass;
  }
  
  @Override
  public String toString() {
    return klass.name + " instance";
  }

  /*
   * Get the field of instance
   */
  Object get(Token name) {
    // * Find field on instance
    if(fields.containsKey(name.lexeme)) {
      return fields.get(name.lexeme);
    }
      
    // * Find method 
    LoxFunction method = klass.findMethod(name.lexeme);
    if(method != null) return method.bind(this);
 
    throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
  }

  /*
   * Set the field of instance
   */
  void set(Token name, Object value) {
    fields.put(name.lexeme, value);
  }

}
