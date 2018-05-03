package net.hudup.evaluate.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.hudup.core.Constants;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.evaluate.MetricWrapper;
import net.hudup.evaluate.Metrics;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MetricsTable extends SortableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 * @param algTable
	 */
	public MetricsTable(final RegisterTable algTable) {
		// TODO Auto-generated constructor stub
		super (new MetricsTM());
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e)) {
					final Metrics metrics = getMetricsTM().getMetrics();
					if (metrics == null || metrics.size() == 0)
						return;
					
					JPopupMenu contextMenu = new JPopupMenu();
					
					JMenuItem miCopyToClipboard = UIUtil.makeMenuItem((String)null, "Copy to clipboard", 
						new ActionListener() {
							
							/**
							 * 
							 */
							public void actionPerformed(ActionEvent e) {
								
								ClipboardUtil.util.setText(metrics.translate());
							}
						});
					contextMenu.add(miCopyToClipboard);
					
					JMenuItem miSave = UIUtil.makeMenuItem((String)null, "Save", 
						new ActionListener() {
							
							/**
							 * 
							 */
							public void actionPerformed(ActionEvent e) {
								MetricsUtil util = new MetricsUtil(metrics, algTable);
								util.export(getThis());
							}
						});
					contextMenu.add(miSave);

					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
					
				}
			}
			
		});
	}

	
	
	/**
	 * 
	 * @param metrics
	 */
	public synchronized void update(Metrics metrics) {
		
		getMetricsTM().update(metrics);
		
		init();
		if (getColumnCount() > 0)
			getColumnModel().getColumn(0).setMaxWidth(50);
	}

	
	/**
	 * 
	 */
	public synchronized void clear() {
		getMetricsTM().clear();
	}
	
	
	/**
	 * 
	 * @return {@link MetricsTM}
	 */
	private MetricsTM getMetricsTM() {
		return (MetricsTM) getModel();
	}
	
	
	/**
	 * 
	 * @return this metrics table
	 */
	private MetricsTable getThis() {
		return this;
	}
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MetricsTM extends SortableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected Metrics metrics = null;
	

	/**
	 * 
	 */
	public MetricsTM() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * 
	 * @param metrics
	 */
	public void update(Metrics metrics) {
		if (metrics == null) {
			clear();
			return;
		}
		
		this.metrics = metrics;
		
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		int no = 0;
		for (int i = 0; i < this.metrics.size(); i++) {
			MetricWrapper wrapper = this.metrics.get(i);
			if (!wrapper.isValid())
				continue;
			
			Metric metric = wrapper.getMetric();
			
			Vector<Object> row = Util.newVector();
			
			row.add(++no);
			row.add(wrapper.getAlgName());
			row.add(wrapper.getDatasetId());
			row.add(metric.getName());
			row.add(metric.getTypeName());
			
			MetricValue accumValue = wrapper.getAccumValue();
			float value = accumValue != null && accumValue.isUsed() ? accumValue.value() : Constants.UNUSED;
			if (Util.isUsed(value))
				row.add(MathUtil.round(value));
			else
				row.add("");
				
			data.add(row);
		}
		
		
		try {
			setDataVector(data, columns);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 */
	public void clear() {
		Vector<String> columns = createColumns();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		this.metrics = null;
		
		try {
			setDataVector(data, columns);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @return vector of column names
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Algorithm name");
		columns.add("Dataset Id");
		columns.add("Metric name");
		columns.add("Metric type");
		columns.add("Value");
		
		return columns;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isSortable(int column) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	/**
	 * 
	 * @return {@link Metrics}
	 */
	public Metrics getMetrics() {
		return metrics;
	}
	
	
	
}

