package text.vectorization;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class creates numeric mapping to words in the vocabulary so that we can create vectors later.
 * Use a Parser to parse data first and add the tokens to dictionary. There no support for parsing, stemming , stop words elimination here.
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
	
	public Dictionary(){
		this.docs = 0;
		dictionary = new ConcurrentHashMap<String, Integer>();
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
	 * gets the numeric mapping of the word. This can be used for vectorization.
	 * It can also be used to anonymize data. For this method to work dictionary must be populated first
	 * @param key
	 * @return
	 * @throws Exception 
	 */
	public Integer get(String key) throws Exception{
		if(dictionary.size() == 0){
			throw new Exception("add data first");
		}			
		if(dictionary.containsKey(key))
			return dictionary.get(key);
		return null;
	}
	/**
	 * This method adds words to the dictionary from the given Iterable type.
	 * @param data
	 */
	public void add(Iterable<String> data){
		Iterator<String> iter = data.iterator();
		while(iter.hasNext()){
			String s = iter.next();
			if(!dictionary.containsKey(s)){
				dictionary.put(s, dictionary.size() + 1);
				termDocCount(this.docs, s);
			}
		}
	}
	/**
	 * This method adds parameter word to the dictionary. 
	 * @param data
	 */
	public void add(String data){
		String s = data;
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
