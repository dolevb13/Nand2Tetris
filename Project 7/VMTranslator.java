
public class VMTranslator {

    public static void main(String[] args) throws Exception {
    	Parser parser = new Parser(args[0]);
    	String outputFile = args[0].substring(0, args[0].lastIndexOf(".")) + ".asm";
        CodeWriter writer = new CodeWriter(outputFile);
        parser.advance();
        while (parser.hasMoreCommands()) {
        	String type = parser.commandType();
        	if (type == "C_ARITHMETIC") {
        		writer.writeArithmetic(parser.arg1());
        	} else if (type == "C_POP") {
        		writer.writePop(parser.arg1(), parser.arg2());
        	} else if (type == "C_PUSH") {
        		writer.writePush(parser.arg1(), parser.arg2());
        	}
        	 parser.advance();
        }
       writer.close();		
    }
}
            
 
