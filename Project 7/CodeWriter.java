import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class CodeWriter {
	private static final String ADD =
		"@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "M=D+M\n";

	private static final String SUB =
		"@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "M=M-D\n";

	private static final String NEG =
		"@SP\n" + "A=M-1\n" + "M=-M\n";

	private static final String AND =
		"@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "M=D&M\n";

	private static final String OR =
		"@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "M=D|M\n";

	private static final String NOT =
		"@SP\n" + "A=M-1\n" + "M=!M\n"; 
  
	private static final String PUSH =
		"@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n";

	private static final String POP =
		"@R13\n" + "M=D\n" + "@SP\n" + "AM=M-1\n" + "D=M\n" + "@R13\n" + "A=M\n" + "M=D\n";

	private static int count = 0;
	private String fileName;
	private BufferedWriter outputFile;
		

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
	    String equal = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@EQ.true." + num + "\n" + "D;JEQ\n" +
	    			"@SP\n" + "A=M-1\n" + "M=0\n" + "@EQ.after." + num + "\n" + "0;JMP\n" + "(EQ.true." + num + ")\n" +
	    			"@SP\n" + "A=M-1\n" + "M=-1\n" + "(EQ.after." + num + ")\n";
	    return equal;
		}

		private String GT() {
	    String num = counter();
	    String greaterThen = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@GT.true." + num + "\n" + "\nD;JGT\n" +
	    			"@SP\n" + "A=M-1\n" + "M=0\n" + "@GT.after." + num + "\n" + "0;JMP\n" + "(GT.true." + num + ")\n" +
	    			"@SP\n" + "A=M-1\n" + "M=-1\n" + "(GT.after." + num + ")\n";
	    return greaterThen;
		}

		private String LT() {
	    String num = counter();
	    String lessThen = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@LT.true." + num + "\n" + "D;JLT\n" +
	    			"@SP\n" + "A=M-1\n" + "M=0\n" + "@LT.after." + num + "\n" + "0;JMP\n" + "(LT.true." + num + ")\n" +
	    			"@SP\n" + "A=M-1\n" + "M=-1\n" + "(LT.after." + num + ")\n";
	    return lessThen;
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
        default: throw new Exception("unidentified memory segment");
		}
	}

	public void writePush(String memorySegment, String num) throws Exception {
		switch(memorySegment) {	
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
		default: throw new Exception("unidentified memory segment");
		}
	}
	
	public void close() throws IOException{
        outputFile.close();
    }
}