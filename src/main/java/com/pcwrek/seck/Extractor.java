package com.pcwrek.seck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xml.sax.ContentHandler;

public class Extractor {

	public static String path = "";
	public static final String hw1 = "hw1.json";
	public static final String hw2 = "hw2.json";

	public static void main(String[] args) throws Exception {
		
		String resorucePath = new File(new File("").getAbsolutePath()).getParent();		
		resorucePath = resorucePath + "\\resource\\";
		File resoruceFolder = new File(resorucePath);
		resoruceFolder.mkdir();
		path = resoruceFolder.getAbsolutePath() + "\\";
		
		String cfile = args[1];
		

		if(args!=null && args.length == 2 && args[0].equals("-c")){
				JSONArray jsonArray = readJsonFile(path + cfile);
		
				for (Object object : jsonArray) {
		
					 JSONObject jsonObject = (JSONObject) object;
					 getFileMetadate((JSONArray) jsonObject.get("images"));
					 getFileMetadate((JSONArray) jsonObject.get("resources"));
		
				}
		
				 writeJsonToFile(jsonArray, path + hw2);
		}
		 
		 System.out.println("====== Done ========" + path);	

	}

	public static void getFileMetadate(JSONArray jsonArray) throws Exception {

		for (Object images : jsonArray) {
			JSONObject imageJsonObject = (JSONObject) images;
			extractFileMetadata(path + imageJsonObject.get("newname"),
					imageJsonObject);
			System.out.println(imageJsonObject.toJSONString());
		}

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
	 * @param filePath
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> extractFileMetadata(String filePath,
			JSONObject jsonObject) {

		FileInputStream fileInputStream = null;

		JSONObject metaDateJson = new JSONObject();

		try {
			File file = new File(filePath);
			fileInputStream = new FileInputStream(file);

			ContentHandler contenthandler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
			AutoDetectParser parser = new AutoDetectParser();

			parser.parse(fileInputStream, contenthandler, metadata);
//			metaDateJson.put("date", metadata.get(Metadata.CREATION_DATE));
//			metaDateJson.put("author", metadata.get(Metadata.AUTHOR));
//			metaDateJson.put("language", metadata.get(Metadata.LANGUAGE));
//			metaDateJson.put("subject", metadata.get(Metadata.SUBJECT));
//			metaDateJson.put("title", metadata.get(Metadata.TITLE));
//			metaDateJson.put("latitude", metadata.get(Metadata.LATITUDE));
//			metaDateJson.put("location", metadata.get(Metadata.LOCATION));
//			metaDateJson.put("length", metadata.get(Metadata.IMAGE_LENGTH));
//			metaDateJson.put("width", metadata.get(Metadata.IMAGE_WIDTH));
//			metaDateJson.put("publisher", metadata.get(Metadata.PUBLISHER));
			
			for(int i = 0; i <metadata.names().length; i++) 
			{ 
				String name = metadata.names()[i]; 						
				metaDateJson.put(name, metadata.get(name));
			} 

			jsonObject.put("metadate", metaDateJson);

		}
		catch(Exception ex){
			
		}
		finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return metaDateJson;
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

}
