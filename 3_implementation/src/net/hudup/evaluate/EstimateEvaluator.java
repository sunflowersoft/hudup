package net.hudup.evaluate;

import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.data.DatasetPair;
import net.hudup.evaluate.EvaluatorEvent.Type;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class EstimateEvaluator extends Evaluator {

	
	/**
	 * 
	 * @param config
	 */
	public EstimateEvaluator(EvaluatorConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int unratedCount = config.getAsInt(DataConfig.MAX_RECOMMEND_FIELD);
		int progressStep = 0;
		int progressTotal = pool.getTotalTestingUserNumber() * algList.size();
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; i < algList.size() && current == thread; i++) {
			Recommender recommender = (Recommender)algList.get(i);
			
			for (int j = 0; j < pool.size() && current == thread; j++) {
				
				Fetcher<Integer> testingUserIds = null;
				try {
					DatasetPair dsPair = pool.get(j);
					Dataset     training = dsPair.getTraining();
					Dataset     testing = dsPair.getTesting();
					int         datasetId = j + 1;
					xURI         datasetUri = testing.getConfig().getUriId();
					
					// Adding default metrics to metric result
					result.add( recommender.getName(), datasetId, datasetUri, ((NoneWrapperMetricList)metricList.clone()).sort().list() );
					
					long beginSetupTime = System.currentTimeMillis();
					//
					recommender.setup(training);
					//
					long endSetupTime = System.currentTimeMillis();
					long setupElapsed = endSetupTime - beginSetupTime;
					Metrics setupMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							SetupTimeMetric.class, 
							new Object[] { setupElapsed / 1000.0f }
						); // calculating setup time metric
							
					
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, setupMetrics)); // firing setup time metric

					testingUserIds = testing.fetchUserIds();
					int vCurrentTotal = testingUserIds.getMetadata().getSize();
					int vCurrentCount = 0;
					int vEstimatedCount = 0;
					while (testingUserIds.next() && current == thread) {
						
						progressStep++;
						vCurrentCount++;
						ProgressEvent progressEvt = new ProgressEvent(this, progressTotal, progressStep);
						progressEvt.setAlgName(recommender.getName());
						progressEvt.setDatasetId(datasetId);
						progressEvt.setCurrentCount(vCurrentCount);
						progressEvt.setCurrentTotal(vCurrentTotal);
						fireProgressEvent(progressEvt);
						
						Integer testingUserId = testingUserIds.pick();
						if (testingUserId == null || testingUserId < 0)
							continue;
						
						RatingVector vTesting = testing.getUserRating(testingUserId); 
						RatingVector vQuery = (RatingVector) vTesting.clone();
						Set<Integer> queryIds = setupQueryIds(unratedCount, vQuery);
						if (queryIds.size() == 0)
							continue;
						
						RecommendParam param = new RecommendParam(vQuery, testing.getUserProfile(testingUserId));
						//
						long beginRecommendTime = System.currentTimeMillis();
						RatingVector estimated = recommender.estimate(param, queryIds);
						long endRecommendTime = System.currentTimeMillis();
						//
						param.clear();
						long recommendElapsed = endRecommendTime - beginRecommendTime;
						Metrics speedMetrics = result.recalc(
								recommender.getName(), 
								datasetId, 
								SpeedMetric.class, 
								new Object[] { recommendElapsed / 1000.0f }
							); // calculating time speed metric
						
						fireEvaluatorEvent(new EvaluatorEvent(
								this, 
								Type.doing, 
								speedMetrics,
								estimated, 
								vTesting)); // firing time speed metric
						
						
						if (estimated != null) { // successful recommendation
							
							Metrics recommendedMetrics = result.recalc(
									recommender.getName(), 
									datasetId,
									new Object[] { estimated, vTesting, testing }
								); // calculating recommendation metric
							
							vEstimatedCount++;
							
							fireEvaluatorEvent(new EvaluatorEvent(
									this, 
									Type.doing, 
									recommendedMetrics, 
									estimated, 
									testing.getUserRating(testingUserId))); // firing recommendation metric
						}
						
						
						synchronized (this) {
							while (paused) {
								notifyAll();
								wait();
							}
						}
						
					} // User id iterate
					
					Metrics hudupRecallMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							HudupRecallMetric.class, 
							new Object[] { new FractionMetricValue(vEstimatedCount, vCurrentTotal) }
						);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, hudupRecallMetrics));
					
					Metrics doneOneMetrics = result.gets(recommender.getName(), datasetId);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.done_one, doneOneMetrics));
					
				} // end try
				catch (Throwable e) {
					e.printStackTrace();
				}
				finally {
					try {
						if (testingUserIds != null)
							testingUserIds.close();
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					recommender.unsetup();
				}
				
				
			} // dataset iterate
			
		} // algorithm iterate
		
		
		synchronized (this) {
			thread = null;
			paused = false;
			clear();
			
			notifyAll();
			
		}
		fireEvaluatorEvent(new EvaluatorEvent(this, Type.done, result));
		
	}

	
	/**
	 * Setting up the set of query item id (s)
	 * @param unratedCount
	 * @param outQuery
	 * @return the set of query item id (s)
	 */
    protected static Set<Integer> setupQueryIds(int unratedCount, RatingVector outQuery) {
		
		List<Integer> ratedList = Util.newList();
		ratedList.addAll(outQuery.fieldIds(true) );
		
		Set<Integer> result = Util.newSet();
		if (ratedList.size() < 2)
			return result;
		
		unratedCount = unratedCount == 0 ? ratedList.size() : unratedCount;
		unratedCount = Math.min(unratedCount, ratedList.size());
		List<Integer> rndList = MathUtil.pickingOrdering(ratedList, unratedCount);
		result.addAll(rndList);
		
		outQuery.remove(rndList);
		
		return result;
    }


}
