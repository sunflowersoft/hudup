package net.hudup.core.alg.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * This graphic user interface (GUI) component as a {@link JList} shows a list of algorithms.
 * It also allows users to choose and configure algorithms.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class AlgListBox extends JList<Alg> implements AlgListUI {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Whether enabling double-click operator. The default value is {@code true}.
     */
    private boolean enableDoubleClick = true;
    
    
    /**
     * Whether this list is sorted. The default value is {@code true}.
     */
    private boolean sorting = true;

    
    /**
     * Constructor with flag of sorting.
     * @param sorting if {@code true}, this list is sorted.
     */
    public AlgListBox(boolean sorting) {
		super();
		this.sorting = sorting;
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (!getThis().isEnabled())
					return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = AlgListUIUtil.createContextMenu(getThis());
					if(contextMenu == null)
						return;
					
					addToContextMenu(contextMenu);
					contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (enableDoubleClick && e.getClickCount() >= 2) {
					AlgListUIUtil.config(getThis());
				}
			}
			
		});
		
	}
	
	
    /**
     * Adding the context menu to this list.
     * 
     * @param contextMenu specified context menu.
     */
    private void addToContextMenu(JPopupMenu contextMenu) {
    	final int selectedRow = getSelectedIndex();
    	if (selectedRow == -1)
    		return;
    	
		contextMenu.addSeparator();
		if (selectedRow == 0) {
			JMenuItem miMoveDown = UIUtil.makeMenuItem((String)null, "Move down", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						moveRowDown(selectedRow);
					}
				});
			contextMenu.add(miMoveDown);

			JMenuItem miMoveLast = UIUtil.makeMenuItem((String)null, "Move last", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						moveRowLast(selectedRow);
					}
				});
			contextMenu.add(miMoveLast);
		}
		else if (selectedRow == getModel().getSize() - 1) {
			JMenuItem miMoveFirst = UIUtil.makeMenuItem((String)null, "Move first", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						moveRowFirst(selectedRow);
					}
				});
			contextMenu.add(miMoveFirst);
				
			JMenuItem miMoveUp = UIUtil.makeMenuItem((String)null, "Move up", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						moveRowUp(selectedRow);
					}
				});
			contextMenu.add(miMoveUp);
		}
		else {
			JMenuItem miMoveFirst = UIUtil.makeMenuItem((String)null, "Move first", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						moveRowFirst(selectedRow);
					}
				});
			contextMenu.add(miMoveFirst);
				
			JMenuItem miMoveUp = UIUtil.makeMenuItem((String)null, "Move up", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						moveRowUp(selectedRow);
					}
				});
			contextMenu.add(miMoveUp);
			
			JMenuItem miMoveDown = UIUtil.makeMenuItem((String)null, "Move down", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						moveRowDown(selectedRow);
					}
				});
			contextMenu.add(miMoveDown);
			
			JMenuItem miMoveLast = UIUtil.makeMenuItem((String)null, "Move last", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						moveRowLast(selectedRow);
					}
				});
			contextMenu.add(miMoveLast);
		}
    	
    }
    
    
    /**
     * Updating this {@link AlgListBox} by the specified list of algorithms.
     * @param algList specified list of algorithms.
     */
	public void update(List<Alg> algList) {
		
		Vector<Alg> data = Util.newVector();
		data.addAll(algList);
		
		if (sorting) {
			Collections.sort(data, new Comparator<Alg>() {
	
				@Override
				public int compare(Alg alg1, Alg alg2) {
					// TODO Auto-generated method stub
					return alg1.getName().compareTo(alg2.getName());
				}
			});
		}
		setListData(data);
		
	}
	
	
	/**
     * Updating this {@link AlgListBox} by the specified array of algorithms.
	 * @param algList specified array of algorithms.
	 */
	public void update(Alg[] algList) {
		update(Arrays.asList(algList));
	}
	
	
	/**
	 * Adding an algorithm into this {@link AlgListBox}.
	 * @param alg specified algorithm which is added into this {@link AlgListBox}.  
	 */
	public void add(Alg alg) {
		List<Alg> algList = getAlgList();
		algList.add(alg);
		update(algList);
	}
	
	
	/**
	 * Adding the specified collection of algorithms into this {@link AlgListBox}.
	 * @param algList specified collection of algorithms.
	 */
	public void addAll(Collection<Alg> algList) {
		Set<Alg> algSet = Util.newSet();
		algSet.addAll(getAlgList());
		algSet.addAll(algList);
		
		List<Alg> wholeList = Util.newList();
		wholeList.addAll(algSet);
		update(wholeList);
	}
	
	
	/**
	 * Removing the specified algorithm from this {@link AlgListBox}.
	 * @param alg specified algorithm that is removed from this {@link AlgListBox}.
	 */
	public void remove(Alg alg) {
		List<Alg> algList = getAlgList();
		algList.remove(alg);
		update(algList);
	}
	
	
	/**
	 * Removing an algorithm from this list at specified index.
	 * @param idx specified index.
	 */
	public void remove(int idx) {
		List<Alg> algList = getAlgList();
		algList.remove(idx);
		update(algList);
	}
	

	/**
	 * Removing algorithms inside the specified collection from this {@link AlgListBox}.
	 * @param algs specified collection of algorithms. 
	 */
	public void removeAll(Collection<Alg> algs) {
		List<Alg> algList = getAlgList();
		algList.removeAll(algs);
		update(algList);
	}
	
	
	/**
	 * Removing selected algorithms from this list.
	 * @return list of algorithms which were removed from this list.
	 */
	public List<Alg> removeSelectedList() {
		List<Alg> selected = getSelectedList();
		removeAll(selected);
		return selected;
	}
	
	
	/**
	 * Moving the row at specified index up to top in this list.
	 * @param rowIndex specified index.
	 */
	protected void moveRowFirst(int rowIndex) {
		int rowCount = getModel().getSize();
		if (rowIndex == 0 || rowCount == 0)
			return;
		
		moveRow(rowIndex, rowIndex, 0);
	}

	
	/**
	 * Moving the row at specified index up to one level.
	 * @param rowIndex specified index.
	 */
	protected void moveRowUp(int rowIndex) {
		int rowCount = getModel().getSize();
		if (rowIndex == 0 || rowCount == 0)
			return;
		
		moveRow(rowIndex, rowIndex, rowIndex - 1);
	}

	
	/**
	 * Moving the row at specified index down to one level.
	 * @param rowIndex specified index.
	 */
	protected void moveRowDown(int rowIndex) {
		int rowCount = getModel().getSize();
		if (rowIndex == rowCount - 1 || rowCount == 0)
			return;
		
		moveRow(rowIndex, rowIndex, rowIndex + 1);
	}
	
	
	/**
	 * Moving the row at specified index down to last in this list.
	 * @param rowIndex specified index.
	 */
	protected void moveRowLast(int rowIndex) {
		int rowCount = getModel().getSize();
		if (rowIndex == rowCount - 1 || rowCount == 0)
			return;
		
		moveRow(rowIndex, rowIndex, rowCount - 1);
	}

	/**
	 * Moving the range of rows specified by the start position and the end position to the new position.
	 * @param start start position of the range.
	 * @param end end position of the range.
	 * @param to new position to which the range of rows is moved.
	 */
	protected void moveRow(int start, int end, int to) {
		List<Alg> algList = getAlgList();
		DSUtil.moveRow(algList, start, end, to);
		update(algList);
	}
	
	
	/**
	 * Clearing this {@link AlgListBox}, which means that all algorithms are removed from this list.
	 */
	public void clear() {
		List<Alg> algList = getAlgList();
		algList.clear();
		update(algList);
	}
	
	
	/**
	 * Getting the list of algorithms in this {@link AlgListBox}.
	 * @return {@link List} of algorithms in this {@link AlgListBox}.
	 */
	public List<Alg> getAlgList() {
		List<Alg> algList = Util.newList();
		
		ListModel<Alg> model = getModel();
		int n = model.getSize();
		for (int i = 0; i < n; i++) {
			algList.add(model.getElementAt(i));
		}
		
		return algList;
	}
	

	/**
	 * Getting the name list of algorithms in this {@link AlgListBox}.
	 * @return name list of algorithms in this {@link AlgListBox}.
	 */
	public List<String> getAlgNameList() {
		List<Alg> algList = getAlgList();
		List<String> nameList = Util.newList();
		
		for (Alg alg : algList)
			nameList.add(alg.getName());
		
		return nameList;
	}
	
	
	/**
	 * Getting the list of selected algorithms.
	 * @return {@link List} of selected algorithms.
	 */
	@SuppressWarnings("deprecation")
	public List<Alg> getSelectedList() {
		Object[] list = getSelectedValues();
		List<Alg> algList = Util.newList();
		for (Object object : list) {
			algList.add((Alg)object);
		}
		return algList;
	}
	
	
	/**
	 * Setting whether or not this {@link AlgListBox} enables double-click operator.
	 * @param enable if {@code true}, this {@link AlgListBox} enables double-click operator.
	 */
	public void setEnableDoubleClick(boolean enable) {
		this.enableDoubleClick = enable;
	}
	
	
	/**
	 * Returning this {@link AlgListBox}.
	 * @return this {@link AlgListBox}.
	 */
	private AlgListBox getThis() {
		return this;
	}
	
	
	@Override
	public Alg getSelectedAlg() {
		return getSelectedValue();
	}

	
}
