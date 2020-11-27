import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PushbackInputStream;

/**
 * Lexer
 * 
 * Lexer.java
 * CS403 | DPL
 *
 * @author Ryan Josey
 */
public class Lexer {
	PushbackInputStream readFile;
	Dequeue pushBackCharacters;
	int lineNumber;
			
	public Lexer(String fileName) throws FileNotFoundException {
		this.readFile = new PushbackInputStream(new FileInputStream(fileName));
		this.pushBackCharacters = new Dequeue();
		lineNumber = 1;
	}
	
	private Integer myRead() throws IOException {
		if (!pushBackCharacters.isEmpty()) {
			return pushBackCharacters.popFront();
		}
		else { 
			int ch = readFile.read(); 
			return ch;
		}
	}
	
	private void myPushBack(Integer ch) { 
		pushBackCharacters.pushBack(ch);
	}
	
	private void skipWhiteSpace() throws IOException {
		int ch = myRead();
		boolean flag = false;
		//while ch is not a space, new line, or tab
		while((ch == 32) || (ch == 10) || (ch == 9)) {
			if (ch == 10) { this.lineNumber++; }
			ch = myRead();
			flag = true;
		}
		if (flag) { myPushBack(ch); }
		else pushBackCharacters.pushFront(ch);
			
	}
	
	private void skipComment() throws IOException {
		int ch = myRead();
		while(true) {
			if(ch == 10) { lineNumber++; }
			else if (ch == 45) { 
				//check if at end of comment -->
				ch = myRead();
				if(ch == 10) { lineNumber++; }
				else if (ch == 45) {
					ch = myRead();
					if(ch == 10) { lineNumber++; }
					else if (ch == 62) break;
				}
			}
			ch = myRead();
		}
	}
	
	public Lexeme lex() throws IOException {
		int nextByte;
		String ch = null;
		skipWhiteSpace();
		nextByte = myRead();
		if(nextByte == -1) { return new Lexeme("ENDofINPUT", lineNumber); }
		else {
			ch = Character.toString((char) nextByte); 
			switch(ch) {
				case "(" : return new Lexeme("OPAREN", lineNumber);
				case ")" : return new Lexeme("CPAREN", lineNumber);
				case ";" : return new Lexeme("SEMICOLON", lineNumber);
				case "{" : return new Lexeme("LEFT-CURLY-BRACKET", lineNumber);
				case "}" : return new Lexeme("RIGHT-CURLY-BRACKET", lineNumber);
				case "'" : return new Lexeme("SINGLE-QOUTE", lineNumber);
				case "#" : return new Lexeme("POUND", lineNumber);
				case "!" : return new Lexeme("EXCLAMATION-POINT", lineNumber);
				case "/" : return new Lexeme("FORWARD-SLASH", lineNumber);
				case "*" : return new Lexeme("TIMES", lineNumber);
				case "^" : return new Lexeme("CARET", lineNumber);
				case "%" : return new Lexeme("MOD", lineNumber);
				case "\\" : return new Lexeme("DIVIDE", lineNumber);
				case "." : return new Lexeme("DOT", lineNumber);
				case "+" : {
					int checkPlus = myRead();
					if (Character.toString((char) checkPlus).equals("+")) return new Lexeme("PLUS-PLUS", lineNumber);
					else { myPushBack(checkPlus); return new Lexeme("PLUS", lineNumber);  }
				}
				case "-" : {
					int checkMinus = myRead();
					if (Character.toString((char) checkMinus).equals("-")) return new Lexeme("MINUS-MINUS", lineNumber);
					else { myPushBack(checkMinus); return new Lexeme("MINUS", lineNumber);  }
				}
				case "=" : {
					int checkEqual = myRead();
					if (Character.toString((char) checkEqual).equals("=")) return new Lexeme("EQUAL-EQUAL", lineNumber);
					else { myPushBack(checkEqual); return new Lexeme("EQUAL", lineNumber); }
				}
				case "<" : {
					int checkBang = myRead();
					if (Character.toString((char) checkBang).equals("!")) {
						int checkDash = myRead();
						if(Character.toString((char) checkDash).equals("-")) {
							skipComment();
							return lex();
						} else { myPushBack(checkBang); myPushBack(checkDash); return new Lexeme("LESS-THAN", lineNumber); }
					} 
					else if (Character.toString((char) checkBang).equals("=")) return new Lexeme("LESS-THAN-EQUAL", lineNumber);
					else { myPushBack(checkBang); return new Lexeme("LESS-THAN", lineNumber); }
					}
				case ">" : {
					int checkEqual = myRead();
					if (Character.toString((char) checkEqual).equals("=")) return new Lexeme("GREATER-THAN-EQUAL", lineNumber);
					else { myPushBack(checkEqual); return new Lexeme("GREATER-THAN", lineNumber); }
				}
				default: {
					if (Character.isDigit((char) nextByte)) {
						myPushBack(nextByte);
						return lexNumber();
					}
					else if (Character.isLetter((char) nextByte) || nextByte == 95) {
						myPushBack(nextByte);
						return lexVariableOrKeyword();
					}
					else if (ch.equals("\"")) {
						return lexString();
					}
					else return new Lexeme("UNKNOWN", ch, lineNumber);
				}
			}	
		}
	}
	
	private Lexeme lexVariableOrKeyword() throws IOException {
		int nextByte;
		String token = "";
		nextByte = myRead();
		while (Character.isDigit((char) nextByte) || Character.isLetter((char) nextByte) || nextByte == 95) {
			token = token + Character.toString((char) nextByte);
			nextByte = myRead();
		}
		myPushBack(nextByte);

		if (token.equals("DOCTYPE")) return new Lexeme("DOCTYPE", lineNumber);
		else if (token.equals("scml")) return new Lexeme("SCML", lineNumber);
		else if (token.equals("head")) return new Lexeme("HEAD", lineNumber);
		else if (token.equals("link")) return new Lexeme("LINK", lineNumber);
		else if (token.equals("global")) return new Lexeme("GLOBAL", lineNumber);
		else if (token.equals("href")) return new Lexeme("HREF", lineNumber);
		else if (token.equals("main")) return new Lexeme("MAIN", lineNumber);
		else if (token.equals("helper")) return new Lexeme("HELPER", lineNumber);
		else if (token.equals("lambda")) return new Lexeme("LAMBDA", lineNumber);
		else if (token.equals("array")) return new Lexeme("ARRAY", lineNumber);
		else if (token.equals("size")) return new Lexeme("SIZE", lineNumber);
		else if (token.equals("args")) return new Lexeme("ARGS", lineNumber);
		else if (token.equals("is")) return new Lexeme("IS", lineNumber);
		else if (token.equals("head")) return new Lexeme("HEAD", lineNumber);
		else if (token.equals("assign")) return new Lexeme("ASSIGN", lineNumber);
		else if (token.equals("print")) return new Lexeme("PRINT", lineNumber);
		else if (token.equals("println")) return new Lexeme("PRINTLN", lineNumber);
		else if (token.equals("function")) return new Lexeme("FUNCTION", lineNumber);
		else if (token.equals("id")) return new Lexeme("ID", lineNumber);
		else if (token.equals("if")) return new Lexeme("IF", lineNumber);
		else if (token.equals("check")) return new Lexeme("CHECK", lineNumber);
		else if (token.equals("else")) return new Lexeme("ELSE", lineNumber);
		else if (token.equals("while")) return new Lexeme("WHILE", lineNumber);
		else if (token.equals("true")) return new Lexeme("TRUE", "true", lineNumber);
		else if (token.equals("false")) return new Lexeme("FALSE", "false", lineNumber);
		else if (token.equals("or")) return new Lexeme("OR", "or", lineNumber);
		else if (token.equals("and")) return new Lexeme("AND", "and", lineNumber);
		else if (token.equals("break")) return new Lexeme("BREAK", lineNumber);
		else if (token.equals("null")) return new Lexeme("NULL", "null", lineNumber);
		else return new Lexeme("VARIABLE", token, lineNumber);
	}
	
	private Lexeme lexNumber() throws IOException {
		int nextByte;
		String token = "";
		nextByte = myRead();
		while (Character.isDigit((char) nextByte)) {
			token = token + Character.toString((char) nextByte);
			nextByte = myRead();
		}
		myPushBack(nextByte);
		return new Lexeme("INTEGER", Integer.parseInt(token), lineNumber);
	}
	
	private Lexeme lexString() throws IOException {
		int nextByte;
		String token = "";
		nextByte = myRead();
		while (nextByte != 34) {
			if (nextByte == 92) {
				token = token + Character.toString((char) nextByte);
				nextByte = myRead();
				token = token + Character.toString((char) nextByte);
				nextByte = myRead();
			}
			else {
				token = token + Character.toString((char) nextByte);
				nextByte = myRead();
			}
		}
		return new Lexeme("STRING", token, lineNumber);
	}
}