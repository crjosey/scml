import java.io.IOException;

/**
 * Recognizer
 * 
 * Recognizer.java
 * CS403 | DPL
 *
 * @author Ryan Josey
 */
public class Recognizer {
	private Lexeme pendingLexeme = null;
	private Lexeme nextLexeme = null;
	private Lexeme nextNextLexeme = null;
	private Lexer lexer = null;
	
	public Recognizer(String fileName) throws IOException {
		lexer = new Lexer(fileName);
		nextLexeme = lexer.lex();
		nextNextLexeme = lexer.lex();
		advance();
		if(pendingLexeme.getType().equals("ENDofINPUT")) System.exit(0);
	}
	
	private boolean check(String type) {
		return pendingLexeme.getType().equals(type);
	}
	
	private boolean check(String pending, String next) {
		return (pendingLexeme.getType().equals(pending) && nextLexeme.getType().equals(next)) ;
	}
	
	private boolean check(String pending, String next, String nextNext) {
		return    (pendingLexeme.getType().equals(pending) && nextLexeme.getType().equals(next)) 
			   && (nextNextLexeme.getType().equals(nextNext));
	}
	
	private void advance() throws IOException {
		pendingLexeme = nextLexeme;
		nextLexeme = nextNextLexeme;
		nextNextLexeme = lexer.lex();
	}
	
	private void match(String type) throws IOException {
		matchNoAdvance(type);
		System.out.println(pendingLexeme);
		advance();
	}
	
	private void matchNoAdvance(String type) {
		if(!check(type)) {
			this.pendingLexeme.setErrorCode("syntax error");
			System.err.println("Syntax Error: " + "Expected " + type + " on line: " + this.pendingLexeme.getLineNumber());
			System.exit(0);
		}
	}
	
	public void program() throws IOException {
		doctype();
		scml();
	}
	
	private void doctype() throws IOException {
		match("LESS-THAN");
		match("EXCLAMATION-POINT");
		match("DOCTYPE");
		match("SCML");
		match("GREATER-THAN");
	}
	
	private void scml() throws IOException {
		openScmlTag();
		head();
		main();
		helper();
		closeScmlTag();
	}
	
	private void openScmlTag() throws IOException {
		match("LESS-THAN");
		match("SCML");
		match("GREATER-THAN");
	}
	
	private void closeScmlTag() throws IOException {
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("SCML");
		match("GREATER-THAN");
	}
	
	private void head() throws IOException {
		openHead();
		optLink();
		optGlobal();
		closeHead();
	}
	
	private void openHead() throws IOException {
		match("LESS-THAN");
		match("HEAD");
		match("GREATER-THAN");
	}
	
	private void closeHead() throws IOException {
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("HEAD");
		match("GREATER-THAN");
	}
	
	private void link() throws IOException {
		match("LESS-THAN");
		match("LINK");
		match("HREF");
		match("EQUAL");
		match("SINGLE-QOUTE");
		match("STRING");
		match("SINGLE-QOUTE");
		match("GREATER-THAN");
	}
	
	private void optLink() throws IOException {
		if (check("LESS-THAN", "LINK")) {
			link();
			optLink();
		}
	}
	
	private void global() throws IOException {
		match("LESS-THAN");
		match("GLOBAL");
		match("GREATER-THAN");
		optVarAssign();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("GLOBAL");
		match("GREATER-THAN");
	}
	
	private void optGlobal() throws IOException {
		if (check("LESS-THAN", "GLOBAL")) {
			global();
			optGlobal();
		}
	}
	
	private void optVarAssign() throws IOException {
		if (check("VARIABLE")) {
			match("VARIABLE");
		}
		else if (optAssignTagPending()) {
			optAssignTag();
		}
	}
	
	private void main() throws IOException {
		openMain();
		statements();
		closeMain();
	}
	
	private void openMain() throws IOException {
		match("LESS-THAN");
		match("MAIN");
		match("GREATER-THAN");
	}
	
	private void closeMain() throws IOException {
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("MAIN");
		match("GREATER-THAN");
	}
	
	private void helper() throws IOException {
		openHelper();
		functions();
		closeHelper();
	}
	
	private void openHelper() throws IOException {
		match("LESS-THAN");
		match("HELPER");
		match("GREATER-THAN");
	}
	
	private void closeHelper() throws IOException {
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("HELPER");
		match("GREATER-THAN");
	}
	
	private void unary() throws IOException {
		if (check("INTEGER")) match("INTEGER");
		else if (check("STRING")) match("STRING");
		else if (check("VARIABLE")) match("VARIABLE");
		else if (check("MINUS")) {
			match("MINUS");
			unary();
		}
		else if (check("OPAREN")) {
			match("OPAREN");
			expression();
			match("CPAREN");
		}
		else if (functionCallPending()) {
			functionCall();
		}
		else if (arrayPending()) {
			array();
		}
		else if (lambdaPending()) {
			lambda();
		}
		//add class checks here
	}
	
	private boolean unaryPending() {
		return     check("INTEGER") || check("STRING") || check("VARIABLE") 
				|| check("OPAREN")  || check("MINUS")  || functionCallPending()
				|| arrayPending()   || lambdaPending() || check("LESS-THAN", "CLASS");
	}
	
	private void functionCall() throws IOException {
		match("POUND");
		if(lambdaPending()) {
			lambda();
		}
		else match("VARIABLE");
		match("LEFT-CURLY-BRACKET");
		optExpressionList();
		match("RIGHT-CURLY-BRACKET");
	}
	
	private boolean functionCallPending() {
		return check("POUND") || check("POUND", "LESS-THAN", "LAMBDA");
	}
	
	private void array() throws IOException {
		match("LESS-THAN");
		match("ARRAY");
		if (check("SIZE")) {
			match("SIZE");
			match("EQUAL");
			match("SINGLE-QOUTE");
			match("INTEGER");
			match("SINGLE-QOUTE");
		}
		match("GREATER-THAN");
		match("VARIABLE");
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("ARRAY");
		match("GREATER-THAN");
	}
	
	private boolean arrayPending() {
		return check("LESS-THAN", "ARRAY");
	}
	
	private void lambda() throws IOException {
		match("LESS-THAN");
		match("LAMBDA");
		if (check("ARGS")) {
			match("ARGS");
			match("EQUAL");
			match("SINGLE-QOUTE");
			optParamList();
			match("SINGLE-QOUTE");
		}
		match("GREATER-THAN");
		statements();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("LAMBDA");
		match("GREATER-THAN");
	}
	
	private boolean lambdaPending() {
		return check("LESS-THAN", "LAMBDA");
	}
	
	//class functions will go here
	
	//used for print functions
	private void optUnaryList() throws IOException {
		if(unaryPending() && !check("MINUS")) {
			unary();
			optUnaryList();
		}
	}
	
	private void optExpressionList() throws IOException {
		if(expressionPending()) {
			expression();
			match("SEMICOLON");
			optExpressionList();
		}
	}
	
	private void expression() throws IOException {
		unary();
		if (operatorPending()) {
			operator();
			expression();
		}
	}
	
	private boolean expressionPending() {
		return unaryPending();
	}
	
	private void operator() throws IOException {
		if(check("PLUS")) match("PLUS");
		else if(check("MINUS")) match("MINUS");
		else if(check("TIMES")) match("TIMES");
		else if(check("DIVIDE")) match("DIVIDE");
		else if(check("MOD")) match("MOD");
		else if(check("PLUS-PLUS")) match("PLUS-PLUS");
		else if(check("MINUS-MINUS")) match("MINUS-MINUS");
		else if(check("CARET")) match("CARET");
		else if(check("LESS-THAN")) match("LESS-THAN");
		else if(check("GREATER-THAN")) match("GREATER-THAN");
		else if(check("LESS-THAN-EQUAL")) match("LESS-THAN-EQUAL");
		else if(check("GREATER-THAN-EQUAL")) match("GREATER-THAN-EQUAL");
		else match("EQUAL-EQUAL");
	}
	
	private boolean operatorPending() throws IOException {
		return  (  check("PLUS")            || check("MINUS")              || check("TIMES")        
				|| check("PLUS-PLUS")       || check("MINUS-MINUS")        || check("CARET")         
				|| check("LESS-THAN-EQUAL") || check("GREATER-THAN-EQUAL") || check("EQUAL-EQUAL")
				|| check("DIVIDE")          || check("MOD")                || check("LESS-THAN")
				|| check("GREATER-THAN")) && !endTagPending() && !statementTagPending()
				&& !arrayPending() && !lambdaPending();
	}
	
	private void statements() throws IOException {
		if (statementsPending()) {
			statement();
			statements();
		}
	}
	
	private boolean statementsPending() {
		return expressionPending() || ifStatementPending() || whileLoopPending() || statementTagPending() || functionDefPending();
	}

	private void statement() throws IOException {
		if (expressionPending()) expression();
		else if (ifStatementPending()) ifStatement();
		else if (whileLoopPending()) whileLoop();
		else if (statementTagPending()) statementTag();
		else if (functionDefPending()) functions();
	}
	
	private void statementTag() throws IOException {
		if (optAssignTagPending()) {
			optAssignTag();
		}
		else if (check("LESS-THAN", "PRINT")) {
			match("LESS-THAN");
			match("PRINT");
			match("GREATER-THAN");
			optUnaryList();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("PRINT");
			match("GREATER-THAN");
		}
		else if (check("LESS-THAN", "PRINTLN")) {
			match("LESS-THAN");
			match("PRINTLN");
			match("GREATER-THAN");
			optUnaryList();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("PRINTLN");
			match("GREATER-THAN");
		}
		else if (check("LESS-THAN", "RETURN")) {
			match("LESS-THAN");
			match("RETURN");
			match("GREATER-THAN");
			expression();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("RETURN");
			match("GREATER-THAN");
		}
	}
	
	private boolean statementTagPending() {
		return check("LESS-THAN", "ASSIGN") || check("LESS-THAN", "PRINT") || check("LESS-THAN", "PRINTLN") || check("LESS-THAN", "RETURN");
	}
	
	private void optAssignTag() throws IOException {
		match("LESS-THAN");
		match("ASSIGN");
		match("GREATER-THAN");
		varDef();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("ASSIGN");
		match("GREATER-THAN");
	}
	
	private boolean optAssignTagPending() {
		return check("LESS-THAN", "ASSIGN");
	}
	
	private boolean endTagPending() {
		return check("LESS-THAN", "FORWARD-SLASH");
	}
	
	private void varDef() throws IOException {
		match("VARIABLE");
		optInit();
	}
	
	private void optInit() throws IOException {
		if(check("IS")) {
			match("IS");
			expression();
		}
	}
	
	private void functions() throws IOException {
		if(functionDefPending()) {
			functionDef();
			functions();
		}
	}
	
	private void functionDef() throws IOException {
		match("LESS-THAN");
		match("FUNCTION");
		match("ID");
		match("EQUAL");
		match("SINGLE-QOUTE");
		match("VARIABLE");
		match("SINGLE-QOUTE");
		if (check("ARGS")) {
			match("ARGS");
			match("EQUAL");
			match("SINGLE-QOUTE");
			optParamList();
			match("SINGLE-QOUTE");
		}
		match("GREATER-THAN");
		statements();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("FUNCTION");
		match("GREATER-THAN");
	}
	
	private boolean functionDefPending() {
		return check("LESS-THAN", "FUNCTION");
	}
	
	private void paramList() throws IOException {
		match("VARIABLE");
		match("SEMICOLON");
		if (check("VARIABLE")) {
			paramList();
		}
	}
	
	private void optParamList() throws IOException {
		if(check("VARIABLE")) {
			paramList();
		}
	}
	
	private void ifStatement() throws IOException {
		if(check("LESS-THAN")) {
			match("LESS-THAN");
		}
		match("IF");
		match("CHECK");
		match("SINGLE-QOUTE");
		expression();
		match("SINGLE-QOUTE");
		match("GREATER-THAN");
		statements();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("IF");
		match("GREATER-THAN");
		if (optElsePending()) {
			optElse();
		}
	}
	
	private boolean ifStatementPending() {
		return check("IF") || check("LESS-THAN", "IF");
	}
	
	private void optElse() throws IOException {
		match("LESS-THAN");
		match("ELSE");
		if (check("IF")) {
			match("IF");
			match("CHECK");
			match("SINGLE-QOUTE");
			expression();
			match("SINGLE-QOUTE");
			match("GREATER-THAN");
			statements();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("ELSE");
			match("IF");
			match("GREATER-THAN");
			if (optElsePending()) {
				optElse();
			}
		}
		else {
			match("GREATER-THAN");
			statements();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("ELSE");
			match("GREATER-THAN");
		}
	}
	
	private boolean optElsePending() {
		return check("LESS-THAN", "ELSE");
	}

	private void whileLoop() throws IOException {
		match("LESS-THAN");
		match("WHILE");
		match("CHECK");
		match("EQUAL");
		match("SINGLE-QOUTE");
		expression();
		match("SINGLE-QOUTE");
		match("GREATER-THAN");
		statements();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("WHILE");
		match("GREATER-THAN");
	}
	
	private boolean whileLoopPending() {
		return check("LESS-THAN", "WHILE");
	}
}