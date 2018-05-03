package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.ui.AlgChooser;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.DraggableTabbedPane;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface (GUI) component shows a dialog for users to configure many algorithms.
 * It can be called the composite algorithm configurator.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class AlgConfigDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The pane has many tabs. Each tab shows the configuration of an algorithm.
	 */
	protected DraggableTabbedPane body = null;
	
	
	/**
	 * List of configured algorithms as a result.
	 */
	protected AlgList resultAlgList = null;

	
	/**
	 * URI pointing to where to store configurations of algorithms.
	 */
	protected xURI storeUri = null;
	
	
	/**
	 * Whether there is an algorithm configuration that was modified. 
	 */
	protected boolean modified = false;
	
	
	/**
	 * The tab as {@link JPanel} shows the configuration of an algorithm.
	 * This tab is called algorithm configuration tab or tab of algorithm configuration.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	protected class AlgTab extends JPanel {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;
		
		
		/**
		 * The algorithm to be configure.
		 */
		protected Alg alg = null;
		
		
		/**
		 * The table as {@link PropTable} to show the configuration of the algorithm {@link #alg}.
		 */
		protected PropTable propTable = null;
		
		
		/**
		 * Constructor with specified algorithm.
		 * @param alg specified algorithm.
		 */
		public AlgTab(Alg alg) {
			super();
			setLayout(new BorderLayout());
			
			this.alg = alg;
			this.setName(alg.getName());
		
			this.propTable = new PropTable();
			this.propTable.update(alg.getConfig());
			this.propTable.setName(alg.getName());
			
			add(new JScrollPane(this.propTable), BorderLayout.CENTER);
		}
		
		
		/**
		 * Getting the internal algorithm to be configured.
		 * @return internal algorithm to be configured.
		 */
		public Alg getAlg() {
			return alg;
		}
		
		
		/**
		 * Getting the table as {@link PropTable} to show the configuration of the algorithm {@link #alg}.
		 * @return current table as {@link PropTable} to show the configuration of the algorithm {@link #alg}.
		 */
		public PropTable getPropTable() {
			return propTable;
		}
		
		
	}
	
	
	/**
	 * Constructor of this dialog with parent component, default list of algorithms, and URI pointing to where to store configurations of algorithms.
	 * @param comp parent component.
	 * @param defaultAlgList default list of algorithms.
	 * @param storeUri URI pointing to where to store configurations of algorithms.
	 * @param key specified key to shown in the title of this dialog.
	 */
	public AlgConfigDlg(Component comp, AlgList defaultAlgList, xURI storeUri, String key) {
		super(UIUtil.getFrameForComponent(comp), "Configure key " + key, true);
		this.resultAlgList = null;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				if (!isModified())
					return;
				
				int confirm = JOptionPane.showConfirmDialog(
						getThis(), 
						"Attributes are modified. Do you want to apply them?", 
						"Attributes are modified", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				
				if (confirm == JOptionPane.YES_OPTION)
					onApply();
			}
			
		});
		
		body = new DraggableTabbedPane();
		add(body, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnAdd = new JButton("Add algorithm");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addAlgConfigTab();
			}
		});
		footer.add(btnAdd);

		JButton btnRemove = new JButton("Remove algorithm");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeCurrentAlgConfigTab();
			}
		});
		footer.add(btnRemove);

		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				onApply();
			}
			
		});
		footer.add(btnApply);

		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				onOk();
			}
			
		});
		footer.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onCancel();
			}
		});
		footer.add(btnCancel);

		this.storeUri = storeUri;
		init(defaultAlgList);
		
		setVisible(true);
	}
	
	
	/**
	 * Getting this {@link AlgConfigDlg}.
	 * @return this {@link AlgConfigDlg}.
	 */
	private AlgConfigDlg getThis() {
		return this;
	}
	
	
	/**
	 * Initializing this {@link AlgConfigDlg} with default list of algorithms.
	 * @param defaultAlgList default list of algorithms.
	 */
	protected void init(AlgList defaultAlgList) {
		
		body.removeAll();
		for (int i = 0; i < defaultAlgList.size(); i++) {
			Alg alg = defaultAlgList.get(i);
			addAlgConfigTab(alg);
		}
		
		modified = false;
	}
	
	
	/**
	 * Testing whether composite configuration is modified.
	 * @return whether composite configuration is modified.
	 */
	private boolean isModified() {
		boolean totalModified = modified || body.isModified();
		for (int i = 0; i < getAlgConfigTabCount(); i++) {
			AlgTab algTab = getAlgConfigTab(i);
			totalModified = totalModified || algTab.getPropTable().isModified();
		}
		
		return totalModified;
	}
	
	
	/**
	 * Extracting the list of configured algorithms from tabs.
	 * @return extracted algorithm list.
	 */
	protected AlgList extractAlgList() {
		AlgList algList = new AlgList();
		for (int i = 0; i < getAlgConfigTabCount(); i++) {
			AlgTab algTab = getAlgConfigTab(i);
			algList.add(algTab.getAlg());
		}
		
		return algList;
	}
	
	
	/**
	 * This is event-driven method which responds the action user clicks on &quot;Apply&quot; button.
	 * All changes in configurations of algorithms are effective.
	 * The algorithms that are configured are returned as the result.
	 * 
	 */
	protected void onApply() {
		if (!isModified())
			return;
		
		body.setModified(false);
		for (int i = 0; i < getAlgConfigTabCount(); i++) {
			AlgTab algTab = getAlgConfigTab(i);
			if (algTab.getPropTable().isModified())
				algTab.getPropTable().apply();
		}
		modified = false;
		resultAlgList = extractAlgList();
		
		JOptionPane.showMessageDialog(
				this, 
				"Apply successfully", 
				"Apply successfully", 
				JOptionPane.INFORMATION_MESSAGE);
	}

	
	/**
	 * This is event-driven method which responds the action user clicks on &quot;OK&quot; button.
	 * This method calls {@link #onApply()} method to apply all changes in configurations of algorithms and then closes this {@link AlgConfigDlg} with OK code.
	 */
	protected void onOk() {
		onApply();
		
		dispose();
	}
	
	
	/**
	 * This is event-driven method which responds the action user clicks on &quot;Cancel&quot; button.
	 * The method closes this {@link AlgConfigDlg} with &quot;Cancel&quot; code.
	 * 
	 */
	protected void onCancel() {
		if (isModified()) {
			int confirm = JOptionPane.showConfirmDialog(
					this, 
					"Attributes are modified. Do you want to apply them?", 
					"Attributes are modified", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			
			if (confirm == JOptionPane.YES_OPTION)
				onApply();
		}

		dispose();
	}

	
	/**
	 * Getting the count of algorithm configuration tabs.
	 * @return count of algorithm configuration tabs.
	 */
	protected int getAlgConfigTabCount() {
		return body.getTabCount();
	}
	
	
	/**
	 * Getting the algorithm configuration tab at specified index.
	 * @param tabIndex specified index.
	 * @return algorithm configuration tab at specified index.
	 */
	protected AlgTab getAlgConfigTab(int tabIndex) {
		return (AlgTab) body.getComponent(tabIndex);
	}
	
	
	/**
	 * Getting the current algorithm configuration tab which is the selected one.
	 * @return current algorithm configuration tab which is the selected one.
	 */
	protected AlgTab getCurrentAlgConfigTab() {
		int selected = body.getSelectedIndex();
		if (selected != -1)
			return getAlgConfigTab(selected);
		else
			return null;
	}

	
	/**
	 * Getting the algorithm configuration tab by algorithm class name or algorithm name.
	 * @param algNameOrClassName algorithm class name or algorithm name.
	 * @return algorithm configuration tab by algorithm class name or algorithm name.
	 */
	protected AlgTab getAlgConfigTab(String algNameOrClassName) {
		int found = indexOfAlgConfigTab(algNameOrClassName);
		if (found != -1)
			return getAlgConfigTab(found);
		else
			return null;
	}

	
	/**
	 * Adding a configuration tab for the specified algorithm.
	 * @param alg specified algorithm.
	 * @return whether adding algorithm configuration successfully.
	 */
	protected boolean addAlgConfigTab(Alg alg) {
		if (indexOfAlgConfigTab(alg.getName()) != -1)
			return false;
		
		body.addTab(alg.getName(), new AlgTab(alg));
		modified = true;

		return true;
	}
	
	
	/**
	 * This method shows a dialog for user to choose an algorithm and then adds a configuration tab for such chosen algorithm.
	 * @return whether adding algorithm configuration successfully.
	 */
	protected boolean addAlgConfigTab() {
		
		Alg defaultAlg = getAlgConfigTabCount() > 0 ? getAlgConfigTab(getAlgConfigTabCount() - 1).getAlg() : null;
		List<Alg> removedAlgList = Util.newList();
		
		for (int i = 0; i < getAlgConfigTabCount(); i++) {
			AlgTab algTab = getAlgConfigTab(i);
			removedAlgList.add(algTab.getAlg());
		}
		
		
		AlgChooser chooser = new AlgChooser(this, defaultAlg, null, removedAlgList);
		Alg alg = chooser.getResult();
		if (alg == null)
			return false;
		else {
			xURI subStore = storeUri == null ? null : storeUri.concat(alg.getName());
			if (subStore != null)
				alg.getConfig().setStoreUri(subStore);
			return addAlgConfigTab(alg);
		}
	}
	
	
	/**
	 * Finding the index of the configuration tab of a algorithm having specified name.
	 * @param algName specified name.
	 * @return index of the configuration tab of a algorithm having specified name.
	 */
	protected int indexOfAlgConfigTab(String algName) {
		for (int i = 0; i < getAlgConfigTabCount(); i++) {
			AlgTab algTab = getAlgConfigTab(i);
			if (algTab.getAlg().getName().equals(algName))
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Removing the algorithm configuration tab by algorithm class name or algorithm name.
	 * @param algNameOrClassName algorithm class name or algorithm name.
	 */
	protected void removeAlgConfigTab(String algNameOrClassName) {
		
		int found = indexOfAlgConfigTab(algNameOrClassName);
		if (found != -1) {
			body.removeTabAt(found);
			modified = true;
		}
	}
	
	
	/**
	 * Removing the current configuration tab which is the selected one.
	 */
	protected void removeCurrentAlgConfigTab() {
		int selected = body.getSelectedIndex();
		if (selected != -1) {
			body.removeTabAt(selected);
			modified = true;
		}
	}

	
	/**
	 * Getting the list of configured algorithm as a result.
	 * @return resulted list of configured algorithm.
	 */
	public AlgList getResult() {
		return resultAlgList;
	}
	
	
}


