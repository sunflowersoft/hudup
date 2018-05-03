package net.hudup.logistic.inference.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JPanel;

import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.logistic.inference.BnetBinaryGraph;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BinaryBayesGraphDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * 
	 * @param comp
	 * @param bayesGraph
	 * @param modal
	 */
	public BinaryBayesGraphDlg(
			Component comp, 
			BnetBinaryGraph bayesGraph, 
			boolean modal) {
		
		super(UIUtil.getFrameForComponent(comp), "Binary Bayesian network", modal);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		BinaryBayesGraphPane bayesGraphPane = 
				new BinaryBayesGraphPane(bayesGraph);
		body.add(bayesGraphPane, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	
	
	
}
