package net.hudup.evaluate;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.logistic.MathUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class EvaluatorEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static enum Type {doing, done_one, done}
	
	
	/**
	 * 
	 */
	protected Type type = Type.doing;

	
	/**
	 * 
	 */
	protected Metrics metrics = null;
	
	
	/**
	 * 
	 */
	protected Object[] params = null;
	
	
	/**
	 * 
	 * @param evaluator
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type) {
		super(evaluator);
		// TODO Auto-generated constructor stub
		
		this.type = type;
	}

	
	
	/**
	 * 
	 * @param evaluator
	 * @param metrics
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type, Metrics metrics) {
		this(evaluator, type);
		// TODO Auto-generated constructor stub
		
		setMetrics(metrics);
	}

	
	/**
	 * 
	 * @param evaluator
	 * @param type
	 * @param metrics
	 * @param params
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type, Metrics metrics, Object... params) {
		this(evaluator, type, metrics);
		// TODO Auto-generated constructor stub
		
		setParams(params);
	}

	
	/**
	 * 
	 * @return {@link Evaluator}
	 */
	public Evaluator getEvaluator() {
		return (Evaluator) getSource();
	}
	
	
	
	/**
	 * 
	 * @return {@link Type}
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * 
	 * @return {@link Metrics}
	 */
	public Metrics getMetrics() {
		return metrics;
	}
	
	
	/**
	 * 
	 * @param metrics
	 */
	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}
	
	
	
	/**
	 * 
	 * @return parameter list
	 */
	public Object[] getParams() {
		return params;
	}
	
	
	/**
	 * 
	 * @param params
	 */
	public void setParams(Object... params) {
		this.params = params;
	}
	
	
	
	/**
	 * 
	 * @return translated text
	 */
	public String translate() {
		return translate(null, -1);
	}
	
	
	/**
	 * @param fAlgName
	 * @param fDatasetId
	 * 
	 * @return translated text
	 */
	public String translate(String fAlgName, int fDatasetId) {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		if (this.metrics == null)
			return buffer.toString();
		
		RatingVector recommended = null;
		RatingVector testing = null;
		if (params != null && params.length >= 2 && 
				params[0] instanceof RatingVector && params[1] instanceof RatingVector) {
			recommended = (RatingVector) params[0];
			testing = (RatingVector) params[1];
		}
		
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		int i = 0;
		for (String algName : algNameList) {
			if (fAlgName != null && !fAlgName.isEmpty() && !algName.equals(fAlgName))
				continue;
			
			if (i > 0)
				buffer.append("\n\n\n");
			
			i++;
			buffer.append("========== Algorithm \"" + algName + "\"" + (type == Type.doing ? "" : " - Final result ") + "==========");
			List<Integer> datasetIdList = this.metrics.getDatasetIdList(algName);
			Collections.sort(datasetIdList);
			
			for (int datasetId : datasetIdList) {
				if (fDatasetId >= 0 && datasetId != fDatasetId)
					continue;
				
				buffer.append("\n\n----- Testing dataset \"" + datasetId + "\" -----");
				
				if (testing != null)
					buffer.append("\nTesting = [" + testing.toText() + "]");
				if (recommended != null)
					buffer.append("\nRecommend = [" + recommended.toText() + "]");
				
				Metrics metrics = this.metrics.gets(algName, datasetId);
				for (int k = 0; k < metrics.size(); k++) {
					
					MetricWrapper wrapper = metrics.get(k);
					if (!wrapper.isValid())
						continue;
					
					MetricValue metricValue = (type == Type.doing ? wrapper.getCurrentValue() : wrapper.getAccumValue());
					float value = metricValue.isUsed() ? metricValue.value() : Constants.UNUSED;
					
					buffer.append("\n" + wrapper.getName() + " = " + MathUtil.format(value));
				}
				buffer.append("\n----- Testing dataset \"" + datasetId + "\" -----");
			}
			
			buffer.append("\n\n========== Algorithm \"" + algName + "\"" + (type == Type.doing ? "" : " - Final result ") + "==========");
			
			
		}
		
		return buffer.toString();
	}

	
}
