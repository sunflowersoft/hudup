package net.hudup.data.ui.toolkit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;
import net.hudup.data.DatasetSplitting;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetSplitter extends JPanel implements ProgressListener, Dispose {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	protected JButton btnDatasetBrowse = null;

	protected JTextField txtDatasetBrowse = null;
	
	protected JButton btnSaveTo = null;

	protected JTextField txtSaveTo = null;

	protected JTextField txtTestRatio = null;
	
	protected JButton btnSplit = null;
	
	protected JProgressBar prgRunning = null;
	
	protected volatile Thread runningThread = null;

	
	/**
	 * 
	 */
	public DatasetSplitter() {
		setLayout(new BorderLayout(5, 5));
		
		JPanel header = new JPanel(new BorderLayout(5, 5));
		add(header, BorderLayout.NORTH);
		
		JPanel header_up = new JPanel(new BorderLayout());
		header.add(header_up, BorderLayout.NORTH);

		JPanel header_up_left = new JPanel(new GridLayout(0, 1));
		header_up.add(header_up_left, BorderLayout.WEST);
		
		btnDatasetBrowse = new JButton("Browse dataset");
		btnDatasetBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				UriAdapter adapter = new UriAdapter(); 
				xURI uri = adapter.chooseUri(getThis(), true, null, null, null);
				adapter.close();
				
				if (uri == null) {
					JOptionPane.showMessageDialog(
						getThis(), 
						"Not open URI", 
						"Not open URI", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				adapter = new UriAdapter(uri); 
				txtDatasetBrowse.setText(uri.toString());
				if (txtSaveTo.getText().isEmpty() && adapter.getStoreOf(uri) != null) {
					txtSaveTo.setText(adapter.getStoreOf(uri).toString());
					enableControls(true);
				}
				adapter.close();
			}
		});
		header_up_left.add(btnDatasetBrowse);
		
		btnSaveTo = new JButton("Save to");
		btnSaveTo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				UriAdapter adapter = new UriAdapter(); 
				xURI store = adapter.chooseStore(getThis());
				adapter.close();
				
				if (store == null) {
					JOptionPane.showMessageDialog(
						getThis(), 
						"Not open store", 
						"Not open store", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtSaveTo.setText(store.toString());
				enableControls(true);
			}
		});
		header_up_left.add(btnSaveTo);

		header_up_left.add(new JLabel("  Test ratio"));

		JPanel header_up_center = new JPanel(new GridLayout(0, 1));
		header_up.add(header_up_center, BorderLayout.CENTER);
		
		txtDatasetBrowse = new JTextField();
		txtDatasetBrowse.setEditable(false);
		header_up_center.add(txtDatasetBrowse);
		
		txtSaveTo = new JTextField();
		txtSaveTo.setEditable(false);
		header_up_center.add(txtSaveTo);
		
		txtTestRatio = new JTextField("0.2");
		txtTestRatio.setEditable(true);
		header_up_center.add(txtTestRatio);

		JPanel header_down = new JPanel();
		header.add(header_down, BorderLayout.SOUTH);
		
		btnSplit = new JButton("Split");
		btnSplit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				splitDataset();
			}
		});
		header_down.add(btnSplit);
		
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
	 * @return this
	 */
	private DatasetSplitter getThis() {
		return this;
	}
	
	
	/**
	 * 
	 * @return whether parameters are valid
	 */
	private boolean validateParameters() {
		String uriText = txtDatasetBrowse.getText();
		xURI uri = xURI.create(uriText);
		if (uri == null)
			return false;
		
		UriAdapter adapter = new UriAdapter(uri);
		boolean existed = adapter.exists(uri);
		adapter.close();
		if (!existed)
			return false;
		
		String sTestRatio = txtTestRatio.getText();
		if (sTestRatio.isEmpty())
			return false;
		
		try {
			Float.parseFloat(sTestRatio);
		}
		catch (Exception ex) {
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 
	 */
	private void splitDataset() {
		String uriText = txtDatasetBrowse.getText();
		final xURI uri = xURI.create(uriText);
		if (uri == null) {
			JOptionPane.showMessageDialog(null, 
					"Dataset URI not exist", "Dataset URI not exist", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		 
		UriAdapter adapter = new UriAdapter(); 
		boolean existed = adapter.exists(uri);
		adapter.close();
		if (!existed) {
			JOptionPane.showMessageDialog(null, 
					"Dataset URI not exist", "Dataset URI not exist", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String saveTo = txtSaveTo.getText();
		final xURI store;
		if (saveTo.isEmpty()) {
			adapter = new UriAdapter(uri);
			store = adapter.getStoreOf(uri);
			adapter.close();
		}
		else {
			xURI temp = xURI.create(saveTo);
			adapter = new UriAdapter(temp);
			if (adapter.isStore(temp))
				store = temp;
			else
				store = adapter.getStoreOf(temp);
			adapter.close();
		}
		
		adapter = new UriAdapter(store);
		if (!adapter.exists(store))
			adapter.create(store, true);
		adapter.close();
		
		String sTestRatio = txtTestRatio.getText();
		if (sTestRatio.isEmpty()) {
			JOptionPane.showMessageDialog(null, 
					"Test ratio empty", "Test ratio empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		float ratio = 0;
		try {
			ratio = Float.parseFloat(sTestRatio);
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(null, 
					"Invalid test ratio format", "Invalid test ratio format", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		if (ratio <= 0 || 0.5 <= ratio) {
			JOptionPane.showMessageDialog(null, 
					"Invalid test ratio", "Invalid test ratio", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		
		final float testRatio = ratio;
		enableControls(false);
		prgRunning.setValue(0);
		prgRunning.setVisible(true);
		
		runningThread = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					DatasetSplitting.split(uri, store, testRatio, getThis());
					
					JOptionPane.showMessageDialog(
							getThis(), 
							"Split dataset successfully", 
							"Split dataset successfully", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				catch (Throwable e) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Split failed", 
							"Split failed", 
							JOptionPane.ERROR_MESSAGE);

					e.printStackTrace();
				}
				
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
		boolean flag2 = validateParameters();
		
		btnDatasetBrowse.setEnabled(flag);
		txtDatasetBrowse.setEnabled(flag);
		btnSaveTo.setEnabled(flag);
		txtSaveTo.setEnabled(flag);
		txtTestRatio.setEnabled(flag);
		btnSplit.setEnabled(flag && flag2);
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


