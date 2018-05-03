/**
 * 
 */
package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.hudup.core.Constants;
import net.hudup.core.data.PropList;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * The graphic user interface (GUI) component as panel {@link JPanel} shows a properties list represented by {@link PropList}.
 * It also allows user to modify, save, and load the property list.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PropPane extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The graphic user interface (GUI) component as table shows a properties list represented by {@link PropList}.
	 */
	protected PropTable tblProp = null;
	
	
	/**
	 * The tool-bar with interactive buttons such as load button, save button.
	 */
	protected JPanel toolbar = null;
	
	
	/**
	 * The control-bar with interactive buttons such as reset button, apply button.
	 */
	protected JPanel control = null;
	
	
	/**
	 * Load button.
	 */
	protected JButton btnLoad = null;

	
	/**
	 * Save button.
	 */
	protected JButton btnSave = null;

	
	/**
	 * Reset button.
	 */
	protected JButton btnReset = null;
	
	
	/**
	 * Apply button.
	 */
	protected JButton btnApply = null;
	
	
	/**
	 * OK button.
	 */
	protected JButton btnOk = null;
	
	
	/**
	 * Cancel button.
	 */
	protected JButton btnCancel = null;
	
	
	/**
	 * Default constructor.
	 */
	public PropPane() {
		super();
		
		setLayout(new BorderLayout());
		
		this.tblProp = new PropTable();
		this.add(new JScrollPane(this.tblProp), BorderLayout.CENTER);
		
		toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.add(toolbar, BorderLayout.NORTH);
		toolbar.setVisible(false);
		
		this.btnLoad = UIUtil.makeIconButton("open-16x16.png", "load", "Load", "Load", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					onLoad();
				}
			});
		this.btnLoad.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(btnLoad);

		this.btnSave = UIUtil.makeIconButton("save-16x16.png", "save", "Save", "Save", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					onSave();
				}
			});
		this.btnSave.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(btnSave);

		
		control = new JPanel();
		this.add(control, BorderLayout.SOUTH);
		
		btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}
			
		});
		control.add(btnReset);

		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				onApply();
			}
		});
		control.add(btnApply);
		
		btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				onOK();
			}
			
		});
		control.add(btnOk);
		

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		control.add(btnCancel);
	
	}
	
	
	/**
	 * Setting the visibility of the tool-bar via specified flag.
	 * @param flag if {@code true}, the tool-bar is visible.
	 */
	public void setToolbarVisible(boolean flag) {
		toolbar.setVisible(flag);
	}

	
	/**
	 * Setting the visibility of the control-bar via specified flag.
	 * @param flag if {@code true}, the control-bar is visible.
	 */
	public void setControlVisible(boolean flag) {
		control.setVisible(flag);
	}
	
	
	/**
	 * Updating this panel by specified property list.
	 * @param propList specified property list.
	 */
	public void update(PropList propList) {
		tblProp.update(propList);
	}
	
	
	/**
	 * Loading property list from chosen URI and then updating this {@link PropPane}.
	 * Of course this method shows a dialog for users to choose source URI.
	 */
	private void onLoad() {
		UriAdapter adapter = new UriAdapter();
		xURI uri = adapter.chooseUri(
				this, 
				true, 
				new String[] {
						Constants.DEFAULT_EXT,
						"xml"
					}, 
				new String[] {
						"Hudup file (*." + Constants.DEFAULT_EXT + ")",
						"XML file (*.xml)"
					},
				null);
		adapter.close();
		
		if (uri == null)
			return;
		
		PropList propList = new PropList();
		propList.load(uri);
		tblProp.getPropTableModel().updateNotSetup(propList);
	}
	
	
	/**
	 * Saving property list to chosen URI. Of course this method shows a dialog for users to choose target URI.
	 */
	private void onSave() {
		UriAdapter adapter = new UriAdapter();

		xURI uri = adapter.chooseUri(
				this, 
				false, 
				new String[] {
						Constants.DEFAULT_EXT,
						"xml"
					}, 
				new String[] {
						"Hudup file (*." + Constants.DEFAULT_EXT + ")",
						"XML file (*.xml)"
					},
				null);
		adapter.close();
		
		if (uri == null)
			return;
		
		if (tblProp.isModified()) {
			int confirm = JOptionPane.showConfirmDialog(
					this, 
					"Data is modified. Do you want to apply it?", 
					"Data is modified", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (confirm == JOptionPane.YES_OPTION)
				onApply();
		}
		
		adapter = new UriAdapter(uri);
		boolean existed = adapter.exists(uri);
		adapter.close();
		
		if (existed) {
			int confirm = JOptionPane.showConfirmDialog(
					this, 
					"URI exists. Do you want to overwrite it?", 
					"URI exists", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (confirm != JOptionPane.YES_OPTION) {
				onSave();
				return;
			}
		}
		
		PropList propList = tblProp.getPropTableModel().getModelPropList();
		propList.save(uri);
		
		JOptionPane.showMessageDialog(
				this, 
				"Save successfully", 
				"Save successfully", 
				JOptionPane.INFORMATION_MESSAGE);
	}

	
	/**
	 * This is event-driven method which asking for user whether applying changes into property list.
	 * If user agrees, this method applies changes from graphic user interface (GUI) into the internal property list by calling {@link #apply()}.
	 */
	private void onApply() {
		boolean apply = apply();
		if (!apply) {
			JOptionPane.showMessageDialog(
					this, 
					"Cannot apply", 
					"Cannot apply", 
					JOptionPane.ERROR_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(
					this, 
					"Apply successfully", 
					"Apply successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	
	/**
	 * This method calls {@link #apply()} to apply changes from graphic user interface (GUI) into the internal property list.
	 * This method correspond to OK action on dialog. 
	 */
	protected void onOK() {
		if (tblProp.isModified())
			onApply();
		close();
	}
	
	
	/**
	 * Applying changes from graphic user interface (GUI) into the internal property list.
	 * @return whether apply successfully
	 */
	public boolean apply() {
		return tblProp.apply();
	}
	
	
	/**
	 * {@link PropPane} does not support this method.
	 */
	public void reset() {
		JOptionPane.showMessageDialog(
				this, 
				"Not support this function", 
				"Not support this function", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * {@link PropPane} does not support this method.
	 */
	public void close() { 
		JOptionPane.showMessageDialog(
				this, 
				"Not support this function", 
				"Not support this function", 
				JOptionPane.INFORMATION_MESSAGE);
	}


	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		
		tblProp.setEnabled(enabled);
		control.setEnabled(enabled);
		btnReset.setEnabled(enabled);
		btnApply.setEnabled(enabled);
		btnOk.setEnabled(enabled);
		btnCancel.setEnabled(enabled);
	}
	
	
	/**
	 * Getting the internal {@link PropTable} which is the main component of {@link PropPane}.
	 * {@link PropTable} is the graphic user interface (GUI) component as table {@link JTable} showing a properties list represented by {@link PropList}.
	 * @return internal {@link PropTable}.
	 */
	public PropTable getPropTable() {
		return tblProp;
	}
	
	
	/**
	 * Hiding OK button and Cancel button.
	 */
	public void hideOkCancel() {
		btnOk.setVisible(false);
		btnCancel.setVisible(false);
	}
	
	
}
