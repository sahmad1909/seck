package com.pcwrek.seck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PageRank {

	// TFIDF
	//
	// DOCUMENT - Total document word
	// - Frequency of each word in the document.
	// Totalword / unique words

	public static String path = "";
	public static final String hw2 = "hw2.json";
	public static Map<String, Map<String, Integer>> wordMap = new HashMap<String, Map<String, Integer>>();
	public static Map<String, Integer> documentIdMap = new HashMap<String, Integer>();
	public static int documentId = 1;
	public static Map<String, List<String>> pageLinks = new HashMap<String, List<String>>();
	public static Map<String, Double> pageRankingMap = new HashMap<String, Double>();
	public static Map<String, Map<String, Integer>> tfidfMap = new HashMap<String, Map<String, Integer>>();
	public static Map<String, Set<String>> wordExsitInDocument = new HashMap<String, Set<String>>();
	public static Map<String, Integer> totalWordCount = new HashMap<String, Integer>();
	public static Map<String,Integer> uniqueWordCount = new HashMap<String,Integer>();
	public static Integer totalLinksCount = 0;
	public static String[] stopwords;

	public static Map<String, Integer> googePageRankMap = new HashMap<String, Integer>();

	public static JSONObject tfidfJsonObject = new JSONObject();

	public static final String index = "index.json";
	
	public static final String rank = "rank.json";

	// Read Stop words fiel and create array
	// Read hw2 json file and get metadata and document data.
	// Remove stop words.
	// Create inverded index
	// Write into JSON file.

	public static void main(String[] args) throws Exception {

		String resorucePath = new File(new File("").getAbsolutePath())
				.getParent();
		resorucePath = resorucePath + "//resource//";
		File resoruceFolder = new File(resorucePath);
		resoruceFolder.mkdir();
		path = resoruceFolder.getAbsolutePath() + "\\";
		
		
		
		String cfile = args[1];
		
	
//		-c hw2.json -s words.txt -i index.json -r rank.json
		
		if(args!=null && args.length == 8 && args[0].equals("-c")  && args[2].equals("-s") && args[3].equals("words.txt") && args[4].equals("-i") && args[5].equals("index.json") && args[6].equals("-r")  && args[7].equals("rank.json")){

		// Read Stop Words.
		ClassLoader classLoader = PageRank.class.getClassLoader();
//		classLoader.getResource("words.txt").toString();
		
//		cclassLoader.getResource("words.txt").toString());
	
		InputStream is = classLoader.getResourceAsStream("words.txt");
		
	

//		System.out.println(classLoader.getResource("words.txt").toString());
		stopwords = readStopWord(is);

		
		// Reading Control file created in hw2.
		JSONArray jsonArray = readJsonFile(path + cfile);

		for (Object object : jsonArray) {

			totalLinksCount++;
			JSONObject jsonObject = (JSONObject) object;

			JSONArray jsonArrayImage = (JSONArray) jsonObject.get("images");
			JSONArray jsonArrayResoruce = (JSONArray) jsonObject
					.get("resources");

			JSONArray jsonArrayLink = (JSONArray) jsonObject.get("links");

			String rootURL = (String) jsonObject.get("url");
			String metadata = readResoruceArray(jsonArrayImage,
					jsonArrayResoruce);
			String pageWords = removeStopWords((String) jsonObject
					.get("pagedata"));
			
			String pageDateWithMetaData = pageWords + metadata;

			System.out.println(pageDateWithMetaData);

			
			// Getting the word count for each document and save in the mage 
			wordCount(pageDateWithMetaData, (String) jsonObject.get("url"));
			
			// Creating the link map to be use for getting the page rank using link page rank.
			createPageLinkMap(rootURL, jsonArrayLink);

			
			// Build the object to get page TFIDF freq.
			buildTFIDF(pageDateWithMetaData, rootURL);
			
			
			// Getting Page Rank using Google utility
			googlePageRank(rootURL);
			
			
			System.out
					.println(" ----------------------------------------------------------------------------------------------------------------------------------------");

		}

		// printIndexMap();
		// printDocumentMap();
		
		// Writing Josn into Index file.
		writeInexJsonFile(path + index);
		
		// Setting the initial page ranking using the links map created above  
		setupFirstPageRanking();
		
		
		// Calculateing the page rank using 3 iteration.
		pageRanking();

		
		printRanking();

		// Creating the TFIDF using the algorithim 
		createTFIDFRank();

		
		// Write Ranking JSON file at disk
		mergeRankingandWriteIntoFile(path + rank);
		
		System.out.println(tfidfJsonObject.toJSONString());
		
		}else{
			
		System.out.println("=== Invalid Parameters Passed ===");
		}

	}

	
	
	public static void mergeRankingandWriteIntoFile(String filePath) throws IOException{
		
		/**
		 *  document   linkRank  TFIDF GoogleRank.
		 *  
		 *  document:http://www.calstatela.edu, linkPageRank:0.9999, TFIDF:['hello':3434,'one':0.343], googlePageRank:4
		 */
		
		
		JSONArray jsonArray = new JSONArray();
		
		for(Map.Entry<String, Double> entry:pageRankingMap.entrySet()){
			
			JSONObject jsonObject = new JSONObject();
			
			String documentUrl = entry.getKey();
			
			
			jsonObject.put("doc", documentUrl);
			jsonObject.put("linkPageRank", formatDecimal(entry.getValue()));
			jsonObject.put("TFIDF", tfidfJsonObject.get(documentUrl));
//			jsonObject.put("googlePageRank", googePageRankMap.get(documentUrl));
			
			jsonArray.add(jsonObject);
		}
//		pageRankingMap
		
		writeJsonToFile(jsonArray, filePath);
		
	}
	
	/**
	 * 
	 * @param inputUrl
	 */
	public static void googlePageRank(String inputUrl) {

		String result = "";

		JenkinsHash jenkinsHash = new JenkinsHash();
		long hash = jenkinsHash.hash(("info:" + inputUrl).getBytes());

		String googleURL = "http://toolbarqueries.google.com/tbr?client=navclient-auto&hl=en&"
				+ "ch=6"
				+ hash
				+ "&ie=UTF-8&oe=UTF-8&features=Rank&q=info:"
				+ inputUrl;

		try {
			URLConnection conn = new URL(googleURL).openConnection();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String input;
			while ((input = br.readLine()) != null) {

				// What Google returned? Example : Rank_1:1:9, PR = 9
				System.out.println(input);

				result = input.substring(input.lastIndexOf(":") + 1);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		if ("".equals(result)) {
			//return 0;
			
			googePageRankMap.put(inputUrl, 0);
		} else {
			//return Integer.valueOf(result);
			
			googePageRankMap.put(inputUrl, Integer.valueOf(result));

		}

	}

	/**
	 * 
	 */
	public static void createTFIDFRank() {

		// TF = word count / total word
		// IDF = log(totalPages / this word into pages)
		// TFIDF = TF * IDF

		// Map<String, Map<String, Integer>>

		for (Map.Entry<String, Map<String, Integer>> entry : tfidfMap
				.entrySet()) {

			JSONArray jsongArray = new JSONArray();

			String documentURL = entry.getKey();
			Map<String, Integer> wordWithCount = entry.getValue();

			for (Map.Entry<String, Integer> wordEntry : wordWithCount
					.entrySet()) {

				JSONObject json = new JSONObject();
				String wordString = wordEntry.getKey();
				Integer wordCount = wordEntry.getValue();

				double TF = (double) wordCount
						/ totalWordCount.get(documentURL);
				double IDF = (double) Math.log10(totalLinksCount
						/ wordExsitInDocument.get(wordString).size());
				double TFIDF = TF * IDF;
                 
				double thirdRank = (double) wordCount / uniqueWordCount.get(documentURL);
						
				JSONArray wordRankingArray = new JSONArray();
				
				wordRankingArray.add(formatDecimal(TFIDF));				
				wordRankingArray.add(formatDecimal(thirdRank));
				
				json.put(wordString, wordRankingArray);
				jsongArray.add(json);

			}

			tfidfJsonObject.put(documentURL, jsongArray);

			System.out.println(entry.getKey() + " : " + entry.getValue());

		}

	}

	/**
	 * 
	 * @param document
	 * @param URL
	 */
	public static void buildTFIDF(String document, String URL) {

		String[] wordsArray = document.split(" ");

		Integer totalWords = wordsArray.length;

		for (String token : wordsArray) {
			if (tfidfMap.get(URL) != null) {
				if (tfidfMap.get(URL).get(token) != null) {
					Integer count = tfidfMap.get(URL).get(token);
					count++;
					tfidfMap.get(URL).put(token, count);
				} else {
					tfidfMap.get(URL).put(token, 1);
				}
			} else {

				Map<String, Integer> word = new HashMap<String, Integer>();
				word.put(token, 1);
				tfidfMap.put(URL, word);
			}// End of Main If

			if (wordExsitInDocument.get(token) != null) {
				wordExsitInDocument.get(token).add(URL);
			} else {
				Set<String> documentCount = new HashSet<String>();
				documentCount.add(URL);
				wordExsitInDocument.put(token, documentCount);
			}
		}

		if (wordsArray != null && wordsArray.length > 0) {
			totalWordCount.put(URL, wordsArray.length);
		}
		
		if (wordsArray != null && wordsArray.length > 0) {
			
			Set<String> unqiueWord = new HashSet<String>();
			
			for(String word:wordsArray){
				unqiueWord.add(word);
			}			
			uniqueWordCount.put(URL, unqiueWord.size());
		}
		
	}

	/**
	 * 
	 */
	public static void printRanking() {

		for (Map.Entry<String, Double> entry : pageRankingMap.entrySet()) {

			System.out.println(entry.getKey() + " : " + entry.getValue());

		}

	}

	/**
	 * 
	 * @param page
	 */
	public static void pageRankingOfThisPage(String page) {

		// P rank = PC / link count

		double pageRankValue = pageRankingMap.get(page);

		double newPageRankValue = 0;

		for (Map.Entry<String, List<String>> entry : pageLinks.entrySet()) {

			if (!page.equals(entry.getKey())) {

				for (String pointLink : entry.getValue()) {

					if (page.equalsIgnoreCase(pointLink)) {
						newPageRankValue += (double) pageRankingMap
								.get(pointLink) / entry.getValue().size();
					}
				}

			}

		}

		if (newPageRankValue != 0) {
			pageRankingMap.put(page, newPageRankValue);
		}

	}

	/*
	 * 
	 */
	public static void pageRanking() {

		for (int i = 0; i < 3; i++) {
			for (Map.Entry<String, List<String>> entry : pageLinks.entrySet()) {
				pageRankingOfThisPage(entry.getKey());
			}
		}

	}

	/**
	 * 
	 */
	public static void setupFirstPageRanking() {

		double arraySize = pageLinks.size();
		double initialK = 1;

		double k = initialK / arraySize;
		for (Map.Entry<String, List<String>> entry : pageLinks.entrySet()) {
			pageRankingMap.put(entry.getKey(), k);
		}

	}

	/**
	 * 
	 * @param url
	 * @param listOfUrl
	 */
	public static void createPageLinkMap(String url, JSONArray jsonArrayLink) {

		if (!StringUtils.isEmpty(url)) {

			for (Object link : jsonArrayLink.toArray()) {

				JSONObject jsonObject = (JSONObject) link;

				if (pageLinks.get(url) != null) {
					pageLinks.get(url).add((String) jsonObject.get("url"));
				} else {
					List<String> linkList = new ArrayList<String>();
					linkList.add((String) jsonObject.get("url"));
					pageLinks.put(url, linkList);

				}
			}

		}

	}

	/**
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeInexJsonFile(String filePath) throws IOException {
		JSONArray jsonWordArray = new JSONArray();

		for (Map.Entry<String, Map<String, Integer>> entry : wordMap.entrySet()) {

//			String index = "";
//			index += entry.getKey() + "  ";

			JSONObject jsonObject = new JSONObject();
			JSONArray jsonDocumentArray = new JSONArray();

			for (Map.Entry<String, Integer> pageWordCount : entry.getValue().entrySet()) {
				JSONObject jsonDoucment = new JSONObject();
				jsonDoucment.put(StringEscapeUtils.escapeHtml4(pageWordCount.getKey()),pageWordCount.getValue());
				jsonDocumentArray.add(jsonDoucment);
			}

			jsonObject.put(entry.getKey(), jsonDocumentArray);

			jsonWordArray.add(jsonObject);

		}
		writeJsonToFile(jsonWordArray, filePath);

	}

	/**
	 * 
	 * @param jsonObject
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeJsonToFile(JSONArray jsonObject, String filePath)
			throws IOException {

		FileWriter file = new FileWriter(filePath, true);
		file.write(jsonObject.toJSONString());
		file.flush();
		file.close();

	}

	/**
	 * 
	 * @param jSONArray
	 * @param JSONArray1
	 * @return
	 */
	public static String readResoruceArray(JSONArray jSONArray,
			JSONArray JSONArray1) {

		String metadatString = " ";

		for (Object object : jSONArray) {

			JSONObject jsonObject = (JSONObject) object;

			JSONObject metdataObject = (JSONObject) jsonObject.get("metadate");
			
			
			if(metdataObject!=null && metdataObject.size() > 0){
			
			Collection<String> imageMetadata = metdataObject.values();
			
				
					for(String obj:imageMetadata){
						
						 
						
						 
						obj = StringUtils.remove(obj, ",");
				
						 obj = StringUtils.remove(obj, ".");
				
						
						 
						if(!obj.endsWith(".png")){
							metadatString += obj + " ";
						}
					}
			}
		}

		for (Object object : JSONArray1) {

			JSONObject jsonObject = (JSONObject) object;

			JSONObject metdataObject = (JSONObject) jsonObject.get("metadate");
			
			if(metdataObject!=null && metdataObject.size() > 0){

				Collection<String> imageMetadata = metdataObject.values();
				
				for(String obj:imageMetadata){
					
					obj = StringUtils.remove(obj, ",");
					
					 obj = StringUtils.remove(obj, ".");
			
					
					
					if(!obj.endsWith(".png")){
						metadatString += obj + " ";
					}
				}
			}

		}

		return metadatString;

	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String[] readStopWord(InputStream in) throws Exception {
		
		

		InputStreamReader is = new InputStreamReader(in);
//		StringBuilder sb=new StringBuilder();
//		BufferedReader br = new BufferedReader(is);

		BufferedReader br = new BufferedReader(is);
		StringBuilder sb = new StringBuilder();

		try {
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}

		} finally {
			br.close();
		}

		System.out.print(" Stop Word ======="+sb.toString());
		return sb.toString().split(",");
	}

	/**
	 * 
	 * @param jsonObject
	 */
	public static void printObject(JSONObject jsonObject) {

		System.out.println(jsonObject.get("url"));
		System.out.println(jsonObject.get("lastupdate"));
		System.out.println(jsonObject.get("title"));
		System.out.println(jsonObject.get("pagedata"));

		JSONArray jsonArrayLink = (JSONArray) jsonObject.get("links");

		for (Object object : jsonArrayLink) {

			JSONObject json = (JSONObject) object;

			System.out.print(json.get("url") + "    ");

		}

		System.out.println("");
		System.out
				.println(" ----------------------------------------------------------------------------------------------------------------------------------------");

	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static JSONArray readJsonFile(String fileName) throws Exception {
		JSONParser parser = new JSONParser();
		Object object = parser.parse(new FileReader(fileName));
		JSONArray jsonArray = (JSONArray) object;
		return jsonArray;
	}

	/**
	 * 
	 * @param pageString
	 * @return
	 */
	public static String removeStopWords(String pageString) {

		String withoutStopString = "";

		if (pageString != null) {

			Scanner scanner = new Scanner(pageString);

			while (scanner.hasNext()) {

				String token = scanner.next();
				boolean stopFlag = false;
			
				token = StringUtils.remove(token, ",");
				
				token = StringUtils.remove(token, ".");
				
			
				for (String stopWord : stopwords) {

					if (token.trim().equalsIgnoreCase(stopWord.trim())) {
						stopFlag = true;
						// System.out.println("Stopping Word = " + token);
						break;
					}

				}

				if (!stopFlag) {
					withoutStopString += token + " ";
				}
			}

		}

		System.out.println("Without Stop word Count : "
				+ withoutStopString.length());
		return withoutStopString;
	}

	/**
	 * 
	 * @param document
	 * @param URL
	 */
	public static void wordCount(String document, String URL) {
		if (document != null
				&& !org.apache.commons.lang3.StringUtils.isEmpty(document)) {

			String wordArray[] = document.split(" ");
			String pageName = URL; // FilenameUtils.getName(URL);

			if (!StringUtils.isEmpty(pageName)) {
				for (String word : wordArray) {

					if (wordMap.get(word) == null) {
						Map<String, Integer> indexPage = new HashMap<String, Integer>();
						indexPage.put(pageName, 1);
						wordMap.put(word, indexPage);
					} else {

						if (wordMap.get(word).get(pageName) != null) {
							Integer count = wordMap.get(word).get(pageName);
							count++;
							wordMap.get(word).put(pageName, count);
						} else {
							wordMap.get(word).put(pageName, 1);
						}

					}

				}

			}

		}

	}

	/**
	 * 
	 */
	public static void printIndexMap() {

		for (Map.Entry<String, Map<String, Integer>> entry : wordMap.entrySet()) {

			String index = "";
			index += entry.getKey() + "  ";
			// Map<String,Integer> pageWordCount = entry.getValue();

			for (Map.Entry<String, Integer> pageWordCount : entry.getValue()
					.entrySet()) {

				index += " [ " + pageWordCount.getKey() + " : "
						+ pageWordCount.getValue() + " ] ";
			}

			System.out.println(index);

		}

	}

	public static void printDocumentMap() {

		for (Map.Entry<String, Integer> entry : documentIdMap.entrySet()) {

			System.out.println("[ " + entry.getKey() + " : " + entry.getValue()
					+ "]");
		}

	}
	
	
	public static double formatDecimal(Double value){		
		if(value!=null){
			
			DecimalFormat newFormat = new DecimalFormat("#.#####");
			return Double.valueOf(newFormat.format(value));
		}
		
		return 0.0;
	}

	/*
	 * static final String[] stopwords = { "a", "about", "above", "above",
	 * "across", "after", "afterwards", "again", "against", "all", "almost",
	 * "alone", "along", "already", "also", "although", "always", "am", "among",
	 * "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow",
	 * "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at",
	 * "back", "be", "became", "because", "become", "becomes", "becoming",
	 * "been", "before", "beforehand", "behind", "being", "below", "beside",
	 * "besides", "between", "beyond", "bill", "both", "bottom", "but", "by",
	 * "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry",
	 * "de", "describe", "detail", "do", "done", "down", "due", "during",
	 * "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty",
	 * "enough", "etc", "even", "ever", "every", "everyone", "everything",
	 * "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire",
	 * "first", "five", "for", "former", "formerly", "forty", "found", "four",
	 * "from", "front", "full", "further", "get", "give", "go", "had", "has",
	 * "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby",
	 * "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how",
	 * "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest",
	 * "into", "is", "it", "its", "itself", "keep", "last", "latter",
	 * "latterly", "least", "less", "ltd", "made", "many", "may", "me",
	 * "meanwhile", "might", "mill", "mine", "more", "moreover", "most",
	 * "mostly", "move", "much", "must", "my", "myself", "name", "namely",
	 * "neither", "never", "nevertheless", "next", "nine", "no", "nobody",
	 * "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off",
	 * "often", "on", "once", "one", "only", "onto", "or", "other", "others",
	 * "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part",
	 * "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem",
	 * "seemed", "seeming", "seems", "serious", "several", "she", "should",
	 * "show", "side", "since", "sincere", "six", "sixty", "so", "some",
	 * "somehow", "someone", "something", "sometime", "sometimes", "somewhere",
	 * "still", "such", "system", "take", "ten", "than", "that", "the", "their",
	 * "them", "themselves", "then", "thence", "there", "thereafter", "thereby",
	 * "therefore", "therein", "thereupon", "these", "they", "thickv", "thin",
	 * "third", "this", "those", "though", "three", "through", "throughout",
	 * "thru", "thus", "to", "together", "too", "top", "toward", "towards",
	 * "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us",
	 * "very", "via", "was", "we", "well", "were", "what", "whatever", "when",
	 * "whence", "whenever", "where", "whereafter", "whereas", "whereby",
	 * "wherein", "whereupon", "wherever", "whether", "which", "while",
	 * "whither", "who", "whoever", "whole", "whom", "whose", "why", "will",
	 * "with", "within", "without", "would", "yet", "you", "your", "yours",
	 * "yourself", "yourselves", "1", "2", "3", "4", "5", "6", "7", "8", "9",
	 * "10", "1.", "2.", "3.", "4.", "5.", "6.", "11", "7.", "8.", "9.", "12",
	 * "13", "14", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
	 * "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
	 * "terms", "CONDITIONS", "conditions", "values", "interested.", "care",
	 * "sure", ".", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "{", "}",
	 * "[", "]", ":", ";", ",", "<", ".", ">", "/", "?", "_", "-", "+", "=",
	 * "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
	 * "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "contact",
	 * "grounds", "buyers", "tried", "said,", "plan", "value", "principle.",
	 * "forces", "sent:", "is,", "was", "like", "discussion", "tmus",
	 * "diffrent.", "layout", "area.", "thanks", "thankyou", "hello", "bye",
	 * "rise", "fell", "fall", "psqft.", "http://", "km", "miles" };
	 */

}
