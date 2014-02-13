package diego.dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

/**
 * This class creates numeric mapping to words in the vocabulary. It assumes that data source of the dictionary has to be read line by line 
 * and split by regex. Regex has to be passed to that method as a parameter. Stemming is set to true by default.
 * Use other constructor to set it to false.
 * It provides an API to create vectors using the words in vocabulary.
 * It can be used to create sparse matrix of text samples compatible with matlab.  
 * @author Apurv
 */
public class Dictionary {
	
	/**
	 * number of documents
	 */
	private int docs;
	/**
	 * thread safe map
	 */
	private ConcurrentHashMap<String, Integer> dictionary;
	/**
	 * words in documents
	 */
	private ConcurrentHashMap<Integer, HashSet<String>> termMap;	
	/**
	 * inverse document frequency
	 */
	private ConcurrentHashMap<String, Double> idf;
	
	private boolean STEMMING = true;
	
	public Dictionary(){
		this.docs = 0;
		dictionary = new ConcurrentHashMap<String, Integer>();
		idf = new ConcurrentHashMap<String, Double>();
		termMap = new ConcurrentHashMap<Integer, HashSet<String>>();
	}
	
	public Dictionary(boolean stemming){
		this.docs = 0;
		dictionary = new ConcurrentHashMap<String, Integer>();
		this.STEMMING = stemming;
		idf = new ConcurrentHashMap<String, Double>();
		termMap = new ConcurrentHashMap<Integer, HashSet<String>>();
	}
	/**
	 * @return the docs
	 */
	public int getDocs() {
		return docs;
	}
	/**
	 * This method increments the doc count by 1.
	 */
	public void incrementDocCount(){
		this.docs++;
	}
	/**
	 * This method adds the words to dictionary from given datasource path.
	 * @param dataSource - the path to the data source
	 */
	public void addDataFromSource(String dataSource, String regex){
		try{
			BufferedReader dataReader = new BufferedReader(new FileReader(dataSource));
			String data = "";
			while((data = dataReader.readLine()) != null){
				String [] dataArr = data.split(regex);
				for(int i = 0 ; i < dataArr.length; i++){
					String s = dataArr[i];
					s = s.toLowerCase(); // convert string to lower case. this is important to ensure correctness.
					if(STEMMING){
						s = stem(s); //stem the word if option is set
					}
					if(!dictionary.containsKey(s)){
						dictionary.put(s, dictionary.size() + 1);//size increases automatically once element is put. No need to increment
						termDocCount(this.docs, s);
					}
				}
			}
			dataReader.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}		
	}
	/**
	 * Stemmer using lucene
	 * @param text
	 * @return
	 */
	public String stem(String text){
		StringBuffer result = new StringBuffer();
        if (text!=null && text.trim().length()>0){
            StringReader tReader = new StringReader(text);
            
            Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_30, "English");
            TokenStream tStream = analyzer.tokenStream("contents", tReader);
            TermAttribute term = tStream.addAttribute(TermAttribute.class);

            try {
                while (tStream.incrementToken()){
                    result.append(term.term());
                    result.append(" ");
                }
            } catch (IOException ioe){
                System.out.println("Error: "+ioe.getMessage());
            }
        }
        if (result.length()==0)
            result.append(text);
        return result.toString().trim();
    }
	/**
	 * gets the numeric mapping of the word. This can be used for vectorization.
	 * It can also be used to anonymize data. For this method to work dictionary must be populated first
	 * @param key
	 * @return
	 * @throws Exception 
	 */
	public Integer get(String key) throws Exception{
		if(dictionary.size() == 0){
			throw new Exception("Dictionary not populated use addDataFromSource(String dataSource, String regex) method to populate");
		}			
		if(dictionary.containsKey(key))
			return dictionary.get(key);
		return null;
	}
	/**
	 * This method adds words to the dictionary from the given Iterable type. It will stem the words if option is not set to false.
	 * @param data
	 */
	public void add(Iterable<String> data){
		Iterator<String> iter = data.iterator();
		while(iter.hasNext()){
			String s = iter.next();
			s = s.toLowerCase();
			if(STEMMING){
				s = stem(s);
			}
			if(!dictionary.containsKey(s)){
				dictionary.put(s, dictionary.size() + 1);
				termDocCount(this.docs, s);
			}
		}
	}
	/**
	 * This method adds parameter word to the dictionary. It will stem the words if option is not set to false.
	 * @param data
	 */
	public void add(String data){
		String s = data;
		s = s.toLowerCase();
		if(STEMMING){
			s = stem(s);
		}
		if(!dictionary.containsKey(s)){
			dictionary.put(s, dictionary.size() + 1);
			termDocCount(this.docs, s);
		}
	}
	/**
	 * Checks if the word is found the contextual vocabulary
	 * @param key
	 * @return
	 */
	public boolean contains(String key) {
		if(this.dictionary.containsKey(key))
			return true;
		return false;
	}	
	/**
	 * This method returns the iterator over dictionary. 
	 * This method is used for inverting a vector
	 * @return
	 */
	public Iterator<Entry<String, Integer>> iterator(){
		return this.dictionary.entrySet().iterator();
	}
	/**
	 * This method returns the size of the dictionary
	 * @return
	 */
	public int size() {
		return this.dictionary.size();
	}
	/**
	 * This method gets the idf of a term from dictioanry
	 * @param term
	 * @return
	 */
	public Double idf(String term){
		if(idf.containsKey(term)){
			if(idf.get(term) > 1.0){
				System.out.println(idf.get(term));
			}
			Double res = Math.log(this.docs / idf.get(term));
			return res;
		}
		return null;
	}
	
	/**
	 * This method adds word to the document.
	 */
	private void addWordToTermMap(Integer docId, String term){
		if(this.termMap.containsKey(docId)){
			HashSet<String> termSet = this.termMap.get(docId);
			termSet.add(term);
			this.termMap.put(docId, termSet);
		}else{
			HashSet<String> termSet = new HashSet<String>();
			termSet.add(term);
			this.termMap.put(docId, termSet);
		}
	}
	/**
	 * This method maintains the count of word across document for IDF
	 */
	private void termDocCount(Integer docId, String term){
		if(this.termMap.containsKey(docId)){
			//termset has all the words in document
			HashSet<String> termSet = this.termMap.get(docId);
			//add term to the idf map only if we have not seen the term before 
			if(!termSet.contains(term)){
				//idf map counts the occurances of terms across documents
				if(this.idf.containsKey(term))
					this.idf.put(term, this.idf.get(term) + 1);
				else
					this.idf.put(term, 1d);
				addWordToTermMap(docId, term);
			}
		}
		//if termmap does not contain the document id, that means we have not seen the document before
		else{
			if(this.idf.containsKey(term))
				this.idf.put(term, this.idf.get(term) + 1);
			else
				this.idf.put(term, 1d);
			addWordToTermMap(docId, term);
		}
	}
}
