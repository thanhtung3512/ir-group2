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
import java.util.ArrayList;

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
	
	public List<double[]> search(String inTitle, List<String> notInTitle, String inAbstract, List<String> notInAbstract, List<String> inSearchTaskNumber, List<String> inQuery) throws IOException {
		
		//printQuery(inTitle, notInTitle, inAbstract, notInAbstract, inSearchTaskNumber, inQuery);

		List<double[]> precisionRecall = new LinkedList<double[]>();
		

		QueryParser qp = new QueryParser("", iwc.getAnalyzer());
		String[] inTitleSplit = null;
		String[] inAbstractSplit = null;
		try {
			inTitleSplit = qp.parse(inTitle).toString().split(" ");
			inAbstractSplit = qp.parse(inAbstract).toString().split(" ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// implement the Lucene search here
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("index/")));
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(iwc.getSimilarity());
			Builder builder = new BooleanQuery.Builder();
			if(inTitle!=null)
			{
				System.out.println("In title:");
				for(String eachWord : inTitleSplit){
					Query query = new TermQuery(new Term("title", eachWord));
					System.out.println(eachWord);
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
				System.out.println("In abstract:");
				for(String eachWord : inAbstractSplit){
					Query query = new TermQuery(new Term("abstract_text", eachWord));
					System.out.println(eachWord);
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
			for(int nTopDocs = 1; nTopDocs<=hits.length ; nTopDocs+=1){
				int countRelevantDoc = 0;
				for(int i=0;i<hits.length && i< nTopDocs;++i) {
				    Document d = searcher.doc(hits[i].doc);
				    // Count Relevant Document retrieved for Precision at K
				    if(d.get("relevant").equals("1")&&d.get("search_task_number").equals(TaskNumber.toString())){
				    	countRelevantDoc++;
				    }
				}
				// Add recall, precision
				double recall = (double)countRelevantDoc/(double)_amountRelevantDocInTaskNumber;
				double precision = (double)countRelevantDoc/(double)nTopDocs;
				
				double[] precRec = {recall, precision};
				precisionRecall.add(precRec);
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return precisionRecall;
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
	
	public void printResults(List<double[]> results) {
		if (results.size() > 0) {
			//Collections.sort(results);
			for (int i=0; i<results.size(); i++) {
				double[] result = results.get(i);
				System.out.println(" " + (i+1) + ". " + result[0] + ", " + result[1] );
			}
		}
		else
			System.out.println(" no results");
	}
	
	public static void main(String[] args) throws IOException {
		String[] queries = {
			"social recommender system",
			"a recommender system with explanations",
			"novelty and diversity in recommender systems",
		};
		int taskNumber = 2;
		if (args.length > 0) {
			// Loop through queries
			List<double[]> averageCurves = new ArrayList<double[]>();
			for(int query = 0; query < queries.length; query++) {
				
				///  LOOP THROUGH 6 Pre-defined Methods
				for (Integer method = 1; method<=6; method++){
					LuceneSearchApp engine = new LuceneSearchApp();
					
					DocumentCollectionParser parser = new DocumentCollectionParser();
					parser.parse(args[0]);
					List<DocumentInCollection> docs = parser.getDocuments();
					
					// SET RANKING METHOD & INDEX
					String indexingMethod = method.toString();
					engine.setRankingMethod(indexingMethod, taskNumber);
					engine.index(docs);
		
					List<double[]> results;
					
					results = engine.search(queries[query], null, queries[query], null, null, null);
					
					// Calculate average
					
					double[] precRecCurve = getInterpolated11stepPrecisionRecallCurve(results);
					// Create base curve if it doesn't exist
					if(averageCurves.size() < method) {
						double[] baseCurve = new double[11];
						averageCurves.add(baseCurve);
					}
					// Online average calculation
					double[] baseCurve = averageCurves.get(method - 1);
					for(int i = 0; i < 11; i++) {
						baseCurve[i] += precRecCurve[i] / queries.length;
					}
					
					System.out.println("\n--- Method " + method + " ---");
					printSingleCurve(precRecCurve);
					
					//engine.printResults(results);
				}
			}
			
			printCurves(averageCurves);
		}
		else
			System.out.println("ERROR: the path of a RSS Feed file has to be passed as a command line argument.");
	}
	
	public static double FMeasure(double precision, double recall) {
		return 2. / (1. / precision + 1. / recall);
	}
	
	public static double[] getInterpolated11stepPrecisionRecallCurve(
			List<double[]> recPrec) {
		// Calculate max precision of list in reverse. When a list element with
		// a recall smaller than current threshold is encountered, the current
		// max value is added to the curve array and the threshold is
		// decremented
		double max = 0;
		double[] curve = new double[11];
		int threshold = 10;
		
		// Zero values if result does not reach a threshold
		double lastRecall = recPrec.get(recPrec.size() - 1)[0];
		for(int i = threshold; i >= 0; i--) {
			if(lastRecall < (double)threshold / 10.) {
				curve[i] = 0.;
				threshold--;
			}
		}
		
		for(int i = recPrec.size() - 1; i >= 0; i--) {
			double[] pair = recPrec.get(i);
			double recall = pair[0];
			double precision = pair[1];
			while(recall <= (double)threshold / 10.) {
				curve[threshold] = max;
				threshold--;
				if(threshold < 0) {
					break;
				}
			}
			max = precision > max ? precision : max;
		}
		curve[0] = max;
		return curve;
	}
	
	public static void printSingleCurve(double[] curve) {
		for(int i = 0; i < 11; i++) {
			String threshold = i < 10 ? "0." + i : "1.0";
			System.out.println(threshold + ", " + curve[i]);
		}
	}
	
	public static void printCurves(List<double[]> curves) {
		System.out.println("\n=== Average ===");
		for(int i = 0; i < curves.size(); i++) {
			double[] curve = curves.get(i);
			System.out.println("\n--- Method " + (i + 1) + " ---");
			printSingleCurve(curve);
		}
	}
}
