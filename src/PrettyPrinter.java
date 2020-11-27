/**
 * Pretty Printer
 * 
 * PrettyPrinter.java
 * CS403 | DPL
 *
 * @author Ryan Josey
 */
public class PrettyPrinter {
	public void prettyPrint(Lexeme tree) {
		prettyPrint(tree, 0, true, false);
	}

	public void prettyPrint(Lexeme tree, int indentation, boolean newLine, boolean indent) {
		if (tree != null) {
			switch(tree.getType()) {
				case "INTEGER" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, tree.getInteger())); 
					if(newLine) System.out.println("");
					break;
				}
				case "VARIABLE" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, tree.getString())); 
					if(newLine) System.out.println("");
					break;
				}
				case "STRING" : {
					System.out.print("\"" + tree.getString() + "\" "); 
					break;
				}
				case "OPAREN" : {
					System.out.print("( ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(" )");
					if(newLine) System.out.println("");
					break;
				}
				case "MINUS" : {
					System.out.print(" - ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "PLUS" : {
					System.out.print(" + ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "TIMES" : {
					System.out.print(" * ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "DIVIDE" : {
					System.out.print(" \\ ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "MOD" : {
					System.out.print(" % ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "PLUS-PLUS" : {
					System.out.print("++");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "MINUS-MINUS" : {
					System.out.print("--");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "CARET" : {
					System.out.print(" ^ ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "LESS-THAN" : {
					System.out.print(" < ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "GREATER-THAN" : {
					System.out.print(" > ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "LESS-THAN-EQUAL" : {
					System.out.print(" <= ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "GREATER-THAN-EQUAL" : {
					System.out.print(" >= ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "EQUAL-EQUAL" : {
					System.out.print(" * ");
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "SCML" : {
					System.out.println("<!DOCTYPE scml>");
					System.out.println("<scml>");
					prettyPrint(tree.getLeft(), (indentation + 10), true, true);
					prettyPrint(tree.getRight().getLeft(), (indentation + 10), true, true);
					prettyPrint(tree.getRight().getRight(), (indentation + 10), true, true);
					System.out.println("</scml>");
					break;
				}
				case "HEAD" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.println(String.format(tab, "<head>"));
					prettyPrint(tree.getLeft(), (indentation + 10), true, true);
					prettyPrint(tree.getRight(), (indentation + 10), true, true);
					System.out.print(String.format(tab, "</head>"));
					if(newLine) System.out.println("");
					break;
				}
				case "LINK" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<link href=\'"));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print("\'>");
					if(newLine) System.out.println("");
					prettyPrint(tree.getRight(), indentation, true, true);
					break;
				}
				case "GLOBAL" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<global> "));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(" </global> ");
					if(newLine) System.out.println("");
					prettyPrint(tree.getRight(), indentation, true, true);
					break;
				}
				case "ASSIGN" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<assign> "));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(" </assign>");
					if(newLine) System.out.println("");
					break;
				}
				case "VARDEF" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(String.format(tab, " is "));
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "MAIN" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.println(String.format(tab, "<main>"));
					prettyPrint(tree.getLeft(), (indentation + 10), true, true);
					System.out.print(String.format(tab, "</main>"));
					if(newLine) System.out.println("");
					break;
				}
				case "HELPER" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.println(String.format(tab, "<helper>"));
					prettyPrint(tree.getLeft(), (indentation + 10), true, true);
					System.out.println(String.format(tab, "</helper>"));
					break;
				}
				case "POUND" : {
					String tab = null;
					if (indent) tab = "%" + (indentation - 10) + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "#"));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(" {");
					prettyPrint(tree.getRight(), indentation, false, false);
					System.out.print("} ");
					if(newLine) System.out.println("");
					break;
				}
				case "ARRAY" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					if(tree.left == null) {
						System.out.print(String.format(tab, "<array>"));
					}
					else {
						System.out.println(String.format(tab, "<array size=\'"));
						prettyPrint(tree.getLeft(), indentation, false, false);
						System.out.print("\'> ");
					}
					prettyPrint(tree.getRight(), indentation, false, false);
					System.out.print(" </array>");
					if(newLine) System.out.println("");
					break;
				}
				case "LAMBDA" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					if(tree.left == null) {
						System.out.println(String.format(tab, "<lambda>"));
					}
					else {
						System.out.print(String.format(tab, "<lambda args=\'"));
						prettyPrint(tree.getLeft(), indentation, false, false);
						System.out.println("\'> ");
					}
					prettyPrint(tree.getRight(), (indentation + 10), true, true);
					System.out.print(String.format(tab,"</lambda>"));
					if(newLine) System.out.println("");
					break;
				}
				case "UNARY-LIST" : {
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "EXPRESSION-LIST" : {
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print("; ");
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "BINARY" : {
					prettyPrint(tree.getRight().getLeft(), indentation, false, false);
					prettyPrint(tree.getLeft(), indentation, false, false);
					prettyPrint(tree.getRight().getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
					}
				case "STATEMENT" : {
					prettyPrint(tree.getLeft(), indentation, true, true);
					prettyPrint(tree.getRight(), indentation, true, true);
					break;
				}
				case "PRINT" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<print> "));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(" </print>");
					if(newLine) System.out.println("");
					break;
				}
				case "PRINTLN" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<println> "));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(" </println>");
					if(newLine) System.out.println("");
					break;
				}
				case "RETURN" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<return> "));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(" </return>");
					if(newLine) System.out.println("");
					break;
				}
				case "FUNCTION-LIST" : {
					prettyPrint(tree.getLeft(), indentation, true, true);
					prettyPrint(tree.getRight(), indentation, true, true);
					break;
				}
				case "FUNCTION" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<function id=\'"));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print("\'");
					tree = tree.getRight();
					if(tree.left != null){
						System.out.print(" args=\'");
						prettyPrint(tree.getLeft(), indentation, false, false);
						System.out.print("\'");
					}
					System.out.println("> ");
					prettyPrint(tree.getRight(), (indentation + 10), true, true);
					System.out.print(String.format(tab,"</function>"));
					if(newLine) System.out.println("");
					break;
				}
				case "VARIABLE-LIST" : {
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print(";");
					prettyPrint(tree.getRight(), indentation, false, false);
					if(newLine) System.out.println("");
					break;
				}
				case "IF" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<if check=\'"));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print("\'> ");
					tree = tree.getRight();
					prettyPrint(tree.getLeft(), (indentation + 10), true, true);
					System.out.println(String.format(tab,"</if>"));
					if(newLine) System.out.println("");
					prettyPrint(tree.getRight(), indentation, true, false);
					break;
				}
				case "ELSE-IF" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<else if check=\'"));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.print("\'> ");
					tree = tree.getRight();
					prettyPrint(tree.getLeft(), (indentation + 10), true, true);
					System.out.print(String.format(tab,"</else if>"));
					if(newLine) System.out.println("");
					prettyPrint(tree.getRight(), indentation, true, false);
					break;
				}
				case "ELSE" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<else>"));
					prettyPrint(tree.getLeft(), (indentation + 10), true, true);
					System.out.print(String.format(tab,"</else>"));
					if(newLine) System.out.println("");
					prettyPrint(tree.getRight(), indentation, true, false);
					break;
				}
				case "WHILE" : {
					String tab = null;
					if (indent) tab = "%" + indentation + "s";
					else tab = "%" + 1 + "s";
					System.out.print(String.format(tab, "<while check=\'"));
					prettyPrint(tree.getLeft(), indentation, false, false);
					System.out.println("\'> ");
					prettyPrint(tree.getRight(), (indentation + 10), true, true);
					System.out.print(String.format(tab,"</while>"));
					if(newLine) System.out.println("");
					break;
				}
			}
		}
	}
}