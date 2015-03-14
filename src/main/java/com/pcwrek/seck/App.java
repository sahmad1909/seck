package com.pcwrek.seck;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

/**
 * Hello world!
 *
 */
public class App {

	public static Map<String, Document> crawlerMap = new HashMap<String, Document>();

	public static List<String> allURLList = new ArrayList<String>();

	// private void traversFolder(String URL, int depth, int currentDepth)
	// throws IOException {
	//
	// if (currentDepth < depth) {
	//
	// getURLList(URL);
	//
	// }
	//
	// currentDepth++;
	// File fileList[] = dir.listFiles();
	// if (fileList != null) {
	// for (int i = 0; i < fileList.length; i++) {
	// if (fileList[i].isDirectory()) {
	// if (currentDepth <= depth
	// && !matchExcludeDir(FilenameUtils
	// .getBaseName(fileList[i].getAbsolutePath()))) {
	// traversFolder(fileList[i], depth, currentDepth, fileExt);
	// deleteFolder(fileList[i], depth, currentDepth);
	// }
	// } else {
	// if (matchFileExt(fileExt,
	// FilenameUtils.getExtension(fileList[i].getPath()))) {
	// deleteFile(fileList[i].getPath(), this.numberOfDays);
	// }
	// }
	// }
	// }
	// }

	public static void visitUrl(int currentValue, int depth, String URL)
			throws IOException {
		List<String> urlList = getURLList(URL);
		currentValue++;
		System.out.println("Current Depth : " + currentValue);
		if (urlList != null && !urlList.isEmpty())
			if (currentValue < depth) {
				for (String url : urlList) {
					allURLList.add(url);
					System.out.println(url);
					getURLList(url);
				}
			}
	}

	/**
	 * 
	 * Input the URL needs to visit Get all HREF link of that URL
	 * 
	 */

	public static List<String> getURLList(String URL) throws IOException {

		// Connection.Response response = Jsoup.connect(URL).execute();
		List<String> urlList = new ArrayList<String>();

		Connection.Response response = Jsoup
				.connect(URL)
				.userAgent(
						"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
				.timeout(10000).execute();

		System.out.println("Code Status : " + response.statusCode());
		if (response.statusCode() != 404 && response.statusCode() != 403) {
			// Document document = Jsoup.connect(URL).get();

			Document document = Jsoup
					.connect(URL)
					.userAgent(
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
					.timeout(10000).get();

			Elements hrefLinks = document.select("a[href]");

			for (Element link : hrefLinks) {

				if (!link.attr("abs:href").startsWith("https")) {
					urlList.add(link.attr("abs:href"));
				}
			}
		}

		return urlList;
	}

	public static List<String> fileExt = new ArrayList<String>();

	public static void fileExtension() {

		String[] resourceFileArray = { "pdf", "doc", "docx", "mp3", "xsl",
				"xslx", "png", "jpeg", "gif" };

		for (String ext : resourceFileArray) {
			fileExt.add(ext);
		}

	}

	private static void extractFromFile(final Parser parser,
			final String fileName) throws IOException, SAXException,
			TikaException {
		long start = System.currentTimeMillis();
		BodyContentHandler handler = new BodyContentHandler(10000000);
		Metadata metadata = new Metadata();
		InputStream content = new FileInputStream(fileName);
		parser.parse(content, handler, metadata, new ParseContext());	

		
		for (String name : metadata.names()) {
			System.out.println(name + ":\t" + metadata.get(name));
		}
		System.out.println(String.format(
				"------------ Processing took %s millis\n\n",
				System.currentTimeMillis() - start));
	}

	public static void main(String[] args) throws Exception {

		String path = "C:\\temp\\resource\\4a036bf2-2acc-48f2-a155-760c5860fff4.gif";

		Tika tika = new Tika();

		System.out.println("File Type : " + tika.detect(path));
		
		Parser parser = new AutoDetectParser();
		
		extractFromFile(parser,path);

	

		// System.out.println(FilenameUtils.getExtension("htt://www.yaaho.com/insid/image/fi.(8ledoc"));

		// Document doc = Jsoup.connect("http://www.google.com").get();
		//
		// Elements links = doc.select("a[href]");
		//
		// print("\nLinks: (%d)", links.size());
		// for (Element link : links) {
		// print(" * a: <%s>  (%s)", link.attr("abs:href"),
		// trim(link.text(), 35));
		// }

		// visitUrl(0, 2, "http://www.calstatela.edu//");

		// Document doc;
		// try {
		//
		// // need http protocol
		// doc = Jsoup.connect("https://play.google.com/?hl=en&tab=w8").get();
		//
		//
		//
		//
		// // get page title
		// String title = doc.title();
		// System.out.println("title : " + title);
		//
		// // get all links
		// Elements links = doc.select("a[href]");
		// for (Element link : links) {
		//
		// // get the value from href attribute
		// System.out.println("\nlink : " + link.attr("href"));
		// System.out.println("text : " + link.text());
		//
		// }
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}

	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

}
