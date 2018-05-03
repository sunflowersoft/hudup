package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;


/**
 * This is the dialog showing help content of Hudup framework.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class HelpContent extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with parent component.
	 * @param comp parent component.
	 */
	public HelpContent(Component comp) {
		super(UIUtil.getFrameForComponent(comp), "Help content", true);
		// TODO Auto-generated constructor stub
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);

		header.add(new JLabel("Hudup Recommender Framework"));
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		TooltipTextArea txtTooltip = new TooltipTextArea();
		body.add(new JScrollPane(txtTooltip), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		JButton btnOK = new JButton("OK");
		footer.add(btnOK);
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		
		setVisible(true);
	}

	
}
