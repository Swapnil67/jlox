package lox;

/*
 * This class wraps the return value with the accoutrements Java requires for a runtime exception class.
 * The weird super constructor call with those null and false arguments disables some JVM machinery that we don’t need
 * Since we’re using our exception class for control flow and not actual error handling, we don’t need overhead like stack traces.
 */
class Return extends RuntimeException {
  final Object value;

  Return(Object value) {
    super(null, null, false, false);
    this.value = value;
  }
}
