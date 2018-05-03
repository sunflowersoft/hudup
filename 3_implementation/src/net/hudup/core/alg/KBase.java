/**
 * 
 */
package net.hudup.core.alg;

import java.awt.Component;
import java.io.Serializable;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Datasource;
import net.hudup.core.parser.TextParserUtil;


/**
 * Data in Hudup framework is organized into two following formats:
 * <br>
 * <ul>
 * <li>
 * Low-level format is structured as rating matrix whose each row consists of user ratings on items, often called raw data.
 * Another low-level format is dataset represented by {@link Dataset} which consists of rating matrix and other information such as user profile, item profile and contextual information.
 * </li>
 * <li>
 * High-level format contains fine-grained information and knowledge extracted from raw data and dataset, for example, user interests and user purchasing pattern; besides, it may have internal inference mechanism which allows us to deduce new knowledge.
 * High-level format structure is called {@code knowledge base} or {@code KBase} in short.
 * </li>
 * </ul>
 * Model-based recommender represented by {@code ModelBasedRecommender} class applies {@code KBase} into performing recommendation task.
 * In other words, {@code KBase} provides both necessary information and inference mechanism to model-based recommender.
 * Ability of inference is the unique feature of {@code KBase}.
 * <br>
 * Every model-based recommender owns distinguished {@code KBase}.
 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, its {@code KBase} contains such pattern.
 * <br>
 * In general, {@code KBase} is a significant aspect of Hudup framework. Followings are essential methods of {code KBase}:
 * <br>
 * <ul>
 * <li>
 * Method {@link #setConfig(DataConfig)} is responsible for setting configurations for {code KBase}.
 * These configurations are used by other methods. The typical configuration is uniform resource identifier (URI) indicating where to store {@code KBase},
 * which is used by methods {@link #load()} and {@link #save()}.
 * </li>
 * <li>
 * Methods {@link #load()} and {@link #save()} are used to read/write {@code KBase} from/to storage system, respectively. Storage system can be files, database, etc.
 * The methods {@link #load()} and {@link #save()} indicate that {@code KBase} can be loaded from and saved to storage system. They do not specify how to load and store {@code KBase}.
 * In other words, they are rules with which the infrastructure must comply.
 * </li>
 * <li>
 * Method {@link #learn(Dataset, Alg)} is responsible for creating {@code KBase} from {@code Dataset} which is the first input parameter.
 * Because every model-based recommender owns distinguished {@code KBase}, the second parameter is such algorithm.
 * The association between model-based recommender and {@code KBase} is tight.
 * The method {@link #learn(Dataset, Alg)} tells us that {@code KBase} can be learned by any approaches: machine learning, data mining, artificial intelligence, statistics, etc.
 * </li>
 * <li>
 * Methods {@link #clear()} and {@link #isEmpty()} are responsible for cleaning out {@code KBase} and checking whether {@code KBase} is empty or not, respectively.
 * </li>
 * </ul>
 * Methods of model-based recommender always using {@code KBase} are {@code ModelBasedRecommender.setup()}, {@code ModelBasedRecommender.createKB()}, {@code ModelBasedRecommender.estimate(...)} and {@code ModelBasedRecommender.recommend(...)}.
 * Especially, it is mandatory that {@code setup()} method of model-based recommender calls method {@code KBase.learn(...)} or {@code KBase.load()}.
 * Conversely, the association between memory-based recommender represented by {@code MemoryBasedRecommender} class and dataset indicates that all memory-based algorithms use dataset for recommendation task.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface KBase extends Serializable {
	
	
	/**
	 * Every KBase always has a configuration represented by {@link DataConfig} class.
	 * This constant defines the default file (archive) name of such configuration.
	 */
	public final static String KBASE_CONFIG = "kbase" + TextParserUtil.CONNECT_SEP + "cfg";
	
	
	/**
	 * In the configuration of KBase, there is an entry for indicating the URI of data source of KBase.
	 * As usual, data source refers to the dataset from which KBase is learned.
	 * This constant defines the key of such entry.
	 */
	public final static String DATASOURCE_URI = "datasource_uri";

	
	/**
	 * In the configuration of KBase, there is an entry for indicating the name of KBase.
	 * This constant defines the key of such entry.
	 */
	public final static String KBASE_NAME = "kbase_name";
	
	
	/**
	 * This method is used to read {@code KBase} from storage system. Storage system can be files, database, etc.
	 */
	void load();
	

	/**
	 * This method is responsible for creating {@code KBase} or learning from specified dataset.
	 * Because every model-based recommender owns distinguished {@code KBase}, the second parameter is such algorithm.
	 * The association between model-based recommender and {@code KBase} is tight.
	 * This method tells us that {@code KBase} can be learned by any approaches: machine learning, data mining, artificial intelligence, statistics, etc.
	 * @param dataset specified dataset.
	 * @param alg the algorithm that owns this KBase. 
	 */
	void learn(Dataset dataset, Alg alg);
	
	
	/**
	 * This method is used to write {@code KBase} to storage system. Storage system can be files, database, etc.
	 */
	void save();
	
	
	/**
	 * Getting the configuration of KBase by copying (exporting) the internal configuration to the specified configuration. 
	 * @param storeConfig specified configuration where the internal configuration is copied.
	 */
	void export(DataConfig storeConfig);
	
	
	/**
	 * Checking whether {@code KBase} is empty or not. If a KBase which is empty, it needs to re-learned by calling the method {@link #learn(Dataset, Alg)} again.
	 * @return whether knowledge base is empty or not.
	 */
	boolean isEmpty();
	
	
	/**
	 * Cleaning out {@code KBase}. After this method is called, KBase becomes empty, which means that method {@link #isEmpty()} returns {@code true} but KBase can be re-learned by calling the method {@link #learn(Dataset, Alg)} again.
	 */
	void clear();
	
	
	/**
	 * Close this KBase. After this method is called, KBase cannot be re-learned. It becomes garbage for system garbage collector.
	 */
	void close();
	
	
	/**
	 * Every KBase has a name. This method returns such name.
	 * @return the name of knowledge base.
	 */
	String getName();
	
	
	/**
	 * Every KBase always has a configuration represented by {@link DataConfig} class.
	 * This method returns the internal configuration of KBase.
	 * @return configuration of KBase.
	 */
	DataConfig getConfig();
	
	
	/**
	 * This method is responsible for setting configurations for {code KBase} by specified configuration.
	 * These configurations are used by other methods. The typical configuration is uniform resource identifier (URI) indicating where to store {@code KBase},
	 * which is used by methods {@link #load()} and {@link #save()}.
	 * @param config specified configuration.
	 */
	void setConfig(DataConfig config);
	
	
	/**
	 * Data source represented by {@link Datasource} class is a special class used to point to a dataset. Of course it contains a URI of such dataset. Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
	 * As usual and for convenience, data source also contains dataset. We can considered data source as a wrapper of dataset with additional information about dataset.
	 * This method returns the datasource of the dataset that KBase is learned or loaded.
	 * @return datasource of the dataset that KBase is learned or loaded.
	 */
	Datasource getDatasource();
	
	
	/**
	 * KBase (knowledge base) can be viewed in visual manner.
	 * This method shows a graphic user interface (GUI) to see and exploit KBase.
	 * @param comp the component works as parent of the graphic user interface (GUI) to see and exploit KBase.
	 */
	void view(Component comp);
	
	
}
