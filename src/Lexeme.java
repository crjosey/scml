import java.util.ArrayList;
import java.util.Arrays;

/**
 * Lexeme
 * 
 * Lexeme.java
 * CS403 | DPL
 *
 * @author Ryan Josey
 */
public class Lexeme {
	public String _type;
	public String _string;
	public Integer _integer;
	public ArrayList<Lexeme> dynamicArray;
	public Lexeme[] staticArray;
	public int _lineNumber;
	public String _errorCode;
	
	public Lexeme left;
	public Lexeme right;
	
	public Lexeme(String type) {
		this._type = type;
		this._string = null;
		this._integer = null;
		this._errorCode = null;
		this.left = null;
		this.right = null;
	}
	
	public Lexeme(String type, int lineNumber) {
		this._type = type;
		this._string = null;
		this._integer = null;
		this._lineNumber = lineNumber;
		this._errorCode = null;
		this.left = null;
		this.right = null;
	}
	
	public Lexeme(String type, String string, int lineNumber) {
		this._type = type;
		this._string = string;
		this._integer = null;
		this._lineNumber = lineNumber;
		this._errorCode = null;
		this.left = null;
		this.right = null;
	}
	
	public Lexeme(String type, int integer, int lineNumber) {
		this._type = type;
		this._string = null;
		this._integer = integer;
		this._lineNumber = lineNumber;
		this.left = null;
		this.right = null;
	}
	
	public String getType() {
		return this._type;
	}
	
	public String getString() {
		return this._string;
	}
	
	public void setString(String value) {
		this._string = value;
	}
	
	public Integer getInteger() {
		return this._integer;
	}
	
	public void setInteger(int value) {
		this._integer = value;
	}
	
	public int getLineNumber() {
		return this._lineNumber;
	}
	
	public void setErrorCode(String code) {
		this._errorCode = code;
	}
	
	public String getErrorCode() {
		return "Error: line: " + this._lineNumber + " code: " + _errorCode;
	}
	
	public void setLeft(Lexeme left) {
		this.left = left;
	}
	
	public Lexeme getLeft() {
		return this.left;
	}
	
	public void setRight(Lexeme right) {
		this.right = right;
	}
	
	public Lexeme getRight() {
		return this.right;
	}
	
	public String debugToString( ) {
		if(this._string != null) {
			return this._type + " " + this._string; 
		}
		else if (this._integer != null) {
			return this._type + " " + this._integer;
		}
		else return this._type;
	}
	
	@Override
	public String toString( ) {
		if(this._string != null) {
			return this._string; 
		}
		else if (this._integer != null) {
			return "" + this._integer;
		}
		else if (this.dynamicArray != null) {
			return "" + this.dynamicArray.toString();
		}
		else if (this.staticArray != null) {
			return "" + Arrays.toString(this.staticArray);
		}
		else if (this._type.equals("NULL")) {
			return "NULL";
		}
		else return this._type;
	}
}