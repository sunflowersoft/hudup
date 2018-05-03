package net.hudup.data.ctx.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class CTselector extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	private ContextTemplate selectedTemplate = null;
	
	
	/**
	 * 
	 * @param cts
	 */
	public CTselector(Component comp, ContextTemplateSchema cts) {
		super(UIUtil.getFrameForComponent(comp), "Context template selector", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		final CTSviewer viewer = new CTSviewer(cts);
		add(new JScrollPane(viewer), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		JButton btnOK = new JButton("OK");
		footer.add(btnOK);
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectedTemplate = viewer.getSelectedTemplate();
				
				if (selectedTemplate == null) {
					int confirm = JOptionPane.showConfirmDialog(
							viewer, 
							"No templated to be selected.\n Do you want to close this selector?", 
							"No templated to be selected", 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE);
					if (confirm == JOptionPane.OK_OPTION) {
						dispose();
						return;
					}
						
				}
				else {
					dispose();
				}
			}
		});
		
		
		JButton btnCancel = new JButton("Cancel");
		footer.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});

		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return selected template
	 */
	public ContextTemplate getSelectedTemplate() {
		return selectedTemplate;
	}
	
	
}
