package com.search.index;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class GenerateIndex {

	public static void main(String args[]) throws Exception{
		String indexPath, inputPath;
		
		indexPath = args[0]; 
		inputPath = args[1];
				
		FileReader fReader = new FileReader(inputPath);
		
		createIndex(indexPath, fReader, new StandardAnalyzer());
		
	}
	
	
	static void createIndex(String indexPath, FileReader fReader, Analyzer analyzer) throws Exception{
		
		String[] fieldTags = {"DOCNO", "HEAD", "BYLINE", "DATELINE", "TEXT"};
		
		Directory indexDir = FSDirectory.open(Paths.get(indexPath));
		
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		
		IndexWriter writer = new IndexWriter(indexDir, iwc);
		
		Node doc;
		while( (doc = fReader.getNextDoc()) != null){
			Map<String, String> fieldData = getFieldDataFromFile(doc, fieldTags);
			
			Document lDoc = new Document();
			
			if(fieldData.containsKey("DOCNO"))
				lDoc.add(new StringField("DOCNO", fieldData.get("DOCNO"), Field.Store.YES));
			if(fieldData.containsKey("HEAD"))
				lDoc.add(new StringField("HEAD", fieldData.get("HEAD"), Field.Store.YES));
			if(fieldData.containsKey("BYLINE"))
				lDoc.add(new StringField("BYLINE", fieldData.get("BYLINE"), Field.Store.YES));
			if(fieldData.containsKey("DATELINE"))
				lDoc.add(new StringField("DATELINE", fieldData.get("DATELINE"), Field.Store.YES));
			if(fieldData.containsKey("TEXT"))
				lDoc.add(new TextField("TEXT", fieldData.get("TEXT"), Field.Store.NO));
			
			writer.addDocument(lDoc);
		}
		
		writer.close();
		

	}
	
	static Map<String, String> getFieldDataFromFile(Node doc, String[] fieldsToGet){
		Map<String, String> fieldData = new HashMap<>();

		//The below part of code has been inspired from the link metioned below to read DOM elements
		//http://www.java2s.com/Code/JavaAPI/org.w3c.dom/ElementNodeListgetChildNodes.htm
		NodeList child = doc.getChildNodes();
		for(String field : fieldsToGet){
			for(int i = 0; i < child.getLength(); i++){
				if(field.equalsIgnoreCase(child.item(i).getNodeName())){
					if(fieldData.containsKey(field)){
						fieldData.put(field, fieldData.get(field) + " " + child.item(i).getTextContent());
					}
					else{
						fieldData.put(field, child.item(i).getTextContent());
					}
				}
			}
		}

		return fieldData;
	}
	
}
