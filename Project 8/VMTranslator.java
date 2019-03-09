import java.io.File;

public class VMTranslator {

	public static void main(String[] args) throws Exception {
		String fileName = args[0];
		File inputFile = new File(fileName);
		String outputFile;
		CodeWriter writer;
		Parser parser;
		if (inputFile.isFile()) {
			parser = new Parser(fileName);
			outputFile = fileName.substring(0, fileName.lastIndexOf(".")) + ".asm";
			writer = new CodeWriter(outputFile);
			parse(parser, outputFile, writer);
			writer.close();
		} else if (inputFile.isDirectory()) {
			File[] files = inputFile.listFiles();
			outputFile = args[0] + "/" + inputFile.getName() + ".asm";
			writer = new CodeWriter(outputFile);
			writer.writeInit();
			for (File file : files) {
				if (file.getName().endsWith(".vm")) {
					writer.setFileName(file);
					parser = new Parser(file.getAbsolutePath());
					parse(parser, outputFile, writer);
				}
			}
			writer.close();
		}
	}

	public static void parse(Parser parser, String outputFile, CodeWriter writer) throws Exception {
		parser.advance();
		while (parser.hasMoreCommands()) {
			String type = parser.commandType();
			if (type.equals("C_ARITHMETIC")) {
				writer.writeArithmetic(parser.arg1());
			} else if (type.equals("C_POP")) {
				writer.writePop(parser.arg1(), parser.arg2());
			} else if (type.equals("C_PUSH")) {
				writer.writePush(parser.arg1(), parser.arg2());
			} else if (type.equals("C_LABEL")) {
				writer.writeLabel(parser.arg1());
			} else if (type.equals("C_GOTO")) {
				writer.writeGoto(parser.arg1());
			} else if (type.equals("C_IF")) {
				writer.writeIf(parser.arg1());
			} else if (type.equals("C_FUNCTION")) {
				writer.writeFunction(parser.arg1(), parser.arg2());
			} else if (type.equals("C_CALL")) {
				writer.writeCall(parser.arg1(), parser.arg2());
			} else if (type.equals("C_RETURN")) {
				writer.writeReturn();
			}
			parser.advance();
		}
	}
}
