/**
 * 
 */
package net.hudup.core.alg;

import net.hudup.core.Constants;
import net.hudup.core.alg.KBase;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Datasource;
import net.hudup.core.data.KBasePointer;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.xURI;


/**
 * Two typical inherited classes of {@link Recommender} are {@code MemoryBasedRecomender} and {@code ModelBasedRecommender}
 * which in turn are abstract classes for memory-based recommendation algorithm and model-based recommendation algorithm.
 * As a convention, this class is called model-based recommender.
 * <br>
 * Model-based recommender applies knowledge database represented by {@link KBase} interface into performing recommendation task.
 * In other words, {@code KBase} provides both necessary information and inference mechanism to model-based recommender.
 * Ability of inference is the unique feature of {@code KBase}. Model-based recommender is responsible for creating {@code KBase} by
 * calling its {@link #createKB()} method and so, every model-based recommender owns distinguished {@code KBase}.
 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, its {@code KBase} contains such pattern.
 * model-based recommender always takes advantages of {@code KBase} whereas memory-based recommender uses dataset in execution.
 * <br>
 * In general, methods of model-based recommender always using {@code KBase} are {@code ModelBasedRecommender.setup()}, {@code ModelBasedRecommender.createKB()}, {@code ModelBasedRecommender.estimate(...)} and {@code ModelBasedRecommender.recommend(...)}.
 * Especially, it is mandatory that {@code setup()} method of model-based recommender calls method {@code KBase.learn(...)} or {@code KBase.load()}.
 * The association between memory-based recommender represented by {@code MemoryBasedRecommender} class and dataset indicates that all memory-based algorithms use dataset for recommendation task.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class ModelBasedRecommender extends Recommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal {@code KBase} of this model-based recommender.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the internal {@code KBase} contains such pattern.
	 */
	protected KBase kb = null;
	

	/**
	 * Default constructor, which always call {@link #createKB()} to create knowledge base.
	 */
	public ModelBasedRecommender() {
		super();
		// TODO Auto-generated constructor stub
		
		if (kb == null)
			kb = createKB();
		kb.setConfig(config);
	}


	@Override
	public void setup(Dataset dataset, Object... params) throws Exception {
		// TODO Auto-generated method stub
		unsetup();
		
		if (dataset instanceof KBasePointer) {
			kb.load();
			dataset.getConfig().putAll(kb.getConfig());
		}
		else if (!(dataset instanceof Pointer)) {
			kb.learn(dataset, this);
			kb.save();
		}
	}

	
	@Override
	public void unsetup() {
		// TODO Auto-generated method stub
		super.unsetup();
		if (kb != null)
			kb.close();
	}


	/**
	 * Getting the internal knowledge base represented by {@link KBase} interface.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the internal {@code KBase} contains such pattern.
	 * @return knowledge base {@link KBase}
	 */
	public KBase getKBase() {
		return kb;
	}

	
	/**
	 * Creating a new knowledge base represented by {@link KBase} interface from specified dataset.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the new {@code KBase} contains such pattern.
	 * @param dataset specified dataset.
	 * @return new instance of {@link KBase}.
	 */
	public KBase newKBase(Dataset dataset) {
		KBase kb = createKB();
		kb.setConfig((DataConfig)config.clone());
		
		if (dataset instanceof KBasePointer)
			kb.load();
		else if (!(dataset instanceof Pointer))
			kb.learn(dataset, this);
		
		return kb;
	}
	
	
	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		Datasource datasource = kb.getDatasource();
		if (datasource != null)
			return datasource.getDataset();
		else
			return null;
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = new DataConfig();
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		return config;
	}
	
	
	/**
	 * Creating a new knowledge base represented by {@link KBase} interface.
	 * Normally, {@code KBase} is created from dataset. Note, any recommender has a reference to dataset returned from the method {@link #getDataset()}.
	 * For example, if model-based recommender uses frequent purchase pattern to make recommendation, the new {@code KBase} contains such pattern.
	 * @return knowledge base {@link KBase}
	 */
	public abstract KBase createKB();
	
	
}
