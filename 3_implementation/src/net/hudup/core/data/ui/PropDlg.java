package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.hudup.core.data.PropList;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * The graphic user interface (GUI) component as a dialog shows a properties list represented by {@link PropList}.
 * It also allows user to edit the property list.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PropDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The table contains the internal property list, as {@link PropTable}.
	 */
	protected PropTable tblProp = null;

	
	/**
	 * The modified property list as a result.
	 */
	protected PropList result = null;
	
	
	/**
	 * Constructor with specified property list.
	 * @param comp parent component.
	 * @param propList specified property list.
	 * @param key the key shown on the title of dialog.
	 */
	public PropDlg(final Component comp, final PropList propList, String key) {
		super(UIUtil.getFrameForComponent(comp), "Configure key " + key, true);
		this.result = null;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				if (!tblProp.isModified())
					return;
				
				int confirm = JOptionPane.showConfirmDialog(
						getThis(), 
						"Attributes are modified. Do you want to apply them?", 
						"Attributes are modified", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				
				if (confirm == JOptionPane.YES_OPTION)
					onApply();
			}
			
		});
		
		
		tblProp = new PropTable();
		tblProp.update(propList);
		add(new JScrollPane(tblProp), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				onApply();
			}
			
		});
		footer.add(btnApply);

		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				onOk();
			}
			
		});
		footer.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCancel();
			}
		});
		footer.add(btnCancel);

		setVisible(true);
	}
	
	
	/**
	 * Getting this {@link PropDlg}
	 * @return this dialog.
	 */
	private PropDlg getThis() {
		return this;
	}
	
	
	/**
	 * This is event-driven method responding the action that user presses on &quot;Apply&quot; button.
	 * It applies all changes on the GUI into the internal property list as a result.
	 */
	protected void onApply() {
		if (!tblProp.isModified())
			return;
		
		if(tblProp.apply()) {
			result = tblProp.getPropList();
			JOptionPane.showMessageDialog(
					this, 
					"Apply successfully", 
					"Apply successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	
	/**
	 * This is event-driven method responding the action that user presses on &quot;OK&quot; button.
	 * It calls method {@link #onApply()} to apply all changes on the GUI into the internal property list as a result and then closes this dialog with &quot;OK&quot; code.
	 */
	protected void onOk() {
		onApply();
		
		dispose();
	}
	
	
	/**
	 * This is event-driven method responding the action that user presses on &quot;Cancel&quot; button.
	 * It closes this dialog with &quot;Cancel&quot; code.
	 */
	protected void onCancel() {
		if (tblProp.isModified()) {
			int confirm = JOptionPane.showConfirmDialog(
					this, 
					"Attributes are modified. Do you want to apply them?", 
					"Attributes are modified", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			
			if (confirm == JOptionPane.YES_OPTION)
				onApply();
		}

		dispose();
	}

	
	/**
	 * Getting the result as modified property list.
	 * @return result as modified property list.
	 */
	public PropList getResult() {
		return result;
	}
	
	
}


/**
 * The graphic user interface (GUI) component as a dialog shows a properties list represented by {@link PropList}.
 * It also allows user to edit the property list.
 * It is similar to {@link PropDlg} except that it uses {@link PropPane} to contain property list {@link PropList} instead of using {@link PropTable}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class PropDlg2 extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * {@link PropPane} to contain property list
	 */
	protected PropPane paneCfg = null;
	
	
	/**
	 * The modified property list as a result.
	 */
	protected PropList result = null;
	
	
	/**
	 * Constructor with specified property list.
	 * @param comp parent component.
	 * @param propList specified property list.
	 * @param key the key shown on the title of dialog.
	 */
	public PropDlg2(final Component comp, final PropList propList, String key) {
		super(UIUtil.getFrameForComponent(comp), "Configure properties list for key " + key, true);
		// TODO Auto-generated constructor stub
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLayout(new BorderLayout());
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				if (paneCfg.getPropTable().isModified()) {
					int confirm = JOptionPane.showConfirmDialog(
							comp, 
							"Attributes are modified. Do you want to apply them?", 
							"Attributes are modified", 
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					
					if (confirm == JOptionPane.YES_OPTION)
						paneCfg.apply();
				}
			}
			
		});
		
		paneCfg = new PropPane() {
			
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public void close() {
				// TODO Auto-generated method stub
				if (paneCfg.getPropTable().isModified()) {
					int confirm = JOptionPane.showConfirmDialog(
							comp, 
							"Attributes are modified. Do you want to apply them?", 
							"Attributes are modified", 
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					
					if (confirm == JOptionPane.YES_OPTION)
						apply();
				}
				dispose();
			}

			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				paneCfg.update(propList);
				JOptionPane.showMessageDialog(
						comp, 
						"Apply successfully", 
						"Apply successfully", 
						JOptionPane.INFORMATION_MESSAGE);
			}

			
			@Override
			public boolean apply() {
				// TODO Auto-generated method stub
				return super.apply();
			}


			@Override
			protected void onOK() {
				// TODO Auto-generated method stub
				boolean isModified = paneCfg.getPropTable().isModified();
				super.onOK();
				
				if (isModified && !paneCfg.getPropTable().isModified())
					result = paneCfg.getPropTable().getPropTableModel().getPropList();
			}

		}; 
		paneCfg.setToolbarVisible(true);
		
		add(paneCfg, BorderLayout.CENTER);

		paneCfg.update(propList);
	}

	
	/**
	 * Getting the result as modified property list.
	 * @return result as modified property list.
	 */
	public PropList getResult() {
		return result;
	}
	
	
}


