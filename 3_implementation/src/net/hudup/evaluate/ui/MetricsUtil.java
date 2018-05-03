package net.hudup.evaluate.ui;

import java.awt.Component;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.hudup.core.Constants;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.PropList;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.evaluate.Metrics;
import net.hudup.logistic.SystemUtil;
import flanagan.plot.PlotGraph;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MetricsUtil {
	
	
	/**
	 * 
	 */
	protected Metrics metrics = null;
	
	
	/**
	 * 
	 */
	protected RegisterTable algTable = new RegisterTable();
	
	
	/**
	 * 
	 * @param metrics
	 * @param algTable
	 */
	public MetricsUtil(Metrics metrics, RegisterTable algTable) {
		this.metrics = metrics;
		this.algTable = algTable == null ? new RegisterTable() : algTable;
	}
	
	
	/**
	 * 
	 * @param datasetId
	 * @return map of metric values
	 */
	private Map<String, Map<String, Float>> parseMetrics(int datasetId) {
		
		Map<String, Map<String, Float>> values = Util.newMap();
		
		List<String> algNameList = this.metrics.getAlgNameList();
		for (String algName : algNameList) {
			Metrics metrics = (datasetId < 0) ? 
				 this.metrics.mean(algName) : this.metrics.gets(algName, datasetId);
				
			Map<String, Float> algValues = values.get(algName);
			if (algValues == null) {
				algValues = Util.newMap();
				values.put(algName, algValues);
			}
			
			for (int i = 0; i < metrics.size(); i++) {
				Metric metric = metrics.get(i);
				
				if (!metric.isValid())
					continue;
				
				MetricValue metricValue = metric.getAccumValue();
				if (metricValue.isUsed())
					algValues.put(metric.getName(), metricValue.value());
			} // end for i
			
		} // end for algorithm name
		
		
		return values;
	}
	
	
	
	/**
	 * 
	 * @param metricName
	 * @return {@link PlotGraph}
	 */
	public PlotGraph createPlotGraph(String metricName) {
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		if (algNameList.size() == 0 || datasetIdList.size() == 0)
			return null;
		
		int curves = datasetIdList.size();
		int points = algNameList.size();
		int n = Math.max(points, 3);
		double[][] data = PlotGraph.data(curves, n);
		
		for (int i = 0; i < curves; i ++) {
			int datasetId = datasetIdList.get(i);
			
			int curveIdx = 2*i;
			for (int j = 0; j < points; j++) {
				Metric metric = this.metrics.get(metricName, algNameList.get(j), datasetId);
				data[curveIdx][j] = j;
				
				float value = 0;
				if (metric != null && metric.isValid() && metric.getAccumValue().isUsed())
					value = metric.getAccumValue().value();
				else {
					String info = "There is no metric \"" + metricName + "\" on algorithm \"" + 
								algNameList.get(j) + "\" and dataset \"" + datasetId + "\"";
					System.out.println(info);
				}
				data[curveIdx + 1][j] = value;
			}
			
		}
		
		
		// Fixing cubic interpolation
		if (points < n)  {
			
			
			for (int i = 0; i < curves; i ++) {
				
				int curveIdx = 2*i;
				for (int j = points; j < n; j++) {
					data[curveIdx][j] = j;
					data[curveIdx + 1][j] = data[curveIdx + 1][points - 1];
				}
			}
			
			
		}
		
		
		PlotGraph graph = new PlotGraph(data);
		graph.setXaxisLegend("Algorithm");
		graph.setYaxisLegend(metricName);
		
		return graph;
		
	}

	
	
	/**
	 * 
	 * @param datasetId
	 * @return {@link JTable}
	 */
	public JTable createDatasetTable(int datasetId) {
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		Map<String, Map<String, Float>> values = parseMetrics(datasetId);
		
		List<String> metricNameList = this.metrics.getMetricNameList();
		Collections.sort(metricNameList);
		
		Vector<Vector<Object>> data = Util.newVector();
		for (String metricName : metricNameList) {
			Vector<Object> row = Util.newVector();
			row.add(metricName);
			
			for (String algName : algNameList) {
				Map<String, Float> algValues = values.get(algName);
				if (algValues.containsKey(metricName))
					row.add(MathUtil.round(algValues.get(metricName)));
				else
					row.add("");
			}
			data.add(row);
		}
		
		Vector<String> columns = Util.newVector();
		columns.add("");
		columns.addAll(algNameList);
		
		DefaultTableModel generalModel = new DefaultTableModel(data, columns) {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		
		
		JTable table = new JTable(generalModel);
		return table;
		
	}
	
	
	
	/**
	 * 
	 * @return {@link JTable}
	 */
	public JTable createDatasetTable() {
		return createDatasetTable(-1);
	}
	
	
	/**
	 * 
	 * @param metricName
	 * @return {@link JTable}
	 */
	public JTable createMetricTable(String metricName) {
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int datasetId : datasetIdList) {
			Vector<Object> row = Util.newVector();
			row.add("Dataset \"" + datasetId + "\"");
			
			for (String algName : algNameList) {
				Metric metric = this.metrics.get(metricName, algName, datasetId);
				if (metric == null || !metric.isValid())
					row.add("");
				else {
					MetricValue metricValue = metric.getAccumValue();
					float value = metricValue.isUsed() ? metricValue.value() : Constants.UNUSED;
					if (Util.isUsed(value))
						row.add(MathUtil.round(value));
					else
						row.add("");
				}
				
			}
			data.add(row);
		}
		
		Vector<String> columns = Util.newVector();
		columns.add("");
		columns.addAll(algNameList);
		
		DefaultTableModel generalModel = new DefaultTableModel(data, columns) {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		
		
		JTable table = new JTable(generalModel);
		return table;
	}

	
	/**
	 * 
	 * @return parameters {@link JTable}
	 */
	public JTable createParametersTable() {
		List<String> algNameList = this.metrics.getAlgNameList();
		
		RegisterTable sysAlgTable = PluginStorage.getRecommenderReg();
		Map<String, Map<Integer, String>> map = Util.newMap();
		int maxParameters = 0;
		for (String algName : algNameList) {
			
			Alg alg = algTable.query(algName);
			Alg sysAlg = sysAlgTable.query(algName);
			if (alg == null || sysAlg == null)
				continue;
			
			Map<Integer, String> pmap = null;
			if (map.containsKey(algName))
				pmap = map.get(algName);
			else {
				pmap = Util.newMap();
				map.put(algName, pmap);
			}
				
			List<String> paramNames = Util.newList();
			paramNames.addAll(sysAlg instanceof Recommender ? ((Recommender)sysAlg).getParameters().keySet() : sysAlg.getConfig().keySet());
			int countParameters = 0;
			for (String paramName : paramNames) {
				Serializable param = alg.getConfig().get(paramName);
				if (param == null)
					continue;
				
				String paramText = paramName + "=";
				if (param instanceof Boolean)
					paramText += param.toString();
				else if (param instanceof java.lang.Number)
					paramText += MathUtil.format( ((java.lang.Number)param).doubleValue() );
				else if (param instanceof Date) {
					SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
					paramText += df.format( (Date)param );
				}
				else
					paramText += param.toString();
				
				pmap.put(countParameters, paramText);
				countParameters ++;
			}
			
			maxParameters = Math.max(maxParameters, countParameters);
		}
		
		Vector<String> columns = Util.newVector();
		columns.addAll(map.keySet());
		Collections.sort(columns);
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < maxParameters; i++) {
			Vector<Object> row = Util.newVector();
			for (String algName : columns) {
				Map<Integer, String> pmap = map.get(algName);
				if (pmap.containsKey(i))
					row.add(pmap.get(i));
				else
					row.add("");
			}
			
			data.add(row);
		}
		
		
		
		DefaultTableModel parameterModel = new DefaultTableModel(data, columns) {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		
		
		JTable table = new JTable(parameterModel);
		return table;
	}
	
	
	/**
	 * 
	 * @param sheet
	 * @param datasetId
	 * @param row
	 * @param col
	 * @return number of rows
	 */
	private int createDatasetExcel(
			WritableSheet sheet, 
			int datasetId, 
			int row, 
			int col) throws Exception {
		int rows = 0;
		
		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		int newcol = col + 1;
		for (String algName : algNameList) {
			Label lblAlg = new Label(newcol, row, algName, boldCellFormat12);
			sheet.addCell(lblAlg);
			
			newcol++;
		}
		row++;
		rows ++;
		
		Map<String, Map<String, Float>> values = parseMetrics(datasetId);
		List<String> metricNameList = this.metrics.getMetricNameList();
		Collections.sort(metricNameList);
		
		for (String metricName : metricNameList) {
			Label lblMetricName = new Label(col, row, metricName, cellFormat12);
			sheet.addCell(lblMetricName);
			
			newcol = col + 1;
			for (String algName : algNameList) {
				Map<String, Float> algValues = values.get(algName);
				float value = Constants.UNUSED;
				if (algValues.containsKey(metricName))
					value = algValues.get(metricName);
				
				if (Util.isUsed(value)) {
					Number numMetricValue = new Number(newcol, row, MathUtil.round(value), cellFormat12);
					sheet.addCell(numMetricValue);
				}
				
				newcol++;
			}
			
			row++;
			rows ++;
		}
		
		return rows;
	}
	
	
	/**
	 * 
	 * @param sheet
	 * @param row
	 * @param col
	 * @return rows to be saved
	 * @throws Exception
	 */
	private int createDatasetExcel(
			WritableSheet sheet, 
			int row, 
			int col) throws Exception {
		
		return createDatasetExcel(sheet, -1, row, col);
	}

	
	
	/**
	 * 
	 * @param sheet
	 * @param measure
	 * @param row
	 * @param col
	 * @return rows to be saved
	 * @throws Exception
	 */
	private int createMetricExcel(
			WritableSheet sheet, 
			String metricName, 
			int row, 
			int col) throws Exception {
		int rows = 0;
		
		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		int newcol = col + 1;
		for (String algName : algNameList) {
			Label lblAlg = new Label(newcol, row, algName, boldCellFormat12);
			sheet.addCell(lblAlg);
			
			newcol++;
		}
		row++;
		rows++;
		
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		for (int datasetId : datasetIdList) {
			Label lblDataset = new Label(
					col, 
					row, 
					"Dataset \"" + datasetId + "\"", 
					cellFormat12);
			
			sheet.addCell(lblDataset);
			
			newcol = col + 1;
			for (String algName : algNameList) {
				Metric metric = this.metrics.get(metricName, algName, datasetId);
				float value = Constants.UNUSED;
				if (metric != null && metric.isValid() && metric.getAccumValue().isUsed())
					value = metric.getAccumValue().value();
				
				if (Util.isUsed(value)) {
					Number numMetricValue = new Number(newcol, row, MathUtil.round(value), cellFormat12);
					sheet.addCell(numMetricValue);
				}
				
				newcol++;
			}
			row ++;
			rows ++;
		}
		
		return rows;
	}

	
	/**
	 * 
	 * @param uri
	 * @throws Exception
	 */
	public void createExcel(xURI uri) throws Exception {
		UriAdapter adapter = new UriAdapter(uri);
		OutputStream os = adapter.getOutputStream(uri, false);
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		
		WritableSheet sheet = workbook.createSheet("Results", 0);
	
		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

		WritableFont boldFont14 = new WritableFont(WritableFont.TIMES, 14,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat14 = new WritableCellFormat(boldFont14);

		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		int row = 0;
		
		
		Label lblGeneral = new Label(0, row, "General evaluation", boldCellFormat14);
		sheet.addCell(lblGeneral);
		row += 1;
		
		int count = createDatasetExcel(sheet, row, 0);
		row += count + 2;
				
		Label lblDsDetails = new Label(0, row, "Datasets evaluation", boldCellFormat14);
		sheet.addCell(lblDsDetails);
		row += 1;

		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		int maxCount = 0;
		int col = 0;
		for (int datasetId : datasetIdList) {
			Label lblDataset = new Label(col, row, "Dataset \"" + datasetId + "\"", boldCellFormat12);
			sheet.addCell(lblDataset);
			
			count = createDatasetExcel(sheet, datasetId, row + 1, col);
			col += algNameList.size() + 3;
			
			if (maxCount < count)
				maxCount = count;
		}
		maxCount++;
		row += maxCount + 2;
		
		// Evaluation on each metric
		List<String> metricNameList = this.metrics.getMetricNameList();
		Collections.sort(metricNameList);
		for (String metricName : metricNameList) {
			Label lblMetricName = new Label(0, row, metricName + " evaluation", boldCellFormat14);
			sheet.addCell(lblMetricName);
			row += 1;
			row += createMetricExcel(sheet, metricName, row, 0) + 2;
		}

		
		// Algorithm parameters
		Label lblParameters = new Label(0, row, "Algorithm parameters", boldCellFormat14);
		sheet.addCell(lblParameters);
		RegisterTable sysAlgTable = PluginStorage.getRecommenderReg();
		int r = 0;
		int c = 0;
		int maxRow = 0;
		row ++;
		for (String algName : algNameList) {
			
			Alg alg = algTable.query(algName);
			Alg sysAlg = sysAlgTable.query(algName);
			if (alg == null || sysAlg == null)
				continue;
			
			r = 0;
			c ++;
			Label lblAlg = new Label(c, row + r, algName, boldCellFormat12);
			sheet.addCell(lblAlg);
			
			List<String> paramNames = Util.newList();
			paramNames.addAll(sysAlg instanceof Recommender ? ((Recommender)sysAlg).getParameters().keySet() : sysAlg.getConfig().keySet());
			int countRow = 0;
			for (String paramName : paramNames) {
				Serializable param = alg.getConfig().get(paramName);
				if (param == null)
					continue;
				
				String paramText = paramName + "=";
				if (param instanceof Boolean)
					paramText += param.toString();
				else if (param instanceof java.lang.Number)
					paramText += MathUtil.format( ((java.lang.Number)param).doubleValue() );
				else if (param instanceof Date) {
					SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
					paramText += df.format( (Date)param );
				}
				else
					paramText += param.toString();
				
				r ++;
				countRow ++;
				Label paramCell = new Label(c, row + r, paramText, cellFormat12);
				sheet.addCell(paramCell);
			}
			maxRow = Math.max(maxRow, countRow);
		}
		row += maxRow + 3;
		
		
		// Note
		Label lblNote = new Label(0, row, "Note", boldCellFormat12);
		sheet.addCell(lblNote);
		row += 1;
		
		for (int datasetId : datasetIdList) {
			xURI datasetUri = metrics.getDatasetUri(datasetId);
			
			if (datasetUri != null) {
				Label lbl = new Label(0, row, 
						"Dataset \"" + datasetId + "\" has path \"" + datasetUri + "\"", 
						cellFormat12);
				sheet.addCell(lbl);
				
				row ++;
			}
		}
		
		PropList sysProps = SystemUtil.getSystemProperties();
		List<String> keys = Util.newList();
		keys.addAll(sysProps.keySet());
		for (int i = 0; i < keys.size(); i++) {
			row ++;
			String key = keys.get(i).toString();
			Label lbl = new Label(0, row, 
					key + ": " + sysProps.getAsString(key), 
					cellFormat12);
			sheet.addCell(lbl);
		}
		
		workbook.write();
		workbook.close();
		try {
			os.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		adapter.close();
	}

	
	
	/**
	 * 
	 * @return plain text of {@link Metrics}
	 */
	public String createPlainText() {
		StringBuffer buffer = new StringBuffer();
		

		buffer.append("General evaluation");
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		Map<String, Map<String, Float>> values = parseMetrics(-1);
		List<String> metricNameList = this.metrics.getMetricNameList();
		Collections.sort(metricNameList);
		
		for (String algName : algNameList) {
			
			buffer.append("\n\n  " + algName);
			for (int j = 0; j < metricNameList.size(); j++) {
				String metricName =  metricNameList.get(j);
				
				Map<String, Float> algValues = values.get(algName);
				float value = Constants.UNUSED;
				if (algValues.containsKey(metricName))
					value = algValues.get(metricName);
				
				buffer.append("\n");
				buffer.append("    " + metricName + " = " + MathUtil.format(value));
			}
		}
		
		
		buffer.append("\n\n\nDataset evaluation");
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		for (int datasetId : datasetIdList) {
			buffer.append("\n\n  Dataset \"" + datasetId + "\"");
			
			for (int j = 0; j < algNameList.size(); j++) {
				String algName = algNameList.get(j);
				
				buffer.append("\n    " + algName);
				Metrics metrics = this.metrics.gets(algName, datasetId);
				for (int k = 0; k < metrics.size(); k++) {
					float value = Constants.UNUSED;
					Metric metric = metrics.get(k);
					if (metric.isValid() && metric.getAccumValue().isUsed())
						value = metric.getAccumValue().value();
					
					buffer.append("\n      " + metric.getName() + " = " + MathUtil.format(value));
				}
				
			}
		}

		
		for (String metricName : metricNameList) {
			buffer.append("\n\n\n" + metricName + " evaluation");
			
			for (String algName : algNameList) {
				buffer.append("\n\n  " + algName);
				
				for (int datasetId : datasetIdList) {
					Metric metric = this.metrics.get(metricName, algName, datasetId);
					if (metric == null)
						buffer.append("\n    Dataset \"" + datasetId + "\" : NaN");
					else {
						float value = Constants.UNUSED;
						if (metric.isValid() && metric.getAccumValue().isUsed())
							value = metric.getAccumValue().value();
						
						buffer.append("\n    Dataset \"" + datasetId + "\" got " + MathUtil.format(value));
					}
					
					
				}
				
			} // algorithm name iteration
			
		} // metric name iteration
		
		
		// Algorithm parameters
		buffer.append("\n\n\nAlgorithm parameters");
		RegisterTable sysAlgTable = PluginStorage.getRecommenderReg();
		for (String algName : algNameList) {
			buffer.append("\n\n  " + algName);
			
			Alg alg = algTable.query(algName);
			Alg sysAlg = sysAlgTable.query(algName);
			if (alg == null || sysAlg == null)
				continue;
			
			List<String> paramNames = Util.newList();
			paramNames.addAll(sysAlg instanceof Recommender ? ((Recommender)sysAlg).getParameters().keySet() : sysAlg.getConfig().keySet());
			for (String paramName : paramNames) {
				Serializable param = alg.getConfig().get(paramName);
				if (param == null)
					continue;
				
				String paramText = paramName + "=";
				if (param instanceof Boolean)
					paramText += param.toString();
				else if (param instanceof java.lang.Number)
					paramText += MathUtil.format( ((java.lang.Number)param).doubleValue() );
				else if (param instanceof Date) {
					SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
					paramText += df.format( (Date)param );
				}
				else
					paramText += param.toString();
				
				buffer.append("\n    " + paramText);
			}
		}
		
		
		buffer.append("\n\n\nNote");
		for (int datasetId : datasetIdList) {
			xURI datasetUri = this.metrics.getDatasetUri(datasetId);
			if (datasetUri != null) {
				buffer.append("\n  Dataset \"" + datasetId + "\" has path \"" + datasetUri + "\"");
			}
		}
		buffer.append("\n\n");
		PropList sysProps = SystemUtil.getSystemProperties();
		List<String> keys = Util.newList();
		keys.addAll(sysProps.keySet());
		for (int i = 0; i < keys.size(); i++) {
			if (i > 0)
				buffer.append("\n");
			String key = keys.get(i);
			buffer.append("  " + key + ": " + sysProps.getAsString(key));
		}
		
		return buffer.toString();
	}

	
	/**
	 * 
	 * @param comp
	 */
	public void export(Component comp) {
		UriAdapter adapter = new UriAdapter();
        xURI uri = adapter.chooseDefaultUri(comp, false, null);
        adapter.close();
        
        if (uri == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"URI not save", 
					"URI not save", 
					JOptionPane.WARNING_MESSAGE);
			return;
        }
        
        adapter = new UriAdapter(uri);
        boolean existed = adapter.exists(uri);
        adapter.close();
        if (existed) {
        	int ret = JOptionPane.showConfirmDialog(
        			comp, 
        			"URI exist. Do you want to override it?", 
        			"URI exist", 
        			JOptionPane.YES_NO_OPTION, 
        			JOptionPane.QUESTION_MESSAGE);
        	if (ret == JOptionPane.NO_OPTION)
        		return;
        }
		
        try {
            String ext = uri.getLastNameExtension();
	        if(ext != null && ext.toLowerCase().equals("xls")) {
	        	createExcel(uri);
	        }
	        else {
	            adapter = new UriAdapter(uri);
        		Writer writer = adapter.getWriter(uri, false);
        		
		        String text = createPlainText();
		        
		        writer.write(text);
		        writer.flush();
		        writer.close();
		        adapter.close();
	        }
        	
        	JOptionPane.showMessageDialog(comp, 
        			"URI saved successfully", 
        			"URI saved successfully", JOptionPane.INFORMATION_MESSAGE);
        }
		catch(Exception e) {
			e.printStackTrace();
		}
        
        
	}
	
	
	
	
}
