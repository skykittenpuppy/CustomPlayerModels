package com.tom.cpm.web.gwt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MainWrapper {
	private static boolean isLoaded;
	private static String mode = "Dev";

	public static void main(String[] args) throws Exception {
		System.out.println("GWT Launch Wrapper initializing");
		boolean build, debug = false;
		if(args.length > 0 && (args[0].equals("--build") || args[0].equals("--buildDebug"))) {
			mode = args[2];
			build = true;
			if(args[0].equals("--buildDebug"))debug = true;
		} else if(args.length > 0 && args[0].equals("--cpFix")) {
			String[] cp = System.getProperty("java.class.path").split(";");
			Map<String, Integer> sortMap = new HashMap<>();
			for (int i = 0; i < cp.length; i++) {
				if(cp[i].endsWith(".jar"))
					sortMap.put(cp[i], i + 100);
				else
					sortMap.put(cp[i], i);
			}
			String ncp = Arrays.stream(cp).sorted(Comparator.comparingInt(sortMap::get)).collect(Collectors.joining(";"));
			String[] nargs = new String[] {"java", "-cp", ncp, "com.tom.cpm.web.gwt.MainWrapper"};
			ProcessBuilder pb = new ProcessBuilder(nargs);
			pb.directory(new File("."));
			pb.inheritIO();
			System.out.println("Launching classpath fixed GWT runtime");
			try {
				int i = pb.start().waitFor();
				System.out.println("GWT exit: " + i);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
			return;
		} else {
			build = false;
		}
		checkLoad(new File("."));
		System.out.println("Making launch args");
		List<String> a = new ArrayList<>();
		a.add("-logLevel");
		a.add("INFO");
		a.add("-war");
		a.add("./war");
		if(build) {
			a.clear();
			a.add("-setProperty");
			a.add("cpm.debug=" + debug);
			a.add("-style");
			if(debug)
				a.add("DETAILED");
			else
				a.add("OBFUSCATED");
			a.add("-setProperty");
			a.add("cpm.version=" + args[1]);
			a.add("-saveSource");
			a.add("-setProperty");
			if(mode.equals("Blockbench")) {
				a.add("cpm.webApiEndpoint=https://tom5454.com/cpm/api");
				a.add("-setProperty");
				a.add("cpm.pluginId=cpm_plugin");
			} else {
				a.add("cpm.webApiEndpoint=/cpm/api");
			}
			a.add(mode.equals("Blockbench") ? "com.tom.cpm.CPMBlockbench" : "com.tom.cpm.web.CPM" + mode);
		} else {
			a.add("-style");
			a.add("DETAILED");
			a.add("-setProperty");
			a.add("cpm.debug=true");
			a.add("-setProperty");
			a.add("cpm.version=dev");
			a.add("-setProperty");
			a.add("cpm.webApiEndpoint=https://localhost/cpm/api");
			a.add("-port");
			a.add("8888");
			a.add("-codeServerPort");
			a.add("9997");
			a.add("com.tom.cpm.web.CPM" + mode);

			try(PrintWriter wr = new PrintWriter("cp.txt")) {
				String[] cp = System.getProperty("java.class.path").split(";");
				Map<String, Integer> sortMap = new HashMap<>();
				for (int i = 0; i < cp.length; i++) {
					if(cp[i].endsWith(".jar"))
						sortMap.put(cp[i], i + 100);
					else
						sortMap.put(cp[i], i);
				}
				wr.println(Arrays.stream(cp).sorted(Comparator.comparingInt(sortMap::get)).collect(Collectors.joining(";")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Launch mode: " + mode);
		System.out.println("Launch args: ");
		System.out.println(a);

		Object[] newArgs = new Object[] {a.toArray(new String[0])};
		if(build) {
			System.out.println("Launching GWT Compiler");
			System.out.println();
			Class.forName("com.google.gwt.dev.Compiler").getDeclaredMethod("main", String[].class).invoke(null, newArgs);
		} else {
			System.out.println("Launching GWT DevMode");
			System.out.println();
			Class.forName("com.google.gwt.dev.DevMode").getDeclaredMethod("main", String[].class).invoke(null, newArgs);
		}
	}

	public static void checkLoad(File wd) {
		if(!isLoaded) {
			isLoaded = true;

			System.out.println("Running resource generator");
			boolean min = mode.equals("Viewer");

			Supplier<String> resGen = new Supplier<String>() {
				private String r;
				private long lastGen;

				@Override
				public String get() {
					if(r == null || System.currentTimeMillis() - lastGen > 5000) {
						r = ResourceGen.run(wd, min);
						lastGen = System.currentTimeMillis();
					}
					return r;
				}
			};

			System.out.println("GWT Loading patchers");
			System.setProperty("java.io.tmpdir", "./gwt_temp");
			ClassSrcTransformer.transformers.add(c -> c.regexTransformBody("String\\.format\\(", "com.tom.cpm.web.client.java.Java.format("));
			ClassSrcTransformer.transformers.add(c -> c.regexTransformBody("Integer\\.parseUnsignedInt\\(", "com.tom.cpm.web.client.java.Java.parseUnsignedInt("));
			ClassSrcTransformer.transformers.add(c -> c.regexTransformBody("(\\w+)\\.class.getResourceAsStream\\(", "com.tom.cpm.web.client.java.Java.getResourceAsStream("));
			ClassSrcTransformer.transformers.add(c -> c.regexTransformBody("System\\.nanoTime\\(\\)", "System.currentTimeMillis()"));
			ClassSrcTransformer.transformers.add(c -> c.regexTransformBody("new ThreadPoolExecutor\\([^;]+;", "new com.tom.cpm.web.client.java.AsyncPool();"));
			ClassSrcTransformer.transformers.add(c -> c.regexTransformBody("Byte\\.toUnsignedInt\\(", "com.tom.cpm.web.client.java.Java.toUnsignedInt("));
			//ClassSrcTransformer.transformers.add(c -> c.regexTransformBody("\\/\\/\\$\\{launchTypeName-djhsdafjlasdjlsdjl\\}\\$", mode));
			ClassSrcTransformer.transformers.add(c -> c.regexTransformBody("\\/\\/\\$\\{fill_resource_map_lqsnlna\\}\\$", resGen));
			ClassSrcTransformer.addImportTransformRegex("^java\\.nio\\.(\\w+)$", "com.tom.cpm.web.client.java.nio.$1");
			ClassSrcTransformer.addImportTransformRegex("^java\\.util\\.zip\\.(\\w+)$", "com.tom.cpm.web.client.java.zip.$1");
			ClassSrcTransformer.addImportTransform("java.io.DataInput", "com.tom.cpm.web.client.java.io.DataInput");
			ClassSrcTransformer.addImportTransform("java.io.DataInputStream", "com.tom.cpm.web.client.java.io.DataInputStream");
			ClassSrcTransformer.addImportTransform("java.io.DataOutput", "com.tom.cpm.web.client.java.io.DataOutput");
			ClassSrcTransformer.addImportTransform("java.io.DataOutputStream", "com.tom.cpm.web.client.java.io.DataOutputStream");
			ClassSrcTransformer.addImportTransform("java.io.EOFException", "com.tom.cpm.web.client.java.io.EOFException");
			ClassSrcTransformer.addImportTransform("java.io.File", "com.tom.cpm.web.client.java.io.File");
			ClassSrcTransformer.addImportTransform("java.io.FileInputStream", "com.tom.cpm.web.client.java.io.FileInputStream");
			ClassSrcTransformer.addImportTransform("java.io.FilenameFilter", "com.tom.cpm.web.client.java.io.FilenameFilter");
			ClassSrcTransformer.addImportTransform("java.io.FileNotFoundException", "com.tom.cpm.web.client.java.io.FileNotFoundException");
			ClassSrcTransformer.addImportTransform("java.io.FileOutputStream", "com.tom.cpm.web.client.java.io.FileOutputStream");
			ClassSrcTransformer.addImportTransform("java.io.RandomAccessFile", "com.tom.cpm.web.client.java.io.RandomAccessFile");
			ClassSrcTransformer.addImportTransform("java.io.UTFDataFormatException", "com.tom.cpm.web.client.java.io.UTFDataFormatException");
			ClassSrcTransformer.addImportTransform("java.io.Writer", "com.tom.cpm.web.client.java.io.Writer");
			ClassSrcTransformer.addImportTransform("java.io.BufferedReader", "com.tom.cpm.web.client.java.io.BufferedReader");
			ClassSrcTransformer.addImportTransform("java.io.StringReader", "com.tom.cpm.web.client.java.io.StringReader");
			ClassSrcTransformer.addImportTransform("java.util.UUID", "com.tom.cpm.web.client.java.UUID");
			ClassSrcTransformer.addImportTransform("java.net.Proxy", "com.tom.cpm.web.client.java.Proxy");
			ClassSrcTransformer.addImportTransform("java.util.Base64", "com.tom.cpm.web.client.java.Base64");
			ClassSrcTransformer.addImportTransform("java.io.InputStreamReader", "com.tom.cpm.web.client.java.io.InputStreamReader");
			ClassSrcTransformer.addImportTransform("com.google.gson.Gson", "com.tom.cpm.web.client.java.gson.Gson");
			ClassSrcTransformer.addImportTransform("com.google.gson.GsonBuilder", "com.tom.cpm.web.client.java.gson.GsonBuilder");
			ClassSrcTransformer.addImportTransform("java.io.Reader", "com.tom.cpm.web.client.java.io.Reader");
			ClassSrcTransformer.addImportTransform("java.net.URL", "com.tom.cpm.web.client.java.URL");
			ClassSrcTransformer.addImportTransform("java.util.regex.Pattern", "com.tom.cpm.web.client.java.Pattern");
			ClassSrcTransformer.addImportTransform("java.util.regex.Matcher", "com.tom.cpm.web.client.java.Pattern.Matcher");
			ClassSrcTransformer.addImportTransform("java.io.OutputStreamWriter", "com.tom.cpm.web.client.java.io.OutputStreamWriter");
			ClassSrcTransformer.addImportTransform("java.text.SimpleDateFormat", "com.tom.cpm.web.client.java.SimpleDateFormat");
			ClassSrcTransformer.addImportTransform("com.google.common.hash.Hashing", "com.tom.cpm.web.client.java.Hashing");
			ClassSrcTransformer.addImportTransform("java.net.URI", "com.tom.cpm.web.client.java.URI");
			ClassSrcTransformer.addImportTransform("java.net.URISyntaxException", "com.tom.cpm.web.client.java.URISyntaxException");
			//WASMInjector.load(ClassSrcTransformer.transformers);
			//ClassSrcTransformer.addImportTransform("java.io.*", "com.tom.cpm.web.client.java.io.*");
			//ClassSrcTransformer.transformers.add(new UGWTTransformer());
			//ClassSrcTransformer.buggyFiles.add("com/tom/cpm/web/client/GuiImpl.java");
			ClassSrcTransformer.buggyFiles.add("com/tom/cpm/web/client/resources/Resources.java");

			System.out.println("Init finished");
		}
	}
}
