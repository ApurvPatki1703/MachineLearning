MachineLearning
===============

This package can be used when text is to be vectorized and used in tools such as matlab or octave. 
The package provides text parsing, vectorization using TF and TF-IDF scheme. 
The output is a sparse file that can directly be loaded in matlab and algorithms can be run on it.

THE WORKFLOW:
=============

For operating any machine learning algorithm on text, first text has to be vectorized. This package is precisely for such processing. Steps to use this package:

1) Use SimpleRegexParser.java to parse your text. You can use any other parser if you want.
2) Add the terms from parser to dictionary using Dictionary.java. Dictionary represents the terms in vocabulary. It maps the terms in vocabulary to an integer index for vectorization.
3) Once the dictionary is created, use Vector.java to vectorize each sentence/ text unit/ as per requirement. You can create a wrapper and use compose your class by having Vector as a member. Vector has Integer key and Double value. This integer corresponds to mapping of term in dictionary. Hence the Vector is sparse vector which is very important in text processing. Populate Vector in the wrapper class using the dictionary.
4) Write the required instances to file.
5) Load file in matlab and run algorithms.

NOTES:
========

Vector.java provides a put and get operations. It also provides operations such as dot product.
Dictionary.java provides a put and get operatins as well. 
SimpleRegexParser.java can be used to parser can be used to parse data and feed terms to dictionary. Constructor offers stemming and stop words removal. For stop words removal one has to provide a hashset of stopwords.

