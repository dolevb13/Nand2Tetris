import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
	private JackTokenizer tokenizer;
	private BufferedWriter writer;

	public CompilationEngine(File inputFile, File outputFile) throws IOException {
		writer = new BufferedWriter(new FileWriter(outputFile));
		tokenizer = new JackTokenizer(inputFile);

		compileClass();

		writer.close();
	}

	public void compileClass() throws IOException {
		writeStartTag("class");

		// 'class'
		tokenizer.advance();
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());

		// 'className'
		tokenizer.advance();
		writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

		// '{'
		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.identifier());

		tokenizer.advance();

		// ('classVarDec')*
		while (tokenizer.tokenType() == TokenType.KEYWORD
				&& (tokenizer.keyword() == Keyword.STATIC || tokenizer.keyword() == Keyword.FIELD)) {
			compileClassVarDec();

			tokenizer.advance();
		}

		// ('subroutineDec')*
		while (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyword() == Keyword.FUNCTION
				|| tokenizer.keyword() == Keyword.CONSTRUCTOR || tokenizer.keyword() == Keyword.METHOD)) {
			compileSubroutineDec();

			tokenizer.advance();
		}

		// '}'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		writeEndTag("class");
	}

	public void compileClassVarDec() throws IOException {
		writeStartTag("classVarDec");

		// 'static' | 'field'
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());

		// 'type'
		compileType(true);

		// 'varName'
		tokenizer.advance();
		writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// ', varName'
		while (tokenizer.symbol() == ',') {
			// 'varName'
			tokenizer.advance();
			writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

			tokenizer.advance();
		}

		// ';'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		writeEndTag("classVarDec");
	}

	public void compileSubroutineDec() throws IOException {
		writeStartTag("subroutineDec");

		// 'constructor' | 'function' | 'method'
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());

		// 'void' | type
		tokenizer.advance();

		if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyword() == Keyword.VOID) {
			writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());
		} else {
			compileType(false);
		}

		// 'subroutineName'
		tokenizer.advance();
		writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

		// '('
		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'parameterList'
		compileParameterList();

		// ')'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'subroutineBody'
		compileSubroutineBody();

		writeEndTag("subroutineDec");
	}

	public void compileParameterList() throws IOException {
		writeStartTag("parameterList");

		tokenizer.advance();

		if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')') {
			writeEndTag("parameterList");

			return;
		}

		compileType(false);

		// 'varName'
		tokenizer.advance();
		writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

		tokenizer.advance();

		while (tokenizer.symbol() == ',') {
			// ','
			writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

			// 'type'
			tokenizer.advance();
			compileType(false);

			// 'className'
			tokenizer.advance();
			writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

			tokenizer.advance();
		}

		// ';'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		writeEndTag("parameterList");
	}

	public void compileSubroutineBody() throws IOException {
		writeStartTag("subroutineBody");

		// '{'
		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'varDec'
		tokenizer.advance();

		while (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyword() == Keyword.VAR) {
			compileVarDec();
			tokenizer.advance();
		}

		// 'statements'
		compileStatements();

		// '}'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		writeEndTag("subroutineBody");
	}

	public void compileVarDec() throws IOException {
		writeStartTag("varDec");

		// 'var'
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());

		// 'type'
		tokenizer.advance();
		compileType(false);

		// 'varName'
		tokenizer.advance();
		writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

		tokenizer.advance();

		while (tokenizer.symbol() == ',') {
			// ','
			writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

			// 'varName'
			tokenizer.advance();
			writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

			tokenizer.advance();
		}

		// ';'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		writeEndTag("varDec");
	}

	public void compileStatements() throws IOException {
		writeStartTag("statements");
		// tokenizer.advance();

		while (tokenizer.tokenType() == TokenType.KEYWORD) {
			switch (tokenizer.keyword()) {
			case LET:
				compileLet();
				tokenizer.advance();
				break;
			case IF:
				compileIf();
				break;
			case WHILE:
				compileWhile();
				
				break;
			case DO:
				compileDo();
				tokenizer.advance();
				break;
			case RETURN:
				compileReturn();
				break;
			}
		}
		writeEndTag("statements");
	}

	public void compileLet() throws IOException {
		writeStartTag("letStatement");

		// 'let'
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());

		// 'varName'
		tokenizer.advance();
		writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());

		tokenizer.advance();

		// optional '['
		if (tokenizer.symbol() == '[') {
			writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

			// 'expression'
			tokenizer.advance();
			compileExpression();

			// ']'
			writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

			tokenizer.advance();
		}

		// '='
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'expression'
		tokenizer.advance();
		compileExpression();

		// ';'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		writeEndTag("letStatement");
	}

	public void compileIf() throws IOException {
		writeStartTag("ifStatement");

		// 'if'
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());

		// '('
		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'expression'
		compileExpression();

		// ')'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// '{'
		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'statements'
		compileStatements();

		// '}'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		tokenizer.advance();

		// 'else'
		if (tokenizer.keyword() == Keyword.ELSE) {
			// 'else'
			writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());

			// '{'
			tokenizer.advance();
			writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

			// 'statements'
			compileStatements();

			// }
			writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		}

		writeEndTag("ifStatement");
	}

	public void compileWhile() throws IOException {
		writeStartTag("whileStatement");

		// 'while'
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());

		// '('
		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'expression'
		tokenizer.advance();
		compileExpression();

		// ')'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// '{'
		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'statements'
		tokenizer.advance();
		compileStatements();

		// '}'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());
		tokenizer.advance();
		writeEndTag("whileStatement");
	}

	public void compileDo() throws IOException {
		writeStartTag("doStatement");

		// 'do'
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());
		tokenizer.advance();

		// 'subroutineCall'
		writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());
		tokenizer.advance();
		compileSubroutineCall();

		// ';'
		tokenizer.advance();
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		writeEndTag("doStatement");
	}

	public void compileReturn() throws IOException {
		writeStartTag("returnStatement");
		writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());
		// 'TRUE' | 'FALSE' | 'NULL' | 'THIS'
		tokenizer.advance();

		// (expression)?
		if (tokenizer.tokenType() != TokenType.SYMBOL
				|| (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() != ';')) {
			compileExpression();
		}

		// ';'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		writeEndTag("returnStatement");
		tokenizer.advance();
	}

	/* ends with advance command */
	public void compileExpression() throws IOException {
		writeStartTag("expression");

		// 'term'
		compileTerm();

		// (op term)?
		while (isOp(tokenizer.symbol())) {
			writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

			tokenizer.advance();
			compileTerm();
		}
		writeEndTag("expression");

	}

	/* ends with advance command */
	public void compileTerm() throws IOException {
		writeStartTag("term");
		//tokenizer.advance();

		switch (tokenizer.tokenType()) {
		case STRING_CONST:
			writeXmlLine(TokenType.STRING_CONST, tokenizer.stringVal());
			break;
		case INT_CONST:
			writeXmlLine(TokenType.INT_CONST, tokenizer.intVal());
			break;
		case KEYWORD:
			// if true || false || null || this
			if (tokenizer.keyword() == Keyword.THIS || tokenizer.keyword() == Keyword.NULL
					|| tokenizer.keyword() == Keyword.TRUE || tokenizer.keyword() == Keyword.FALSE) {
				writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());
			}
			break;
		case SYMBOL:
			// if - || ~
			if (tokenizer.symbol() == '~' || tokenizer.symbol() == '-') {
				writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());
			}
			// if (expression)
			else if (tokenizer.symbol() == '(') {
				writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

				tokenizer.advance();
				compileExpression();

				tokenizer.advance();
				writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());
			}
			break;
		case IDENTIFIER:
			writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());
			tokenizer.advance();
			if (tokenizer.symbol() == '.') {
				// is Subroutine
				compileSubroutineCall();
				tokenizer.advance();
			}
			if (tokenizer.symbol() == '[') {
				// if identifier [expression]
				writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());
				tokenizer.advance();
				compileExpression();
				//tokenizer.advance();
				writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());
				tokenizer.advance();
			}
			writeEndTag("term");
			return;
		}

		writeEndTag("term");
		tokenizer.advance();
	}

	public void compileExpressionList() throws IOException {
		writeStartTag("expressionList");

		if (tokenizer.symbol() != ')') {
			compileExpression();

			while (tokenizer.symbol() == ',') {
				writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

				tokenizer.advance();
				compileExpression();
			}
		}

		writeEndTag("expressionList");
	}

	private boolean isOp(char op) {
		return (op == '+' || op == '-' || op == '*' || op == '/' || op == '&' || op == '|' || op == '<' || op == '>'
				|| op == '=');
	}

	private void compileSubroutineCall() throws IOException {
		// '(' | '.'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		tokenizer.advance();

		if (tokenizer.tokenType() == TokenType.IDENTIFIER) {
			// identifier.identifier(expressionList) else identifier(expressionList)
			writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());
			tokenizer.advance();
		}

		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());

		// 'expression'
		tokenizer.advance();
		compileExpressionList();

		// ')'
		writeXmlLine(TokenType.SYMBOL, tokenizer.symbol());
	}

	private void compileType(boolean toAdvance) throws IOException {
		if (toAdvance == true) {
			tokenizer.advance();
		}

		if (tokenizer.tokenType() == TokenType.KEYWORD) {
			writeXmlLine(TokenType.KEYWORD, tokenizer.keyword());
		} else {
			writeXmlLine(TokenType.IDENTIFIER, tokenizer.identifier());
		}
	}

	private void writeXmlLine(TokenType tag, Keyword keyword) throws IOException {
		writeXmlLine(tag.name().toLowerCase(), keyword.name().toLowerCase());
	}

	private void writeXmlLine(TokenType tag, char symbol) throws IOException {
		switch (symbol) {
		
		case ('<'):
			writeXmlLine(tag.name().toLowerCase(), "&lt;");
			break;
		case ('>'):
			writeXmlLine(tag.name().toLowerCase(), "&gt;");
			break;
		case ('\"'):
			writeXmlLine(tag.name().toLowerCase(), "&quot;");
			break;
		case ('&'):
			writeXmlLine(tag.name().toLowerCase(), "&amp;");
			break;
		default :
			writeXmlLine(tag.name().toLowerCase(), String.valueOf(symbol));
		}
	}

	private void writeXmlLine(TokenType tag, int number) throws IOException {
		writeXmlLine("integerConstant", String.valueOf(number));
	}

	private void writeXmlLine(TokenType tag, String value) throws IOException {
		if (tag == TokenType.STRING_CONST) {
			writeXmlLine("stringConstant", value.replace("\"", ""));
		}
		else {
			writeXmlLine(tag.name().toLowerCase(), value);
		}
	}

	private void writeXmlLine(String tag, String value) throws IOException {
		writer.write(String.format("<%s> ", tag));
		writer.write(value);
		writer.write(String.format(" </%s>", tag));
		writer.newLine();
	}

	private void writeStartTag(String tag) throws IOException {
		writer.write(String.format("<%s>", tag));
		writer.newLine();
	}

	private void writeEndTag(String tag) throws IOException {
		writer.write(String.format("</%s>", tag));
		writer.newLine();
	}
}