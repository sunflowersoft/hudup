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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
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
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.CompositeRecommender;
import net.hudup.core.alg.ModelBasedRecommender;
import net.hudup.core.alg.ServiceRecommender;
import net.hudup.core.alg.ui.AlgComboBox;
import net.hudup.core.alg.ui.AlgConfigDlg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.HudupException;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DatasetPool;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.ui.DatasetTextField;
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
public class EvaluateGUI extends AbstractEvaluateGUI {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	protected RegisterTable algRegTable = (RegisterTable) PluginStorage.getRecommenderReg().clone();

	
	/**
	 * 
	 */
	protected AlgComboBox cmbAlgs = null;
	protected JButton btnConfig = null;
	protected JButton btnRefresh = null;
	protected JButton btnClear = null;
	
	protected JButton btnTrainingBrowse = null;
	protected DatasetTextField txtTrainingBrowse = null;
	protected JButton btnTestingBrowse = null;
	protected DatasetTextField txtTestingBrowse = null;
	
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
	public EvaluateGUI(EvaluatorConfig config) {
		this(config, false);
	}
	
	
	/**
	 * 
	 * @param config
	 * @param estimateMode
	 */
	public EvaluateGUI(EvaluatorConfig config, boolean estimateMode) {
		super(config, estimateMode);
		
		setLayout(new BorderLayout(2, 2));
		
		JPanel header = createHeader();
		add(header, BorderLayout.NORTH);
		
		JPanel body = createBody();
		add(body, BorderLayout.CENTER);
		
		JPanel footer = createFooter();
		add(footer, BorderLayout.SOUTH);
		
		algChanged();
		setDisplay(false);
	}
	
	
	/**
	 * 
	 * @return this
	 */
	private EvaluateGUI getThis() {
		return this;	
	}
	
	
	@Override
	public void pluginChanged() {
		algRegTable.copyNewOnes(PluginStorage.getRecommenderReg());
		cmbAlgs.update(algRegTable.getAlgList());
		updateMode();
	}
	
	
	/**
	 * 
	 * @return {@link JPanel}
	 */
	private JPanel createHeader() {
		this.btnTrainingBrowse = new JButton(getMessage("training_set"));

		JPanel header = new JPanel(new BorderLayout(2, 2));
		
		JPanel up = new JPanel();
		up.setLayout(new GridLayout(1, 0));
		header.add(up, BorderLayout.NORTH);

		JPanel paneAlg = new JPanel();
		up.add(paneAlg);
		paneAlg.add(new JLabel(getMessage("algorithm") + ":"));
		this.cmbAlgs = new AlgComboBox(algRegTable.getAlgList());
		this.cmbAlgs.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				
				algChanged();
			}
		});
		paneAlg.add(this.cmbAlgs);
		Dimension preferredSize = this.cmbAlgs.getPreferredSize();
		preferredSize.width = Math.max(preferredSize.width, 200);
		this.cmbAlgs.setPreferredSize(preferredSize);

		this.btnConfig = UIUtil.makeIconButton(
			"config-16x16.png", 
			"config", 
			getMessage("config"), 
			getMessage("config"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new AlgConfigDlg(getThis(), getAlg()).
						setVisible(true);
				}
			});
		this.btnConfig.setMargin(new Insets(0, 0 , 0, 0));
		paneAlg.add(this.btnConfig);
		
		JPanel down = new JPanel(new BorderLayout(2, 2));
		header.add(down, BorderLayout.SOUTH);

		JPanel left = new JPanel();
		left.setLayout(new GridLayout(0, 1));
		down.add(left, BorderLayout.WEST);

		this.btnTrainingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				openTrainingSet();
			}
		});
		left.add(this.btnTrainingBrowse);
		
		this.btnTestingBrowse = new JButton(getMessage("testing_set"));
		this.btnTestingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				openTestingSet();
			}
		});
		left.add(this.btnTestingBrowse);
		
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(0, 1));
		down.add(center, BorderLayout.CENTER);
		
		this.txtTrainingBrowse = new DatasetTextField();
		this.txtTrainingBrowse.setEditable(false);
		center.add(this.txtTrainingBrowse);
		
		this.txtTestingBrowse = new DatasetTextField();
		this.txtTestingBrowse.setEditable(false);
		center.add(this.txtTestingBrowse);

		
		JPanel tool = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		down.add(tool, BorderLayout.SOUTH);
		
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
		tool.add(this.btnRefresh);

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
		tool.add(this.btnClear);

		this.btnForceStop = UIUtil.makeIconButton(
			"forcestop-16x16.png", 
			"force_stop", 
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
		tool.add(this.btnForceStop);

		return header;
	}
	
	/**
	 * 
	 */
	private void algChanged() {
		Alg alg = (Alg)cmbAlgs.getSelectedItem();
		if (alg == null)
			return;
		
		if (alg instanceof ModelBasedRecommender) {
			btnTrainingBrowse.setText(getMessage("training_set_kbase"));
			Dataset dataset = txtTrainingBrowse.getDataset();
			if (dataset != null && (dataset instanceof Pointer))
				txtTrainingBrowse.setDataset(null);
		}
		else if (alg instanceof CompositeRecommender) {
			btnTrainingBrowse.setText(getMessage("any_source"));
			Dataset dataset = txtTrainingBrowse.getDataset();
			if (dataset != null && (dataset instanceof Pointer))
				txtTrainingBrowse.setDataset(null);
		}
		else if (alg instanceof ServiceRecommender) {
			btnTrainingBrowse.setText(getMessage("service_pointer"));
			txtTrainingBrowse.setDataset(null);
		}
		else
			btnTrainingBrowse.setText(getMessage("training_set"));
		
		DatasetUtil2.validateTrainingset(getThis(), txtTrainingBrowse.getDataset(), new Alg[] { alg });
		updateMode();
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
		paneRunInfo.add(new JScrollPane(this.txtRunInfo), BorderLayout.CENTER);
		
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
		this.prgRunning.setVisible(false);
		this.prgRunning.setMaximum(0);
		this.prgRunning.setValue(0);
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
		
		this.tblMetrics = new MetricsTable(new RegisterTable(Arrays.asList(getAlg())));
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
					new MetricsAnalyzeDlg(getThis(), result, new RegisterTable(Arrays.asList(getAlg())));
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

	
	/**
	 * 
	 */
	protected void openTrainingSet() {
		if (evaluator.isStarted())
			return;
		
		DataConfig defaultConfig = txtTrainingBrowse.getConfig(); 
		if (defaultConfig == null)
			defaultConfig = DatasetUtil2.createDefaultConfig(getAlg());
		
		DataConfig config = DatasetUtil2.chooseTrainingConfig(this, defaultConfig, getAlg());
		
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Not open training set", 
				"Not open training set", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		openTrainingSet(config);
	}
	
	
	/**
	 * 
	 */
	protected void openTrainingSet(DataConfig config) {
		if (evaluator.isStarted())
			return;
		
		clearResult();
		updateMode();
		
		Dataset dataset = DatasetUtil.loadDataset(config);
		if (dataset == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Training set not parsed", 
				"Training set not parsed", 
				JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		if (!DatasetUtil2.validateTrainingset(this, dataset, new Alg[] { getAlg() })) {
			dataset.clear();
			return;
		}
		
		this.txtTrainingBrowse.setDataset(dataset);
		updateMode();
	}
	
	
	/**
	 * 
	 */
	protected void openTestingSet() {
		if (evaluator.isStarted())
			return;

		DataConfig config = DatasetUtil2.chooseTestingConfig(this, txtTestingBrowse.getConfig());
		
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Not open testing dataset", 
				"Not open testing dataset", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		openTestingSet(config);
	}
	
	
	/**
	 * 
	 */
	protected void openTestingSet(DataConfig config) {
		if (evaluator.isStarted())
			return;
		
		clearResult();
		updateMode();
		
		Dataset dataset = DatasetUtil.loadDataset(config);
		if (dataset == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Testing dataset not parsed", 
				"Testing dataset not parsed", 
				JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		if (dataset instanceof Pointer) {
			JOptionPane.showMessageDialog(
					this, 
					"Testing dataset is pointer", 
					"Invalid testing dataset", 
					JOptionPane.ERROR_MESSAGE);
				
				return;
		}

		this.txtTestingBrowse.setDataset(dataset);
		updateMode();
	}

	
	@Override
	protected void refresh() {
		if (evaluator.isStarted())
			return;
		
		DataConfig trainingCfg = txtTrainingBrowse.getConfig();
		if (trainingCfg != null)
			trainingCfg = (DataConfig) trainingCfg.clone();
		
		DataConfig testingCfg = txtTestingBrowse.getConfig();
		if (testingCfg != null)
			testingCfg = (DataConfig) testingCfg.clone();
		
		if (trainingCfg != null)
			openTrainingSet(trainingCfg);
		
		if (testingCfg != null)
			openTestingSet(testingCfg);
		
	}
	
	
	@Override
	protected void clear() {
		if (evaluator.isStarted())
			return;
		
		this.txtTrainingBrowse.setDataset(null);
		this.txtTestingBrowse.setDataset(null);
		
		clearResult();
		updateMode();
	}
	
	
	/**
	 * 
	 * @return {@link Alg}
	 */
	private Alg getAlg() {
		// TODO Auto-generated method stub
		if (cmbAlgs.getItemCount() == 0)
			return null;
		else
			return (Alg) cmbAlgs.getSelectedItem();
	}

	
	@Override
	protected List<Alg> getCurrentAlgList() {
		// TODO Auto-generated method stub
		List<Alg> algList = Util.newList();
		
		Alg alg = getAlg();
		if (alg != null)
			algList.add(alg);
		
		return algList;
	}


	@Override
	protected void run() {
		if (evaluator.isStarted())
			return;
		
		Alg alg = getAlg();
		Dataset training = txtTrainingBrowse.getDataset();
		Dataset testing = txtTestingBrowse.getDataset();
		if (alg == null || training == null || testing == null)
			return;
		
		if (!DatasetUtil2.validateTrainingset(this, training, new Alg[] { getAlg() }))
			return;
		
		clearResult();
		
		DatasetPool pool = new DatasetPool(training, testing);
			
		List<Alg> algList = Util.newList();
		algList.add(alg);
		evaluator.evaluate(algList, pool, null);
		
		counterClock.start();
		updateMode();
	}

	
	@Override
	public void receivedEvaluation(EvaluatorEvent evt) throws HudupException {
		if (evt.getMetrics() == null)
			return;
		
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
				
				if (evt.getType() == Type.done) {
					String key = getAlg().getName() + ".eval";
					ByteChannel channel = getIOChannel(store, key, true);

					String info = evt.translate(getAlg().getName(), -1) + "\n\n\n\n";
					ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
					channel.write(buffer);

				    // Exporting excel file
					MetricsUtil util = new MetricsUtil(this.result, new RegisterTable(Arrays.asList(getAlg())));
					util.createExcel(store.concat("analyze.xls"));
					
					closeIOChannels();
				}
				else {
					Map<Integer, Metrics> map = evt.getMetrics().gets(getAlg().getName());
					Set<Integer> datasetIdList = map.keySet();
					for (int datasetId : datasetIdList) {
						String key = getAlg().getName() + "@" + datasetId + ".eval";
						ByteChannel channel = getIOChannel(store, key, true);

						String info = evt.translate(getAlg().getName(), datasetId) + "\n\n\n\n";
						ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
						channel.write(buffer);
						
						if (evt.getType() == Type.done_one)
							closeIOChannel(key);
					}
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
		
		Dataset training = txtTrainingBrowse.getDataset();
		Dataset testing = txtTestingBrowse.getDataset();
		
		if (cmbAlgs.getItemCount() == 0) {
			setInternalEnable(false);
			setResultVisible(false);
			
			cmbAlgs.setEnabled(true);
			
			prgRunning.setMaximum(0);
			prgRunning.setValue(0);
			prgRunning.setVisible(false);
		}
		else if (training == null || testing == null) {
			setInternalEnable(false);
			setResultVisible(false);
			
			cmbAlgs.setEnabled(true);
			btnConfig.setEnabled(true);
			btnTrainingBrowse.setEnabled(true);
			btnTestingBrowse.setEnabled(true);
			btnRefresh.setEnabled(training != null || testing != null);
			btnClear.setEnabled(training != null || testing != null);
			
			prgRunning.setMaximum(0);
			prgRunning.setValue(0);
			prgRunning.setVisible(false);
		}
		else if (evaluator.isStarted()) {
			if (evaluator.isRunning()) {
				setInternalEnable(false);
				setResultVisible(false);
				
				btnPauseResume.setText(getMessage("pause"));
				btnPauseResume.setEnabled(true);
				btnStop.setEnabled(true);
				btnForceStop.setEnabled(true);
				
				prgRunning.setVisible(true);
				
			}
			else {
				setInternalEnable(false);
				setResultVisible(true);
				
				btnPauseResume.setText(getMessage("resume"));
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
			
			btnPauseResume.setText(getMessage("pause"));
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
		
		this.cmbAlgs.setEnabled(flag);
		this.btnConfig.setEnabled(flag);
		
		Dataset trainingSet = txtTrainingBrowse.getDataset();
		Dataset testingSet = txtTestingBrowse.getDataset();
		
		this.btnTrainingBrowse.setEnabled(flag);
		this.txtTrainingBrowse.setEnabled(flag);
		
		this.btnTestingBrowse.setEnabled(
			flag && trainingSet != null);
		this.txtTestingBrowse.setEnabled(
			flag && trainingSet != null);
		
		this.btnRefresh.setEnabled(
				flag && (trainingSet != null || testingSet != null) );
		
		this.btnClear.setEnabled(
				flag && (trainingSet != null || testingSet != null));

		this.btnRun.setEnabled(
			flag && 
			trainingSet != null && 
			testingSet != null && 
			DatasetUtil2.validateTrainingset(this, trainingSet, new Alg[] { getAlg() }) );

		this.btnPauseResume.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.btnStop.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.btnForceStop.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.txtRunInfo.setEnabled(
			flag && 
			trainingSet != null && 
			testingSet != null);
		
		this.chkSave.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);
		
		this.txtSaveBrowse.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.chkDisplay.setEnabled(flag);
		this.btnMetricsOption.setEnabled(flag);
		
		this.btnAnalyzeResult.setEnabled(
			flag && trainingSet != null && testingSet != null && result != null && result.size() > 0);
			
		this.btnCopyResult.setEnabled(
			flag && trainingSet != null && testingSet != null && result != null && result.size() > 0);
	}

	
	/**
	 * 
	 * @param flag
	 */
	private void setResultVisible(boolean flag) {
		Dataset trainingSet = txtTrainingBrowse.getDataset();
		Dataset testingSet = txtTestingBrowse.getDataset();

		boolean visible = flag && 
				trainingSet != null && 
				testingSet != null &&
				result != null && result.size() > 0;
 
		paneResult.setVisible(visible);
		btnAnalyzeResult.setEnabled(visible);
		btnCopyResult.setEnabled(visible);

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
