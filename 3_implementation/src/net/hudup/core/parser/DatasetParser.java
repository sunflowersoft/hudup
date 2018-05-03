/**
 * 
 */
package net.hudup.core.parser;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Dataset;



/**
 * {@code DatasetParser} is one of main interface responsible for reading and parsing coarse data such as CSV file, Excel file, database table into {@link Dataset}.
 * The main method of {@code DatasetParser} is {@link #parse(DataConfig)} that receives the input as configuration of data source and returns the output as a {@code Dataset}.
 * For example, in order to parse <a href="http://grouplens.org/datasets/movielens">Movielens data</a>, programmers often create a MovielensParser which is an implementation of this interface {@link DatasetParser} and then,
 * define the method {@link #parse(DataConfig)} according to features of Movielens data.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface DatasetParser extends Alg {
	
	/**
	 * This is the main method that receives the input as configuration of data source and returns the output as a {@link Dataset}.
	 * For example, in order to parse <a href="http://grouplens.org/datasets/movielens">Movielens data</a>, programmers often create a MovielensParser which is an implementation of this interface {@link DatasetParser} and then,
	 * define this method according to features of Movielens data.
	 * @param config Configuration of data source represented by {@link DataConfig}.
	 * @return {@link Dataset}
	 */
	Dataset parse(DataConfig config);
	
	
	/**
	 * Getting the name of this parser.
	 * For example, in order to parse <a href="http://grouplens.org/datasets/movielens">Movielens data</a>, programmers often create a MovielensParser which is an implementation of this interface and then,
	 * define this method to return string &quot;MovielensParser&quot;.
	 * @return name of parser
	 */
	String getName();
	
	
	/**
	 * Testing whether this parser supports the specified data driver.
	 * @param driver Specified data driver, represented by {@link DataDriver} class
	 * @return {@code true} if this parser supports the specified data driver; otherwise the returned is {@code false} 
	 */
	boolean support(DataDriver driver);
}
