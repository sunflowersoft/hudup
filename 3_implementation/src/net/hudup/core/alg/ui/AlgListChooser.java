package net.hudup.core.alg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * This graphic user interface (GUI) component as a dialog shows algorithms.
 * It also allows users to choose and configure algorithms.
 * It contains two {@link AlgListBox} (s) in which the left one assists users to select algorithms whereas the right one contains chosen algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class AlgListChooser extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The left {@link AlgListBox} assists users to select algorithms.
	 */
	private AlgListBox leftList = null;

	
	/**
	 * The right {@link AlgListBox} contains chosen algorithms.
	 */
	private AlgListBox rightList = null;
	
	
	/**
	 * Result as list of chosen algorithms.
	 */
	private List<Alg> result = null;
	
	
	/**
	 * If {@code true}, users press OK button to close this dialog.
	 */
	private boolean ok = false;
	
	
	/**
	 * Constructor with parent component, the whole list of algorithms, the list of firstly selected algorithms.
	 * @param comp parent component.
	 * @param wholeList whole list of algorithms.
	 * @param selectedList list of firstly selected algorithms.
	 */
	public AlgListChooser(Component comp, List<Alg> wholeList, List<Alg> selectedList) {
		super(UIUtil.getFrameForComponent(comp), "Choosing algorithms", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		JPanel pane = null;
		
		Collections.sort(wholeList, new Comparator<Alg>() {
			
			@Override
			public int compare(Alg alg1, Alg alg2) {
				// TODO Auto-generated method stub
				return alg1.getName().compareTo(alg2.getName());
			}
		});
		Collections.sort(selectedList, new Comparator<Alg>() {
			
			@Override
			public int compare(Alg alg1, Alg alg2) {
				// TODO Auto-generated method stub
				return alg1.getName().compareTo(alg2.getName());
			}
		});

		this.result = Util.newList();
		this.result.addAll(selectedList);
		
		List<Alg> remainList = Util.newList();
		remainList.addAll(wholeList);
		remainList.removeAll(selectedList); 
		
		setLayout(new BorderLayout());
		JPanel body = new JPanel(new GridLayout(1, 0));
		add(body, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		body.add(left);
		
		left.add(new JLabel("Available algorithm list"), BorderLayout.NORTH);
		leftList = new AlgListBox(true);
		leftList.update(remainList);
		leftList.setEnableDoubleClick(false);
		left.add(new JScrollPane(leftList), BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(0, 1));
		left.add(buttons, BorderLayout.EAST);
		
		JButton leftToRight = new JButton("> ");
		leftToRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				leftToRight();
			}
		});
		pane = new JPanel();
		pane.add(leftToRight);
		buttons.add(pane);
		
		JButton leftToRightAll = new JButton(">>");
		leftToRightAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				leftToRightAll();
			}
		});
		pane = new JPanel();
		pane.add(leftToRightAll);
		buttons.add(pane);
		
		JButton rightToLeft = new JButton("< ");
		rightToLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				rightToLeft();
			}
		});
		pane = new JPanel();
		pane.add(rightToLeft);
		buttons.add(pane);
		
		JButton rightToLeftAll = new JButton("<<");
		rightToLeftAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				rightToLeftAll();
			}
		});
		pane = new JPanel();
		pane.add(rightToLeftAll);
		buttons.add(pane);

		
		JPanel right = new JPanel(new BorderLayout());
		body.add(right);
		
		right.add(new JLabel("Selected algorithm list"), BorderLayout.NORTH);
		
		rightList = new AlgListBox(true);
		rightList.setEnableDoubleClick(false);
		rightList.update(selectedList);
		right.add(new JScrollPane(rightList), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ok();
			}
		});
		footer.add(ok);
	
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		footer.add(cancel);

	
		setVisible(true);
	}
	
	
	/**
	 * Transferring selected algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 */
	private void leftToRight() {
		List<Alg> list = leftList.removeSelectedList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithm not selected or empty list", 
					"Algorithm not selected or empty list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		rightList.addAll(list);
		
	}
	

	/**
	 * Transferring all algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 */
	private void leftToRightAll() {
		List<Alg> list = leftList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		rightList.addAll(list);
		leftList.clear();
	}

	
	/**
	 * Transferring selected algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 */
	private void rightToLeft() {
		List<Alg> list = rightList.removeSelectedList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithm not selected or empty list", 
					"Algorithm not selected or empty list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		leftList.addAll(list);
	}
	

	/**
	 * Transferring all algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 */
	private void rightToLeftAll() {
		List<Alg> list = rightList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		leftList.addAll(list);
		rightList.clear();
	}

	
	/**
	 * This is event-driven method which responses the action user pressed on OK button.
	 * This method will sets up the result as list of selected algorithms and then closes this dialog.
	 */
	private void ok() {
		List<Alg> list = rightList.getAlgList();
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		this.result.clear();
		this.result.addAll(list);
		
		ok = true;
		dispose();
	}
	
	
	/**
	 * Getting the result as list of chosen algorithms.
	 * @return result as list of chosen algorithms.
	 */
	public List<Alg> getResult() {
		return result;
	}
	
	
	/**
	 * Checking whether or not the OK button is pressed.
	 * @return whether or not the OK button is pressed.
	 */
	public boolean isOK() {
		return ok;
	}
	
	
	/**
	 * This static method gives convenience for users by showing the {@link AlgListChooser} dialog with specified array of algorithms.
	 * @param comp parent component.
	 * @param algs specified array of algorithms.
	 * @return result as list of chosen algorithms.
	 */
	public static Alg chooseAlg(Component comp, Alg[] algs) {
		return (Alg) JOptionPane.showInputDialog(
				comp, 
				"Choose algorithm", 
				"Choose algorithm", 
				JOptionPane.INFORMATION_MESSAGE, 
				null, 
				algs, 
				algs[0]);		
	}
	
	
}
