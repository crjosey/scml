import java.io.IOException;

/**
 * Interpreter
 * 
 * Interpreter.java
 * CS403 | DPL
 *
 * @author Ryan Josey
 */
public class Interpreter {

	public static void main(String[] args) throws IOException {		
		Parser parser = new Parser(args[0]);
		Lexeme parseTree = parser.program();
		Environment globalENV = new Environment(parseTree);
	}
	
	private static void scanner(String fileName) throws IOException {
		Lexer lexer = new Lexer(fileName);
		Lexeme token = null;
		while ((token == null) || (!token.getType().equals("ENDofINPUT"))) {
			token = lexer.lex();
			System.out.println(token);
		}
	}
}