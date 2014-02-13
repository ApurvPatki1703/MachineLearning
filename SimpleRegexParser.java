package diego.dictionary;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

/**
 * This class is used to parse the text.
 * @author Apurv
 *
 */
public class SimpleRegexParser {
	
	private boolean STEMMING;
	
	private String regex;
	
	private boolean REMOVE_STOP_WORDS;
	
	private HashSet<String> stopWords;
	
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
	public ArrayList<String> parse(String text) throws Exception{
		
		String[] splits = text.split(regex);
		ArrayList<String> resultList = new ArrayList<String>();
		//if the string has to be processed
		
		for(int i = 0 ; i  < splits.length; i++){
			//convert to lowercase
			String s = splits[i].toLowerCase();
			//if stop word has to be removed
			if(this.REMOVE_STOP_WORDS){
				if(stopWords == null){//if stopword set is not populated
					throw new Exception("populate stop words");
				}else{
					if(!stopWords.contains(s))
						resultList.add(s);
				}						
			}
			if(this.STEMMING){//if stemming has to be performed 
				s = stem(s);
				resultList.add(s);
			}
			if(!this.REMOVE_STOP_WORDS && !this.STEMMING)
				resultList.add(s);
		}
		
		return resultList;
	}	
	
	/**
	 * Stemmer using lucene
	 * @param text
	 * @return
	 */
	private String stem(String text){
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

}
