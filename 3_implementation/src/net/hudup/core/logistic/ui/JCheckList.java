package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import net.hudup.core.Util;


/**
 * This class creates a graphic user interface (GUI) component as a {@link JList} (list box) whose items are check boxes.
 * <br>
 * Modified by Loc Nguyen 2011.
 * 
 * @param <E> type of elements attached with check box items of this {@link JCheckList}.
 * Such elements (objects) are wrapped by {@link CheckListItem} class.
 *  
 * @author Someone on internet
 * @version 1.0
 */
public class JCheckList<E> extends JList<CheckListItem<E>> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public JCheckList() {
		super();
		
		setCellRenderer(new CheckListRenderer<E>());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
				int index = locationToIndex(e.getPoint());
				if (index == -1)
					return;
				
				CheckListItem<E> listItem = getModel().getElementAt(index);
				listItem.setSelected(!listItem.isSelected());
				repaint(getCellBounds(index, index));
			}
			
		});
	}
	
	
	/**
	 * Constructor with a specified list of attached objects. Each object is attached with a check box item.
	 * @param listData specified list of attached object.
	 */
	public JCheckList(List<? extends E> listData) {
		this();
		// TODO Auto-generated constructor stub
		
		Vector<CheckListItem<E>> vector = Util.newVector();
		for (E element : listData) {
			vector.add(new CheckListItem<E>(element));
		}
		
		setListData(vector);
	}

	
	/**
	 * Getting the list of objects attached with the selected check box items.
	 * @return list of objects attached with the selected check box items.
	 */
	public List<E> getSelectedItemList() {
		List<CheckListItem<E>> selectedList = getSelectedValuesList();
		List<E> itemList = Util.newList();
		
		for (CheckListItem<E> checkItem : selectedList) {
			itemList.add(checkItem.getItem());
		}
		
		return itemList;
	}
	
	
}


/**
 * This class is the check box {@link JCheckBox} working as a render that guides how to show items in {@link JCheckList} as check boxes.
 * <br>
 * Modified by Loc Nguyen 2011.
 * 
 * @param <E> type of the object attached with this check box.
 * 
 * @author Someone on internet
 * @version 1.0
 */
class CheckListRenderer<E> extends JCheckBox implements ListCellRenderer<CheckListItem<E>> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public Component getListCellRendererComponent(
			JList<? extends CheckListItem<E>> list, CheckListItem<E> value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		
		setEnabled(list.isEnabled());
		setSelected(value.isSelected());
		setFont(list.getFont());
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		setText(value.toString());
		
		return this;
	}
	
}



/**
 * This class is a wrapper of an object attached of an item in {@link JCheckList}.
 * <br>
 * Modified by Loc Nguyen 2011.
 * 
 * @param <E> type of attached object.
 * @author Someone on internet
 * @version 1.0
 *
 */
class CheckListItem<E> {
	
	
	/**
	 * The internal object (item) wrapped by this {@link CheckListItem}.
	 */
	protected E item = null;
	
	
	/**
	 * This variable indicates whether or not this {@link CheckListItem} is selected.
	 */
	protected boolean isSelected = false;
	
	
	/**
	 * Default constructor with the wrapped object (item).
	 * @param item specified object (item).
	 */
	public CheckListItem(E item) {
		this.item = item;
	}
	
	
	/**
	 * Setting that this {@link CheckListItem} is selected or unselected by the specified flag.
	 * @param isSelected if {@code true}, this {@link CheckListItem} is selected. Otherwise, this {@link CheckListItem} is unselected.
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	
	/**
	 * Testing whether or not this {@link CheckListItem} is selected.
	 * @return whether or not this {@link CheckListItem} is selected.
	 */
	public boolean isSelected() {
		return isSelected;
	}
	
	
	/**
	 * Getting the internal object (item).
	 * @return internal object (item).
	 */
	public E getItem() {
		return item;
	}
	
	
	@Override
	public String toString() {
		return item.toString();
	}
	
	
}
