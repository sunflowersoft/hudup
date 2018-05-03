package net.hudup.evaluate;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.Metric;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NoneWrapperMetricList implements Cloneable {

	
	/**
	 * 
	 */
	protected List<Metric> mlist = Util.newList();
	
	
	/**
	 * 
	 */
	public NoneWrapperMetricList() {
		
	}
	
	
	/**
	 * 
	 * @return size of metric list
	 */
	public int size() {
		return mlist.size();
	}
	
	
	/**
	 * 
	 * @param index
	 * @return {@link Metric} at specified index
	 */
	public Metric get(int index) {
		return mlist.get(index);
	}
	
	
	/**
	 * 
	 * @return list of metric
	 */
	public List<Metric> list() {
		return mlist;
	}
	
	
	/**
	 * 
	 * @param metric
	 * @return whether adding successfully
	 */
	public boolean add(Metric metric) {
		if (metric instanceof MetricWrapper)
			return false;
		return mlist.add(metric);
	}
	
	
	/**
	 * 
	 * @param metrics
	 */
	public void addAll(Collection<Metric> metrics) {
		for (Metric metric : metrics)
			add(metric);
	}
	
	
	/**
	 * 
	 * @param index
	 * @return removed {@link MetaMetric}
	 */
	public Metric remove(int index) {
		return mlist.remove(index);
	}
	
	
	/**
	 * 
	 * @param metric
	 * @return whether removal is successfully
	 */
	public boolean remove(Metric metric) {
		return mlist.remove(metric);
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		mlist.clear();
	}
	
	
	/**
	 * 
	 */
	public NoneWrapperMetricList sort() {
		Collections.sort(mlist, new Comparator<Metric>() {

			@Override
			public int compare(Metric metric1, Metric metric2) {
				// TODO Auto-generated method stub
				return metric1.getName().compareTo(metric2.getName());
			}
		});
		
		return this;
	}
	
	
	@Override
	public Object clone() {
		NoneWrapperMetricList result = new NoneWrapperMetricList();
		
		List<MetaMetric> metaMetricList = Util.newList();
		for (Metric metric : mlist) {
			if (metric instanceof MetaMetric)
				metaMetricList.add( (MetaMetric) metric);
			else
				result.add( (Metric) metric.newInstance());
		}
		
		List<Metric> normalMetricList = Util.newList();
		normalMetricList.addAll(result.mlist);
		
		for (MetaMetric metaMetric : metaMetricList) {
			MetaMetric newMetaMetric = (MetaMetric) metaMetric.newInstance();
			
			String[] metaNameList = metaMetric.getMetaNameList();
			List<Metric> metaList = Util.newList();
			
			for (String metaName : metaNameList) {
				for (Metric m : normalMetricList) {
					if (m.getName().equals(metaName)) {
						metaList.add(m);
						break;
					}
				}
			}
			if (metaList.size() == metaNameList.length) {
				newMetaMetric.setup(metaList.toArray());
				result.add(newMetaMetric);
			}
			
			
		}
		
		
		return result;
	}
	
	
	/**
	 * 
	 * @return default metric list
	 */
	public static NoneWrapperMetricList defaultCreate() {
		
		NoneWrapperMetricList metricList = new NoneWrapperMetricList();
		
		SetupTimeMetric setupTime = new SetupTimeMetric();
		metricList.add(setupTime);
		
		SpeedMetric speed = new SpeedMetric();
		metricList.add(speed);
		
		MAE mae = new MAE();
		metricList.add(mae);

		NMAE nmae = new NMAE();
		metricList.add(nmae);
		
		MSE mse = new MSE();
		metricList.add(mse);

		NMSE nmse = new NMSE();
		metricList.add(nmse);
		
		RMSE rmse = new RMSE();
		rmse.setup(mse);
		metricList.add(rmse);

		NRMSE nrmse = new NRMSE();
		nrmse.setup(nmse);
		metricList.add(nrmse);

		Precision precision = new Precision();
		metricList.add(precision);
		
		Recall recall = new Recall();
		metricList.add(recall);
		
		Metric f1 = new F1();
		f1.setup(precision, recall);
		metricList.add(f1);
		
		Pearson pearson = new Pearson();
		metricList.add(pearson);
		
		Spearman pearman = new Spearman();
		metricList.add(pearman);
		
		ARHR arhr = new ARHR();
		metricList.add(arhr);

		HudupRecallMetric hudupRecall = new HudupRecallMetric();
		metricList.add(hudupRecall);
		
		return metricList;
		
	}
	
	
	/**
	 * 
	 * @param rTable
	 * @return {@link NoneWrapperMetricList} extracted from {@link RegisterTable}
	 */
	public static NoneWrapperMetricList extract(RegisterTable rTable) {
		List<Alg> algList = rTable.getAlgList();
		
		NoneWrapperMetricList metricList = new NoneWrapperMetricList();
		for (Alg alg : algList) {
			if (alg instanceof Metric)
				metricList.add((Metric)alg);
		}
		
		return metricList;
	}
	
	
}
