import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

	private BufferedReader reader;
	private String currentLine;
	private String commandType = null;
	private String arg1 = null;
	private String arg2 = null;

	public Parser(String fileName) throws IOException {
		reader = new BufferedReader(new FileReader(fileName));
	}

	public boolean hasMoreCommands() {
		return currentLine != null;
	}

	public void advance() throws IOException {
		currentLine = reader.readLine();
		while (currentLine != null) {
			currentLine = currentLine.replaceAll("(//.*)", "").trim();
			if (!currentLine.isEmpty()) {
				String[] words = currentLine.split(" ");
				if (words[0].equals("add") || words[0].equals("sub") || words[0].equals("neg") || words[0].equals("eq")
						|| words[0].equals("gt") || words[0].equals("lt") || words[0].equals("and")
						|| words[0].equals("or") || words[0].equals("not")) {
					commandType = "C_ARITHMETIC";
					arg1 = words[0];
				} else if (words[0].equals("push")) {
					commandType = "C_PUSH";
					arg1 = words[1];
					arg2 = words[2];
				} else if (words[0].equals("pop")) {
					commandType = "C_POP";
					arg1 = words[1];
					arg2 = words[2];
				} else if (words[0].equals("label")) {
					commandType = "C_LABEL";
					arg1 = words[1]; 
				} else if (words[0].equals("goto")) {
					commandType = "C_GOTO";
					arg1 = words[1];
				} else if (words[0].equals("if-goto")) {
					commandType = "C_IF";
					arg1 = words[1];
				} else if (words[0].equals("function")) {
					commandType = "C_FUNCTION";
					arg1 = words[1];
					arg2 = words[2];
				} else if (words[0].equals("call")) {
					commandType = "C_CALL";
					arg1 = words[1];
					arg2 = words[2];
				} else if (words[0].equals("return")) {
					commandType = "C_RETURN";
				}
				return;
			}
			currentLine = reader.readLine();
		}
		reader.close();
	}

	public String commandType() {
		return this.commandType;
	}

	public String arg1() {
		return this.arg1;
	}

	public String arg2() {
		return this.arg2;
	}
}
