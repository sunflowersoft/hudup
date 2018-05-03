package net.hudup.evaluate;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.xURI;

/**
 * This {@code Metrics} class manages a list of {@code Metric} (s). It uses @{@code metric wrapper} represented by {@link MetricWrapper} for sophisticated management tasks.
 * The most important method of {@code Metrics} is {@link #recalc(String, int, Class, Object[])} method which is responsible for re-calculating all metrics in metric wrappers of this {@code Metrics}.
 * {@code Evaluator} often calls {@link #recalc(String, int, Class, Object[])} method to re-calculate all metrics after each iteration of algorithm execution.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Metrics implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * List of metric wrappers. Each metric wrapper holds an internal metric and so such internal metric is the real one.
	 * The reason of this structure because metric wrapper contains additional information for complex task.
	 * So it is possible to consider metric wrappers as normal metrics.
	 * Note a metric wrapper inherits an abstract metric which is also a metric.
	 */
	protected List<MetricWrapper> metricWrapperList = Util.newList();
	
	
	/**
	 * A map of many dataset URI (s). Each dataset URI points to where to store such dataset.
	 * Each dataset URI is associated with a key which is known as identifier (ID) of the dataset.
	 */
	protected Map<Integer, xURI> datasetUriMap = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public Metrics() {
		
	}

	
	/**
	 * Reseting all metrics, which means
	 */
	public void reset() {
		for (MetricWrapper metricWrapper : metricWrapperList) {
			metricWrapper.reset();
		}
	}
	
	
	/**
	 * Re-calculating all metrics in metric wrappers of this {@link Metrics}.
	 * {@code Evaluator} often calls this method to re-calculate all metrics after each iteration of algorithm execution.
	 * @param algName only metric wrappers (of this {@link Metrics}) having the same algorithm specified by this input parameter are calculated.
	 * @param datasetId only metric wrappers (of this {@link Metrics}) having the same dataset identifier specified by this input parameter are calculated.
	 * @param metricClass if {@code not null}, only metric wrappers (of this {@link Metrics}) having the same class specified by the input parameter {@code metricClass} are calculated.
	 * @param params input parameters of method {@link MetricWrapper#recalc(Object...)} for re-calculating metrics.
	 * @return current computed {@link Metrics}.
	 */
	public Metrics recalc(
			String algName, 
			int datasetId, 
			Class<? extends Metric> metricClass, 
			Object[] params) {
		
		Metrics result = new Metrics();
		
		List<MetricWrapper> metaWrapperList = Util.newList();
		List<MetricWrapper> wrapperList = Util.newList();
		for (MetricWrapper wrapper : this.metricWrapperList) {
			if (wrapper.getMetric() == null)
				continue;
			
			try {
				if  (  
						( wrapper.getAlgName().equals(algName) ) &&
						( metricClass ==  null ? true : wrapper.getMetric().getClass().equals(metricClass) ) &&
						( wrapper.getDatasetId() == datasetId )
					) 
				{
					Metric metric = wrapper.getMetric();
					
					if (metric instanceof MetaMetric) {
						metaWrapperList.add(wrapper); // Meta metrics are calculated later based on normal metrics 
					}
					else {
						if (wrapper.recalc(params)) {
							result.add(wrapper);
							wrapperList.add(wrapper); // normal metric list
						}
					}
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		
		// Calculating meta metric
		for (MetricWrapper metaWrapper : metaWrapperList) { // for each meta metric
			MetaMetric metaMetric = (MetaMetric) metaWrapper.getMetric();
			
			boolean requireCalc = true;
			Metric[] meta = metaMetric.getMeta();
			for (Metric m : meta) { // for each normal metric of meta metric
				
				boolean found = false;
				for (MetricWrapper wrapper : wrapperList) { // checking whether normal metric exists
					if (wrapper.getName().equals(m.getName())) { // if (wrapper == m) 
						found = true;
						break;
					}
				}
				
				if (!found)  { // if normal metric not exists
					requireCalc = false;
					break;
				}
			}
			
			if (requireCalc) { // if normal metric exists
				try {
					if (metaWrapper.recalc( new Object[] { })) // now calculating meta metric based on normal metric determined
						result.add(metaWrapper);
				} 
				catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		return result;
	}
	
	
	/**
	 * This method calls the method {@link #recalc(String, int, Class, Object[])} to re-calculate all metrics in metric wrappers of this {@link Metrics}.
	 * @param algName only metric wrappers (of this {@link Metrics}) having the same algorithm specified by this input parameter are calculated.
	 * @param datasetId only metric wrappers (of this {@link Metrics}) having the same dataset identifier specified by this input parameter are calculated.
	 * @param params input parameters of method {@link MetricWrapper#recalc(Object...)} for re-calculating metrics.
	 * @return current computed {@link Metrics}.
	 */
	public Metrics recalc(String algName, int datasetId, Object[] params) {
		return recalc(algName, datasetId, (Class<? extends Metric>)null, params);
	}
	
	
	/**
	 * 
	 * @return list of distinct {@link MetricWrapper}.
	 */
	protected List<MetricWrapper> getDistinctMetricList() {
		
		List<MetricWrapper> result = Util.newList();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			
			boolean found = false;
			for (MetricWrapper w : result) {
				if (w.getName().equals(wrapper.getName())) {
					found = true;
					break;
				}
			}
			
			if (!found)
				result.add(wrapper);
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param algName
	 * @param datasetId
	 * @return list of metric names
	 */
	public List<String> getMetricNameList(String algName, int datasetId) {
		List<String> names = Util.newList();
		for (MetricWrapper wrapper : metricWrapperList) {
			try {
				if (wrapper.getAlgName().equals(algName) &&
						wrapper.getDatasetId() == datasetId) {
					
					names.add(wrapper.getMetric().getName());
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return names;
	}
	
	
	/**
	 * 
	 * @param algName
	 * @return list of metric names
	 */
	public List<String> getMetricNameList(String algName) {
		Set<String> names = Util.newSet();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			try {
				if (wrapper.getAlgName().equals(algName)) {
					
					names.add(wrapper.getMetric().getName());
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		List<String> list = Util.newList();
		list.addAll(names);
		return list;
	}
	
	
	/**
	 * 
	 * @param datasetId
	 * @return list of metric names
	 */
	public List<String> getMetricNameList(int datasetId) {
		Set<String> names = Util.newSet();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			try {
				if (wrapper.getDatasetId() == datasetId) {
					
					names.add(wrapper.getMetric().getName());
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		List<String> list = Util.newList();
		list.addAll(names);
		return list;
	}

	
	/**
	 * 
	 * @return list of metric names
	 */
	public List<String> getMetricNameList() {
		Set<String> names = Util.newSet();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			try {
				names.add(wrapper.getMetric().getName());
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		List<String> list = Util.newList();
		list.addAll(names);
		return list;
	}

	
	/**
	 * 
	 * @param algName
	 * @return list of dataset id (s)
	 */
	public List<Integer> getDatasetIdList(String algName) {
		Set<Integer> idSet = Util.newSet();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			try {
				if ( wrapper.getAlgName().equals(algName) ) {
					
					idSet.add(wrapper.getDatasetId());
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		List<Integer> idList = Util.newList();
		idList.addAll(idSet);
		return idList;
	}
	
	
	/**
	 * 
	 * @param algClass
	 * @return list of dataset id (s)
	 */
	public List<Integer> getDatasetIdList(Class<? extends Metric> algClass) {
		Set<Integer> idSet = Util.newSet();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			try {
				if ( algClass.isAssignableFrom(wrapper.getMetric().getClass()) ) {
					
					idSet.add(wrapper.getDatasetId());
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		List<Integer> idList = Util.newList();
		idList.addAll(idSet);
		return idList;
	}

	
	/**
	 * 
	 * @return list of dataset id (s)
	 */
	public List<Integer> getDatasetIdList() {
		Set<Integer> idSet = Util.newSet();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			idSet.add(wrapper.getDatasetId());
		}
		
		List<Integer> idList = Util.newList();
		idList.addAll(idSet);
		return idList;
	}
	
	
	/**
	 * 
	 * @param datasetId
	 * @return dataset {@link xURI}
	 */
	public xURI getDatasetUri(int datasetId) {
		return datasetUriMap.get(datasetId);
	}
	
	
	/**
	 * 
	 * @param datasetId
	 * @return list of algorithm names
	 */
	public List<String> getAlgNameList(int datasetId) {
		Set<String> algNameSet = Util.newSet();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			try {
				if ( wrapper.getDatasetId() == datasetId ) {
					
					algNameSet.add(wrapper.getAlgName());
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		List<String> algNameList = Util.newList();
		algNameList.addAll(algNameSet);
		return algNameList;
	}
	
	
	/**
	 * 
	 * @return list of algorithm name (s)
	 */
	public List<String> getAlgNameList() {
		Set<String> nameSet = Util.newSet();
		
		for (MetricWrapper wrapper : metricWrapperList) {
			nameSet.add(wrapper.getAlgName());
		}
		
		List<String> nameList = Util.newList();
		nameList.addAll(nameSet);
		return nameList;
	}
	
	
	/**
	 * 
	 * @param metricName
	 * @param algName
	 * @param datasetId
	 * @return index of {@link MetricWrapper}
	 */
	public int indexOf(String metricName, String algName, int datasetId) {
		try {
			for (int i = 0; i < metricWrapperList.size(); i++) {
				MetricWrapper wrapper = metricWrapperList.get(i);
				if (wrapper.getMetric().getName().equals(metricName) &&
						wrapper.getAlgName().equals(algName) &&
						wrapper.getDatasetId() == datasetId)
					return i;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	
	/**
	 * 
	 * @param wrapper
	 * @return index of {@link MetricWrapper}
	 */
	public int indexOf(MetricWrapper wrapper) {
		return indexOf(wrapper.getName(), wrapper.getAlgName(), wrapper.getDatasetId());
	}
	
	
	/**
	 * 
	 * @param index
	 * @return {@link MetricWrapper}
	 */
	public MetricWrapper get(int index) {
		return metricWrapperList.get(index);
	}
	
	
	/**
	 * 
	 * @param metricName
	 * @param algName
	 * @param datasetId
	 * @return {@link MetricWrapper}
	 */
	public MetricWrapper get(String metricName, String algName, int datasetId) {
		int index = indexOf(metricName, algName, datasetId);
		if (index != -1)
			return get(index);
		else
			return null;
	}
	
	
	/**
	 * 
	 * @param algName
	 * @param datasetId
	 * @return {@link Metrics}
	 */
	public Metrics gets(String algName, int datasetId) {
		Metrics result = new Metrics();
		
		for (int i = 0; i < metricWrapperList.size(); i++) {
			try {
				MetricWrapper wrapper = metricWrapperList.get(i);
				if (wrapper.getAlgName().equals(algName) &&
						wrapper.getDatasetId() == datasetId) {
					
					result.add(wrapper);
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param algName
	 * @return map of {@link Metrics}
	 */
	public Map<Integer, Metrics> gets(String algName) {
		Map<Integer, Metrics> result = Util.newMap();
		
		List<Integer> datasetIdList = getDatasetIdList(algName);
		for (int datasetId : datasetIdList) {
			Metrics metrics = gets(algName, datasetId);
			result.put(datasetId, metrics);
		}
		
		return result;
	}
	
	
	/**
	 * Calculating the mean metric for each algorithm because an algorithm runs on many database which corresponding a kind of metric. 
	 * So there are many metrics that have the same kind and we calculate their mean.
	 * @param algName
	 * @return mean {@link Metrics}
	 */
	public Metrics mean(String algName) {
		List<MetricWrapper> wrapperList = getDistinctMetricList();
		
		Metrics result = new Metrics();
		for (MetricWrapper wrapper : wrapperList) {
			
			MeanMetaMetric meanMetric = new MeanMetaMetric();
			meanMetric.setup(wrapper.getMetric());
			result.add(algName, 0, wrapper.getDatasetUri(), meanMetric);
		}
		

		Map<Integer, Metrics> datasetMetrics = gets(algName);
		Set<Entry<Integer, Metrics>> entries = datasetMetrics.entrySet();
		for (Entry<Integer, Metrics> entry : entries) {
			Metrics metrics = entry.getValue();
			
			for (int i = 0; i < metrics.size(); i++) {
				MetricWrapper wrapper = metrics.get(i);
				
				for (int j = 0; j < result.size(); j++) {
					MetricWrapper meanWrapper = result.get(j);
					try {
						meanWrapper.recalc(wrapper);
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // end for j
				
			} // end for i
		} // end for entries
		
		return result;
	}
	
	
	/**
	 * 
	 * @param datasetId
	 * @return map of {@link Metrics} (s)
	 */
	public Map<String, Metrics> gets(int datasetId) {
		Map<String, Metrics> result = Util.newMap();
		
		List<String> algNameList = getAlgNameList(datasetId);
		for (String algName : algNameList) {
			Metrics metrics = gets(algName, datasetId);
			result.put(algName, metrics);
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param metricWrapper
	 * @return whether adding successfully
	 */
	public boolean add(MetricWrapper metricWrapper) {
		try {
			int index = indexOf(metricWrapper); 
			if (index == -1)
				return metricWrapperList.add(metricWrapper);
			else
				return false;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	/**
	 * 
	 * @param metrics
	 * @return whether adding successfully
	 */
	public boolean add(Metrics metrics) {
		for (int i = 0; i < metrics.size(); i++) {
			MetricWrapper wrapper = metrics.get(i);
			add(wrapper);
		}
		
		return true;
	}
	
	
	/**
	 * 
	 * @param algName
	 * @param datasetId
	 * @param datasetUri
	 * @param metric
	 * @return whether adding successfully
	 */
	public boolean add(String algName, int datasetId, xURI datasetUri, Metric metric) {
		MetricWrapper wrapper = MetricWrapper.create(metric, algName, datasetId, datasetUri);
		if (wrapper == null)
			return false;
		else {
			boolean added = add(wrapper); 
			if (added)
				datasetUriMap.put(datasetId, datasetUri);
			return added;
		}
	}
	
	
	/**
	 * 
	 * @param algName
	 * @param datasetId
	 * @param metric
	 * @return whether adding successfully
	 */
	public boolean add(String algName, int datasetId, Metric metric) {
		return add(algName, datasetId, null, metric);
	}

	
	/**
	 * 
	 * @param algName
	 * @param datasetId
	 * @param datasetUri
	 * @param defaultMetricList
	 * @return whether adding successfully
	 */
	public boolean add(String algName, int datasetId, xURI datasetUri, List<Metric> defaultMetricList) {
		for (Metric metric : defaultMetricList) {
			add(algName, datasetId, datasetUri, metric);
		}
		
		return true;
	}
	
	
	/**
	 * 
	 * @param algName
	 * @param datasetId
	 * @param defaultMetricList
	 * @return whether adding successfully
	 */
	public boolean add(String algName, int datasetId, List<Metric> defaultMetricList) {
		return add(algName, datasetId, null, defaultMetricList);
	}

	
	/**
	 * 
	 * @return size of metrics
	 */
	public int size() {
		return metricWrapperList.size();
	}
	
	
	/**
	 * 
	 * @param index
	 * @return removed {@link MetricWrapper}
	 */
	public MetricWrapper removeByIndex(int index) {
		return metricWrapperList.remove(index);
	}
	
	
	/**
	 * 
	 * @param wrapper
	 * @return removed {@link MetricWrapper}
	 */
	public MetricWrapper remove(MetricWrapper wrapper) {
		int index = indexOf(wrapper);
		if (index != -1)
			return removeByIndex(index);
		else
			return null;
	}
	
	
	/**
	 * 
	 * @param metricName
	 * @param algName
	 * @param datasetId
	 * @return removed {@link MetricWrapper}
	 */
	public MetricWrapper remove(String metricName, String algName, int datasetId) {
		int index = indexOf(metricName, algName, datasetId);
		if (index != -1)
			return removeByIndex(index);
		else
			return null;
	}

	
	/**
	 * 
	 * @param algName
	 * @param datasetId
	 * @return removed {@link Metrics}
	 */
	public Metrics remove(String algName, int datasetId) {
		Metrics removed = new Metrics();
		
		Metrics metrics = gets(algName, datasetId);
		for (int i = 0; i < metrics.size(); i++) {
			MetricWrapper wrapper = metrics.get(i);
			wrapper = remove(wrapper);
			if (wrapper != null)
				removed.add(wrapper);
		}
		
		return removed;
	}
	
	
	/**
	 * 
	 * @param algName
	 * @return removed {@link Metrics}
	 */
	public Metrics remove(String algName) {
		Metrics removed = new Metrics();
		
		List<Integer> datasetIdList = getDatasetIdList(algName);
		for (int datasetId : datasetIdList) {
			removed.add(remove(algName, datasetId));
		}
		
		return removed;
	}

	
	/**
	 * 
	 * @param datasetId
	 * @return removed {@link Metrics}
	 */
	public Metrics removeByDatasetId(int datasetId) {
		Metrics removed = new Metrics();
		
		List<String> algNameList = getAlgNameList(datasetId);
		for (String algName : algNameList) {
			removed.add(remove(algName, datasetId));
		}
		
		datasetUriMap.remove(datasetId);
		return removed;
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		metricWrapperList.clear();
		datasetUriMap.clear();
	}
	
	
	/**
	 * 
	 * @return translated text
	 */
	public String translate() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		
		List<String> algNameList = getAlgNameList();
		Collections.sort(algNameList);
		
		for (int i = 0; i < algNameList.size(); i++) {
			String algName = algNameList.get(i);
			
			if (i > 0)
				buffer.append("\n\n\n");
			
			buffer.append("========== Algorithm \"" + algName + "\" - Final result ==========");
			List<Integer> datasetIdList = getDatasetIdList(algName);
			Collections.sort(datasetIdList);
			for (int j = 0; j < datasetIdList.size(); j++) {
				int datasetId = datasetIdList.get(j);
				
				buffer.append("\n\n----- Testing dataset \"" + datasetId + "\" -----");
				
				Metrics metrics = gets(algName, datasetId);
				for (int k = 0; k < metrics.size(); k++) {
					
					MetricWrapper wrapper = metrics.get(k);
					if (!wrapper.isValid())
						continue;
					
					MetricValue metricValue = wrapper.getAccumValue();
					float value = metricValue.isUsed() ? metricValue.value() : Constants.UNUSED;
					
					buffer.append("\n" + wrapper.getName() + " = " + MathUtil.format(value));
				}
				
				buffer.append("\n----- Testing dataset \"" + datasetId + "\" -----");
			}
			
			buffer.append("\n\n========== Algorithm \"" + algName + "\" - Final result ==========");
			
			
		}
		
		return buffer.toString();
	}

	
}
