package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public class DatasetLoader extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected JButton btnBrowse = null;
	
	
	protected DataConfigTextField txtBrowse = null;

	
	/**
	 * 
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * 
	 * @param comp
	 */
	public DatasetLoader(Component comp) {
		super(UIUtil.getFrameForComponent(comp), "Dataset loader", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 200);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout(10, 10));
		
		JPanel header = new JPanel(new BorderLayout(10, 10));
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				onBrowse();
			}
		});
		left.add(btnBrowse);
		
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		txtBrowse = new DataConfigTextField();
		txtBrowse.setEditable(false);
		right.add(txtBrowse);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				onLoad();
			}
		});
		footer.add(btnLoad);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dataset = null;
				dispose();
			}
		});
		footer.add(btnCancel);
	
		setVisible(true);
	}
	
	
	
	/**
	 * 
	 */
	private void onBrowse() {
		DataConfig config = net.hudup.data.DatasetUtil2.chooseConfig(this, null);
		
		if (config == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Not open", 
					"Not open", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		this.txtBrowse.setConfig(config);
	}
	
	
	/**
	 * 
	 */
	private void onLoad() {
		this.dataset = null;
		
		DataConfig config = txtBrowse.getConfig();
		if (config == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Empty", 
					"Empty", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		Dataset dataset = 
				DatasetUtil.loadDataset(config);
		
		if (dataset == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Load dataset failed", 
					"Load dataset failed", 
					JOptionPane.ERROR_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(
					this, 
					"Load dataset successfully", 
					"Load dataset successfully", 
					JOptionPane.INFORMATION_MESSAGE);
			
			this.dataset = dataset;
			dispose();
		}
		
	}
	
	
	
}
