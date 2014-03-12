package text.vectorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import text.helper.Properties;
import dragon.nlp.tool.lemmatiser.EngLemmatiser;

/**
 * This parsers implements lemmatized regex parser
 * @author Apurv
 *
 */
public class LemmatizedRgexParser  implements Parser{
	
	private boolean LEMMATIZATION;
	
	private String regex;
	
	private boolean REMOVE_STOP_WORDS;
	
	private HashSet<String> stopWords;
	
	private static HashMap<String, String> lemmaCache = new HashMap<String, String>();
	
	private EngLemmatiser lemmatiser = new EngLemmatiser(Properties.lemmatiserPath, true, false);
	
	public LemmatizedRgexParser(String regex, boolean lemmatization, boolean removeStopWords){
		this.regex = regex;
		this.LEMMATIZATION = lemmatization;
		this.REMOVE_STOP_WORDS = removeStopWords;
	}
	
	public LemmatizedRgexParser(String regex, boolean lemmatization){
		this.regex = regex;
		this.LEMMATIZATION = lemmatization;
		this.REMOVE_STOP_WORDS = false;
	}
	
	public LemmatizedRgexParser(String regex){
		this.regex = regex;
		this.LEMMATIZATION = false;
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
				if(this.REMOVE_STOP_WORDS && this.LEMMATIZATION){
					if(stopWords == null){//if stopword set is not populated
						throw new Exception("populate stop words");
					}else{
						if(!stopWords.contains(s) && !s.equals(""))
							resultList.add(s);
					}
					s = getTermByTermWordnet(s);
				}
				if(this.REMOVE_STOP_WORDS && !this.LEMMATIZATION){
					if(stopWords == null){//if stopword set is not populated
						throw new Exception("populate stop words");
					}else{
						if(!stopWords.contains(s) && !s.equals(""))
							resultList.add(s);
					}						
				}
				if(this.LEMMATIZATION && !s.equals("") && !this.REMOVE_STOP_WORDS){//if stemming has to be performed 
					s = getTermByTermWordnet(s);
					resultList.add(s);
				}
				if(!this.REMOVE_STOP_WORDS && !this.LEMMATIZATION)
					resultList.add(s);
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return resultList;
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
