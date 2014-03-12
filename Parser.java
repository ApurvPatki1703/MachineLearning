package text.vectorization;

import java.util.ArrayList;
import java.util.HashSet;
/**
 * A template for parser. This template can be used to create other parsers 
 * All we need is a method to parse and get an araylist of tokens.
 * Code is open for extension but closed for manipulations
 * @author Apurv
 *
 */
public interface Parser {

	public ArrayList<String> parse(String text);
	
	public void setStopWords(HashSet<String> words);
	
}
