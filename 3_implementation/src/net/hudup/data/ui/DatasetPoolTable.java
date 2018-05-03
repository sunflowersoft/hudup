/**
 * 
 */
package net.hudup.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DatasetPair;
import net.hudup.data.DatasetPool;

/**
 * @author Loc Nguyen
 * @version 10.0
 */
public class DatasetPoolTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected boolean enabled = true;
	
	
	/**
	 * 
	 */
	public DatasetPoolTable() {
		// TODO Auto-generated constructor stub
		super(new DatasetPoolTableModel());
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int row = getSelectedRow();
				if(SwingUtilities.isRightMouseButton(e) && row != -1) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					removeSelectedRows();
			}
			
		});
		
	}
	
	
	/**
	 * 
	 * @return this {@link DatasetPoolTable}
	 */
	private DatasetPoolTable getThis() {
		return this;
	}
	
	
	/**
	 * 
	 * @param pool
	 */
	public void update(DatasetPool pool) {
		getPoolTableModel().update(pool);
		
		setupUI();
	}
	
	
	/**
	 * 
	 */
	private void setupUI() {
		if (getColumnCount() > 0)
			getColumnModel().getColumn(0).setMaxWidth(50);
		
		if (getColumnCount() > 3)
			getColumnModel().getColumn(3).setMaxWidth(80);
	}

	
	/**
	 * 
	 * @return {@link DatasetPoolTableModel}
	 */
	public DatasetPoolTableModel getPoolTableModel() {
		return (DatasetPoolTableModel)getModel();
	}
	
	
	/**
	 * 
	 * @return {@link DatasetPool}
	 */
	public DatasetPool getPool() {
		return getPoolTableModel().getPool();
	}
	
	
	/**
	 * 
	 * @return {@link JPopupMenu}
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		final int selectedRow = getSelectedRow();
		final int selectedColumn = getSelectedColumn();
		
		if (isEnabled2()) {
			JMenuItem miRemoveRow = UIUtil.makeMenuItem((String)null, "Remove selected row(s)", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						removeSelectedRows();
					}
				});
			contextMenu.add(miRemoveRow);
		}
		
		contextMenu.addSeparator();
		
		JMenuItem miSave = UIUtil.makeMenuItem((String)null, "Save script", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
		contextMenu.add(miSave);

		contextMenu.addSeparator();

		JMenuItem miCopyURI = UIUtil.makeMenuItem((String)null, "Copy URL", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					int row = getSelectedRow();
					if (row == -1) {
						JOptionPane.showMessageDialog(
							getThis(), 
							"No selected row", 
							"No selected row", 
							JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					int column = getSelectedColumn();
					if (column == -1) {
						JOptionPane.showMessageDialog(
							getThis(), 
							"No selected column", 
							"No selected column", 
							JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					Object value = getThis().getValueAt(row, column);
					if (value != null)
						ClipboardUtil.util.setText(value.toString());
				}
			});
		contextMenu.add(miCopyURI);

		Dataset ds = null;
		if (selectedRow != -1 && selectedColumn != -1) {
			DatasetPoolTableModel model = getPoolTableModel();
			DatasetPool pool = model.getPool();
			DatasetPair pair = pool.get(selectedRow);
			
			if (pair != null) {
				if (selectedColumn == 1) {
					ds = pair.getTraining();
				}
				else if (selectedColumn == 2) {
					ds = pair.getTesting();
				}
				else if (selectedColumn == 3) {
					ds = pair.getWhole();
				}
			}
		}
		
		final Dataset dataset = ds;
		if (dataset != null) {
			contextMenu.addSeparator();
			
			JMenuItem miMetadata = UIUtil.makeMenuItem((String)null, "View metadata", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						
						DatasetMetadataTable.showDlg(getThis(), dataset);
						
					} // end action
					
				});
			
			contextMenu.add(miMetadata);
			
			if (!(dataset instanceof Pointer) ) {
				JMenuItem miData = UIUtil.makeMenuItem((String)null, "View data", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							int confirm = JOptionPane.showConfirmDialog(
									getThis(), 
									"Be careful, out of memory in case of huge dataset", 
									"Out of memory in case of huge dataset", 
									JOptionPane.OK_CANCEL_OPTION, 
									JOptionPane.WARNING_MESSAGE);
							
							if (confirm == JOptionPane.OK_OPTION)
								new DatasetViewer(getThis(), dataset);
						}
					});
				
				contextMenu.add(miData);
			}
		}
		

		if (getRowCount() > 1 && isEnabled2()) {
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
			else if (selectedRow == getRowCount() - 1) {
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

		return contextMenu;
	}
	
	
	/**
	 * 
	 * @return whether remove successfully
	 */
	public boolean removeSelectedRows() {
		int[] idxes = this.getSelectedRows();
		if (idxes == null || idxes.length == 0) {
			JOptionPane.showMessageDialog(
				this, 
				"No selected row", 
				"No selected row", 
				JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		DatasetPoolTableModel model = getPoolTableModel();
		DatasetPool pool = model.getPool();
		pool.remove(idxes);
		model.update(pool);
		setupUI();
		
		return true;
	}
	
	
	/**
	 * 
	 * @param rowIndex
	 */
	private void moveRowFirst(int rowIndex) {
		if (rowIndex == 0 || getRowCount() == 0)
			return;
		
		moveRow(rowIndex, rowIndex, 0);
	}

	
	/**
	 * 
	 * @param rowIndex
	 */
	private void moveRowUp(int rowIndex) {
		if (rowIndex == 0 || getRowCount() == 0)
			return;
		
		moveRow(rowIndex, rowIndex, rowIndex - 1);
	}

	
	/**
	 * 
	 * @param rowIndex
	 */
	private void moveRowDown(int rowIndex) {
		if (rowIndex == getRowCount() - 1 || getRowCount() == 0)
			return;
		
		moveRow(rowIndex, rowIndex, rowIndex + 1);
	}
	
	
	/**
	 * 
	 * @param rowIndex
	 */
	private void moveRowLast(int rowIndex) {
		if (rowIndex == getRowCount() - 1 || getRowCount() == 0)
			return;
		
		moveRow(rowIndex, rowIndex, getRowCount() - 1);
	}

	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param to
	 */
	private void moveRow(int start, int end, int to) {
		DatasetPoolTableModel model = getPoolTableModel();
		DatasetPool pool = model.getPool();
		
		pool.moveRow(start, end, to);
		model.update(pool);
		setupUI();
	}
	
	
	/**
	 * 
	 */
	public void save() {
		JOptionPane.showMessageDialog(this, 
				"Not implement yet", "Not implement yet", JOptionPane.WARNING_MESSAGE);
	}

	
	/**
	 * 
	 * @param enabled
	 */
	public void setEnabled2(boolean enabled) {
		// TODO Auto-generated method stub
		this.enabled = enabled;
	}

	
	/**
	 * 
	 * @return whether enabled
	 */
	public boolean isEnabled2() {
		// TODO Auto-generated method stub
		return enabled;
	}
	
	
}



/**
 * @author Loc Nguyen
 * @version 10.0
 */
class DatasetPoolTableModel extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected DatasetPool pool = null;
	
	
	/**
	 * 
	 */
	public DatasetPoolTableModel() {
		super();
	}
	
	
	/**
	 * 
	 * @param pool
	 */
	public void update(DatasetPool pool) {
		this.pool = pool;
		
		Vector<String> columns = new Vector<String>();
		columns.add("No");
		columns.add("Training set");
		columns.add("Testing set");
		columns.add("Whole set");
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < pool.size(); i++) {
			DatasetPair pair = pool.get(i);
			
			Vector<Object> row = new Vector<Object>();
			
			row.add(i + 1);
			
			Dataset trainingSet = pair.getTraining();
			if (trainingSet != null)
				row.add(trainingSet.getConfig().getUriId().toString());
			else
				row.add("");
			
			Dataset testingSet = pair.getTesting();
			if (testingSet != null)
				row.add(testingSet.getConfig().getUriId().toString());
			else
				row.add("");
			
			if (pair.getWhole() != null)
				row.add(pair.getWhole().getConfig().getUriId().toString());
			else
				row.add("");
			
			data.add(row);
			
		}
		
		this.setDataVector(data, columns);
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/**
	 * 
	 * @return {@link DatasetPool}
	 */
	public DatasetPool getPool() {
		return pool;
	}
	
	
	
}

