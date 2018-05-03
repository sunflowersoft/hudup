package net.hudup.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;

import net.hudup.core.RegisterTableList.RegisterTableItem;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For example, a register table manages recommendation algorithms (recommenders) whereas another manages metrics for evaluating recommenders.
 * This {@link PluginStorageManifest} which is the graphic user interface (GUI) allows users to manage {@link PluginStorage}.
 * Every time {@link PluginStorage} was changed, an event {@link PluginChangedEvent} is issued and dispatched to a listener {@link PluginChangedListener}.
 * Later on, {@link PluginChangedListener} can do some tasks in its method {@link PluginChangedListener#pluginChanged(PluginChangedEvent)}.
 * Please pay attention that such {@link PluginChangedListener} must be registered with {@link PluginStorageManifest} before to receive {@link PluginChangedEvent}.
 * <br> 
 * As a convention, this class is called {@code plug-in storage manifest}, which is really a sortable table represented by {@link SortableTable}.
 * Note, {@link SortableTable} written by David Gilbert and Nobuo Tamemasa allows users to create and show a table which can be sorted its rows according its columns.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PluginStorageManifest extends SortableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal list of registered {@link PluginChangedListener} (s).
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
	/**
	 * Default constructor.
	 */
	public PluginStorageManifest() {
		super(new RegisterTM());
		update();
	}
	
	
	/**
	 * Updating {@link PluginStorageManifest} according to {@link RegisterTM}.
	 */
	private void update() {
		getRegisterTM().update();
		init();
		
		if (getColumnModel().getColumnCount() > 3) {
			getColumnModel().getColumn(3).setMaxWidth(0);
			getColumnModel().getColumn(3).setMinWidth(0);
			getColumnModel().getColumn(3).setPreferredWidth(0);
		}
	}

	
	/**
	 * This public method is called from outside in order to update {@link PluginStorageManifest} according to {@link RegisterTM}.
	 * It really calls method {@link #update()}.
	 * @return true if {@link PluginStorageManifest} is updated successfully from outside.
	 */
	public boolean apply() {
		boolean idle = isListenersIdle();
		
		if (!idle)
			return false;

		
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			
			Alg alg = (Alg) getValueAt(i, 3);
			
			boolean registered = (Boolean)getValueAt(i, 4);
			RegisterTable table = PluginStorage.lookupTable(alg.getClass());
			if (table == null)
				continue;
			
			if (registered) {
				if (!table.contains(alg.getName())) {
					table.register(alg);
					nextUpdateList.remove(alg);
				}
			}
			else if(table.contains(alg.getName())) {
				table.unregister(alg.getName());
				nextUpdateList.add(alg);
			}
		}
		
		update();
		firePluginChangedEvent(new PluginChangedEvent(this));
		
		return true;
	}
	

	/**
	 * Selecting or unselecting all rows according the specified input parameter {@code selected}.
	 * @param selected if {@code true} then all rows are selected. Otherwise, all rows are unselected. 
	 */
	public void selectAll(boolean selected) {
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			
			setValueAt(selected, i, 4);
		}
	}

	
	/**
	 * Getting the model of this {@link PluginStorageManifest}.
	 * @return register table model {@link RegisterTM}
	 */
	public RegisterTM getRegisterTM() {
		return (RegisterTM)getModel();
	}
	
	
	/**
	 * Adding the specified listener to the end of list of listeners, which means that such listener is registered.
	 * 
	 * @param listener specified {@link PluginChangedListener} that is registered.
	 * 
	 */
	public void addPluginChangedListener(PluginChangedListener listener) {
		synchronized (listenerList) {
			listenerList.add(PluginChangedListener.class, listener);
		}
    }

    
	/**
	 * Remove the specified listener from the list of listener
	 * 
	 * @param listener {@link PluginChangedListener} that is unregistered.
	 */
    public void removePluginChangedListener(PluginChangedListener listener) {
		synchronized (listenerList) {
			listenerList.remove(PluginChangedListener.class, listener);
		}
    }
	
    
    /**
     * Return an array of registered {@link PluginChangedListener} (s).
     * 
     * @return array of registered {@link PluginChangedListener} (s).
     * 
     */
    protected PluginChangedListener[] getPluginChangedListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(PluginChangedListener.class);
		}
    }

    
    /**
     * Dispatching {@link PluginChangedEvent} event to registered {@link PluginChangedListener} (s) after {@link PluginStorageManifest} was changed.
     * @param evt {@link PluginChangedEvent} event is issued to registered {@link PluginChangedListener} (s) after {@link PluginStorageManifest} was changed.
     */
    protected void firePluginChangedEvent(PluginChangedEvent evt) {
		PluginChangedListener[] listeners = getPluginChangedListeners();
		
		for (PluginChangedListener listener : listeners) {
			try {
				listener.pluginChanged(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
    }

    
    /**
     * Testing whether a registered {@link PluginChangedListener} is idle.
     * @return whether a registered {@link PluginChangedListener} is idle. Note, there can be many registered {@link PluginChangedListener} (s). 
     */
    protected boolean isListenersIdle() {
		PluginChangedListener[] listeners = getPluginChangedListeners();
		
		for (PluginChangedListener listener : listeners) {
			try {
				if (!listener.isIdle())
					return false;
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return true;
	
    }

    
    /**
	 * Creating {@link JPanel} that contains {@link PluginStorageManifest}.
	 * @param listener {@link PluginChangedListener} to receive {@link PluginChangedEvent} if {@link PluginStorageManifest} is changed.
	 * @return {@link JPanel} that contains {@link PluginStorageManifest}.
	 */
	public static JPanel createPane(PluginChangedListener listener) {
		
		JPanel result = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		result.add(body, BorderLayout.CENTER);
		
		final PluginStorageManifest tblRegister = new PluginStorageManifest();
		if (listener != null)
			tblRegister.addPluginChangedListener(listener);
		body.add(new JScrollPane(tblRegister), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		result.add(footer, BorderLayout.SOUTH);

		JButton apply = new JButton("Apply");
		footer.add(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (tblRegister.apply()) {
					
					JOptionPane.showMessageDialog(
							UIUtil.getFrameForComponent(tblRegister), 
							"Apply successfully. Algorithms are registered", 
							"Apply successfully", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(
							UIUtil.getFrameForComponent(tblRegister), 
							"Listeners not idle", 
							"Listeners not idle", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		});
		
		
		JButton selectAll = new JButton("Select all");
		footer.add(selectAll);
		selectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tblRegister.selectAll(true);
			}
		});

		
		JButton unselectAll = new JButton("Unselect all");
		footer.add(unselectAll);
		unselectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tblRegister.selectAll(false);
			}
		});

		
		return result;
	}
	
	
	/**
	 * Showing a dialog containing {@link PluginStorageManifest}.
	 * @param comp parent component.
	 * @param listener {@link PluginChangedListener} to receive {@link PluginChangedEvent} if {@link PluginStorageManifest} is changed. 
	 * @param modal whether or not the dialog is modal. The modal dialog will block user inputs. Please see {@link JDialog} for more details. 
	 */
	public static void showDlg(Component comp, PluginChangedListener listener, boolean modal) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Plugin storage", modal);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		dlg.add(createPane(listener), BorderLayout.CENTER);
		
		dlg.setVisible(true);
	}
	
	
}


/**
 * This is table model of {@link PluginStorageManifest} because {@link PluginStorageManifest} is itself a table. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RegisterTM extends SortableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public RegisterTM() {
		super();
		update();
	}
	
	
	/**
	 * Updating this model.
	 */
	public void update() {
		Vector<Vector<Object>> data = Util.newVector();
		
		updateReg(data);
		
		updateNextUpdateList(data);
		
		setDataVector(data, toColumns());
	}
	
	
	/**
	 * Updating this model with a matrix of objects.
	 * @param data specified data as a matrix of objects.
	 */
	private void updateReg(Vector<Vector<Object>> data) {
		RegisterTableList list = PluginStorage.getRegisterTableList();
		for (int i = 0; i < list.size(); i++) {
			RegisterTableItem item = list.get(i);
			updateEachReg(data, item.getRegisterTable());
			
		}
		
	}
	
	
	/**
	 * Creating data from the specified register table for this model.
	 * @param data output parameter that is filled from the specified register table.
	 * @param regTable specified register table.
	 */
	private void updateEachReg(Vector<Vector<Object>> data, RegisterTable regTable) {
		
		List<Alg> algList = regTable.getAlgList();
		for (Alg alg : algList) {
			Vector<Object> row = Util.newVector();
			
			row.add(PluginStorage.lookupAlgTypeName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
			row.add(true);
			
			data.add(row);
		}
		
	}
	
	
	/**
	 * Creating data from next-update algorithms for this model.
	 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * List of next-update algorithms is managed by {@link PluginStorage}.
	 * 
	 * @param data output parameter that is filled from next-update algorithms.
	 */
	private void updateNextUpdateList(Vector<Vector<Object>> data) {
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (int i = 0; i < nextUpdateList.size(); i++) {
			Alg alg = nextUpdateList.get(i);
			Vector<Object> row = Util.newVector();
			
			row.add(PluginStorage.lookupAlgTypeName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
			row.add(false);
			
			data.add(row);
			
		}
	}
	
	
	/**
	 * Creating a string vector for columns of this model.
	 * @return a string vector for columns of this model, specified by {@link Vector} of string.
	 */
	protected Vector<String> toColumns() {
		Vector<String> columns = Util.newVector();
		columns.add("Type");
		columns.add("Name");
		columns.add("Java class");
		columns.add("Object");
		columns.add("Registered");
		
		return columns;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 4)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	
	@Override
	public boolean isSortable(int column) {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return column == 4;
	}
	
	
}

