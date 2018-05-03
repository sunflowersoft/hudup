package net.hudup.core.alg;

import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.ctx.ContextList;


/**
 * Traditional recommendation study focuses on inherent information about users and items and how to recommend such relevant items to such users.
 * Database used to build up recommendation algorithms is in form of rating matrix composed of ratings that users give to items.
 * However, currently, additional contextual factors such as time, place, condition and situation existing in real world are considered in recommendation study.
 * Context is modeled by {@link net.hudup.core.data.ctx.Context} interface.
 * There are three approaches (Ricci et al., Recommender Systems Handbook, pp. 232-233) to apply context into recommendation process:
 * <ul>
 * <li>
 * <i>Contextual pre-filtering</i>: Firstly, given context {@code c} is used to select user-item pairs (u, i) which are more relevant to this context, leading to obtain the aware-context cross domain U x I.
 * After that traditional recommendation algorithm is taken on such cross domain.
 * </li>
 * <li>
 * <i>Contextual post-filtering</i>: Firstly, traditional recommendation algorithm is used to produce the list of recommended item.
 * After that context {@code c} is used to fine-tune this list in order to remove irrelevant items according to concrete context.
 * </li>
 * <li>
 * <i>Contextual modeling</i>: The modern context-aware recommendation algorithm is used directly on context-aware cross domain U x I x C including three sets such as users, items and context sets.
 * </li>
 * </ul>
 * This class models the approach of contextual pre-filtering in which contexts are used to pre-filter dataset on which recommendation process are taken to produce context-aware recommended list. 
 * This class inherits directly {@link CompositeRecommender}. Note that {@link CompositeRecommender} represents the recommendation strategy, called {@code composite recommender}.
 * {@link CompositeRecommender} is combination of other {@link Recommender} algorithms in order to produce the best list of recommended items.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ContextPrefilter extends CompositeRecommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This constant specifies the default internal recommendation algorithm.
	 */
	public static final String INNER_RECOMMENDER = "net.hudup.alg.cf.gfall.GreenFallCF";
			

	/**
	 * The original dataset.
	 */
	protected Dataset dataset = null;

	
	/**
	 * The context-aware dataset resulted from filtering the original dataset {@link #dataset}, based on contexts.
	 */
	protected Dataset filteredDataset = null;
	
	
	/**
	 * Default constructor.
	 */
	public ContextPrefilter() {
		super();
		
		Recommender recommender = null;
		try {
			recommender = (Recommender) Class.forName(INNER_RECOMMENDER).newInstance();
		}
		catch (Throwable e) {
			logger.info("Inner recommender " + INNER_RECOMMENDER + " initiated " + this.getClass() + " not exist");
			recommender = null;
		}
		
		if (recommender != null)
			setInnerRecommenders(new AlgList(recommender));
	}
	
	
	@Override
	public void setup(Dataset dataset, Object... params) throws Exception {
		// TODO Auto-generated method stub
		unsetup();
		
		this.dataset = dataset;
		
		if (params.length >= 1) {
			if (params[0] != null && params[0] instanceof ContextList) {
				filteredDataset = dataset.selectByContexts((ContextList)params[0] );
				((Recommender)getInnerRecommenders().get(0)).setup(filteredDataset);
			}
			else
				throw new Exception("Invalid parameter");
		}
		
	}

	
	/**
	 * Setting up the original dataset {@link #dataset} and the filtered dataset {@link #filteredDataset} based on the specified dataset and context list.
	 * Actually, this method uses the specified list of contexts to filter the original dataset so as to achieve the filtered dataset which belongings to the context list.
	 * 
	 * @param dataset specified dataset.
	 * @param contextList specified context list.
	 * @throws Exception if any error raises.
	 */
	public void setup(Dataset dataset, ContextList contextList) throws Exception {
		if (dataset == null)
			throw new Exception("Invalid parameter");
		
		if (contextList == null)
			setup(dataset);
		else
			setup(dataset, new Object[] { contextList });
	}
	
	
	@Override
	public void unsetup() {
		// TODO Auto-generated method stub
		super.unsetup();
		
		if (filteredDataset != null) {
			filteredDataset.clear();
		}
		filteredDataset = null;
		
		dataset = null;
	}


	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		return dataset == null ? filteredDataset : dataset;
	}

	
	/**
	 * Preparing (setting up) the internal recommendation algorithm with regard to context-aware filtered dataset {@link #filteredDataset}.
	 * 
	 * @param param specified recommendation parameter.
	 * @throws Exception if any error raises.
	 */
	private void prepareInnerRecommender(RecommendParam param) throws Exception {
		if (this.filteredDataset != null)
			return;
		
		Recommender recommender = (Recommender)getInnerRecommenders().get(0);
		
		// If there is no contexts list, we recommend as normal situation
		if (param.contextList != null && param.contextList.size() > 0) {
			Dataset filteredDataset = dataset.selectByContexts(param.contextList);
			filteredDataset.setExclusive(true);
			recommender.setup(filteredDataset);
		}
		else if (recommender.getDataset() == null) {
			Dataset dataset = (Dataset) getDataset().clone();
			dataset.setExclusive(true);
			
			recommender.setup(dataset);
		}
	}
	
	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		
		try {
			prepareInnerRecommender(param);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		adjustParam(param);
		return ((Recommender)getInnerRecommenders().get(0)).estimate(param, queryIds);
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub

		try {
			prepareInnerRecommender(param);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		adjustParam(param);
		return ((Recommender)getInnerRecommenders().get(0)).recommend(param, maxRecommend);
	}

	
	/**
	 * The recommendation parameter is very important to method {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)}.
	 * Therefore, this method adjusts the specified recommendation parameter with regard to contexts.
	 * Note, recommendation parameter contains the internal context list.
	 * 
	 * @param param specified recommendation parameter. This is also input and output parameter.
	 */
	private void adjustParam(RecommendParam param) {
		if (param.ratingVector == null || param.contextList == null || param.contextList.size() == 0)
			return;
		
		// Removing item id that not relate to context 
		Set<Integer> fieldIds = Util.newSet();
		fieldIds.addAll(param.ratingVector.fieldIds(true));
		for (int fieldId : fieldIds) {
			Rating rating = param.ratingVector.get(fieldId);
			if (!param.contextList.canInferFrom(rating.contexts))
				param.ratingVector.remove(fieldId);
		}
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "context_prefilter";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ContextPrefilter();
	}

	
}
