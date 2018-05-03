package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DatasetPair;
import net.hudup.data.DatasetPool;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.ui.DataConfigTextField;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class AddingDatasetDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	protected DatasetPool pool = null;
	
	protected List<Alg> algList = null;
	
	protected JButton btnTrainingBrowse = null;

	protected JButton btnTestingBrowse = null;
	
	protected DataConfigTextField txtTrainingBrowse = null;

	protected DataConfigTextField txtTestingBrowse = null;
	
	
	/**
	 * 
	 * @param comp
	 * @param pool
	 */
	public AddingDatasetDlg(Component comp, DatasetPool pool, List<Alg> algList) {
		super(UIUtil.getFrameForComponent(comp), "Add datasets", true);
		this.pool = pool;
		this.algList = algList;
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(600, 200);
		this.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));

		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		
		btnTrainingBrowse = new JButton("Training set / KBase");
		btnTrainingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				openTrainingSet();
			}
		});
		left.add(btnTrainingBrowse);
		
		btnTestingBrowse = new JButton("Testing set");
		btnTestingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				openTestingSet();
			}
		});
		left.add(btnTestingBrowse);

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		

		txtTrainingBrowse = new DataConfigTextField();
		txtTrainingBrowse.setEditable(false);
		right.add(txtTrainingBrowse);
		
		txtTestingBrowse = new DataConfigTextField();
		txtTestingBrowse.setEditable(false);
		right.add(txtTestingBrowse);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnAdd = new JButton("Add dataset");
		btnAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				addDataset();
			}
		});
		footer.add(btnAdd);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				dispose();
			}
		});
		footer.add(btnClose);
		
		setVisible(true);
	}

	
	/**
	 * 
	 */
	protected void openTrainingSet() {
		
		DataConfig defaultCfg = txtTrainingBrowse.getConfig();
		DataConfig config = net.hudup.data.DatasetUtil2.chooseConfig(this, defaultCfg);
		
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Not open training set", 
				"Not open training set", 
				JOptionPane.ERROR_MESSAGE);
			
			return;
		}
	
		txtTrainingBrowse.setConfig(config);
	}
	
	
	/**
	 * 
	 */
	protected void openTestingSet() {
		
		DataConfig defaultCfg = txtTestingBrowse.getConfig();
		DataConfig config = net.hudup.data.DatasetUtil2.chooseConfig(this, defaultCfg);
			
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Not open testing set", 
				"Not open testing set", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		txtTestingBrowse.setConfig(config);
	}
	
	
	/**
	 * 
	 */
	protected void clear() {
		txtTrainingBrowse.setConfig(null);
		txtTestingBrowse.setConfig(null);
	}
	
	
	/**
	 * 
	 */
	protected void addDataset() {
		DataConfig trainingCfg = txtTrainingBrowse.getConfig();
		DataConfig testingCfg = txtTestingBrowse.getConfig();
		
		if (trainingCfg == null || testingCfg == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Can't add dataset because of empty dataset", 
					"Can't add dataset because of empty dataset", 
					JOptionPane.ERROR_MESSAGE);
			clear();
			return;
		}
		
		DatasetPair found = pool.findTrainingTesting(
				trainingCfg.getUriId(), 
				testingCfg.getUriId());
		
		if (found != null) {
			JOptionPane.showMessageDialog(
					this, 
					"Can't add dataset because of duplication", 
					"Can't add dataset because of duplication", 
					JOptionPane.ERROR_MESSAGE);
			clear();
			return;
		}
		
		Dataset trainingSet = DatasetUtil.loadDataset(trainingCfg);
		if (trainingSet == null) {
			
			JOptionPane.showMessageDialog(
					this, 
					"Training dataset is null", 
					"Invalid training dataset", 
					JOptionPane.ERROR_MESSAGE);
			
			clear();
			return;
		}
		
		if (!DatasetUtil2.validateTrainingset(this, trainingSet, algList.toArray(new Alg[] { })) ) {
			trainingSet.clear();
			clear();
			return;
		}

		
		Dataset testingSet = DatasetUtil.loadDataset(testingCfg);
		if (testingSet == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Testing dataset is null", 
					"Invalid testing dataset", 
					JOptionPane.ERROR_MESSAGE);
			
			clear();
			return;
		}
		if (testingSet instanceof Pointer) {
			JOptionPane.showMessageDialog(
					this, 
					"Testing dataset is pointer", 
					"Invalid testing dataset", 
					JOptionPane.ERROR_MESSAGE);
			
			clear();
			return;
		}
		
		
		
		DatasetPair pair = new DatasetPair(trainingSet, testingSet, null);
		pool.add(pair);
		
		JOptionPane.showMessageDialog(
				this, 
				"Added successfully", 
				"Added successfully", 
				JOptionPane.INFORMATION_MESSAGE);

		clear();
		dispose();
		
	}
	
	
}
