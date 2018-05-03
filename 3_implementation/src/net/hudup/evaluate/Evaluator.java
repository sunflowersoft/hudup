package net.hudup.evaluate;

import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.hudup.core.PluginStorage;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.xURI;
import net.hudup.data.DatasetPair;
import net.hudup.data.DatasetPool;
import net.hudup.evaluate.EvaluatorEvent.Type;
import net.hudup.logistic.SystemUtil;


/**
 * {@link Evaluator} is one of main classes of Hudup framework, which is responsible for executing and evaluation algorithms according to built-in and user-defined metrics.
 * Such metrics implement by {@code Metric} interface. As an evaluator of any recommendation algorithm, {@code Evaluator} is the bridge between {@code Dataset} and {@code Recommender} and it has six roles:
 * <ol>
 * <li>
 * It is a loader which loads and configures {@code Dataset}.
 * </li>
 * <li>
 * It is an executor which calls methods {@code Recommender#estimate(...)} and {@code Recommender#recommend(...)}.
 * </li>
 * <li>
 * It is an analyzer which analyzes and translates the result of algorithm execution into the form of evaluation metrics. The execution result is output of method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)}.
 * Evaluation metric is represented by {@code Metric} interface. {@code Metrics} class manages a list of {@code Metric} (s).
 * </li>
 * <li>
 * It is a registry. If external applications require receiving result from {@code Evaluator}, they need to register with it.
 * Such applications must implement {@code EvaluatorListener} interface. In other words, {@code Evaluator} contains a list of {@code EvaluatorListener} (s).
 * </li>
 * <li>
 * Whenever it finishes a call of method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)}, it issues a so-called evaluation event and send back evaluation metrics to external applications after executing algorithm.
 * So it is also a provider. The evaluation event is wrapped by {@code EvaluatorEvent} class.
 * </li>
 * <li>
 * It works as a service which allows scientists to start, pause, resume, and stop the evaluation process via its methods {@code start()}, {@code pause()}, {@code resume()}, and {@code stop()}, respectively.
 * </li>
 * </ol>
 * {@code Evaluator} has four most important methods:
 * <ol>
 * <li>
 * Method {@code evaluate(...)} performs main tasks of {@code Evaluator}, which loads {@code Dataset} and activates method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} on such {@code Dataset}.
 * </li>
 * <li>
 * Method {@code analyze(...)} is responsible for analyzing the result returned by method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} so as to translate such result into evaluation metric.
 * Metrics are used to assess algorithms and they are discussed later. By default implementation, {@code analyze(...)} method will simply call {@code Metric#recalc(...)} method in order to calculate such metric itself.
 * </li>
 * <li>
 * Method {@code issue(...)} issues an evaluation event and sends back evaluation metrics to external applications. Method {@code issue(...)} is also named {@code fireEvaluatorEvent(...)}.
 * </li>
 * </ol>
 * If external applications want to receive metrics, they need to register with {@code Evaluator} by calling {@code Evaluator#addListener(...)} method. The evaluation process has five steps:
 * <ol>
 * <li>
 * {@code Evaluator} calls {@code Evaluator#evaluate(...)} method to load and feed {@code Dataset} to {@code Recommender}.
 * </li>
 * <li>
 * Method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} is executed by {@code Evaluator#evaluate(...)} method to perform recommendation task.
 * </li>
 * <li>
 * Method {@code Evaluator#analyze(...)} analyzes the result returned by method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} and translates such result into {@code Metric}.
 * The {@code Metrics} class manages a list of {@code Metric} (s).
 * </li>
 * <li>
 * External applications that implement {@code EvaluatorListener} interface register with {@code Evaluator} by calling {@code Evaluator#addListener(...)} method.
 * </li>
 * <li>
 * Method {@code Evaluator#issue(...)} sends {@code Metrics} to external applications.
 * </li>
 * </ol>
 * 
 * It is associated with a friendly user interface in order to give facilities to users.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Evaluator extends AbstractRunner {

	
	/**
	 * Configuration of this evaluator.
	 */
	protected EvaluatorConfig config = null;

	
	/**
	 * Holding a list of {@link EventListener} (s)
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
     * List of algorithms that are evaluated by this evaluator. Algorithms are often recommenders.
     */
    protected List<Alg> algList = null;
    
    
    /**
     * This {@code dataset pool} contains many training and testing datasets, which is fed to evaluator, which allows evaluator assesses algorithm on many testing datasets.
     */
    protected DatasetPool pool = null;
    
    
    /**
     * Additional parameter for this evaluator.
     */
    protected Object parameter = null;
    
    
	/**
     * The list of metrics resulted from the evaluation process.
     */
	protected volatile Metrics result = null;
	
	
    /**
     * The list of original metrics used to evaluate algorithms in {@link #algList}.
     */
	protected NoneWrapperMetricList metricList = null;

	
    /**
	 * Constructor with the specified configuration.
	 * @param config specified configuration.
	 */
	public Evaluator(EvaluatorConfig config) {
		this.config = config;
		
		metricList = NoneWrapperMetricList.extract(PluginStorage.getMetricReg());
		metricList.sort();
	}
	
	
	/**
	 * Starting the evaluation process on specified algorithms with specified dataset pool.
	 * The original (built-in) metrics were discovered by Plug-in manager.
	 * 
	 * @param algList specified list of algorithms.
	 * @param pool specified dataset pool containing many training datasets and testing datasets.
	 * @param parameter additional parameter.
	 */
	public synchronized void evaluate(List<Alg> algList, DatasetPool pool, Object parameter) {
		if (isStarted() || this.algList != null || this.pool != null) {
			logger.error("Evaluator is running and so evaluation is not run");
			return;
		}
		
		this.algList = algList;
		this.pool = pool;
		this.parameter = parameter;
		this.result = null;
		start();
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			run0();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Actually, make evaluation process on algorithms with a dataset pool according to original (built-in) metrics.
	 */
	protected void run0() {
		int maxRecommend = config.getAsInt(DataConfig.MAX_RECOMMEND_FIELD);
		int progressStep = 0;
		int progressTotal = pool.getTotalTestingUserNumber() * algList.size();
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; current == thread && algList != null && i < algList.size(); i++) {
			Recommender recommender = (Recommender)algList.get(i);
			
			for (int j = 0; current == thread && pool != null && j < pool.size(); j++) {
				
				Fetcher<Integer> testingUserIds = null;
				try {
					DatasetPair dsPair = pool.get(j);
					Dataset     training = dsPair.getTraining();
					Dataset     testing = dsPair.getTesting();
					int         datasetId = j + 1;
					xURI        datasetUri = testing.getConfig().getUriId();
					
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
					int vRecommendedCount = 0;
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
						
						RecommendParam param = new RecommendParam(testingUserId);
						//
						long beginRecommendTime = System.currentTimeMillis();
						RatingVector recommended = recommender.recommend(param, maxRecommend);
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
						
						RatingVector vTesting = testing.getUserRating(testingUserId); 
						fireEvaluatorEvent(new EvaluatorEvent(
								this, 
								Type.doing, 
								speedMetrics,
								recommended, 
								vTesting)); // firing time speed metric
						
						
						if (recommended != null) { // successful recommendation
							
							Metrics recommendedMetrics = result.recalc(
									recommender.getName(), 
									datasetId,
									new Object[] { recommended, testing }
								); // calculating recommendation metric
							
							vRecommendedCount++;
							
							fireEvaluatorEvent(new EvaluatorEvent(
									this, 
									Type.doing, 
									recommendedMetrics, 
									recommended, 
									vTesting)); // firing recommendation metric
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
							new Object[] { new FractionMetricValue(vRecommendedCount, vCurrentTotal) }
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
				
				SystemUtil.enhanceAuto();

			} // dataset iterate
			
		} // algorithm iterate
		
		
		synchronized (this) {
			thread = null;
			paused = false;
			clear();

			fireEvaluatorEvent(new EvaluatorEvent(this, Type.done, result));

			notifyAll();
		}
		
	}
	
	
	/**
	 * Getting 
	 * @return result {@link Metrics}
	 */
	public Metrics getResult() {
		return result;
	}

	
	/**
	 * 
	 * @param metricList
	 */
	public synchronized void setMetricList(List<Metric> metricList) {
		if (isStarted()) {
			logger.error("Evaluator is started and so it is impossible to set up metric list");
			return;
		}
		
		this.metricList.clear();
		this.metricList.addAll(metricList);
		this.metricList.sort();
	}
	
	
	/**
	 * Getting the list of metrics resulted from the evaluation process.
	 * @return list of metrics resulted from the evaluation process.
	 */
	public List<Metric> getMetricList() {
		return this.metricList.list();
	}
	
	
	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		this.algList = null;
		this.pool = null;
		this.parameter = null;
	}

	
	@Override
	public void task() {
		// TODO Auto-generated method stub
		logger.info("Evaluator#task not used because overriding Evaluator#run");
	}

	
	@SuppressWarnings("static-access")
	@Override
	public synchronized void forceStop() {
		super.forceStop();
		
		try {
			Thread.currentThread().sleep(1000);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fireEvaluatorEvent(new EvaluatorEvent(this, Type.done, result));
	}
	
	
	/**
	 * Getting configuration of this evaluator.
	 * @return configuration of this evaluator.
	 */
	public EvaluatorConfig getConfig() {
		return config;
	}
	
	
	/**
	 * Add the specified listener to the end of listenerList
	 * 
	 * @param listener {@link EvaluatorListener}
	 * 
	 */
	public void addEvaluatorListener(EvaluatorListener listener) {
		synchronized (listenerList) {
			listenerList.add(EvaluatorListener.class, listener);
		}
    }

    
	/**
	 * Remove the specified listener from the listener list
	 * 
	 * @param listener {@link EvaluatorListener}
	 */
    public void removeEvaluatorListener(EvaluatorListener listener) {
		synchronized (listenerList) {
			listenerList.remove(EvaluatorListener.class, listener);
		}
    }
	
    
    /**
     * Return a {@link EvaluatorListener} list
     * 
     * @return array of {@link EvaluatorListener}
     * 
     */
    protected EvaluatorListener[] getEvaluatorListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluatorListener.class);
		}
    }

    
    /**
     * 
     * @param evt
     */
    protected void fireEvaluatorEvent(EvaluatorEvent evt) {
    	
		EvaluatorListener[] listeners = getEvaluatorListeners();
		
		for (EvaluatorListener listener : listeners) {
			try {
				listener.receivedEvaluation(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
    }

    
    /**
     * 
     * @param listener
     */
	public void addProgressListener(ProgressListener listener) {
		synchronized (listenerList) {
			listenerList.add(ProgressListener.class, listener);
		}
    }

    
	/**
	 * 
	 * @param listener
	 */
    public void removeProgressListener(ProgressListener listener) {
		synchronized (listenerList) {
			listenerList.remove(ProgressListener.class, listener);
		}
    }
	
    
    /**
     * 
     * @return array of {@link ProgressListener} (s)
     */
    protected ProgressListener[] getProgressListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(ProgressListener.class);
		}
    }
    
    
    
    /**
     * Firing {@link ProgressEvent}
     * @param evt
     */
    protected void fireProgressEvent(ProgressEvent evt) {
    	if (!isStarted())
    		return;

    	ProgressListener[] listeners = getProgressListeners();
		
		for (ProgressListener listener : listeners) {
			try {
				listener.receiveProgress(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
    }

    
}
