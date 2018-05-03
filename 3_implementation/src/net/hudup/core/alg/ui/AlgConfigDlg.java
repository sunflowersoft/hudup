package net.hudup.core.alg.ui;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * This graphic user interface (GUI) component shows a dialog for users to configure algorithm.
 * In other words, the configuration of the internal algorithm is edited.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class AlgConfigDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The internal algorithm which will be configured.
	 */
	protected Alg thisAlg = null;

	
	/**
	 * The graphic user interface (GUI) component as panel shows a properties list.
	 */
	protected PropPane paneCfg = null;
	
	
	/**
	 * Constructor with parent component and specified algorithm.
	 * @param comp parent component.
	 * @param alg specified algorithm whose configuration is edited.
	 */
	public AlgConfigDlg(final Component comp, final Alg alg) {
		super(UIUtil.getFrameForComponent(comp), "Configure algorithm " + alg.getName(), true);
		// TODO Auto-generated constructor stub
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
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
				thisAlg.resetConfig();
				update(thisAlg.getConfig());
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

		}; 
		paneCfg.setToolbarVisible(true);
		
		add(paneCfg);

		update(alg);
	}

	
	/**
	 * Updating this {@link AlgConfigDlg} by specified algorithm.
	 * @param alg specified algorithm.
	 */
	public void update(Alg alg) {
		this.thisAlg = alg;
		this.paneCfg.update(alg.getConfig());
	}

	
	@Override
	public void setVisible(boolean flag) {
		if (flag && thisAlg.getConfig().size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Config empty", 
					"Config empty", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		super.setVisible(flag);
	}
	
	
}
