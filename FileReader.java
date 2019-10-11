package com.search.index;

import java.util.ArrayList;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FileReader {
	private ArrayList<String> filenames;
	private int currentFileIndex = 0;
	private NodeList docsInCurrentFile;
	private int currentDocIndex = 0;

	
	public FileReader(String inputPath) throws Exception{
		filenames = new ArrayList<>();
		File inputDir = new File(inputPath);
		
		if(inputDir.exists() && inputDir.isDirectory()){
			listFilesInDir(inputDir, ".trectext");	
		}
		
		File firstFile = new File(filenames.get(0));
		loadFromFile(firstFile);
		
	}
	
	private void listFilesInDir(File dir, String extension) throws Exception{
		for(File file : dir.listFiles()){
			if(extension == null || extension == ""){
				filenames.add(file.getAbsolutePath());
			}
			if(file.getName().endsWith(extension)){
				filenames.add(file.getAbsolutePath());
			}
		}
	}
	
	private boolean loadFromFile(File file) throws Exception{
		
		if(!file.exists() || !file.isFile()){
			return false;
		}
		
		FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis)); 
        String lineRead = br.readLine(); 
        StringBuilder stringbuffer = new StringBuilder(); 
 
        while(lineRead != null){
        stringbuffer.append(lineRead).append("\n"); 
        lineRead = br.readLine(); 
        }
        String toString = stringbuffer.toString();
        toString = toString.replaceAll("& ", "&amp;").replaceAll("&$","&amp;").replaceAll("[^\\x00-\\x7F]", "").replaceAll("&\n","&amp;");

		String str="<DOCS>" + toString +"</DOCS>"; 

		System.out.println("Reading: " + file.getName());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		Document doc = dBuilder.parse(new InputSource(new StringReader(str)));
		
		doc.getDocumentElement().normalize();
		
		docsInCurrentFile = doc.getElementsByTagName("DOC");
		return true;
	}
	
	public Node getNextDoc() throws Exception{
		if(currentDocIndex < docsInCurrentFile.getLength()){
			return docsInCurrentFile.item(currentDocIndex++);
		}
		else{
			if(currentFileIndex < filenames.size()-1){
				currentDocIndex = 0;
				if(!loadFromFile(new File(filenames.get(++currentFileIndex)))){
					return null;
				}
				if(currentDocIndex < docsInCurrentFile.getLength()){
					return docsInCurrentFile.item(currentDocIndex++);
				}
				else{
					return null;
				}
			}
			else{
				return null;
			}
		}
	}
	
	
}
