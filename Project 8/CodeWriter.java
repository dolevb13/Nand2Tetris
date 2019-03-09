import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
	private static final String ADD = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "M=D+M\n";

	private static final String SUB = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "M=M-D\n";

	private static final String NEG = "@SP\n" + "A=M-1\n" + "M=-M\n";

	private static final String AND = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "M=D&M\n";

	private static final String OR = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "M=D|M\n";

	private static final String NOT = "@SP\n" + "A=M-1\n" + "M=!M\n";

	private static final String PUSH = "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n";

	private static final String POP = "@R13\n" + "M=D\n" + "@SP\n" + "AM=M-1\n" + "D=M\n" + "@R13\n" + "A=M\n" + "M=D\n";

	private static int count = 0;
	private String fileName;
	private BufferedWriter outputFile;
	private String funcName;

	public CodeWriter(String fileName) throws IOException {
		File file = new File(fileName);
		outputFile = new BufferedWriter(new FileWriter(file));
	}

	private String counter() {
		count += 1;
		return Integer.toString(count);
	}

	private String EQ() {
		String num = counter();
		String equal = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@EQ.true." + num + "\n" + "D;JEQ\n"
				+ "@SP\n" + "A=M-1\n" + "M=0\n" + "@EQ.after." + num + "\n" + "0;JMP\n" + "(EQ.true." + num + ")\n"
				+ "@SP\n" + "A=M-1\n" + "M=-1\n" + "(EQ.after." + num + ")\n";
		return equal;
	}

	private String GT() {
		String num = counter();
		String greaterThen = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@GT.true." + num + "\n"
				+ "\nD;JGT\n" + "@SP\n" + "A=M-1\n" + "M=0\n" + "@GT.after." + num + "\n" + "0;JMP\n" + "(GT.true."
				+ num + ")\n" + "@SP\n" + "A=M-1\n" + "M=-1\n" + "(GT.after." + num + ")\n";
		return greaterThen;
	}

	private String LT() {
		String num = counter();
		String lessThen = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@LT.true." + num + "\n" + "D;JLT\n"
				+ "@SP\n" + "A=M-1\n" + "M=0\n" + "@LT.after." + num + "\n" + "0;JMP\n" + "(LT.true." + num + ")\n"
				+ "@SP\n" + "A=M-1\n" + "M=-1\n" + "(LT.after." + num + ")\n";
		return lessThen;
	}

	public void setFileName(File fileOut) {
		fileName = fileOut.getName();
	}

	public void writeArithmetic(String command) throws Exception {
		switch (command) {
		case "add":
			outputFile.write(ADD);
			break;
		case "sub":
			outputFile.write(SUB);
			break;
		case "neg":
			outputFile.write(NEG);
			break;
		case "eq":
			outputFile.write(EQ());
			break;
		case "gt":
			outputFile.write(GT());
			break;
		case "lt":
			outputFile.write(LT());
			break;
		case "and":
			outputFile.write(AND);
			break;
		case "or":
			outputFile.write(OR);
			break;
		case "not":
			outputFile.write(NOT);
			break;
		default:
			throw new Exception("unidentified command");
		}
	}

	public void writePop(String memorySegment, String num) throws Exception {
		switch (memorySegment) {
		case "local":
			outputFile.write("@LCL\n" + "D=M\n" + "@" + num + "\n" + "D=D+A\n" + POP);
			break;
		case "argument":
			outputFile.write("@ARG\n" + "D=M\n" + "@" + num + "\n" + "D=D+A\n" + POP);
			break;
		case "this":
			outputFile.write("@THIS\n" + "D=M\n" + "@" + num + "\n" + "D=D+A\n" + POP);
			break;
		case "that":
			outputFile.write("@THAT\n" + "D=M\n" + "@" + num + "\n" + "D=D+A\n" + POP);
			break;
		case "pointer":
			if (num.equals("0")) {
				outputFile.write("@THIS\n" + "D=A\n" + POP);
			} else {
				outputFile.write("@THAT\n" + "D=A\n" + POP);
			}
			break;
		case "static":
			outputFile.write("@" + fileName + "." + num + "\n" + "D=A\n" + POP);
			break;
		case "temp":
			outputFile.write("@R5\n" + "D=A\n" + "@" + num + "\n" + "D=D+A\n" + POP);
			break;
		default:
			throw new Exception("unidentified memory segment");
		}
	}

	public void writePush(String memorySegment, String num) throws Exception {
		switch (memorySegment) {
		case "local":
			outputFile.write("@LCL\n" + "D=M\n" + "@" + num + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
			break;
		case "argument":
			outputFile.write("@ARG\n" + "D=M\n" + "@" + num + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
			break;
		case "this":
			outputFile.write("@THIS\n" + "D=M\n" + "@" + num + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
			break;
		case "that":
			outputFile.write("@THAT\n" + "D=M\n" + "@" + num + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
			break;
		case "pointer":
			if (num.equals("0")) {
				outputFile.write("@THIS\n" + "D=M\n" + PUSH);
			} else {
				outputFile.write("@THAT\n" + "D=M\n" + PUSH);
			}
			break;
		case "constant":
			outputFile.write("@" + num + "\n" + "D=A\n" + PUSH);
			break;
		case "static":
			outputFile.write("@" + fileName + "." + num + "\n" + "D=M\n" + PUSH);
			break;
		case "temp":
			outputFile.write("@R5\n" + "D=A\n" + "@" + num + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
			break;
		default:
			throw new Exception("unidentified memory segment");
		}
	}
	
	public void writeInit() throws Exception {
		outputFile.write("@256\n" + "D=A\n" + "@SP\n" + "M=D\n"); 
		writeCall("Sys.init", "0");
	}

	public void writeLabel(String label) throws Exception { 
		outputFile.write("(" + funcName + "$" + label + ")\n");
	}

	public void writeGoto(String label) throws Exception {
		outputFile.write("@" + funcName + "$" + label + "\n" + "0;JMP\n");
	}

	public void writeIf(String label) throws Exception {
		outputFile.write("@SP\n" + "AM=M-1\n" + "D=M\n" + "@" + funcName + "$" + label + "\n" + "D;JNE\n");
	}

	public void writeFunction(String f, String k) throws Exception { 
		String s = "(" + f + ")\n" + "@SP\n" + "A=M\n";
		int kk = Integer.parseInt(k);
		for (int i = 0; i < kk; i += 1) {
			s += "M=0\n" + "A=A+1\n";
		}
		s += "D=A\n" + "@SP\n" + "M=D\n";
		outputFile.write(s);
	}

	public void writeCall(String f, String n) throws Exception { 
		String c = counter();
		outputFile.write("@SP\n" + "D=M\n" + "@R13\n" + "M=D\n" + "@RET." + c + "\n" + "D=A\n" + "@SP\n" + "A=M\n"
				+ "M=D\n" + "@SP\n" + "M=M+1\n" + "@LCL\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n"
				+ "@ARG\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n" + "@THIS\n" + "D=M\n" + "@SP\n"
				+ "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n" + "@THAT\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n"
				+ "M=M+1\n" + "@R13\n" + "D=M\n" + "@" + n + "\n" + "D=D-A\n" + "@ARG\n" + "M=D\n" + "@SP\n" + "D=M\n"
				+ "@LCL\n" + "M=D\n" + "@" + f + "\n" + "0;JMP\n" + "(RET." + c + ")\n");
	}

	public void writeReturn() throws Exception {
		outputFile.write("@LCL\n" + "D=M\n" + "@5\n" + "A=D-A\n" + "D=M\n" + "@R13\n" + "M=D\n" + "@SP\n" + "A=M-1\n"
				+ "D=M\n" + "@ARG\n" + "A=M\n" + "M=D \n" + "D=A+1\n" + "@SP\n" + "M=D\n" + "@LCL\n" + "AM=M-1\n"
				+ "D=M\n" + "@THAT\n" + "M=D\n" + "@LCL\n" + "AM=M-1\n" + "D=M\n" + "@THIS\n" + "M=D\n" + "@LCL\n"
				+ "AM=M-1\n" + "D=M\n" + "@ARG\n" + "M=D\n" + "@LCL\n" + "A=M-1\n" + "D=M\n" + "@LCL\n" + "M=D\n"
				+ "@R13\n" + "A=M\n" + "0;JMP\n");
	}

	public void close() throws IOException {
		outputFile.close();
	}
}