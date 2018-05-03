/**
 * 
 */
package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.ui.AlgListBox;
import net.hudup.core.alg.ui.AlgListChooser;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.HudupException;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.BatchScript;
import net.hudup.data.DatasetPair;
import net.hudup.data.DatasetPool;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.ui.DatasetPoolTable;
import net.hudup.data.ui.StatusBar;
import net.hudup.data.ui.TxtOutput;
import net.hudup.evaluate.EstimateEvaluator;
import net.hudup.evaluate.EvaluatorConfig;
import net.hudup.evaluate.EvaluatorEvent;
import net.hudup.evaluate.EvaluatorEvent.Type;
import net.hudup.evaluate.Metrics;
import net.hudup.evaluate.ProgressEvent;

/**
 * @author Loc Nguyen
 * @version 10.0
 */
public class BatchEvaluateGUI extends AbstractEvaluateGUI {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	protected RegisterTable algRegTable = (RegisterTable) PluginStorage.getRecommenderReg().clone();

	protected DatasetPool pool = null;
	
	protected DatasetPoolTable tblDatasetPool = null;
	
	protected JButton btnConfigAlgs = null;
	protected AlgListBox lbAlgs = null;
	protected JButton btnRefresh = null;
	protected JButton btnClear = null;
	
	protected JButton btnAddDataset = null;
	protected JButton btnLoadBatchScript = null;
	
	protected JButton btnRun = null;
	protected JButton btnPauseResume = null;
	protected JButton btnStop = null;
	protected JButton btnForceStop = null;

	protected JPanel paneRunInfo = null;
	protected TxtOutput txtRunInfo = null;

	protected JPanel paneRunSave = null;
	protected JTextField txtSaveBrowse = null;
	protected JCheckBox chkSave = null;
	protected JProgressBar prgRunning = null;

	protected JCheckBox chkDisplay = null;
	protected JButton btnMetricsOption = null;

	protected JPanel paneResult = null;
	protected MetricsTable tblMetrics = null;
	protected JButton btnAnalyzeResult = null;
	protected JButton btnCopyResult = null;

	protected StatusBar statusBar = null;
	
	
	/**
	 * 
	 * @param config
	 */
	public BatchEvaluateGUI(EvaluatorConfig config) {
		this(config, false);
	}
	
	
	/**
	 * 
	 * @param config
	 * @param estimateMode
	 */
	public BatchEvaluateGUI(EvaluatorConfig config, boolean estimateMode) {
		super(config, estimateMode);
		// TODO Auto-generated constructor stub
		pool = new DatasetPool();
		
		setLayout(new BorderLayout(2, 2));
		
		JPanel header = createHeader();
		add(header, BorderLayout.NORTH);
		
		JPanel body = createBody();
		add(body, BorderLayout.CENTER);
		
		JPanel footer = createFooter();
		add(footer, BorderLayout.SOUTH);
		
		setDisplay(false);
	}

	
	/**
	 * 
	 * @return this
	 */
	private BatchEvaluateGUI getThis() {
		return this;	
	}
	
	
	@Override
	public void pluginChanged() {
		algRegTable.copyNewOnes(PluginStorage.getRecommenderReg());
		this.lbAlgs.update(algRegTable.getAlgList());
		updateMode();
	}

	
	/**
	 * 
	 * @return {@link JPanel}
	 */
	private JPanel createHeader() {

		JPanel header = new JPanel(new BorderLayout(2, 2));
		
		JPanel up = new JPanel();
		up.setLayout(new GridLayout(1, 0));
		header.add(up, BorderLayout.NORTH);

		JPanel paneAlg = new JPanel();
		up.add(paneAlg);
		
		paneAlg.add(new JLabel(getMessage("algorithm") + ":"));
		
		this.lbAlgs = new AlgListBox(false);
		this.lbAlgs.update(algRegTable.getAlgList());
		this.lbAlgs.setVisibleRowCount(3);
		paneAlg.add(new JScrollPane(this.lbAlgs));
		Dimension preferredSize = this.lbAlgs.getPreferredSize();
		preferredSize.width = Math.max(preferredSize.width, 200);
		this.lbAlgs.setPreferredSize(preferredSize);
		
		JPanel paneAlgTool = new JPanel();
		paneAlgTool.setLayout(new BoxLayout(paneAlgTool, BoxLayout.Y_AXIS));
		paneAlg.add(paneAlgTool);
		
		this.btnConfigAlgs = UIUtil.makeIconButton(
			"config-16x16.png", 
			"config", 
			getMessage("config"), 
			getMessage("config"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					List<Alg> list = lbAlgs.getAlgList();
					if (list.size() == 0) {
						JOptionPane.showMessageDialog(
								getThis(), 
								"List empty", 
								"List empty", 
								JOptionPane.ERROR_MESSAGE);
						
						return;
						
					}
					
					AlgListChooser dlg = new AlgListChooser(getThis(), algRegTable.getAlgList(), lbAlgs.getAlgList());
					if (!dlg.isOK())
						return;
					
					lbAlgs.update(dlg.getResult());
					updateMode();
					
					validate();
					updateUI();
				}
			});
		this.btnConfigAlgs.setMargin(new Insets(0, 0 , 0, 0));
		paneAlgTool.add(this.btnConfigAlgs);
			

		JPanel down = new JPanel(new BorderLayout(2, 2));
		header.add(down, BorderLayout.CENTER);
		
		this.tblDatasetPool = new DatasetPoolTable() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean removeSelectedRows() {
				// TODO Auto-generated method stub
				boolean ret = super.removeSelectedRows();
				
				if (ret) {
					clearResult();
					updateMode();
					
				}
				
				return ret;
			}
			
			
			@Override
			public void save() {
				UriAdapter adapter = new UriAdapter(); 
		        xURI uri = adapter.chooseUri(
						this, 
						false, 
						new String[] {"properties", "script", "hudup"}, 
						new String[] {"Properties URI (s)", "Script files", "Hudup URI (s)"},
						null);
		        adapter.close();
		        
		        if (uri == null) {
					JOptionPane.showMessageDialog(
							this, 
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
		        			this, 
		        			"URI exist. Do you want to override it?", 
		        			"URI exist", 
		        			JOptionPane.YES_NO_OPTION, 
		        			JOptionPane.QUESTION_MESSAGE);
		        	if (ret == JOptionPane.NO_OPTION)
		        		return;
		        }
				
				adapter = null;
        		Writer writer = null;
		        try {
					adapter = new UriAdapter(uri);
	        		writer = adapter.getWriter(uri, false);
	        		
					BatchScript script = BatchScript.assign(
							pool, lbAlgs.getAlgNameList());
					
					script.save(writer);
	        		writer.flush();
	        		writer.close();
	        		writer = null;
			        
		        	JOptionPane.showMessageDialog(this, 
		        			"URI saved successfully", "URI saved successfully", JOptionPane.INFORMATION_MESSAGE);
		        }
				catch(Exception e) {
					e.printStackTrace();
				}
		        finally {
		        	try {
		        		if (writer != null)
		        			writer.close();
		        	}
		        	catch (Exception e) {
		        		e.printStackTrace();
		        	}
		        	
		        	if (adapter != null)
		        		adapter.close();
		        }
		        
				
			}
			
		};
		this.tblDatasetPool.setPreferredScrollableViewportSize(new Dimension(200, 80));
		down.add(new JScrollPane(this.tblDatasetPool), BorderLayout.CENTER);

		JPanel tool = new JPanel(new BorderLayout());
		down.add(tool, BorderLayout.SOUTH);
		
		JPanel toolGrp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tool.add(toolGrp1, BorderLayout.WEST);
		
		this.btnAddDataset = new JButton(getMessage("add"));
		this.btnAddDataset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				addDataset();
			}
		});
		toolGrp1.add(this.btnAddDataset);
		
		this.btnLoadBatchScript = new JButton(getMessage("load_script"));
		this.btnLoadBatchScript.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				loadBatchScript();
			}
		});
		toolGrp1.add(this.btnLoadBatchScript);

		JPanel toolGrp2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		tool.add(toolGrp2, BorderLayout.EAST);
		
		this.btnRefresh = UIUtil.makeIconButton(
			"refresh-16x16.png", 
			"refresh", 
			getMessage("refresh"), 
			getMessage("refresh"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					refresh();
				}
			});
		this.btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		toolGrp2.add(this.btnRefresh);

		this.btnClear = UIUtil.makeIconButton(
			"clear-16x16.png", 
			"clear", 
			getMessage("clear"), 
			getMessage("clear"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					clear();
				}
			});
		this.btnClear.setMargin(new Insets(0, 0 , 0, 0));
		toolGrp2.add(this.btnClear);

		this.btnForceStop = UIUtil.makeIconButton(
			"forcestop-16x16.png", 
			getMessage("force_stop"), 
			getMessage("force_stop_tooltip"), 
			getMessage("force_stop"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					forceStop();
				}
			});
		this.btnForceStop.setMargin(new Insets(0, 0 , 0, 0));
		toolGrp2.add(this.btnForceStop);

		return header;
	}

	
	/**
	 * 
	 * @return {@link JPanel}
	 */
	private JPanel createBody() {
		JPanel body = new JPanel(new BorderLayout(2, 2));
		
		JPanel paneControl = new JPanel();
		body.add(paneControl, BorderLayout.NORTH);
		
		this.btnRun = new JButton(getMessage("run"));
		this.btnRun.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				run();
			}
		});
		paneControl.add(this.btnRun);
		
		this.btnPauseResume = new JButton(getMessage("pause"));
		this.btnPauseResume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				pauseResume();
			}
		});
		paneControl.add(this.btnPauseResume);

		this.btnStop = new JButton(getMessage("stop"));
		this.btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				stop();
			}
		});
		paneControl.add(this.btnStop);

		JPanel main = new JPanel(new GridLayout(0, 1));
		body.add(main, BorderLayout.CENTER);

		this.paneRunInfo = new JPanel(new BorderLayout());
		main.add(this.paneRunInfo);
		this.txtRunInfo = new TxtOutput();
		this.txtRunInfo.setRows(10);
		this.txtRunInfo.setColumns(10);
		this.txtRunInfo.setEditable(false);
		this.paneRunInfo.add(new JScrollPane(this.txtRunInfo), BorderLayout.CENTER);
		
		this.paneRunSave = new JPanel(new BorderLayout(2, 2));
		main.add(this.paneRunSave);
		JPanel pane = new JPanel(new BorderLayout(2, 2));
		this.paneRunSave.add(pane, BorderLayout.NORTH);
		this.txtSaveBrowse = new JTextField();
		this.txtSaveBrowse.setEditable(false);
		pane.add(this.txtSaveBrowse, BorderLayout.CENTER);
		this.chkSave = new JCheckBox(getMessage("save"));
		this.chkSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(chkSave.isSelected()) {
					UriAdapter adapter = new UriAdapter();
					xURI store = adapter.chooseStore(getThis());
					adapter.close();
					if (store == null) {
						JOptionPane.showMessageDialog(
							getThis(), 
							"Not open store", 
							"Not open store", 
							JOptionPane.WARNING_MESSAGE);
					}
					else
						txtSaveBrowse.setText(store.toString());
				}
				else {
					txtSaveBrowse.setText("");
				}
				updateMode();
			}
		});
		pane.add(this.chkSave, BorderLayout.WEST);

		JPanel tool = new JPanel(new BorderLayout());
		body.add(tool, BorderLayout.SOUTH);
		JPanel buttons = new JPanel();
		tool.add(buttons, BorderLayout.EAST);

		this.chkDisplay = new JCheckBox(new AbstractAction(getMessage("display")) {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				boolean display = chkDisplay.isSelected();
				setDisplay(display);
			}
		});
		buttons.add(this.chkDisplay);
		
		this.btnMetricsOption = UIUtil.makeIconButton(
			"option-16x16.png", 
			"metrics_option", 
			getMessage("metrics_option"), 
			getMessage("metrics_option"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					metricsOption();
				}
			});
		this.btnMetricsOption.setMargin(new Insets(0, 0 , 0, 0));
		buttons.add(this.btnMetricsOption);
				
		this.prgRunning = new JProgressBar();
		this.prgRunning.setStringPainted(true);
		this.prgRunning.setToolTipText(getMessage("evaluation_progress"));
		this.prgRunning.setMaximum(0);
		this.prgRunning.setValue(0);
		this.prgRunning.setVisible(false);
		tool.add(this.prgRunning, BorderLayout.CENTER);

		return body;
	}

	
	/**
	 * 
	 * @return {@link JPanel}
	 */
	private JPanel createFooter() {
		JPanel footer = new JPanel(new BorderLayout());
		
		this.paneResult = new JPanel(new BorderLayout());
		footer.add(this.paneResult, BorderLayout.CENTER);
		
		this.tblMetrics = new MetricsTable(new RegisterTable(lbAlgs.getAlgList()));
		this.tblMetrics.setPreferredScrollableViewportSize(new Dimension(600, 100));
		this.paneResult.add(new JScrollPane(this.tblMetrics), BorderLayout.CENTER);

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.paneResult.add(toolbar, BorderLayout.SOUTH);
		
		this.btnAnalyzeResult = new JButton(getMessage("analyze_result"));
		this.btnAnalyzeResult.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (result != null)
					new MetricsAnalyzeDlg(getThis(), result, new RegisterTable(lbAlgs.getAlgList()));
				else {
					JOptionPane.showMessageDialog(
							getThis(), 
							"No result", 
							"No result", 
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		toolbar.add(this.btnAnalyzeResult);

		
		this.btnCopyResult = UIUtil.makeIconButton(
				"copy-16x16.png", 
				"copy_result_to_clipboard", 
				getMessage("copy_result_to_clipboard"), 
				getMessage("copy_result_to_clipboard"), 
					
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						
						if (result != null) {
							ClipboardUtil.util.setText(result.translate());
						}
					}
			});
		this.btnCopyResult.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(this.btnCopyResult);

		this.statusBar = new StatusBar();
		this.counterClock.setTimeLabel(this.statusBar.getLastPane());
		footer.add(statusBar, BorderLayout.SOUTH);

		return footer;
		
		
	}

	
	
	/**
	 * 
	 * @param display
	 */
	private void setDisplay(boolean display) {
		Container container = this.paneRunSave.getParent();
		if (container == null)
			container = this.paneRunInfo.getParent();
		
		container.remove(this.paneRunInfo);
		container.remove(this.paneRunSave);
		if (display)
			container.add(this.paneRunInfo);
		else
			container.add(this.paneRunSave);
			
		this.paneRunInfo.setVisible(display);
		this.paneRunSave.setVisible(!display);
		
		this.chkDisplay.setSelected(display);
	
		updateMode();
	}
	
	
	
	@Override
	protected List<Alg> getCurrentAlgList() {
		// TODO Auto-generated method stub
		return lbAlgs.getAlgList();
	}


	@Override
	protected void refresh() {
		if (evaluator.isStarted())
			return;
		
		this.pool.reload();

		tblDatasetPool.update(pool);
		clearResult();
		updateMode();
		
		validate();
		updateUI();
	}
	
	
	@Override
	protected void clear() {
		if (evaluator.isStarted())
			return;
		
		this.pool.clear();
		this.tblDatasetPool.update(this.pool);
		this.lbAlgs.update(algRegTable.getAlgList());
		
		clearResult();
		
		updateMode();
		validate();
		updateUI();
	}
	

	
	@Override
	protected void run() {
		if (evaluator.isStarted())
			return;
		
		if (pool.size() == 0 || lbAlgs.getAlgList().size() == 0) {
			JOptionPane.showMessageDialog(
				getThis(), 
				"Not load data set pool", 
				"Not load data set pool", 
				JOptionPane.WARNING_MESSAGE);
			
			return;
		}
		
		clearResult();
		evaluator.evaluate(lbAlgs.getAlgList(), pool, null);
		
		counterClock.start();
		updateMode();
	}

	
	@Override
	public void receivedEvaluation(EvaluatorEvent evt) throws HudupException {
		
		if (chkDisplay.isSelected()) {
			String info = evt.translate() + "\n\n\n\n";
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkSave.isSelected()){
			String storePath = this.txtSaveBrowse.getText().trim();
			if (storePath.length() == 0)
				return;
			
			try {
				xURI store = xURI.create(storePath);
				UriAdapter adapter = new UriAdapter(store);
				boolean existed = adapter.exists(store);
				if (!existed)
					adapter.create(store, true);
				adapter.close();
				
				List<Alg> algs = lbAlgs.getAlgList();
				for (Alg alg : algs) {
					if (evt.getType() == Type.done) {
						String key = alg.getName() + ".eval";
						ByteChannel channel = getIOChannel(store, key, true);
						
						String info = evt.translate(alg.getName(), -1) + "\n\n\n\n";
						ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
						channel.write(buffer);
					}
					else {
						Map<Integer, Metrics> map = evt.getMetrics().gets(alg.getName());
						Set<Integer> datasetIdList = map.keySet();
						for (int datasetId : datasetIdList) {
							String key = alg.getName() + "@" + datasetId + ".eval";
							ByteChannel channel = getIOChannel(store, key, true);

							String info = evt.translate(alg.getName(), datasetId) + "\n\n\n\n";
							ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
							channel.write(buffer);
							
							if (evt.getType() == Type.done_one)
								closeIOChannel(key);
						}
					}
				}
				
			    // Exporting excel file
				if (evt.getType() == Type.done || evt.getType() == Type.done_one) {
					MetricsUtil util = new MetricsUtil(this.result, new RegisterTable(lbAlgs.getAlgList()));
					util.createExcel(store.concat("analyze.xls"));
					
					if (evt.getType() == Type.done)
						closeIOChannels();
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		this.result = evt.getEvaluator().getResult();
		if (evt.getType() == Type.done) {
			counterClock.stop();
			updateMode();
		}
		
	}

	
	
	@Override
	public void receiveProgress(ProgressEvent evt) {
		// TODO Auto-generated method stub
		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		String algName = evt.getAlgName();
		int datasetId = evt.getDatasetId();
		int vCurrentCount = evt.getCurrentCount();
		int vCurrentTotal = evt.getCurrentTotal();
		
		if (this.prgRunning.getMaximum() < progressTotal)
			this.prgRunning.setMaximum(progressTotal);
		if (this.prgRunning.getValue() < progressStep) 
			this.prgRunning.setValue(progressStep);
		
		statusBar.setTextPane1(
				"Algorithm '" + algName + "' " +
				"dataset '" + datasetId + "': " + 
				vCurrentCount + "/" + vCurrentTotal);
		
		statusBar.setTextPane2("Total: " + progressStep + "/" + progressTotal);
	}

	
	@Override
	protected void updateMode() {
		closeIOChannels();
		
		if (lbAlgs.getAlgList().size() == 0) {
			setInternalEnable(false);
			setResultVisible(false);
			
			btnConfigAlgs.setEnabled(true);
			
			prgRunning.setMaximum(0);
			prgRunning.setValue(0);
			prgRunning.setVisible(false);
		}
		else if (pool.size() == 0) {
			setInternalEnable(false);
			setResultVisible(false);
			
			lbAlgs.setEnabled(true);
			btnConfigAlgs.setEnabled(true);
			btnAddDataset.setEnabled(true);
			btnLoadBatchScript.setEnabled(true);
			
			prgRunning.setMaximum(0);
			prgRunning.setValue(0);
			prgRunning.setVisible(false);
		}
		else if (evaluator.isStarted()) {
			if (evaluator.isRunning()) {
				setInternalEnable(false);
				setResultVisible(false);
				
				result = null;
				tblMetrics.clear();
				txtRunInfo.setText("");
				btnPauseResume.setText("Pause");
				btnPauseResume.setEnabled(true);
				btnStop.setEnabled(true);
				btnForceStop.setEnabled(true);
				
				prgRunning.setVisible(true);
			}
			else {
				setInternalEnable(false);
				setResultVisible(true);
				
				btnPauseResume.setText("Resume");
				btnPauseResume.setEnabled(true);
				btnStop.setEnabled(true);
				btnForceStop.setEnabled(true);
				txtRunInfo.setEnabled(true);
				chkSave.setEnabled(true);
				chkDisplay.setEnabled(true);
				
				tblMetrics.update(result);
			}
		}
		else {
			setInternalEnable(true);
			setResultVisible(true);
			
			btnPauseResume.setText("Pause");
			btnPauseResume.setEnabled(false);
			btnStop.setEnabled(false);
			btnForceStop.setEnabled(false);
			
			tblMetrics.update(result);
			prgRunning.setMaximum(0);
			prgRunning.setValue(0);
			prgRunning.setVisible(false);
		}
		
		if (chkSave.isSelected() && (txtSaveBrowse.getText() == null || txtSaveBrowse.getText().isEmpty()))
			chkSave.setSelected(false);

		if (result == null)
			statusBar.clearText();
		
		this.statusBar.setTextPane0( 
				(chkDisplay.isSelected() ? getMessage("display") : getMessage("undisplay")) + "-" + 
				(evaluator instanceof EstimateEvaluator ? getMessage("estimate") : getMessage("recommend")));
	}
	
	
	/**
	 * 
	 * @param flag
	 */
	private void setInternalEnable(boolean flag) {
		this.btnConfigAlgs.setEnabled(flag);
		this.lbAlgs.setEnabled(flag);
		this.tblDatasetPool.setEnabled2(flag && pool.size() > 0);
		this.btnAddDataset.setEnabled(flag);
		this.btnLoadBatchScript.setEnabled(flag);
		
		this.btnRefresh.setEnabled(flag && pool.size() > 0);
		
		this.btnClear.setEnabled(flag && pool.size() > 0);

		this.btnRun.setEnabled(flag && pool.size() > 0);

		this.btnPauseResume.setEnabled(flag && pool.size() > 0);

		this.btnStop.setEnabled(flag && pool.size() > 0);

		this.btnForceStop.setEnabled(flag && pool.size() > 0);

		this.txtRunInfo.setEnabled(flag && pool.size() > 0);
		
		this.chkSave.setEnabled(flag && pool.size() > 0);

		this.chkSave.setEnabled(flag && pool.size() > 0);

		this.txtSaveBrowse.setEnabled(flag && pool.size() > 0);

		this.chkDisplay.setEnabled(flag);
		
		this.btnMetricsOption.setEnabled(flag);
		
		this.btnAnalyzeResult.setEnabled(
			flag && pool.size() > 0 && result != null && result.size() > 0);
		
		this.btnCopyResult.setEnabled(
				flag && pool.size() > 0 && result != null && result.size() > 0);
	}

	
	/**
	 * 
	 * @param flag
	 */
	private void setResultVisible(boolean flag) {
		
		boolean visible = flag && pool.size() > 0 && result != null && result.size() > 0;
 
		paneResult.setVisible(visible);
		btnAnalyzeResult.setEnabled(visible);
		btnCopyResult.setEnabled(visible);
	}

	
	
	
	/**
	 * 
	 */
	protected void loadBatchScript() {
		if (evaluator.isStarted() || this.lbAlgs.getAlgList().size() == 0)
			return;
		
		UriAdapter adapter = new UriAdapter();
		xURI uri = adapter.chooseUri(
				this, 
				true, 
				new String[] {"properties", "script", "hudup"}, 
				new String[] {"Properties files", "Script files", "Hudup files"},
				null);
		adapter.close();
		
		if (uri == null) {
			JOptionPane.showMessageDialog(
					this, 
					"URI not open", 
					"URI not open", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			adapter = new UriAdapter(uri);
			Reader reader = adapter.getReader(uri);
			BatchScript script = BatchScript.parse(reader);
			reader.close();
			adapter.close();
			
			if (script == null) {
				JOptionPane.showMessageDialog(
						this, 
						"Batch not created", 
						"Batch not created", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			this.lbAlgs.update(algRegTable.getAlgList(script.getAlgNameList()));
			
			this.pool.clear();
			DatasetPool scriptPool = script.getPool();
			Alg[] algList = this.lbAlgs.getAlgList().toArray(new Alg[] {} );
			for (int i = 0; i < scriptPool.size(); i++) {
				DatasetPair pair = scriptPool.get(i);
				if (pair == null || pair.getTraining() == null)
					continue;
				
				Dataset trainingSet = pair.getTraining();
				if (DatasetUtil2.validateTrainingset(this, trainingSet, algList))
					this.pool.add(pair);
			}
			this.tblDatasetPool.update(this.pool);
			clearResult();
			updateMode();
			
			validate();
			updateUI();
				
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	
	/**
	 * 
	 */
	protected void addDataset() {
		if (evaluator.isStarted() || this.lbAlgs.getAlgList().size() == 0)
			return;
		
		new AddingDatasetDlg(this, pool, this.lbAlgs.getAlgList());
		this.tblDatasetPool.update(this.pool);
		
		clearResult();
		updateMode();
	}


	/**
	 * 
	 */
	private void clearResult() {
		try {
			this.txtRunInfo.setText("");
			this.result = null;
			this.tblMetrics.clear();
			this.counterClock.stopAndClearText();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
}
