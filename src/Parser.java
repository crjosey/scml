import java.io.IOException;

/**
 * Parser
 * 
 * Parser.java
 * CS403 | DPL
 *
 * @author Ryan Josey
 */
public class Parser {
	private Lexeme pendingLexeme = null;
	private Lexeme nextLexeme = null;
	private Lexeme nextNextLexeme = null;
	private Lexer lexer = null;
	
	public Parser(String fileName) throws IOException {
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
	
	private Lexeme advance() throws IOException {
		Lexeme currLexeme = pendingLexeme;
		pendingLexeme = nextLexeme;
		nextLexeme = nextNextLexeme;
		nextNextLexeme = lexer.lex();
		return currLexeme;
	}
	
	private Lexeme match(String type) throws IOException {
		matchNoAdvance(type);
		return advance();
	}
	
	private void matchNoAdvance(String type) {
		if(!check(type)) {
			this.pendingLexeme.setErrorCode("syntax error");
			System.err.println("Syntax Error: " + "Expected " + type + " on line: " + this.pendingLexeme.getLineNumber());
			System.exit(0);
		}
	}
	
	public Lexeme program() throws IOException {
		doctype();
		return scml();
	}
	
	private void doctype() throws IOException {
		match("LESS-THAN");
		match("EXCLAMATION-POINT");
		match("DOCTYPE");
		match("SCML");
		match("GREATER-THAN");
	}
	
	private Lexeme scml() throws IOException {
		Lexeme tree = new Lexeme("SCML");
		openScmlTag();
		tree.left = head();
		tree.right = new Lexeme("GLUE");
		tree.right.left = main();
		tree.right.right = helper();
		closeScmlTag();
		return tree;
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
	
	private Lexeme head() throws IOException {
		Lexeme tree = new Lexeme("HEAD");
		openHead();
		tree.left = optLink();
		tree.right = optGlobal();
		closeHead();
		return tree;
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
	
	private Lexeme link() throws IOException {
		Lexeme tree = null;
		match("LESS-THAN");
		match("LINK");
		match("HREF");
		match("EQUAL");
		match("SINGLE-QOUTE");
		tree = match("STRING");
		match("SINGLE-QOUTE");
		match("GREATER-THAN");
		return tree;
	}
	
	private Lexeme optLink() throws IOException {
		if (check("LESS-THAN", "LINK")) {
			Lexeme tree = new Lexeme("LINK");
			tree.left = link();
			tree.right = optLink();
			return tree;
		}
		else return null;
	}
	
	private Lexeme global() throws IOException {
		Lexeme tree = null;
		match("LESS-THAN");
		match("GLOBAL");
		match("GREATER-THAN");
		tree = optVarAssign();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("GLOBAL");
		match("GREATER-THAN");
		return tree;
	}
	
	private Lexeme optGlobal() throws IOException {
		if (check("LESS-THAN", "GLOBAL")) {
			Lexeme tree = new Lexeme("GLOBAL");
			tree.left = global();
			tree.right = optGlobal();
			return tree;
		}
		else return null;
	}
	
	private Lexeme optVarAssign() throws IOException {
		if (check("VARIABLE")) {
			return match("VARIABLE");
		}
		else if (assignTagPending()) {
			return assignTag();
		}
		else return null;
	}
	
	private Lexeme main() throws IOException {
		Lexeme tree = new Lexeme("MAIN");
		openMain();
		tree.left = statements();
		closeMain();
		return tree;
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
	
	private Lexeme helper() throws IOException {
		Lexeme tree = new Lexeme("HELPER");
		openHelper();
		tree.left = functions();
		closeHelper();
		return tree;
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
	
	private Lexeme unary() throws IOException {
		Lexeme tree = null;
		if (check("INTEGER")) tree = match("INTEGER");
		else if (check("STRING")) tree = match("STRING");
		else if (check("VARIABLE")) tree = match("VARIABLE");
		else if (check("TRUE")) tree = match("TRUE");
		else if (check("FALSE")) tree = match("FALSE");
		else if (check("BREAK")) tree = match("BREAK");
		else if (check("NULL")) tree = match("NULL");
		else if (check("MINUS")) {
			tree = match("MINUS");
			tree.left = unary();
		}
		else if (check("OPAREN")) {
			tree = match("OPAREN");
			tree.left = expression();
			match("CPAREN");
		}
		else if (functionCallPending()) {
			tree = functionCall();
		}
		else if (arrayPending()) {
			tree = array();
		}
		else if (lambdaPending()) {
			tree = lambda();
		}
		//add class checks here
		return tree;
	}
	
	private boolean unaryPending() {
		return     check("INTEGER") || check("STRING") || check("VARIABLE") 
				|| check("OPAREN")  || check("MINUS")  || functionCallPending()
				|| arrayPending()   || lambdaPending() || check("LESS-THAN", "CLASS")
				|| check("TRUE")    || check("FALSE")  || check("NULL")
				|| check("BREAK");
	}
	
	private Lexeme functionCall() throws IOException {
		Lexeme tree = match("POUND");
		if(lambdaPending()) {
			tree.left = lambda();
		}
		else {
			tree.left = match("VARIABLE");
		}
		match("LEFT-CURLY-BRACKET");
		tree.right = optExpressionList();
		match("RIGHT-CURLY-BRACKET");
		return tree;
	}
	
	private boolean functionCallPending() {
		return check("POUND") || check("POUND", "LESS-THAN", "LAMBDA");
	}
	
	//array will be removed and added as a built in function
	private Lexeme array() throws IOException {
		match("LESS-THAN");
		Lexeme tree = match("ARRAY");
		if (check("SIZE")) {
			match("SIZE");
			match("EQUAL");
			match("SINGLE-QOUTE");
			tree.left = match("INTEGER");
			match("SINGLE-QOUTE");
		}
		match("GREATER-THAN");
		tree.right = match("VARIABLE");
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("ARRAY");
		match("GREATER-THAN");
		return tree;
	}
	
	private boolean arrayPending() {
		return check("LESS-THAN", "ARRAY");
	}
	
	private Lexeme lambda() throws IOException {
		match("LESS-THAN");
		Lexeme tree = match("LAMBDA");
		if (check("ARGS")) {
			match("ARGS");
			match("EQUAL");
			match("SINGLE-QOUTE");
			tree.left = optParamList();
			match("SINGLE-QOUTE");
		}
		match("GREATER-THAN");
		tree.right = statements();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("LAMBDA");
		match("GREATER-THAN");
		return tree;
	}
	
	private boolean lambdaPending() {
		return check("LESS-THAN", "LAMBDA");
	}
	
	//class functions will go here
	
	//used for print functions
	private Lexeme optUnaryList() throws IOException {
		Lexeme tree = new Lexeme("UNARY-LIST");
		if(unaryPending() && !check("MINUS")) {
			tree.left = unary();
			tree.right = optUnaryList();
			return tree;
		}
		else return null;
	}
	
	private Lexeme optExpressionList() throws IOException {
		Lexeme tree = new Lexeme("EXPRESSION-LIST");
		if(expressionPending()) {
			tree.left = expression();
			if(check("SEMICOLON")) {
				match("SEMICOLON");
			}
			tree.right = optExpressionList();
			return tree;
		}
		else return null;
	}
	
	private Lexeme expression() throws IOException {
		Lexeme tree = unary();
		while (operatorPending()) {
			Lexeme temp = new Lexeme("BINARY");
			temp.left = operator();
			temp.right = new Lexeme("GLUE");
			temp.right.left = tree;
			temp.right.right = unary();
			tree = temp;
		}
		return tree;
	}
	
	private boolean expressionPending() {
		return unaryPending();
	}
	
	private Lexeme operator() throws IOException {
		Lexeme tree = null;
		if(check("PLUS")) tree = match("PLUS");
		else if(check("MINUS")) tree = match("MINUS");
		else if(check("TIMES")) tree = match("TIMES");
		else if(check("DIVIDE")) tree = match("DIVIDE");
		else if(check("MOD")) tree = match("MOD");
		else if(check("PLUS-PLUS")) tree = match("PLUS-PLUS");
		else if(check("MINUS-MINUS")) tree = match("MINUS-MINUS");
		else if(check("CARET")) tree = match("CARET");
		else if(check("EXCLAMATION-POINT")) tree = match("EXCLAMATION-POINT");
		else if(check("LESS-THAN")) tree = match("LESS-THAN");
		else if(check("AND")) tree = match("AND");
		else if(check("OR")) tree = match("OR");
		else if(check("GREATER-THAN")) tree = match("GREATER-THAN");
		else if(check("LESS-THAN-EQUAL")) tree = match("LESS-THAN-EQUAL");
		else if(check("GREATER-THAN-EQUAL")) tree = match("GREATER-THAN-EQUAL");
		else if(check("DOT")) tree = match("DOT");
		else tree = match("EQUAL-EQUAL");
		return tree;
	}
	
	private boolean operatorPending() throws IOException {
		return  (  check("PLUS")            || check("MINUS")              || check("TIMES")        
				|| check("PLUS-PLUS")       || check("MINUS-MINUS")        || check("CARET")         
				|| check("LESS-THAN-EQUAL") || check("GREATER-THAN-EQUAL") || check("EQUAL-EQUAL")
				|| check("DIVIDE")          || check("MOD")                || check("LESS-THAN")
				|| check("GREATER-THAN") 	|| check("EXCLAMATION-POINT")  || check("OR")
				|| check("AND")             || check("DOT"))               && !endTagPending() 
				&& !statementTagPending()   && !arrayPending()             && !lambdaPending();
	}
	
	private Lexeme statements() throws IOException {
		Lexeme tree = null;
		if (statementsPending()) {
			tree = new Lexeme("STATEMENT");
			tree.left = statement();
			tree.right = statements();
		}
		return tree;
	}
	
	private boolean statementsPending() {
		return expressionPending() || ifStatementPending() || whileLoopPending() || statementTagPending() || functionDefPending();
	}

	private Lexeme statement() throws IOException {
		if (expressionPending()) return expression();
		else if (ifStatementPending()) return ifStatement();
		else if (whileLoopPending()) return whileLoop();
		else if (statementTagPending()) return statementTag();
		else if (functionDefPending()) return functionDef();
		else return null;
	}
	
	private Lexeme statementTag() throws IOException {
		Lexeme tree = null;
		if (assignTagPending()) {
			return assignTag();
		}
		else if (check("LESS-THAN", "PRINT")) {
			match("LESS-THAN");
			tree = match("PRINT");
			match("GREATER-THAN");
			//tree.left = optUnaryList();
			tree.left = optExpressionList();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("PRINT");
			match("GREATER-THAN");
		}
		else if (check("LESS-THAN", "PRINTLN")) {
			match("LESS-THAN");
			tree = match("PRINTLN");
			match("GREATER-THAN");
			//tree.left = optUnaryList();
			tree.left = optExpressionList();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("PRINTLN");
			match("GREATER-THAN");
		}
		else if (check("LESS-THAN", "RETURN")) {
			match("LESS-THAN");
			tree = match("RETURN");
			match("GREATER-THAN");
			tree.left = expression();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("RETURN");
			match("GREATER-THAN");
		}
		return tree;
	}
	
	private boolean statementTagPending() {
		return check("LESS-THAN", "ASSIGN") || check("LESS-THAN", "PRINT") || check("LESS-THAN", "PRINTLN") || check("LESS-THAN", "RETURN");
	}
	
	private Lexeme assignTag() throws IOException {
		Lexeme tree =  null;
		match("LESS-THAN");
		tree = match("ASSIGN");
		match("GREATER-THAN");
		if (varDefPending()) {
			tree.left =  varDef();
		}
		else {
			tree.left = expression();
			tree.right = optInit();
		}
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("ASSIGN");
		match("GREATER-THAN");
		return tree;
	}
	
	private boolean assignTagPending() {
		return check("LESS-THAN", "ASSIGN");
	}
	
	private boolean endTagPending() {
		return check("LESS-THAN", "FORWARD-SLASH");
	}
	
	private Lexeme varDef() throws IOException {
		Lexeme tree = new Lexeme("VARDEF");
		tree.left = match("VARIABLE");
		tree.right = optInit();
		return tree;
	}
	
	private boolean varDefPending() {
		return check("VARIABLE", "IS");
	}
	
	private Lexeme optInit() throws IOException {
		if(check("IS")) {
			match("IS");
			return expression();
		}
		else return null;
	}
	
	private Lexeme functions() throws IOException {
		if(functionDefPending()) {
			Lexeme tree = new Lexeme("FUNCTION-LIST");
			tree.left = functionDef();
			tree.right = functions();
			return tree;
		}
		else return null;
	}
	
	private Lexeme functionDef() throws IOException {
		Lexeme tree = null;
		match("LESS-THAN");
		tree = match("FUNCTION");
		match("ID");
		match("EQUAL");
		match("SINGLE-QOUTE");
		tree.left = match("VARIABLE");
		match("SINGLE-QOUTE");
		tree.setRight(new Lexeme("GLUE"));
		if (check("ARGS")) {
			match("ARGS");
			match("EQUAL");
			match("SINGLE-QOUTE");
			tree.right.left = optParamList();
			match("SINGLE-QOUTE");
		}
		match("GREATER-THAN");
		tree.right.right = statements();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("FUNCTION");
		match("GREATER-THAN");
		return tree;
	}
	
	private boolean functionDefPending() {
		return check("LESS-THAN", "FUNCTION");
	}
	
	private Lexeme paramList() throws IOException {
		Lexeme tree = new Lexeme("VARIABLE-LIST");
		tree.left = match("VARIABLE");
		match("SEMICOLON");
		if (check("VARIABLE")) {
			tree.right = paramList();
			return tree;
		}
		else return tree;
	}
	
	private Lexeme optParamList() throws IOException {
		if(check("VARIABLE")) {
			return paramList();
		}
		else return null;
	}
	
	private Lexeme ifStatement() throws IOException {
		Lexeme tree = null;
		if(check("LESS-THAN")) {
			match("LESS-THAN");
		}
		tree = match("IF");
		match("CHECK");
		match("EQUAL");
		match("SINGLE-QOUTE");
		tree.left = expression();
		match("SINGLE-QOUTE");
		match("GREATER-THAN");
		tree.right = (new Lexeme("GLUE"));
		tree.right.left = statements();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("IF");
		match("GREATER-THAN");
		if (optElsePending()) {
			tree.right.right = optElse();
		}
		return tree;
	}
	
	private boolean ifStatementPending() {
		return check("IF") || check("LESS-THAN", "IF");
	}
	
	private Lexeme optElse() throws IOException {
		Lexeme tree = null;
		match("LESS-THAN");
		match("ELSE");
		if (check("IF")) {
			tree = new Lexeme("ELSE-IF");
			match("IF");
			match("CHECK");
			match("EQUAL");
			match("SINGLE-QOUTE");
			tree.left = expression();
			match("SINGLE-QOUTE");
			match("GREATER-THAN");
			tree.right = new Lexeme("GLUE");
			tree.right.left = statements();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("ELSE");
			match("IF");
			match("GREATER-THAN");
			if (optElsePending()) {
				tree.right.right = optElse();
			}
		}
		else {
			tree = new Lexeme("ELSE");
			match("GREATER-THAN");
			tree.left = statements();
			match("LESS-THAN");
			match("FORWARD-SLASH");
			match("ELSE");
			match("GREATER-THAN");
		}
		return tree;
	}
	
	private boolean optElsePending() {
		return check("LESS-THAN", "ELSE");
	}

	private Lexeme whileLoop() throws IOException {
		Lexeme tree = null;
		match("LESS-THAN");
		tree = match("WHILE");
		match("CHECK");
		match("EQUAL");
		match("SINGLE-QOUTE");
		tree.left = expression();
		match("SINGLE-QOUTE");
		match("GREATER-THAN");
		tree.right = statements();
		match("LESS-THAN");
		match("FORWARD-SLASH");
		match("WHILE");
		match("GREATER-THAN");
		return tree;
	}
	
	private boolean whileLoopPending() {
		return check("LESS-THAN", "WHILE");
	}
}