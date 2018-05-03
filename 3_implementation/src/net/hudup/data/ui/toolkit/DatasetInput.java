package net.hudup.data.ui.toolkit;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Provider;
import net.hudup.core.data.Unit;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.ProviderImpl;
import net.hudup.data.ui.DataConfigTextField;
import net.hudup.data.ui.UnitListBoxExt;
import net.hudup.data.ui.UnitTable;
import net.hudup.data.ui.UnitTableFactory;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetInput extends JPanel implements Dispose {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	JButton btnConfig = null;
	
	JButton btnRefresh = null;

	DataConfigTextField txtConfig = null;
	
	UnitTable unitTable = null;
	
	UnitListBoxExt unitList = null;
	
	Provider provider = null;
	
	
	/**
	 * 
	 */
	public DatasetInput() {
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		btnConfig = new JButton("Browse dataset");
		btnConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				DataConfig config = DatasetUtil2.chooseConfig(getThis(), txtConfig.getConfig());
				if (config == null) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Not open training set", 
							"Not open training set", 
							JOptionPane.ERROR_MESSAGE);
						return;
				}
				
				update(config);
			}
		});
		header.add(btnConfig, BorderLayout.WEST);
		
		txtConfig = new DataConfigTextField();
		txtConfig.setEditable(false);
		header.add(txtConfig, BorderLayout.CENTER);

		btnRefresh = UIUtil.makeIconButton(
			"refresh-16x16.png", 
			"refresh", "Refresh", "Refresh", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					update(txtConfig.getConfig());
				}
			});
		btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		header.add(btnRefresh, BorderLayout.EAST);

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		unitTable = UnitTableFactory.create();
		body.add(unitTable.getComponent(), BorderLayout.CENTER);

		unitList = new UnitListBoxExt() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public void clearData() {
				// TODO Auto-generated method stub
				super.clearData();
				unitTable.clear();
			}

			
			@Override
			public void modify() {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(
						getThis(), 
						"Not support this method", 
						"Not support this method", 
						JOptionPane.INFORMATION_MESSAGE);
			}

			
			@Override
			public void drop() {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(
						getThis(), 
						"Not support this method", 
						"Not support this method", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			
		};
		

		body.add(new JScrollPane(unitList), BorderLayout.WEST);
		unitList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
			
				Unit unit = unitList.getSelectedValue();
				if (unit == null) {
					unitTable.clear();
					return;
				}
				
				
				unitTable.update(provider, unit.getName());
			}
		});
		if (txtConfig.getConfig() != null)
			unitList.connectUpdate(txtConfig.getConfig());

		
	}
	
	
	/**
	 * 
	 * @return this dataset input
	 */
	private DatasetInput getThis() {
		return this;
	}


	/**
	 * 
	 * @param config
	 */
	private void update(DataConfig config) {
		if (config == null)
			return;
		
		if (provider != null) {
			provider.close();
		}
		provider = new ProviderImpl(config);
		
		txtConfig.setConfig(config);
		unitList.connectUpdate(config);
		unitTable.clear();
	}


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		unitTable.clear();
		if (provider != null) {
			provider.close();
			provider = null;
		}
	}


	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return provider != null;
	}
	
	
}
