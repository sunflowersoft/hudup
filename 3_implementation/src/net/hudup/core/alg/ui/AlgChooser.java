package net.hudup.core.alg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.RegisterTableList.RegisterTableItem;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * This graphic user interface (GUI) component shows a dialog for choosing an algorithm.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AlgChooser extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal combo box for choosing an register table (choosing a algorithm type).
	 * Note a group of same-type algorithms are registered in the same register table.
	 * For example, recommender (s) are registered in recommender register table.
	 */
	protected RegisterTableComboBox cmbAlgTypes = null;
	
	
	/**
	 * The internal combo box for choosing an algorithm.
	 */
	protected AlgComboBox cmbAlgs = null;
	
	
	/**
	 * The result as chosen algorithm. 
	 */
	protected Alg result = null;
	
	
	/**
	 * The algorithm is selected by default when the dialog is shown first time.
	 */
	protected Object defaultAlg = null;
	
	
	/**
	 * List of removed algorithms. Such algorithms are not shown in the internal combo box {@link #cmbAlgs} for choosing algorithm.
	 */
	protected List<Alg> removedAlgList = null;
	
	
	/**
	 * Constructor with parent component, algorithm selected by default, initial list of algorithms, list of removed algorithms.
	 * @param comp parent component.
	 * @param defaultAlg algorithm selected by default.
	 * @param initAlgList initial list of algorithms.
	 * @param removedAlgList list of removed algorithms.
	 */
	public AlgChooser(Component comp, Object defaultAlg, List<Alg> initAlgList, List<Alg> removedAlgList) {
		super(UIUtil.getFrameForComponent(comp), "Choosing algorithms", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		this.defaultAlg = defaultAlg;
		this.removedAlgList = removedAlgList; 
		
		setLayout(new BorderLayout(2, 2));
		
		JPanel header = new JPanel(new BorderLayout(2, 2));
		add(header, BorderLayout.NORTH);
		
		JPanel pane = new JPanel(new BorderLayout());
		header.add(pane, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		pane.add(left, BorderLayout.WEST);
		if (initAlgList == null || initAlgList.size() == 0)
			left.add(new JLabel("Choose algorithm type"));
		left.add(new JLabel("Choose algorithm"));
		
		JPanel center = new JPanel(new GridLayout(0, 1));
		pane.add(center, BorderLayout.CENTER);

		if (initAlgList == null || initAlgList.size() == 0) {
			cmbAlgTypes = new RegisterTableComboBox(PluginStorage.getRegisterTableList());
			cmbAlgTypes.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					if (e.getStateChange() != ItemEvent.SELECTED)
						return;
					
					algTypeChanged();
				}
			});
			center.add(cmbAlgTypes);
		}
		
		JPanel algPane = new JPanel(new BorderLayout());
		center.add(algPane);

		cmbAlgs = new AlgComboBox(initAlgList);
		cmbAlgs.setDefaultSelected(defaultAlg);
		algPane.add(cmbAlgs, BorderLayout.CENTER);
		
		JButton btnConfig = UIUtil.makeIconButton(
			"config-16x16.png", 
			"config", "Config", "Config", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					Alg alg = getAlg();
					if (alg.getConfig() == null)
						JOptionPane.showMessageDialog(getThis(), "Algorithm has no configuration", "Algorithm has no configuration", JOptionPane.INFORMATION_MESSAGE);
					else
						new AlgConfigDlg(getThis(), alg).setVisible(true);
				}
			});
		btnConfig.setMargin(new Insets(0, 0 , 0, 0));
		algPane.add(btnConfig, BorderLayout.EAST);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				result = getAlg();
				
				dispose();
			}
		});
		footer.add(ok);
	
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				result = null;
				dispose();
			}
		});
		footer.add(cancel);

		algTypeChanged();
		
		setVisible(true);
	}


	/**
	 * Returning this {@link AlgChooser}.
	 * @return this {@link AlgChooser}.
	 */
	private AlgChooser getThis() {
		return this;
	}
	
	
	/**
	 * This method is event-driven method because it updates the internal combo box for choosing algorithm
	 * when the internal combo box for choosing register table (choosing a algorithm type) was changed.
	 */
	private void algTypeChanged() {
		if (cmbAlgTypes != null) {
			RegisterTableItem item = (RegisterTableItem) cmbAlgTypes.getSelectedItem();
			cmbAlgs.update( ((RegisterTable)item.getRegisterTable().clone()).getAlgList() );
		}
		
		cmbAlgs.remove(removedAlgList);
		cmbAlgs.setDefaultSelected(defaultAlg);
	}
	
	
	/**
	 * Getting the currently selected algorithm.
	 * @return currently selected algorithm.
	 */
	private Alg getAlg() {
		// TODO Auto-generated method stub
		if (cmbAlgs.getItemCount() == 0)
			return null;
		else
			return (Alg) cmbAlgs.getSelectedItem();
	}

	
	/**
	 * Getting the result as finally chosen algorithm.
	 * @return result as finally chosen algorithm.
	 */
	public Alg getResult() {
		return result;
	}
	
	
}
