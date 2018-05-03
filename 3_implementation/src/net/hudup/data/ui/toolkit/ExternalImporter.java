package net.hudup.data.ui.toolkit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;
import net.hudup.data.DefaultExternalQuery;
import net.hudup.data.ui.DataConfigTextField;
import net.hudup.data.ui.ExternalConfigurator;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalImporter extends JPanel implements ProgressListener, Dispose {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	protected JButton btnInternalConfig = null;
	
	protected DataConfigTextField txtInternalConfig = null;

	protected JButton btnExternalConfig = null;
	
	protected DataConfigTextField txtExternalConfig = null;

	protected JButton btnImport = null;
	
	protected JProgressBar prgRunning = null;

	protected volatile Thread runningThread = null;

	
	/**
	 * 
	 */
	public ExternalImporter() {
		super();
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel header_up = new JPanel(new BorderLayout());
		header.add(header_up, BorderLayout.NORTH);
		
		JPanel header_up_left = new JPanel(new GridLayout(0, 1));
		header_up.add(header_up_left, BorderLayout.WEST);
		
		btnExternalConfig = new JButton("External configure");
		btnExternalConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				externalConfig();
			}
		});
		header_up_left.add(btnExternalConfig);

		btnInternalConfig = new JButton("Internal configure");
		btnInternalConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				internalConfig();
			}
		});
		header_up_left.add(btnInternalConfig);
		
		JPanel header_up_center = new JPanel(new GridLayout(0, 1));
		header_up.add(header_up_center, BorderLayout.CENTER);
		
		txtExternalConfig = new DataConfigTextField();
		txtExternalConfig.setEditable(false);
		header_up_center.add(txtExternalConfig);

		txtInternalConfig = new DataConfigTextField();
		txtInternalConfig.setEditable(false);
		header_up_center.add(txtInternalConfig);
		
		JPanel header_down = new JPanel();
		header.add(header_down, BorderLayout.SOUTH);
		
		btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				importData();
			}
			
		});
		header_down.add(btnImport);

		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		prgRunning = new JProgressBar();
		prgRunning.setStringPainted(true);
		prgRunning.setToolTipText("Importing progress");
		prgRunning.setVisible(false);
		prgRunning.setValue(0);
		footer.add(prgRunning, BorderLayout.SOUTH);
		
		enableControls(true);
	}
	
	
	/**
	 * 
	 * @return {@link ExternalImporter}
	 */
	private ExternalImporter getThis() {
		return this;
	}
	
	
	/**
	 * 
	 */
	protected void internalConfig() {
		
		DataConfig defaultConfig = txtInternalConfig.getConfig(); 
		
		DataConfig config = net.hudup.data.DatasetUtil2.chooseConfig(this, defaultConfig);
		if (config == null || config.size() == 0) {
			JOptionPane.showMessageDialog(
				this, 
				"Not query internal configuration", 
				"Not query internal configuration", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		txtInternalConfig.setConfig(config);
		enableControls(true);
	}
	
	
	/**
	 * 
	 */
	protected void externalConfig() {
		
		ExternalConfig defaultConfig = (ExternalConfig) txtExternalConfig.getConfig(); 
		ExternalConfigurator configurator = new ExternalConfigurator(
				this, DataDriverList.list(), defaultConfig);
		
		ExternalConfig config = configurator.getResult();
		if (config == null || config.size() == 0) {
			JOptionPane.showMessageDialog(
				this, 
				"Not query external configuration", 
				"Not query external configuration", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		txtExternalConfig.setConfig(config);
		enableControls(true);
	}
	
	
	/**
	 * 
	 */
	protected void importData() {
		final DataConfig internalConfig = txtInternalConfig.getConfig();
		final ExternalConfig externalConfig = (ExternalConfig) txtExternalConfig.getConfig();
		
		if (internalConfig == null || externalConfig == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Configuration empty", 
				"Configuration empty", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		enableControls(false);
		prgRunning.setValue(0);
		prgRunning.setVisible(true);
		
		runningThread = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ExternalQuery externalQuery = new DefaultExternalQuery();
				boolean setup = externalQuery.setup(internalConfig, externalConfig);
				
				if (!setup) {
					try {
						externalQuery.close();
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
				}
				else {
					externalQuery.importData(getThis());
					try {
						externalQuery.close();
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
				}
				
				JOptionPane.showMessageDialog(
						getThis(), 
						"Import successfully", 
						"Import successfully", 
						JOptionPane.INFORMATION_MESSAGE);
				
				enableControls(true);
				prgRunning.setVisible(false);
				
				runningThread = null;
			}
			
		};
		
		runningThread.start();
	}
	
	
	/**
	 * 
	 * @param flag
	 */
	private void enableControls(boolean flag) {
		DataConfig internalConfig = txtInternalConfig.getConfig();
		DataConfig externalConfig = txtExternalConfig.getConfig();
		boolean flag2 = internalConfig != null && externalConfig != null;
		
		btnInternalConfig.setEnabled(flag);
		txtInternalConfig.setEnabled(flag);
		btnExternalConfig.setEnabled(flag);
		txtExternalConfig.setEnabled(flag);
		btnImport.setEnabled(flag && flag2);
		prgRunning.setEnabled(flag && flag2);
		
	}


	@Override
	public void receiveProgress(ProgressEvent evt) {
		// TODO Auto-generated method stub
		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		
		this.prgRunning.setMaximum(progressTotal);
		if (this.prgRunning.getValue() < progressStep) 
			this.prgRunning.setValue(progressStep);
		
		System.out.println(evt.getMsg());
	}
	

	@SuppressWarnings("deprecation")
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		if (runningThread == null)
			return;
		
		try {
			runningThread.stop();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		
		return runningThread != null;
	}
	
}



