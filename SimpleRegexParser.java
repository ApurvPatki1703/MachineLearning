package text.vectorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import text.helper.Properties;
import dragon.nlp.tool.lemmatiser.EngLemmatiser;

/**
 * This class is used to parse the text.
 * @author Apurv
 *
 */
public class SimpleRegexParser implements Parser{
	
	private boolean STEMMING;
	
	private String regex;
	
	private boolean REMOVE_STOP_WORDS;
	
	private HashSet<String> stopWords;
	
	private static HashMap<String, String> lemmaCache = new HashMap<String, String>();
	
	private EngLemmatiser lemmatiser = new EngLemmatiser(Properties.lemmatiserPath, true, false);
	
	public SimpleRegexParser(String regex, boolean stemming, boolean removeStopWords){
		this.regex = regex;
		this.STEMMING = stemming;
		this.REMOVE_STOP_WORDS = removeStopWords;
	}
	
	public SimpleRegexParser(String regex, boolean stemming){
		this.regex = regex;
		this.STEMMING = stemming;
		this.REMOVE_STOP_WORDS = false;
	}
	
	public SimpleRegexParser(String regex){
		this.regex = regex;
		this.STEMMING = false;
		this.REMOVE_STOP_WORDS = false;
	}
	
	public void setStopWords(HashSet<String> words){
		this.stopWords = words;
	}
	/**
	 * This method parses the input string. It has operations to remove stop words and stem the word. 
	 * In case removal of stop word is set in constructor, then use setStopWords method to set stopword 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	@Override
	public ArrayList<String> parse(String text){
		
		String[] splits = text.split(regex);
		ArrayList<String> resultList = new ArrayList<String>();
		//if the string has to be processed
		try{
			for(int i = 0 ; i  < splits.length; i++){
				//convert to lowercase
				String s = splits[i].toLowerCase();
				s.trim();
				//if stop word has to be removed
				if(this.REMOVE_STOP_WORDS && this.STEMMING){
					s = stem(s);
					if(stopWords == null){//if stopword set is not populated
						throw new Exception("populate stop words");
					}else{
						if(!stopWords.contains(s) && !s.equals(""))
							resultList.add(s);
					}	
				}
				if(this.REMOVE_STOP_WORDS && !this.STEMMING){
					if(stopWords == null){//if stopword set is not populated
						throw new Exception("populate stop words");
					}else{
						if(!stopWords.contains(s) && !s.equals(""))
							resultList.add(s);
					}						
				}
				if(this.STEMMING && !s.equals("") && !this.REMOVE_STOP_WORDS){//if stemming has to be performed 
					s = stem(s);
					resultList.add(s);
				}
				if(!this.REMOVE_STOP_WORDS && !this.STEMMING)
					resultList.add(s);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return resultList;
	}	
	
	/**
	 * Stemmer using lucene
	 * @param text
	 * @return
	 */
	public String stem(String text){
		return Stemmer.stem(text);
    }
	/**
	 * THis methods stems the word such that new word is in the English dictionary
	 * @param s
	 * @return
	 */
	private String getTermByTermWordnet(String s) {
		String[] words = s.split(" ");
        StringBuilder rootString = new StringBuilder();
        for(int i=0;i<words.length;i++)
        {
            String lemma = lemmaCache.get(words[i]);
            if(lemma == null)
            {
                lemma = lemmatiser.stem(words[i]);
                lemmaCache.put(words[i], lemma);
            }
            rootString = rootString.append(lemma+" ");
        }
        return rootString.toString().trim();
	}

}
