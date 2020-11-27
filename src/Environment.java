import java.io.IOException;
import java.util.ArrayList;

/**
 * Environment/Evaluator
 * 
 * Environment.java
 * CS403 | DPL
 *
 * @author Ryan Josey
 */
public class Environment {
	private void throwError(String error, Lexeme lexeme) {
		System.err.println(error + lexeme.debugToString());
		System.exit(0);
	}
	
	public Environment(Lexeme parseTree) throws IOException {
		Lexeme newEnv = createEnv();
		newEnv = eval(parseTree, newEnv);
	}
	
	public Lexeme cons(String type, Lexeme carValue, Lexeme cdrValue) {
		Lexeme _this = new Lexeme(type);
		_this.left = carValue;
		_this.right = cdrValue;
		return _this;
	}
	
	public Lexeme car(Lexeme cell) {
		return cell.left;
	}
	
	public Lexeme cdr(Lexeme cell) {
		return cell.right;
	}
	
	public Lexeme setCar(Lexeme cell, Lexeme value) {
		return cell.left = value;
	}
	
	public Lexeme setCdr(Lexeme cell, Lexeme value) {
		return cell.right = value;
	}
	
	public Lexeme createEnv() {
		return extendEnv(null, null, null);
	}
	
	public Lexeme lookupEnv(Lexeme variable, Lexeme env) {
		while (env != null) {
			Lexeme table = car(env);
			Lexeme vars = car(table);
			Lexeme vals = cdr(table);
			while (vars != null) {
				if (sameVariable(variable, car(vars))) {
					return car(vals);
				}
				vars = cdr(vars);
				vals = cdr(vals);
			}
			env = cdr(env);
		}
		throwError("Error variable undefined: ", variable);
		return new Lexeme("NULL", "null", -1);
	}
	
	public Lexeme updateEnv(Lexeme variable, Lexeme newValue, Lexeme env) {
		Lexeme originalENV = env;
		while (env != null) {
			Lexeme table = car(env);
			Lexeme vars = car(table);
			Lexeme vals = cdr(table);
			while (vars != null) {
				if (sameVariable(variable, car(vars))) {
					return setCar(vals, newValue);
				}
				vars = cdr(vars);
				vals = cdr(vals);
			}
			env = cdr(env);
		}
		return insertEnv(variable, newValue, originalENV);
	}
	
	private boolean sameVariable(Lexeme x1, Lexeme x2) {
		if(x1.getType().equals("VARIABLE") && x2.getType().equals("VARIABLE")) {
			return x1.getString().equals(x2.getString());
		}
		else return false;
	}
	
	public Lexeme insertEnv(Lexeme variable, Lexeme value, Lexeme env) {
		Lexeme table = car(env);
		setCar(table, cons("JOIN", variable, car(table)));
		setCdr(table, cons("JOIN", value, cdr(table)));
		return value;
	}
	
	public Lexeme extendEnv(Lexeme variables, Lexeme values, Lexeme env) {
		return cons("ENV", cons("TABLE", variables, values), env);
	}
	
	public Lexeme eval(Lexeme tree, Lexeme env) throws IOException {
		if(tree != null) {
			switch(tree.getType()) {
				case "INTEGER" : return tree;
				case "VARIABLE" : return lookupEnv(tree, env);
				case "STRING" : return tree;
				case "OPAREN" : return eval(tree.getLeft(), env);
				case "MINUS" : return evalMinus(tree.getLeft(), env);
				case "SCML" : return evalSCML(tree,env);
				case "HEAD" : return evalHead(tree,env);
				case "LINK" : return evalLink(tree,env);
				case "GLOBAL" : return evalGlobal(tree,env);
				case "ASSIGN" : return evalAssign(tree,env);
				case "MAIN" : return eval(tree.left,env);
				case "HELPER" : return eval(tree.left,env);
				case "POUND" : return evalFuncCall(tree,env); 
				case "ARRAY" : return evalArray(tree,env);
				case "LAMBDA" : return evalLambda(tree,env);
				case "TRUE" : return tree;
				case "FALSE" : return tree;
				case "BREAK" : return tree;
				case "NULL" : return tree;
				case "EXPRESSION-LIST" : return evalExpressionList(tree,env);
				case "BINARY" : return evalBinary(tree,env);
				case "STATEMENT" :  return evalStatement(tree,env);
				case "PRINT" : return evalPrint(tree,env);
				case "PRINTLN" : return evalPrintln(tree,env);
				case "FUNCTION-LIST" : return evalFunctionList(tree,env);
				case "FUNCTION" : return evalFuncDef(tree,env);
				case "VARIABLE-LIST" : return evalVariableList(tree,env);
				case "IF" : return evalIf(tree,env);
				case "ELSE-IF" : return evalIf(tree,env);
				case "ELSE" : return evalElse(tree,env);
				case "WHILE" : return evalWhile(tree,env);
				default: {
					throwError("Error Bad Expression: ", tree);
					return new Lexeme("NULL", "null", -1);
				}
			}
		}
		return new Lexeme("NULL", "null", -1);
	}
	
	private Lexeme evalSCML(Lexeme tree, Lexeme env) throws IOException {
		eval(tree.getLeft(), env); //eval head
		eval(tree.getRight().getRight(), env); //eval helper
		eval(tree.getRight().getLeft(), env); //eval main
		return env;
	}
	
	private Lexeme evalHead(Lexeme tree, Lexeme env) throws IOException {
		eval(tree.getLeft(), env); //eval optLink
		eval(tree.getRight(), env); //eval optGlobal
		return env;
	}
	
	private Lexeme evalGlobal(Lexeme tree, Lexeme env) throws IOException {
		if(tree.left != null) {
			Lexeme var = tree.left;
			if (var.getType().equals("VARIABLE")){
				insertEnv(var, new Lexeme("NULL", "null", -1), env);
			}
			else {
				evalAssign(var, env);
			}
		}
		if(tree.right != null) {
			eval(tree.right, env);
		}
		return env;
	}
	
	private Lexeme evalAssign(Lexeme tree, Lexeme env) throws IOException {
		Lexeme value = null;
		if(tree.left.right == null) {
			value = insertEnv(tree.left.left, new Lexeme("NULL", -1), env);
		}
		else if (tree.left._type.equals("VARDEF")) {
			value = eval(tree.left.right, env);
			value = updateEnv(tree.left.left, value, env);
		}
		else if (tree.left.left._type.equals("DOT")) {
			Lexeme object = eval(tree.left.right.left, env);
			value = eval(tree.right, env);
			value = updateEnv(tree.left.right.right, value, object);
		}
		return value;
	}
	
	private Lexeme evalLink(Lexeme tree, Lexeme env) throws IOException {
		importLink(tree, env);
		eval(tree.getRight(), env); //eval optLink
		return env;
	}
	
	private Lexeme importLink(Lexeme tree, Lexeme env) throws IOException {
		Parser parser = new Parser(tree.getLeft().getString());
		Lexeme newParseTree = parser.program();
		return eval(newParseTree, env);
	}
	
	private Lexeme evalFuncDef(Lexeme tree, Lexeme env) {
		Lexeme closure = cons("CLOSURE",env,tree);
		insertEnv(getFuncCallName(tree),closure,env);
		return closure;
	}
	
	private Lexeme evalLambda(Lexeme tree, Lexeme env) {
		Lexeme closure = cons("CLOSURE", env, tree);
		return closure;
	}
	
	private Lexeme evalFuncCall(Lexeme tree, Lexeme env) throws IOException {
		Lexeme closure, args, params, body, denv, eargs, xenv = null;
		args = getFuncCallArgs(tree);
		eargs = evalArgs(args, env);
		Lexeme builtIn = evalBuiltIn(getFuncCallName(tree), eargs, env);
		if(!builtIn._type.equals("FALSE")) {
			return builtIn;
		}
		if(tree.left._type.equals("VARIABLE")) {
			closure = lookupEnv(getFuncCallName(tree), env);
		} else {
			//lambda call
			closure = evalLambda(tree.left, env);
		}
		if(closure.right._type.equals("FUNCTION")) {
			params = getClosureParams(closure);
			body = getClosureBody(closure);
		} else {
			//lambda call
			params = getLambdaClosureParams(closure);
			body = getLambdaClosureBody(closure);
		}
		denv = getClosureEnvironment(closure);
		xenv = extendEnv(params, eargs, denv);
		
		//insert a variable that pints to xenv for object encapsulation
		insertEnv(new Lexeme("VARIABLE", "this", -1), xenv, xenv);
		
		return eval(body, xenv);
	}
	
	private Lexeme getFuncCallName(Lexeme tree) {
		return tree.left;
	}
	
	private Lexeme getFuncCallArgs(Lexeme tree) {
		return tree.right;
	}
	
	private Lexeme getClosureParams(Lexeme tree) {
		return tree.right.right.left;
	}
	
	private Lexeme getLambdaClosureParams(Lexeme tree) {
		return tree.right.left;
	}
	
	private Lexeme getClosureBody(Lexeme tree) {
		return tree.right.right.right;
	}
	
	private Lexeme getLambdaClosureBody(Lexeme tree) {
		return tree.right.right;
	}
	
	private Lexeme getClosureEnvironment(Lexeme tree) {
		return tree.left;
	}
	
	private Lexeme evalArgs(Lexeme args, Lexeme env) throws IOException {
		return eval(args, env);
	}
	
	private Lexeme evalBuiltIn(Lexeme name, Lexeme eargs, Lexeme env) {
		if(name._string != null && name._string.equals("arrayInsert")) {
			return evalArrayInsert(eargs, env);
		}
		else if(name._string != null && name._string.equals("arrayAdd")) {
			return evalArrayAdd(eargs, env);
		}
		else if(name._string != null && name._string.equals("arrayGet")) {
			return evalArrayGet(eargs, env);
		}
		else if(name._string != null && name._string.equals("arraySize")) {
			return evalArraySize(eargs, env);
		}
		else if(name._string != null && name._string.equals("eq")) {
			return evalEQ(eargs, env);
		}
		else if(name._string != null && name._string.equals("isInt")) {
			return evalISINT(eargs, env);
		}
		return new Lexeme("FALSE", "false", -1);
	}
	
	private Lexeme evalEQ(Lexeme eargs, Lexeme env) {
		if(eargs.left == eargs.right.left) {
			return new Lexeme("TRUE", "true", -1);
		}
		else return new Lexeme("FALE", "false", -1);
	}
	
	private Lexeme evalISINT(Lexeme eargs, Lexeme env) {
		if(eargs.left._type.equals("INTEGER")) {
			return new Lexeme("TRUE", "true", -1);
		}
		else return new Lexeme("FALE", "false", -1);
	}
	
	private Lexeme evalMinus(Lexeme tree, Lexeme env) throws IOException {
		Lexeme value = eval(tree, env);
		value.setInteger(-value._integer);
		return value;
	}
	
	private Lexeme evalArray(Lexeme tree, Lexeme env) throws IOException {
		Lexeme newArray = new Lexeme("ARRAY", -1);
		if (tree.left != null) {
			newArray.staticArray = new Lexeme[tree.left.getInteger()];
			newArray._type = "STATIC-ARRAY";
		}
		else {
			newArray.dynamicArray = new ArrayList<Lexeme>();
			newArray._type = "DYNAMIC-ARRAY";
		}
		return insertEnv(tree.right, newArray, env);
	}
	
	private Lexeme evalArrayInsert(Lexeme eargs, Lexeme env) {
		Lexeme array = eargs.left;
		Lexeme newValue = eargs.right.left;
		Lexeme index = eargs.right.right.left;
		if(array._type.equals("STATIC-ARRAY")) {
			array.staticArray[index._integer] = newValue;
			return array;
		}
		else if(array._type.equals("DYNAMIC-ARRAY")) {
			array.dynamicArray.add(index._integer, newValue);
			return array;
		}
		return new Lexeme("FALSE", "false", -1);
	}
	
	private Lexeme evalArrayAdd(Lexeme eargs, Lexeme env) {
		Lexeme array = eargs.left;
		Lexeme newValue = eargs.right.left;
		if(array._type.equals("STATIC-ARRAY")) {
			throwError("Error addArray only works with dyanamic arrays: ", array);
		}
		else if(array._type.equals("DYNAMIC-ARRAY")) {
			array.dynamicArray.add(newValue);
			return array;
		}
		return new Lexeme("FALSE", "false", -1);
	}
	
	private Lexeme evalArrayGet(Lexeme eargs, Lexeme env) {
		Lexeme array = eargs.left;
		Lexeme index = eargs.right.left;
		try {
			if(array._type.equals("STATIC-ARRAY")) {
				Lexeme curr = array.staticArray[index._integer];
				if(curr != null) {
					return curr;
				} else return new Lexeme("NULL", "null", -1);
			}
			else if(array._type.equals("DYNAMIC-ARRAY")) {
				Lexeme curr = array.dynamicArray.get(index._integer);
				if(curr != null) {
					return curr;
				} else return new Lexeme("NULL", "null", -1);
			}
		}
		catch (NullPointerException e) {
			throwError("Error index out of range: ", array);
		}
		return new Lexeme("FALSE", "false", -1);
	}
	
	private Lexeme evalArraySize(Lexeme eargs, Lexeme env) {
		Lexeme array = eargs.left;
		if(array._type.equals("STATIC-ARRAY")) {
			throwError("Error addSize only works with dynamic arrays: ", array);
		}
		else if(array._type.equals("DYNAMIC-ARRAY")) {
			return new Lexeme("INTEGER", array.dynamicArray.size(), -1);
		}
		return new Lexeme("NULL", "null", -1);
	}
	
	private Lexeme evalBinary(Lexeme tree, Lexeme env) throws IOException {
		if(tree.left != null) {
			if(tree.left.getType().equals("DOT")) return evalDot(tree.right, env);
			Lexeme leftArg = eval(tree.right.left, env);
			Lexeme rightArg = eval(tree.right.right, env);
			switch(tree.left.getType()) {
				case "PLUS-PLUS" : {
					Lexeme value = eval(tree.right.left, env);
					if(value._type.equals("INTEGER")) {
						Lexeme newValue = new Lexeme ("INTEGER", (value.getInteger() + 1), -1);
						return updateEnv(tree.right.left, newValue, env);
					} else throwError("ERROR ++ NOT SUPPORTED: ", value);
				}
				case "MINUS-MINUS" : {
					Lexeme value = eval(tree.right.left, env);
					if(value._type.equals("INTEGER")) {
						Lexeme newValue = new Lexeme ("INTEGER", (value.getInteger() - 1), -1);
						return updateEnv(tree.right.left, newValue, env);
					} else throwError("ERROR -- NOT SUPPORTED: ", value);
				}
				case "EXCLAMATION-POINT" : {
					Lexeme condition = eval(tree.right.right, env);
					if(condition._type.equals("TRUE")) {
						return new Lexeme("FALSE", "false", -1);
					} else return new Lexeme("TRUE", "true", -1);
				}
				case "AND" : {
					if(leftArg._type.equals("TRUE") && rightArg._type.equals("TRUE")) {
						return new Lexeme("TRUE", "true", -1);
					} else return new Lexeme("FALSE", "false", -1);
				}
				case "OR" : {
					if(leftArg._type.equals("TRUE") || rightArg._type.equals("TRUE")) {
						return new Lexeme("TRUE", "true", -1);
					} else return new Lexeme("FALSE", "false", -1);
				}
				default : {
					if(rightArg._type.equals("NULL")) {
						return evalBinaryNull(tree.left._type, leftArg, rightArg, env);
					}
					else if(leftArg._type.equals("NULL")) {
						throwError("NULL ERROR: ", tree.right);
					}
					switch(leftArg.getType()) {
						case "STRING" : return evalBinaryString(tree.left._type, leftArg, rightArg, env);
						default : return evalBinaryInteger(tree.left._type, leftArg, rightArg, env);
					}
				}
			}
		}
		return new Lexeme("NULL", "null", -1);
	}
	
	private Lexeme evalBinaryInteger(String type, Lexeme leftArg, Lexeme rightArg, Lexeme env) throws IOException {
		switch(type) {
			case "PLUS" : return new Lexeme("INTEGER", leftArg.getInteger() + rightArg.getInteger(), -1);
			case "MINUS" : return new Lexeme("INTEGER", leftArg.getInteger() - rightArg.getInteger(), -1);
			case "TIMES" : return new Lexeme("INTEGER", leftArg.getInteger() * rightArg.getInteger(), -1);
			case "DIVIDE" : {
				if(rightArg.getInteger() != 0) {
					return new Lexeme("INTEGER", leftArg.getInteger() / rightArg.getInteger(), -1);
				}
				else {
					throwError("Error divide by zero: ", rightArg);
				}
			}
			case "MOD" : return new Lexeme("INTEGER", leftArg.getInteger() % rightArg.getInteger(), -1);
			case "CARET" : return new Lexeme("INTEGER", new Double(Math.pow(leftArg.getInteger(), rightArg.getInteger())).intValue(), -1);
			case "LESS-THAN" : {
				if(leftArg.getInteger() < rightArg.getInteger()) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "LESS-THAN-EQUAL" : {
				if(leftArg.getInteger() <= rightArg.getInteger()) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "GREATER-THAN" : {
				if(leftArg.getInteger() > rightArg.getInteger()) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "GREATER-THAN-EQUAL" : {
				if(leftArg.getInteger() >= rightArg.getInteger()) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "EQUAL-EQUAL" : {
				if(leftArg.getInteger() == rightArg.getInteger()) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
		}
		return new Lexeme("NULL", "null", -1);
	}
	
	private Lexeme evalBinaryString(String type, Lexeme leftArg, Lexeme rightArg, Lexeme env) throws IOException {
		switch(type) {
			case "PLUS" : return new Lexeme("STRING", leftArg.getString() + rightArg.getString(), -1);
			case "MINUS" : throwError("ERROR - NOT SUPPORTED FOR STRINGS: ", leftArg);
			case "TIMES" : throwError("ERROR * NOT SUPPORTED FOR STRINGS: ", leftArg);
			case "DIVIDE" : throwError("ERROR / NOT SUPPORTED FOR STRINGS: ",leftArg);
			case "MOD" : throwError("ERROR % NOT SUPPORTED FOR STRINGS: ", leftArg);
			case "CARET" : throwError("ERROR ^ NOT SUPPORTED FOR STRINGS: ", leftArg);
			case "LESS-THAN" : {
				if(leftArg.getString().compareTo(rightArg.getString()) < 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "LESS-THAN-EQUAL" : {
				if(leftArg.getString().compareTo(rightArg.getString()) <= 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "GREATER-THAN" : {
				if(leftArg.getString().compareTo(rightArg.getString()) > 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "GREATER-THAN-EQUAL" : {
				if(leftArg.getString().compareTo(rightArg.getString()) >= 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "EQUAL-EQUAL" : {
				if(leftArg.getString().compareTo(rightArg.getString()) == 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
		}
		return new Lexeme("NULL", "null", -1);
	}
	
	private Lexeme evalBinaryNull(String type, Lexeme leftArg, Lexeme rightArg, Lexeme env) throws IOException {
		switch(type) {
			case "LESS-THAN" : {
				if(leftArg._type.compareTo(rightArg._type) < 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "LESS-THAN-EQUAL" : {
				if(leftArg._type.compareTo(rightArg._type) <= 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "GREATER-THAN" : {
				if(leftArg._type.compareTo(rightArg._type) > 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "GREATER-THAN-EQUAL" : {
				if(leftArg._type.compareTo(rightArg._type) >= 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
			case "EQUAL-EQUAL" : {
				if(leftArg._type.compareTo(rightArg._type) == 0) {
					return new Lexeme("TRUE", "true", -1);
				} else return new Lexeme("FALSE", "false", -1);
			}
		}
	return new Lexeme("NULL", "null", -1);
	}
	
	private Lexeme evalDot(Lexeme tree, Lexeme env) throws IOException {
		Lexeme closure, args, params, body, denv, eargs, xenv = null;
		Lexeme object = eval(tree.left, env);
		if(tree.right._type.equals("POUND")) {
			args = getFuncCallArgs(tree.right);
			eargs = evalArgs(args, env);
			closure = lookupEnv(getFuncCallName(tree.right), object);
			params = getClosureParams(closure);
			body = getClosureBody(closure);
			denv = getClosureEnvironment(closure);
			xenv = extendEnv(params, eargs, denv);
			//insert a variable that pints to xenv for object encapsulation
			insertEnv(new Lexeme("VARIABLE", "this", -1), xenv, xenv);
			return eval(body, xenv);
		}
		else return eval(tree.right, object);
	}
	
	private Lexeme evalStatement(Lexeme tree, Lexeme env) throws IOException {
		Lexeme result = null;
		while (tree != null) {
			result = eval(tree.left, env);
			tree = tree.right;
		}
		return result;
	}
	
	private Lexeme evalExpressionList(Lexeme tree, Lexeme env) throws IOException {
		Lexeme eTree = new Lexeme("EXPRESSION-LIST", -1);
		if(tree.left != null) {
			eTree.left = eval(tree.left, env);
			eTree.right = eval(tree.right, env);
		}
		return eTree;
	}
	
	private Lexeme evalVariableList(Lexeme tree, Lexeme env) throws IOException {
		Lexeme eTree = new Lexeme("VARIABLE-LIST", -1);
		if(tree.left != null) {
			eTree.left = eval(tree.left, env);
			eTree.right = eval(tree.right, env);
		}
		return eTree;
	}
	
	private Lexeme evalFunctionList(Lexeme tree, Lexeme env) throws IOException {
		if(tree.left != null) {
			eval(tree.left, env);
			eval(tree.right, env);
		}
		return tree;
	}
	
	private Lexeme evalPrint(Lexeme tree, Lexeme env) throws IOException {
		Lexeme eargs = tree.left;
		Lexeme arg = null;
		while(eargs != null && eargs.left != null) {
			arg = eval(eargs.left, env);
			System.out.print(arg);
			eargs = eargs.right;
		}
		return arg;
	}
	
	private Lexeme evalPrintln(Lexeme tree, Lexeme env) throws IOException {
		Lexeme eargs = tree.left;
		Lexeme arg = null;
		while(eargs != null && eargs.left != null) {
			arg = eval(eargs.left, env);
			System.out.print(arg);
			eargs = eargs.right;
		}
		System.out.println("");
		return arg;
	}
	
	private Lexeme evalIf(Lexeme tree, Lexeme env) throws IOException {
		Lexeme check = eval(tree.left, env);
		Lexeme result = null;
		if(check._type.equals("TRUE")) {
			result = eval(tree.right.left, env);
		}
		else {
			result = eval(tree.right.right, env);
		}
		return result;
	}
	
	private Lexeme evalElse(Lexeme tree, Lexeme env) throws IOException {
		Lexeme result = eval(tree.left, env);
		return result;
	}
	
	private Lexeme evalWhile(Lexeme tree, Lexeme env) throws IOException {
		Lexeme check = eval(tree.left, env);
		while(check._type.equals("TRUE")) {
			Lexeme result = eval(tree.right, env);
			if (result._type.equals("BREAK")) break;
			check = eval(tree.left, env);
		}
		return new Lexeme("NULL", "null", -1);
	}
}