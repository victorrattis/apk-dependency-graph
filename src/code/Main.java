package code;


import java.io.File;

import code.io.ArgumentReader;
import code.io.Arguments;
import code.io.Writer;

public class Main {
	
	public static void main(String[] args) {
		/*Arguments arguments = new ArgumentReader(args).read();
		if (arguments == null) {
			return;
		}*/

		SmaliDecoder.decode(
			new File("/home/vhra/Documents/analise-apk/app-local-debug.apk"),
			new File("/home/vhra/workspace/apk-dependency-graph/output/"),
			"classes.dex",
			false,
			15);

		SmaliDecoder.decode(
			new File("/home/vhra/Documents/analise-apk/app-local-debug.apk"),
			new File("/home/vhra/workspace/apk-dependency-graph/output/"),
			"classes2.dex",
			false,
			15);	

		SmaliDecoder.decode(
			new File("/home/vhra/Documents/analise-apk/app-local-debug.apk"),
			new File("/home/vhra/workspace/apk-dependency-graph/output/"),
			"classes3.dex",
			false,
			15);

		/*File resultFile = new File(arguments.getResultPath());
		SmaliAnalyzer analyzer = new SmaliAnalyzer(arguments);
		if (analyzer.run()) {
			new Writer(resultFile).write(analyzer.getDependencies());
			System.out.println("Success! Now open index.html in your browser.");
		}*/
	}
}