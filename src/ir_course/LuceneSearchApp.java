/*
 * Skeleton class for the Lucene search program implementation
 *
 * Created on 2011-12-21
 * * Jouni Tuominen <jouni.tuominen@aalto.fi>
 * 
 * Modified on 2015-30-12
 * * Esko Ikkala <esko.ikkala@aalto.fi>
 * 
 */
package ir_course;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.TFIDFSimilarity;


public class LuceneSearchApp {
	String analyzer = "bm25";
	boolean stopwords = true;
	boolean stemmer = true;
	Integer TaskNumber = null;
	IndexWriterConfig iwc = null;
	Analyzer standardAnalyzer = new StandardAnalyzer();
	private final static Logger LOGGER = Logger.getLogger(LuceneSearchApp.class.getName());
	
	public LuceneSearchApp() {
	}
	
	public void setRankingMethod(String type, Integer tasknumber){
		TaskNumber = tasknumber;
		// VSM + Porter stemming + stopwords
		if(type.equals("1")){
			analyzer = "vsm";
			stopwords = true;
			stemmer = true;
		}
		// VSM + Porter stemming - stopwords
		if(type.equals("2")){
			analyzer = "vsm";
			stopwords = false;
			stemmer = true;
		}
		// VSM + standard stemming + stopwords
		if(type.equals("3")){
			analyzer = "vsm";
			stopwords = true;
			stemmer = false;
		}
		// BM25 + Porter stemming + stopwords
		if(type.equals("4")){
			analyzer = "bm25";
			stopwords = true;
			stemmer = true;
		}
		// BM25 + Porter stemming - stopwords
		if(type.equals("5")){
			analyzer = "bm25";
			stopwords = false;
			stemmer = true;
		}
		// BM25 + standard stemming + stopwords
		if(type.equals("6")){
			analyzer = "bm25";
			stopwords = true;
			stemmer = false;
		}
	}
	
	public void index(List<DocumentInCollection> docs) throws IOException {		
		// implement the Lucene indexing here
		
		if (analyzer.equals("vsm") && stopwords && stemmer) 
        {
            //VSM cosine similarity with TFIDF + stopwords + stemmer
            CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
            standardAnalyzer = new EnglishAnalyzer(stopWords);
            iwc = new IndexWriterConfig(standardAnalyzer);
            iwc.setSimilarity(new ClassicSimilarity());
        } 
        else if (analyzer.equals("vsm") && !stopwords && stemmer) 
        {
            //VSM cosine similarity with TFIDF - stopwords + stemmer
        	standardAnalyzer = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
        	iwc = new IndexWriterConfig(new EnglishAnalyzer(CharArraySet.EMPTY_SET));
        	iwc.setSimilarity(new ClassicSimilarity());
        } 
        else if (analyzer.equals("vsm") && stopwords && !stemmer) 
        {
            //VSM cosine similarity with TFIDF + stopwords - stemmer
            CharArraySet stopWords = StandardAnalyzer.STOP_WORDS_SET;
            standardAnalyzer = new StandardAnalyzer(stopWords);
            iwc = new IndexWriterConfig(standardAnalyzer);
            iwc.setSimilarity(new ClassicSimilarity());
        } 
        else if (analyzer.equals("bm25") && stopwords && stemmer) 
        {
            //Analyzer + stopwords + stemmer
            CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
            standardAnalyzer = new EnglishAnalyzer(stopWords);
            iwc = new IndexWriterConfig(standardAnalyzer);
            //BM25 ranking method
            iwc.setSimilarity(new BM25Similarity());
        } 
        else if (analyzer.equals("bm25") && !stopwords && stemmer) 
        {
            //Analyzer - stopwords + stemmer
        	standardAnalyzer = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
        	iwc = new IndexWriterConfig(standardAnalyzer);
            //BM25 ranking method
        	iwc.setSimilarity(new BM25Similarity());
        } 
        else if (analyzer.equals("bm25") && stopwords && !stemmer) 
        {
            //Analyzer + stopwords - stemmer
            CharArraySet stopWords = StandardAnalyzer.STOP_WORDS_SET;
            standardAnalyzer = new StandardAnalyzer(stopWords);
            iwc = new IndexWriterConfig(standardAnalyzer);
            //BM25 ranking method
            iwc.setSimilarity(new BM25Similarity());
        }
        else if (analyzer.equals("LMDirichlet") && stopwords && stemmer) 
        {
            //Analyzer + stopwords + stemmer
            CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
            standardAnalyzer = new EnglishAnalyzer(stopWords);
            iwc = new IndexWriterConfig(standardAnalyzer);
            //BM25 ranking method
            iwc.setSimilarity(new LMDirichletSimilarity());
        } 
        else if (analyzer.equals("LMDirichlet") && !stopwords && stemmer) 
        {
            //Analyzer - stopwords + stemmer
        	standardAnalyzer = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
        	iwc = new IndexWriterConfig(standardAnalyzer);
            //BM25 ranking method
        	iwc.setSimilarity(new LMDirichletSimilarity());
        } 
        else if (analyzer.equals("LMDirichlet") && stopwords && !stemmer) 
        {
            //Analyzer + stopwords - stemmer
            CharArraySet stopWords = StandardAnalyzer.STOP_WORDS_SET;
            standardAnalyzer = new StandardAnalyzer(stopWords);
            iwc = new IndexWriterConfig(standardAnalyzer);
            //BM25 ranking method
            iwc.setSimilarity(new LMDirichletSimilarity());
        }
        else
        {
            //default settings
        	standardAnalyzer = new StandardAnalyzer();
            iwc = new IndexWriterConfig(standardAnalyzer);
            iwc.setSimilarity(new ClassicSimilarity());
        }
		

		Directory fsDirectory = FSDirectory.open(Paths.get("index/"));
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(fsDirectory, iwc);
        System.out.println("Setting : "+analyzer+" "+(stopwords?"+":"-") +" stopwords "+(stemmer?"+":"-") +" stemmer");
		
		for(DocumentInCollection _rssfeeddoc : docs){
			Document doc = new Document();
			//System.out.println(_rssfeeddoc.getTitle()+" : "+_rssfeeddoc.getPubDate().getTime()+" - "+_rssfeeddoc.getDescription());
			if(_rssfeeddoc.getSearchTaskNumber() == TaskNumber){
				doc.add(new TextField("title", _rssfeeddoc.getTitle(), Field.Store.YES));
				doc.add(new TextField("abstract_text", _rssfeeddoc.getAbstractText(), Field.Store.YES));
				doc.add(new StoredField("search_task_number", _rssfeeddoc.getSearchTaskNumber()));
				doc.add(new TextField("query", _rssfeeddoc.getQuery(), Field.Store.YES));
				doc.add(new StoredField("relevant", ((_rssfeeddoc.isRelevant())? 1 : 0)));
				//doc.add(new LongPoint("publication_date", _rssfeeddoc.getPubDate().getTime()));
				writer.addDocument(doc);
			}
		}
		writer.close();
		
		
	}
	
	public List<String> search(List<String> inTitle, List<String> notInTitle, List<String> inAbstract, List<String> notInAbstract, List<String> inSearchTaskNumber, List<String> inQuery) throws IOException {
		
		printQuery(inTitle, notInTitle, inAbstract, notInAbstract, inSearchTaskNumber, inQuery);

		List<String> results = new LinkedList<String>();
		List<String> relevantDocs = new LinkedList<String>();
		
		// implement the Lucene search here
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("index/")));
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(iwc.getSimilarity());
			Builder builder = new BooleanQuery.Builder();
			if(inTitle!=null)
			{
				for(String eachWord : inTitle){
					Query query = new TermQuery(new Term("title", eachWord));
					builder.add(query, BooleanClause.Occur.SHOULD);
				}
			}
			if(notInTitle!=null)
			{
				for(String eachWord : notInTitle){
					Query query = new TermQuery(new Term("title", eachWord));
					builder.add(query, BooleanClause.Occur.MUST_NOT);
				}
			}
			if(inAbstract!=null)
			{
				for(String eachWord : inAbstract){
					Query query = new TermQuery(new Term("abstract_text", eachWord));
					builder.add(query, BooleanClause.Occur.SHOULD);
				}
			}
			if(notInAbstract!=null)
			{
				for(String eachWord : notInAbstract){
					Query query = new TermQuery(new Term("abstract_text", eachWord));
					builder.add(query, BooleanClause.Occur.MUST_NOT);
				}
			}
			
			/// get the amount of Relevant Documents in the Task Number (relevant == 1)
			int _amountRelevantDocInTaskNumber = 0;
			for(int doc_index = 0; doc_index<reader.maxDoc(); doc_index++){
				Document d = reader.document(doc_index);
				if (d.get("relevant").equals("1")&&d.get("search_task_number").equals(TaskNumber.toString())){
					_amountRelevantDocInTaskNumber++;
				}
			}
			
			BooleanQuery booleanQuery = builder.build();
			int hitsPerPage = 100000000;
			TopDocs docs = searcher.search(booleanQuery, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;
			// Loop through result list at Precision K 
			for(int precisionAt = 1; precisionAt<=hits.length ; precisionAt+=1){
				int countRelevantDoc = 0;
				for(int i=0;i<hits.length && i< precisionAt;++i) {
				    Document d = searcher.doc(hits[i].doc);
				    /// Remove comment for assigment submission ??
				    /*results.add(d.get("title")+"\n Score: "+hits[i].score
				    		+"\n Query: "+d.get("query")
				    		+"\n Relevant: "+d.get("relevant"));*/
				    // Count Relevant Document retrieved for Precision at K
				    if(d.get("relevant").equals("1")&&d.get("search_task_number").equals(TaskNumber.toString())){
				    	countRelevantDoc++;
				    	String title = d.get("title");
				    	if (!relevantDocs.contains(title)){
				    		relevantDocs.add(title);
				    	}
				    }
				}
				//results.add("Recall: "+(double)countRelevantDoc/(double)_amountRelevantDocInTaskNumber+", Precision: "+((double)countRelevantDoc/(double)precisionAt));
				results.add(","+(double)countRelevantDoc/(double)_amountRelevantDocInTaskNumber+","+((double)countRelevantDoc/(double)precisionAt));
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.logResults(relevantDocs);
		return results;
	}
	
	public void logResults(List<String> results){
		for(int i = 0; i < results.size(); i++){
			this.LOGGER.log(Level.INFO,results.get(i));
		}
	}
	
	public void printQuery(List<String> inTitle, List<String> notInTitle, List<String> inAbstract, List<String> notInAbstract, List<String> inSearchTaskNumber, List<String> inQuery) {
		System.out.print("Search (");
		if (inTitle != null) {
			System.out.print("in title: "+inTitle);
			if (notInTitle != null || inAbstract != null || notInAbstract != null || inSearchTaskNumber != null || inQuery != null)
				System.out.print("; ");
		}
		if (notInTitle != null) {
			System.out.print("not in title: "+notInTitle);
			if (inAbstract != null || notInAbstract != null || inSearchTaskNumber != null || inQuery != null)
				System.out.print("; ");
		}
		if (inAbstract != null) {
			System.out.print("in abstract: "+inAbstract);
			if (notInAbstract != null || inSearchTaskNumber != null || inQuery != null)
				System.out.print("; ");
		}
		if (notInAbstract != null) {
			System.out.print("not in description: "+notInAbstract);
			if (inSearchTaskNumber != null || inQuery != null)
				System.out.print("; ");
		}
		if (inSearchTaskNumber != null) {
			System.out.print("search task number: "+inSearchTaskNumber);
			if (inQuery != null)
				System.out.print("; ");
		}
		if (inQuery != null)
			System.out.print("query: "+inQuery);
		System.out.println("):");
	}
	
	public void printResults(List<String> results) {
		if (results.size() > 0) {
			//Collections.sort(results);
			for (int i=0; i<results.size(); i++){
				//System.out.println(" " + (i+1) + ". " + results.get(i));
				LOGGER.info(" " + (i+1) + ". " + results.get(i));
			}
		}
		else
			System.out.println(" no results");
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			///  LOOP THROUGH 6 Pre-defined Methods
			for (Integer method = 1; method<=6; method++){
				LuceneSearchApp engine = new LuceneSearchApp();
				
				DocumentCollectionParser parser = new DocumentCollectionParser();
				parser.parse(args[0]);
				List<DocumentInCollection> docs = parser.getDocuments();
				
				// SET RANKING METHOD & INDEX
				//Integer taskNumber = 1;
				//Integer taskNumber = 18;
				Integer taskNumber = 11;
				String indexingMethod = method.toString();
				engine.setRankingMethod(indexingMethod,taskNumber);
				engine.index(docs);
	
				List<String> inTitle;
				List<String> inAbstract;
				List<String> results;
				
				inTitle = new LinkedList<String>();
				inAbstract = new LinkedList<String>();
				
				// QUERY 1 - Task 1
				// 1) search documents in the title
				/*inTitle.add("motion");
				inTitle.add("control");
				inTitle.add("and");
				inTitle.add("social");
				inTitle.add("interaction");
				
				// 2) search documents in the abstract
				inAbstract.add("motion");
				inAbstract.add("control");
				inAbstract.add("and");
				inAbstract.add("social");
				inAbstract.add("interaction");*/
				
				
				// QUERY 2 - Task 1
				// 1) search documents in the title
				/*inTitle.add("gesture");
				inTitle.add("recognition");
				inTitle.add("user");
				inTitle.add("interface");
				
				// 2) search documents in the abstract
				inAbstract.add("gesture");
				inAbstract.add("recognition");
				inAbstract.add("user");
				inAbstract.add("interface");*/
				
				// QUERY 3 task 18 - online game
				// 1) search documents in the title
				/*inTitle.add("multiplayer");
				inTitle.add("online");
				inTitle.add("game");
				inTitle.add("behavior");
				
				// 2) search documents in the abstract
				inAbstract.add("multiplayer");
				inAbstract.add("online");
				inAbstract.add("game");
				inAbstract.add("behavior");*/
				
				// QUERY 4 - Task 11 smart homes
				// 1) search documents in the title
				inTitle.add("smart");
				inTitle.add("home");
				inTitle.add("ubiquitous");
				inTitle.add("computing");
				
				// 2) search documents in the abstract
				inAbstract.add("smart");
				inAbstract.add("home");
				inAbstract.add("ubiquitous");
				inAbstract.add("computing");
				
				results = engine.search(inTitle, null, inAbstract, null, null, null);
				engine.printResults(results);
			}
		}
		else
			System.out.println("ERROR: the path of a RSS Feed file has to be passed as a command line argument.");
	}
}
