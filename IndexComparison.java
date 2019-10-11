package com.search.index;

import java.nio.file.Paths;
import java.io.File;
import org.apache.commons.io.FileUtils;
//import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;



public class IndexComparison {
	static String indexPath;
	static String inputPath;
	
	public static void main(String args[]) throws Exception{
		//This program assumes that the path of index and corpus are provided to the program at runtime
		if(args.length != 2){ 
	      System.out.println("Wrong number of arguments!"); 
	      System.out.println("The input Parameters to the program are: index_directory input_directory"); 
	      System.exit(1); 
	    } 
	    //Fetch the index and corpus path in the variables
	    indexPath = args[0]; 
	    inputPath = args[1];
	    
	    //Delete existing Index folders for each analyzer
	    File filekeyword = new File(indexPath + "/keyword");
	    if (filekeyword.exists())
	    {
	    FileUtils.deleteDirectory(filekeyword);
	    System.out.println("Deleting Keyword index folder");
	    }
	    
	    File fileSimple = new File(indexPath + "/simple");
	    if (fileSimple.exists())
	    {
	    FileUtils.deleteDirectory(fileSimple);
	    System.out.println("Deleting Simple index folder");
	    }
	    
	    File fileStop = new File(indexPath + "/stop");
	    if (fileStop.exists())
	    {
	    FileUtils.deleteDirectory(fileStop);
	    System.out.println("Deleting Stop index folder");
	    }
	    
	    File fileStandard = new File(indexPath + "/standard");
	    if (fileStandard.exists())
	    {
	    FileUtils.deleteDirectory(fileStandard);
	    System.out.println("Deleting Standard index folder");
	    }
	    
		//Generate indexes for each of the Analyzer
	    System.out.println("Creating index with Keyword Analyzer");
	    GenerateIndex.createIndex(indexPath + "/keyword", new FileReader(inputPath), new KeywordAnalyzer());
	    
	    System.out.println("Creating index with Simple Analyzer");
	    GenerateIndex.createIndex(indexPath + "/simple", new FileReader(inputPath), new SimpleAnalyzer());
	    
	    System.out.println("Creating index with Stop Analyzer");
	    GenerateIndex.createIndex(indexPath + "/stop", new FileReader(inputPath), new StopAnalyzer());
	    
	    System.out.println("Creating index with Standard Analyzer");
	    GenerateIndex.createIndex(indexPath + "/standard", new FileReader(inputPath), new StandardAnalyzer());
	    
	    printStatistics();
	}
	
	public static void printStatistics() throws Exception{
		
		//Read index for each analyzer

		IndexReader keywordReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath + "/keyword")));
		IndexReader simpleReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath + "/simple")));	
		IndexReader stopReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath + "/stop")));
		IndexReader standardReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath + "/standard")));	
		
		System.out.println("Total number of documents in the corpus: "+standardReader.maxDoc()); 
		
		System.out.println("\nStatistics Using KeywordAnalyzer");
		Terms keywordVocab = MultiFields.getTerms(keywordReader, "TEXT");
		System.out.println("Number of token for TEXT field: " + keywordVocab.getSumTotalTermFreq());
		System.out.println("Number of terms in the dictionary: " + termsCount(keywordVocab));
		
		System.out.println("\nStatistics Using SimpleAnalyzer");
		Terms simpleVocab = MultiFields.getTerms(simpleReader, "TEXT");
		System.out.println("Number of token for TEXT field: " + simpleVocab.getSumTotalTermFreq());
		System.out.println("Number of terms in the dictionary : " + termsCount(simpleVocab));
		
		System.out.println("\nStatistics Using StopAnalyzer");
		Terms stopVocab = MultiFields.getTerms(stopReader, "TEXT");
		System.out.println("Number of token for TEXT field: " + stopVocab.getSumTotalTermFreq());
		System.out.println("Number of terms in the dictionary : " + termsCount(stopVocab));

		System.out.println("\nStatistics Using StandardAnalyzer");
		Terms standardVocab = MultiFields.getTerms(standardReader, "TEXT");
		System.out.println("Number of token for TEXT field: " + standardVocab.getSumTotalTermFreq());
		System.out.println("Number of terms in the dictionary : " + termsCount(standardVocab));
		
		keywordReader.close();
		simpleReader.close();
		stopReader.close();
		standardReader.close();
	}
	
	static long termsCount(Terms vocabulary) throws Exception{
		long count = 0;
		
		TermsEnum iterator = vocabulary.iterator();
		
		while(iterator.next() != null) {
			count++;
		}
		
		return count;
	}
}