package net.hudup.core.logistic.ui;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.hudup.core.Util;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class creates a graphic user interface (GUI) component as a list of radio buttons.
 * <br>
 * Modified by Loc Nguyen 2011.
 * 
 * @author Someone on internet.
 *
 * @param <E> type of elements attached with radio buttons.
 * @version 10.0
 */
public class JRadioList<E> extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of ratio entries. Each entry has a radio button {@link JRadioButton} and an attached object (attached element).
	 */
	protected List<Object[]> radioList = Util.newList();
	
	
	/**
	 * Constructor with a specified list of attached object. Each object is attached with a radion button {@link JRadioButton}.
	 * @param listData specified list of attached object.
	 * @param listName name of this {@link JRadioList}.
	 */
	public JRadioList(List<E> listData, String listName) {
		super();
		setLayout(new GridLayout(0, 1));
		if (listName == null || listName.isEmpty()) {
			setBorder(BorderFactory.createEtchedBorder());
		}
		else {
			setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), listName) );
		}
		
		ButtonGroup bg = new ButtonGroup();
		for (E e : listData) {
			String text = e.toString();
			JRadioButton rb = new JRadioButton(TextParserUtil.split(text, TextParserUtil.LINK_SEP, null).get(0));
			bg.add(rb);
			add(rb);
			
			radioList.add(new Object[] { rb, e});
		}
	}
	
	
	/**
	 * Getting the object attached with the selected radio button (selected item).
	 * @return object attached with the selected radio button (selected item).
	 */
	@SuppressWarnings("unchecked")
	public E getSelectedItem() {
		for (Object[] pair : radioList) {
			JRadioButton rb = (JRadioButton)pair[0];
			if (rb.isSelected())
				return (E) pair[1];
		}
		
		return null;
	}
	
	
	
}
