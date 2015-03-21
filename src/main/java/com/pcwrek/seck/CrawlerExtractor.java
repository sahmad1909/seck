package com.pcwrek.seck;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * Ref:http://jsoup.org/
 *
 */

public class CrawlerExtractor {

	public static Set<String> allURLList = new HashSet<String>();
	
	public static List<String> fileExt = new ArrayList<String>();
	
	public static final String path = "C:\\temp\\resource\\";
	


	static final String[] stopwords = { "a", "about", "above", "above",
			"across", "after", "afterwards", "again", "against", "all",
			"almost", "alone", "along", "already", "also", "although",
			"always", "am", "among", "amongst", "amoungst", "amount", "an",
			"and", "another", "any", "anyhow", "anyone", "anything", "anyway",
			"anywhere", "are", "around", "as", "at", "back", "be", "became",
			"because", "become", "becomes", "becoming", "been", "before",
			"beforehand", "behind", "being", "below", "beside", "besides",
			"between", "beyond", "bill", "both", "bottom", "but", "by", "call",
			"can", "cannot", "cant", "co", "con", "could", "couldnt", "cry",
			"de", "describe", "detail", "do", "done", "down", "due", "during",
			"each", "eg", "eight", "either", "eleven", "else", "elsewhere",
			"empty", "enough", "etc", "even", "ever", "every", "everyone",
			"everything", "everywhere", "except", "few", "fifteen", "fify",
			"fill", "find", "fire", "first", "five", "for", "former",
			"formerly", "forty", "found", "four", "from", "front", "full",
			"further", "get", "give", "go", "had", "has", "hasnt", "have",
			"he", "hence", "her", "here", "hereafter", "hereby", "herein",
			"hereupon", "hers", "herself", "him", "himself", "his", "how",
			"however", "hundred", "ie", "if", "in", "inc", "indeed",
			"interest", "into", "is", "it", "its", "itself", "keep", "last",
			"latter", "latterly", "least", "less", "ltd", "made", "many",
			"may", "me", "meanwhile", "might", "mill", "mine", "more",
			"moreover", "most", "mostly", "move", "much", "must", "my",
			"myself", "name", "namely", "neither", "never", "nevertheless",
			"next", "nine", "no", "nobody", "none", "noone", "nor", "not",
			"nothing", "now", "nowhere", "of", "off", "often", "on", "once",
			"one", "only", "onto", "or", "other", "others", "otherwise", "our",
			"ours", "ourselves", "out", "over", "own", "part", "per",
			"perhaps", "please", "put", "rather", "re", "same", "see", "seem",
			"seemed", "seeming", "seems", "serious", "several", "she",
			"should", "show", "side", "since", "sincere", "six", "sixty", "so",
			"some", "somehow", "someone", "something", "sometime", "sometimes",
			"somewhere", "still", "such", "system", "take", "ten", "than",
			"that", "the", "their", "them", "themselves", "then", "thence",
			"there", "thereafter", "thereby", "therefore", "therein",
			"thereupon", "these", "they", "thickv", "thin", "third", "this",
			"those", "though", "three", "through", "throughout", "thru",
			"thus", "to", "together", "too", "top", "toward", "towards",
			"twelve", "twenty", "two", "un", "under", "until", "up", "upon",
			"us", "very", "via", "was", "we", "well", "were", "what",
			"whatever", "when", "whence", "whenever", "where", "whereafter",
			"whereas", "whereby", "wherein", "whereupon", "wherever",
			"whether", "which", "while", "whither", "who", "whoever", "whole",
			"whom", "whose", "why", "will", "with", "within", "without",
			"would", "yet", "you", "your", "yours", "yourself", "yourselves",
			"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "1.", "2.",
			"3.", "4.", "5.", "6.", "11", "7.", "8.", "9.", "12", "13", "14",
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
			"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			"terms", "CONDITIONS", "conditions", "values", "interested.",
			"care", "sure", ".", "!", "@", "#", "$", "%", "^", "&", "*", "(",
			")", "{", "}", "[", "]", ":", ";", ",", "<", ".", ">", "/", "?",
			"_", "-", "+", "=", "a", "b", "c", "d", "e", "f", "g", "h", "i",
			"j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z", "contact", "grounds", "buyers", "tried",
			"said,", "plan", "value", "principle.", "forces", "sent:", "is,",
			"was", "like", "discussion", "tmus", "diffrent.", "layout",
			"area.", "thanks", "thankyou", "hello", "bye", "rise", "fell",
			"fall", "psqft.", "http://", "km", "miles" };

	public static String[] resourceFileArray = { "pdf", "doc", "docx", "mp3",
			"xsl", "xslx" };

	/**
	 * 
	 * @param currentValue
	 * @param depth
	 * @param URL
	 * @param rootURL
	 * @throws IOException
	 */
	public static void visitUrl(int currentValue, int depth, String URL,
			String rootURL) throws IOException {
		List<String> urlList = getURLList(URL);

		// if (!URL.contains(rootURL)) {
		currentValue++;
		// System.out.println("Current Depth : " + currentValue);
		// }

		if (urlList != null && !urlList.isEmpty())
			if (currentValue < depth && URL.contains(rootURL)) {
				for (String url : urlList) {
					if (!allURLList.contains(url)) {
						allURLList.add(url);
						System.out.println(url);
						visitUrl(currentValue, depth, url, rootURL);
					}
				}
			}

	}

	/**d
	 * Get all list of URL for specified URL
	 * 
	 * @param URL
	 * @return
	 * @throws IOException
	 */
	public static List<String> getURLList(String URL) throws IOException {

		List<String> urlList = new ArrayList<String>();

		if (URL != null && URL.length() > 0) {

			try {
				Connection.Response response = Jsoup
						.connect(URL)
						.userAgent(
								"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(10000).execute();

				System.out.println("Code Status : " + response.statusCode());
				if (response.statusCode() != 404
						&& response.statusCode() != 403) {

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
			} catch (Exception ex) {
				System.err.print(" SSL certificate required for :" + URL);
			}
		}

		return urlList;
	}

	/**
	 * Storing the data into File
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void storeData(String filePath) throws Exception {

		// Map<String,Map<String,Object>> urlData = new
		// HashMap<String,Map<String,Object>>();

		JSONObject urlDataJsonObject = new JSONObject();

		for (String url : allURLList) {
			// Map<String, Set<String>> resoruceFileMap = new HashMap<String,
			// Set<String>>();
			
			try{
			Map<String, Object> urlSpecificData = new HashMap<String, Object>();

			Connection.Response response = Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
					.timeout(10000).execute();

			if (response.statusCode() != 404 && response.statusCode() != 403) {

				Document document = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
						.timeout(10000).get();

				// Get Body Text.
				if (document.body() != null && document.body().text() != null) {
					urlSpecificData.put(url, removeStopWords(document.body()
							.text()));

				}

				// Get HTML Header.
				// if (document.head() != null) {
				// urlSpecificData.put("htmlheader", document.head());
				// }

				// Get HTTP Header.
				if (response.headers() != null) {
					urlSpecificData.put("httpheader", response.headers());
				}

				// Itreate Resource File array

				for (String resource : resourceFileArray) {
					String query = "a[href$=." + resource + "]";
					urlSpecificData.put(resource, getResource(query, document));
				}

				// Get All images resoruce.
				// resoruceFileMap.put("images", pageImageFiles(document));

				urlSpecificData.put("images", pageImageFiles(document));

				urlDataJsonObject.put(url, urlSpecificData);

				writeJsonToFile(urlDataJsonObject, filePath);

				// JSONObject obj = new JSONObject();
				//
				// obj.put("resources", resoruceFileMap);

				System.out.println(" === Record Inserted =====");

			}

			}catch(Exception ex){
				System.out.println("Failed to Connect URL : " + url);
			}	
		} // For loop end.

	}

	/**
	 * Writing JSON object inot file
	 * 
	 * @param jsonObject
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeJsonToFile(JSONObject jsonObject, String filePath)
			throws IOException {

		FileWriter file = new FileWriter(filePath, true);
		file.write(jsonObject.toJSONString());
		file.flush();
		file.close();

	}

	/**
	 * This function will be using from pulling the resources url from document
	 * of the page
	 * 
	 * @param query
	 * @param document
	 * @return
	 * @throws Exception 
	 */
	public static Set<String> getResource(String query, Document document) throws Exception {
		Set<String> resourceList = new HashSet<String>();

		Elements elements = document.select(query);

		for (Element element : elements) {
			
			System.out.println(" Resource File = " + element.attr("abs:href"));
			
			String resourceFile = element.attr("abs:href");
			
			String newFileName = newFileName(resourceFile);
			
			if(newFileName!=null){
				
				downLoadResourceFile(resourceFile, path+newFileName);
				resourceList.add(resourceFile);
			}
			
			
		}
		return resourceList;
	}

	/**
	 * Get image files specified in URL document.
	 * 
	 * @param document
	 * @throws Exception 
	 */
	public static Set<String> pageImageFiles(Document document) throws Exception {

		Set<String> imageList = new HashSet<String>();
		Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		for (Element image : images) {
			System.out.println("\nImage src : " + image.attr("abs:src"));
            
			String imageSource = image.attr("abs:src");			
			String newFileName = newFileName(imageSource);			
			if(newFileName!=null){				
				downLoadResourceFile(imageSource, path+newFileName);
				imageList.add(newFileName);
			}
			
			
		}
		return imageList;
	}
	
	
	/**
	 * 
	 */
	public static void fileExtension(){		
		
		 String[] resourceFileArray = { "pdf", "doc", "docx", "mp3",
			"xsl", "xslx","png","jpeg","gif" };
		 
		 for(String ext:resourceFileArray){		 
			 fileExt.add(ext);
		 }
		
	}

	
	/**
	 * 
	 * @param pageString
	 * @return
	 */
	public static String removeStopWords(String pageString) {

		String withoutStopString = "";

		if (pageString != null) {

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(pageString);

			while (scanner.hasNext()) {

				String token = scanner.next();
				boolean stopFlag = false;

				for (String stopWord : stopwords) {

					if (token.trim().equalsIgnoreCase(stopWord.trim())) {
						stopFlag = true;
						System.out.println("Stopping Word = " + token);
						break;
					}

				}

				if (!stopFlag) {
					withoutStopString += token + " ";
				}

			}

		}

		return withoutStopString;
	}

	
	/**
	 * 
	 * @param soruceFileUrl
	 * @param renameFile
	 * @return
	 * @throws Exception
	 */
	public static String downLoadResourceFile(String soruceFileUrl, String renameFile) {
		
        String fileExtString = FilenameUtils.getExtension(soruceFileUrl);
        
        String fileName="";        
        
        if(fileExtString != null && fileExtString.length() > 0 && fileExt.contains(fileExtString.toLowerCase())){
        	
        	fileName = FilenameUtils.getName(soruceFileUrl);
        	
        	try{
        	URL url = new URL(soruceFileUrl);
			InputStream in = url.openStream();
			
			OutputStream out = new BufferedOutputStream(new FileOutputStream(renameFile));
			
			for (int b; (b = in.read()) != -1;) {
				out.write(b);
			}

			out.close();
			in.close();
        	}catch(Exception ex){
        		System.out.println("Failed to open connection");
        	}
        }		
      return fileName;
	}
	
	
	/**
	 * 
	 * @param sourceFile
	 * @return
	 */
	public static String newFileName(String sourceFile){
		
		String fileExtString = FilenameUtils.getExtension(sourceFile);
		
		if(fileExtString != null && fileExtString.length() > 0 && fileExt.contains(fileExtString.toLowerCase())){			
			return UUID.randomUUID().toString() + "."+fileExtString;
		}		
		
		return null;	}
	
	
	
	public static void main(String[] args) throws Exception {
		
	
		
//		 String url = args[0];
//		 String depth = args[1];
		
		fileExtension();
		String filePath = "hw1.json";
		String url = "http://www.calstatela.edu";
		String depth = "2";
		 visitUrl(0, new Integer(depth), url, url);
		 storeData(path+filePath);

	}
	
	
	
}
