import java.io.File;
import java.io.IOException;

public class JackAnalyzer {

	public static void main(String[] args) throws IOException {
		File output = null;
	
		String fileName = args[0];
		File inputFile = new File(fileName);
		
			
			String outputFile = args[0].substring(0, args[0].lastIndexOf(".")) + ".xml";
			output = new File(outputFile);
			
			new CompilationEngine(inputFile, output);
		
	}
}
