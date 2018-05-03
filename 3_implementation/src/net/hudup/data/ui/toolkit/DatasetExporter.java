package net.hudup.data.ui.toolkit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.hudup.core.PluginStorage;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.Provider;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;
import net.hudup.data.ProviderImpl;
import net.hudup.data.ui.DataConfigTextField;
import net.hudup.data.ui.DatasetConfigurator;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetExporter extends JPanel implements ProgressListener, Dispose {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton btnSource = null;

	private DataConfigTextField txtSource = null;
	
	private JButton btnDestination = null;
	
	private DataConfigTextField txtDestination = null;
	
	private JButton btnExport = null;
	
	protected JProgressBar prgRunning = null;
	
	protected volatile Thread runningThread = null;
	
	
	/**
	 * 
	 */
	public DatasetExporter() {
		super();
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel header_up = new JPanel(new BorderLayout());
		header.add(header_up, BorderLayout.NORTH);

		JPanel header_up_left = new JPanel(new GridLayout(0, 1));
		header_up.add(header_up_left, BorderLayout.WEST);

		btnSource = new JButton("Source");
		btnSource.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				DatasetConfigurator chooser = new DatasetConfigurator(
						btnSource, 
						PluginStorage.getParserReg().getAlgList(),
						DataDriverList.list(),
						null);
				
				DataConfig config = chooser.getResult();
				if (config == null || config.size() == 0) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Configuration empty", 
							"Configuration empty", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				txtSource.setConfig(config);
				enableControls(true);
			}
		});
		header_up_left.add(btnSource);

		
		btnDestination = new JButton("Destination");
		btnDestination.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				DatasetConfigurator chooser = new DatasetConfigurator(
						getThis(),
						PluginStorage.getParserReg().getAlgList(),
						DataDriverList.list(),
						null);
				
				DataConfig config = chooser.getResult();
				if (config == null || config.size() == 0) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Configuration empty", 
							"Configuration empty", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtDestination.setConfig(config);
				enableControls(true);
			}
		});
		header_up_left.add(btnDestination);

		JPanel header_up_center = new JPanel(new GridLayout(0, 1));
		header_up.add(header_up_center, BorderLayout.CENTER);
		
		txtSource = new DataConfigTextField();
		txtSource.setEditable(false);
		header_up_center.add(txtSource);
		
		txtDestination = new DataConfigTextField();
		txtDestination.setEditable(false);
		header_up_center.add(txtDestination);
		

		JPanel header_down = new JPanel();
		header.add(header_down, BorderLayout.SOUTH);
		
		btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				exportData();
			}
		});
		header_down.add(btnExport);
		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		prgRunning = new JProgressBar();
		prgRunning.setStringPainted(true);
		prgRunning.setToolTipText("Exporting progress");
		prgRunning.setVisible(false);
		prgRunning.setValue(0);
		footer.add(prgRunning, BorderLayout.SOUTH);
		
		enableControls(true);
	}

	
	/**
	 * 
	 * @return this {@link DatasetExporter}
	 */
	private DatasetExporter getThis() {
		return this;
	}
	
	
	/**
	 * 
	 * @param flag
	 */
	private void enableControls(boolean flag) {
		DataConfig srcConfig = (DataConfig) txtSource.getConfig();
		DataConfig destConfig = (DataConfig) txtDestination.getConfig();
		boolean flag2 = srcConfig != null && destConfig != null;
		
		btnSource.setEnabled(flag);
		txtSource.setEnabled(flag);
		btnDestination.setEnabled(flag);
		txtDestination.setEnabled(flag);
		btnExport.setEnabled(flag && flag2);
		prgRunning.setEnabled(flag && flag2);
		
	}
	
	
	/**
	 * 
	 */
	private void exportData() {
		final DataConfig srcConfig = (DataConfig) txtSource.getConfig();
		final DataConfig destConfig = (DataConfig) txtDestination.getConfig();
		
		if (srcConfig == null || destConfig == null) {
			JOptionPane.showMessageDialog(
					this, 
					"There is no configuration", 
					"No config", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (srcConfig.getUriId().equals(destConfig.getUriId())) {
			JOptionPane.showMessageDialog(
					this, 
					"Source and destination are the same place", 
					"Source and destination are the same place", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		

		destConfig.fillUnitList(DataConfig.getDefaultUnitList());
		
		
		enableControls(false);
		prgRunning.setValue(0);
		prgRunning.setVisible(true);
		runningThread = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				
				Provider provider = new ProviderImpl(destConfig);
				provider.importData(srcConfig, true, getThis());
				provider.close();
				
				JOptionPane.showMessageDialog(
						getThis(), 
						"Export successfully", 
						"Export successfully", 
						JOptionPane.INFORMATION_MESSAGE);
				
				enableControls(true);
				prgRunning.setVisible(false);
				
				runningThread = null;
			}
			
		};
		
		runningThread.start();
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

