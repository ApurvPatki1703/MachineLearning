package diego.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
/**
 * This class represents a vector of String, Integer.
 * It supports put, get, norm and dot product operations.
 * @author Apurv
 */
public class Vector {
	/**
	 * Storage of the vector
	 */
	private HashMap<Integer, Double> vector;
	/**
	 * Constructor
	 */
	public Vector(){
		vector = new HashMap<Integer, Double>();
	}
	/**
	 * put the key value pair in the vector. This put method is incremental, which means if 
	 * key is already present then values are added.
	 * @param key
	 * @param val
	 */
	public void put(Integer key, Double val){
		if(vector.containsKey(key)){
			vector.put(key, vector.get(key) + val);
			return;
		}			
		vector.put(key, val);
	}
	/**
	 * Puts the key value pair. If it already exits then overwrites.
	 * @param key
	 * @param val
	 */
	public void putReplace(Integer key, Double val){
		vector.put(key, val);
	}
	/**
	 * Get the corresponding value of the key
	 * @param key
	 * @return
	 */
	public Double get(Integer key){
		if(vector.containsKey(key))
			return vector.get(key);
		return null;
	}
	/**
	 * Method to check if an element exists in the vector
	 * @param key
	 * @return
	 */
	public boolean contains(Integer key){
		if(this.vector.containsKey(key))
			return true;
		else
			return false;
	}
	/**
	 * Method to compute dotProduct of the two vectors.
	 * This method does not normalize the dot product by norms. Use normalizedDotProduct if normalized result is required.
	 * This method throws exception for null input
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	public Double dotProduct(Vector b) throws Exception{
		Double res = 0.0;
		Vector a = this;
		if(b == null)
			throw new Exception("Null vector not allowed");
		//iterate on the smaller vector
		if(a.size() <= b.size()){
			Iterator<Entry<Integer, Double>> iter = a.iterator();
			while(iter.hasNext()){
				Entry<Integer, Double> e = iter.next();
				if(b.contains(e.getKey())){
					res += a.get(e.getKey()) * b.get(e.getKey());
				}
			}
		}else{
			Iterator<Entry<Integer, Double>> iter = b.iterator();
			while(iter.hasNext()){
				Entry<Integer, Double> e = iter.next();
				if(a.contains(e.getKey())){
					res += a.get(e.getKey()) * b.get(e.getKey());
				}
			}
		}		
		return res;
	}
	/**
	 * Method to compute normalized dot product of two vectors. Result of dot product is normalized by L2 norm.
	 * This method throws Exception for null input and zero vectors as norm is zero
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public Double normalizedDotProduct(Vector b) throws Exception{
		Double res = dotProduct(b);
		Double norm1 = this.norm();
		Double norm2 = b.norm();
		if(norm1 == 0 || norm2 == 0)
			throw new Exception("Cannot normalize by zero Vector, ensure vector has some length greater than zero");
		return res / (norm1 * norm2);
	}
	/**
	 * Number of elements in the vector
	 * @return
	 */
	public Integer size(){
		return vector.size();
	}
	/**
	 * Method to iterate over vector
	 * @return
	 */
	public Iterator<Entry<Integer, Double>> iterator(){
		return vector.entrySet().iterator();
	}
	/**
	 * Method to compute norm of a vector. This is L2 norm
	 * @return
	 * @throws Exception 
	 */
	public Double norm() throws Exception{
		Vector a = this;
		return Math.sqrt(a.dotProduct(a));
	}
	/**
	 * This method scales a vector by a scalar. It is product of a scalar and vector
	 * @param alpha
	 * @return
	 */
	public Vector scale(Double alpha){
		Vector a = this;
		Vector res = new Vector();
		Iterator<Entry<Integer, Double>> iter = a.iterator();
		while(iter.hasNext()){
			Entry<Integer, Double> e = iter.next();
			res.put(e.getKey(), alpha * e.getValue());
		}
		return res;
	}
	/**
	 * Method to add two vectors. This method throws exception on null vector input.
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	public Vector add(Vector b) throws Exception{
		if(b == null)
			throw new Exception("Null vector not allowed");
		Vector a = this;
		Vector res = new Vector();
		Iterator<Entry<Integer, Double>> iter = a.iterator();
		while(iter.hasNext()){
			Entry<Integer, Double> e = iter.next();
			res.put(e.getKey(),  e.getValue());
		}
		iter = b.iterator();
		while(iter.hasNext()){
			Entry<Integer, Double> e = iter.next();
			if(res.contains(e.getKey()))
				res.put(e.getKey(),  e.getValue() + res.get(e.getKey()));
			else
				res.put(e.getKey(), e.getValue());
		}
		return res;
	}
	/**
	 * Once the vector is created, this method normalizes each item by the length of vector.
	 * Vector must be created first.
	 * @return 
	 * @throws Exception 
	 */
	public Vector normalizeL2() throws Exception{
		Double norm = this.norm();
		if(norm == 0)
			throw new Exception("Cannot normalize, norm should be non zero, make sure there are some elment in vector");
		return scale(1/norm);
	}
	/**
	 * This method multiplies each term in vector by its idf. This converts the initial TF vector to TF IDF vector. 
	 * @param dict -- dictionary which has the idf values
	 * @param map 
	 * @return
	 */
	public Vector makeTFIDFvector(Dictionary dict, HashMap<Integer, String> map) {
		Vector a = this;
		Vector v = new Vector();
		Iterator<Entry<Integer, Double>> iter = a.iterator();
		while(iter.hasNext()){
			Entry<Integer, Double> e = iter.next();
			if(dict.contains(map.get(e.getKey())))
				v.put(e.getKey(), dict.idf(map.get(e.getKey())) * e.getValue());
		}
		return v;
	}
}
