package lox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: generate_ast <output directory>");
    }
    String outputDir = args[0];

    // * Expression = Assign | Binary | Grouping | Literal | Variable | Unary
    defineAst(outputDir, "Expr", Arrays.asList(
        "Assign   : Token name, Expr value",
        "Binary        : Expr left, Token operator, Expr right",
        "Call          : Expr callee, Token paren, List<Expr> arguments",
        "Get           : Expr object, Token name",
        "Grouping      : Expr expression",
        "Literal       : Object value",
        "Logical       : Expr left, Token operator, Expr right",
        "Set           : Expr object, Token name, Expr value",
        "This          : Token keyword",
        "Unary         : Token operator, Expr right",
        "Variable      : Token name"));

    // * Statement = Expression | Print
    defineAst(outputDir, "Stmt", Arrays.asList(
      "Block   : List<Stmt> statements", 
        "Class   : Token name, List<Stmt.Function> methods", 
        "Expression : Expr expression",
        "Function   : Token name, List<Token> params," + 
                    " List<Stmt> body",
        "If         : Expr condition, Stmt thenBranch," +
                    " Stmt elseBranch",
        "Print      : Expr expression",
        "Return     : Token keyword, Expr value",
        "Var        : Token name, Expr initializer",
        "While      : Expr condtion, Stmt body"
        ));
        
  }

  private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
    String path = outputDir + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, "UTF-8");

    writer.println("package lox;");
    writer.println();
    writer.println("import java.util.List;");
    writer.println();
    writer.println("abstract class " + baseName + " {");

    defineVisitor(writer, baseName, types);

    // * AST classes
    for (String type : types) {
      String className = type.split(":")[0].trim();
      String fields = type.split(":")[1].trim();
      defineType(writer, baseName, className, fields);
      writer.println();
    }

    // * The base accept() method
    writer.println();
    writer.println("  abstract <R> R accept(Visitor<R> visitor);");

    writer.println("}");
    writer.close();

  }

  private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
    writer.println("  interface  Visitor<R> {");
    for (String type : types) {
      String typeName = type.split(":")[0].trim();
      writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
    }
    writer.println("  }");
    writer.println();
  }

  public static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
    writer.println("  static class " + className + " extends " + baseName + " {");

    // * Constructor
    writer.println("    " + className + "(" + fieldList + ") {");

    // * Store parameters in fields
    String[] fields = fieldList.split(", ");
    for (String field : fields) {
      String name = field.split(" ")[1];
      writer.println("      this." + name + " = " + name + ";");
    }
    writer.println("    }");

    // * Visitor Pattern
    writer.println();
    writer.println("    @Override");
    writer.println("    <R> R accept(Visitor<R> visitor) {");
    writer.println("      return visitor.visit" + className + baseName + "(this);");
    writer.println("    }");

    // * Fields
    writer.println();
    for (String field : fields) {
      writer.println("    final " + field + ";");
    }

    writer.println("  }");
  }

}

/*
 * run this file with following argument
 * $PWD/lox
 * 
 * Example
 * Run Following command to generate AST
 * /usr/bin/env /opt/homebrew/Cellar/openjdk@11/11.0.20/libexec/openjdk.jdk/Contents/Home/bin/java -cp /Users/swapnil67/Library/Application\ Support/Code/User/workspaceStorage/56eed772553f1fb6d0679af17f7144e1/redhat.java/jdt_ws/complier_c86cd7a4/bin lox.tool.GenerateAst  /Users/swapnil67/Developer/complier/lox
 */