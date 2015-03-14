package com.pcwrek.seck;

import java.io.BufferedOutputStream;
import java.io.File;
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
import java.util.Set;
import java.util.UUID;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

	public static Set<String> allURLList = new HashSet<String>();

	public static List<String> fileExt = new ArrayList<String>();
	
	public static String[] resourceFileArray = { "pdf", "doc", "docx", "mp3",
		"xsl", "xslx" };
	
	
	public static int urlCount = 0;
	
	public static String path = "";
	


	public static void main(String[] args) throws Exception {

		String resorucePath = new File(new File("").getAbsolutePath()).getParent();		
		resorucePath = resorucePath + "//resource//";
		File resoruceFolder = new File(resorucePath);
		resoruceFolder.mkdir();
		path = resoruceFolder.getAbsolutePath() + "\\";
	
		
	    
		
		String depth = args[1];
		String url = args[3];
		
		fileExtension();	
		String filePath = "hw1.json";
		//String url = "http://www.calstatela.edu";
		//String depth = "2";
		
		if(args.length == 5 && args[4] != null && args[4].equals("demo")){
			
			System.out.println("Demo Parameter called");			
			demovisitUrl(0, new Integer(depth), url, url);

		}else{	
			
			System.out.println("Without Demo Parameter ");
			 visitUrl(0, new Integer(depth), url, url);		

		}
		
		 storeData(path+filePath);
		
//		storeData(folder+filePath);
		System.out.println("====== Done ========" + path);	
		
	}
	
	

	/**
	 * 
	 */
	public static void storeData(String filePath) throws Exception {

		JSONArray urlJsonArray = new JSONArray();

		for (String url : allURLList) {		
			
			try{

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


				urlJsonArray.add(getURLJson(document,url,response.headers().get("Last-Modified")));
				

				System.out.println(" === Record Inserted =====");

			}

			}catch(Exception ex){
				System.out.println("Failed to Connect URL : " + url);
			}	
		} // For loop end.
		
		if(urlJsonArray!=null && urlJsonArray.size() > 0){
			writeJsonToFile(urlJsonArray,filePath);			
		}

	}
	
	
	
	
	public static void demovisitUrl(int currentValue, int depth, String URL,
			String rootURL) throws IOException {
		List<String> urlList = getURLList(URL);

		System.out.println(" Demo url vistor called");
		
		int i = 0;
	

		if (urlList != null && !urlList.isEmpty())
		
				for (String url : urlList) {
					if (!allURLList.contains(url)) {
						allURLList.add(url);
						System.out.println(url);
					}
					
					i ++;
					
					if(i==10){
						break;
					}
			}

	}
	


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
		
		System.out.println(" Non Demo Mode");

		 if (!URL.contains(rootURL)) {
		currentValue++;
//		 System.out.println("Current Depth : " + currentValue);
		 }

		if (urlList != null && !urlList.isEmpty())
			if (currentValue < depth) {
				for (String url : urlList) {
					if (!allURLList.contains(url)) {
						allURLList.add(url);
						System.out.println(url);
						visitUrl(currentValue, depth, url, rootURL);
					}
				}
			}

	}
	
	
	/*
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
	 * @param document
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getURLJson(Document document, String url,String date) throws Exception{
		
		JSONObject urlJSON = new JSONObject();	
		
		String htmlFile = UUID.randomUUID().toString() + ".html";
		
		FileWriter fileWriter = new FileWriter(path+htmlFile);
		
//		FileWriter fileWriter = new FileWriter(folder+htmlFile);
		
		
		fileWriter.write(document.body().html());
		
		urlJSON.put("url", url);
		urlJSON.put("title", document.title());		
		urlJSON.put("lastupdate", date);
		
		if (document.body() != null && document.body().text() != null) {					
			urlJSON.put("pagedata", document.body().text());
		}
		
		
		urlJSON.put("links", getAcnhorTextandURL(document));		
		urlJSON.put("images",  getImages(document));		
		urlJSON.put("resources",getResources(document));

		return urlJSON;
	}

	
	/**
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getResources(Document document) throws Exception {
		
		JSONArray jSONArray = new JSONArray();
		
		for (String resource : resourceFileArray) {
			String query = "a[href$=." + resource + "]";
			//urlSpecificData.put(resource, getResource(query, document));
			
			Elements elements = document.select(query);			
			

			for (Element element : elements) {				
				System.out.println(" Resource File = " + element.attr("abs:href"));				
				String resourceFile = element.attr("abs:href");				
				String newFileName = newFileName(resourceFile);
				if(newFileName!=null){					
					downLoadResourceFile(resourceFile, newFileName);
					
					JSONObject jSONObject = new JSONObject();
					
					jSONObject.put("atext", element.text());
					jSONObject.put("url", element.attr("abs:href"));
					jSONObject.put("newname",newFileName);
					jSONObject.put(""
							+ "", FilenameUtils.getExtension(newFileName));

					jSONArray.add(jSONObject);
				}			
				
			}		
		}

		return jSONArray;
	}

	/**
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getAcnhorTextandURL(Document document)
			throws Exception {

		JSONArray jSONArray = new JSONArray();
		
		Elements elements = document.select("a[href]");

		for (Element element : elements) {
			JSONObject jSONObject = new JSONObject();
			System.out.println("Ancho Text  : " + element.text()
					+ "     Anchor URL = " + element.attr("abs:href"));
			jSONObject.put("atext", element.text());
			jSONObject.put("url", element.attr("abs:href"));
			jSONArray.add(jSONObject);
		}
		return jSONArray;
	}

	
	/**
	 * 
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public static JSONArray getImages(Document document)
			throws Exception {
		
		JSONArray jSONArray = new JSONArray();

		Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		for (Element image : images) {
			System.out.println("\nImage src : " + image.attr("abs:src"));

			String imageSource = image.attr("abs:src");
			String newFileName = newFileName(imageSource);
			if (newFileName != null) {
				downLoadResourceFile(imageSource, newFileName);
				
				JSONObject jSONObject = new JSONObject();
				
				jSONObject.put("atext", image.text());
				jSONObject.put("url", image.attr("abs:src"));
				jSONObject.put("newname",newFileName);
				jSONObject.put("type", FilenameUtils.getExtension(newFileName));

				jSONArray.add(jSONObject);
			}

		}
		return jSONArray;
	}

	
	/**
	 * 
	 * @param sourceFile
	 * @return
	 */
	public static String newFileName(String sourceFile) {

		String fileExtString = FilenameUtils.getExtension(sourceFile);

		if (fileExtString != null && fileExtString.length() > 0
				&& fileExt.contains(fileExtString.toLowerCase())) {
			return UUID.randomUUID().toString() + "." + fileExtString;
		}

		return null;

	}

	
	/**
	 * 
	 */
	public static void fileExtension() {

		String[] resourceFileArray = { "pdf", "doc", "docx", "xsl", "xslx",
				"png", "jpeg", "gif" };

		for (String ext : resourceFileArray) {
			fileExt.add(ext);
		}

	}

	
	/**
	 * 
	 * @param soruceFileUrl
	 * @param renameFile
	 * @return
	 */
	public static String downLoadResourceFile(String soruceFileUrl,
			String renameFile) {

		String fileExtString = FilenameUtils.getExtension(soruceFileUrl);

		String fileName = "";

		if (fileExtString != null && fileExtString.length() > 0
				&& fileExt.contains(fileExtString.toLowerCase())) {

			fileName = FilenameUtils.getName(soruceFileUrl);

			try {
				URL url = new URL(soruceFileUrl);
				InputStream in = url.openStream();

				OutputStream out = new BufferedOutputStream(new FileOutputStream(path+renameFile));
				
//				OutputStream out = new BufferedOutputStream(new FileOutputStream(folder+renameFile));
				
				

				for (int b; (b = in.read()) != -1;) {
					out.write(b);
				}

				out.close();
				in.close();
			} catch (Exception ex) {
				System.out.println("Failed to open connection");
			}
		}
		return fileName;
	}

}
