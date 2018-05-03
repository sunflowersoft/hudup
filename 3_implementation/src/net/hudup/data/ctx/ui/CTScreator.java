package net.hudup.data.ctx.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ProviderImpl;
import net.hudup.data.ui.AttributeListTable;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class CTScreator extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected CTStable tblCTS = null;
	
	
	/**
	 * 
	 */
	protected CTSManager ctsManager = null;
	
	
	/**
	 * 
	 * @param comp
	 * @param ctxManager
	 */
	public CTScreator(Component comp, CTSManager ctxManager) {
		super(UIUtil.getFrameForComponent(comp), "Context creator", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		ctsManager = ctxManager;
		
		add(createHeader(), BorderLayout.NORTH);
		add(createBody(), BorderLayout.CENTER);
		add(createFooter(), BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				commit();
				dispose();
			}
			
		});
		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return header panel
	 */
	private JPanel createHeader() {
		JPanel main = new JPanel(new BorderLayout());
		
		return main;
	}
	
	
	/**
	 * 
	 * @return body panel
	 */
	private JPanel createBody() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		main.add(body, BorderLayout.CENTER);
		
		ContextTemplateSchema cts = ctsManager.getCTSchema();
		tblCTS = new CTStable(cts);
		tblCTS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		body.add(new JScrollPane(tblCTS), BorderLayout.CENTER);

		final AttributeListTable attList = new AttributeListTable();
		attList.setEnabled(false);
		attList.setPreferredScrollableViewportSize(new Dimension(100, 100));
		body.add(new JScrollPane(attList), BorderLayout.SOUTH);
		
		tblCTS.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				// TODO Auto-generated method stub
				attList.clear();
				
				TreePath path = e.getPath();
				if (path == null || path.getLastPathComponent() == null || 
						!(path.getLastPathComponent() instanceof ContextTemplate) )
					return;
				
				ContextTemplate template = (ContextTemplate)path.getLastPathComponent();
				attList.set(template.getProfileAttributes());
				
			}
		});
		
		
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		main.add(footer, BorderLayout.SOUTH);
		
		JButton btnAddTopMost = UIUtil.makeIconButton(
			"addtemplate-16x16.png", 
			"add_top_most_template", 
			"Add top most template", 
			"Add top most template", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					tblCTS.addTopMostTemplate();
				}
			});
		btnAddTopMost.setMargin(new Insets(0, 0 , 0, 0));
		footer.add(btnAddTopMost);

		JButton btnRemove = UIUtil.makeIconButton(
			"removetemplate-16x16.png", 
			"remove_template", 
			"Remove template", 
			"Remove template", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					tblCTS.removeSelectedTemplate();
				}
			});
		btnRemove.setMargin(new Insets(0, 0 , 0, 0));
		footer.add(btnRemove);

		JButton btnReload = UIUtil.makeIconButton(
			"reload-16x16.png", 
			"reload", 
			"Reload", 
			"Reload", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					ctsManager.reload();
					tblCTS.reload();
					tblCTS.setModified(false);
					attList.clear();
				}
			});
		btnReload.setMargin(new Insets(0, 0 , 0, 0));
		footer.add(btnReload);

		
		return main;
	}
	
	
	/**
	 * 
	 * @return footer panel
	 */
	private JPanel createFooter() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel footer = new JPanel();
		main.add(footer, BorderLayout.SOUTH);
		
		JButton btnSaveSchema = new JButton("Save schema");
		btnSaveSchema.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				saveSchema();
			}
		});
		footer.add(btnSaveSchema);
		
		
		JButton btnCreateDefault = new JButton("Default schema");
		btnCreateDefault.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				defaultCTSchema();
			}
		});
		footer.add(btnCreateDefault);

		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				commit();
				dispose();
			}
		});
		footer.add(btnClose);

		return main;
	}
	
	
	/**
	 * 
	 */
	private void saveSchema() {
		boolean committed = ctsManager.commitCTSchema();
		if (committed)
			JOptionPane.showMessageDialog(this, "Save schema successfully", "Save schema successfully", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(this, "Save schema failedly", "Save schema failedly", JOptionPane.INFORMATION_MESSAGE);
		
		tblCTS.setModified(!committed);
	}
	
	
	/**
	 * 
	 */
	private void defaultCTSchema() {
		ctsManager.defaultCTSchema();
		tblCTS.reload();
		JOptionPane.showMessageDialog(this, "Create default schema successfully", "Create default schema successfully", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * 
	 */
	private void commit() {
		if (tblCTS.isModified) {
			int response = JOptionPane.showConfirmDialog(
					this, 
					"Schema was modified! Do you want to save it?", 
					"Schema was modified", JOptionPane.YES_NO_OPTION);
			
			if (response == JOptionPane.OK_OPTION)
				saveSchema();
		}
	}
	
	
	/**
	 * 
	 * @return whether schema is committed
	 */
	public boolean isSaved() {
		return !tblCTS.isModified();
	}
	
	
	/**
	 * 
	 * @param comp
	 * @param config
	 */
	public static void create(Component comp, DataConfig config) {
		Provider provider = new ProviderImpl(config);
		if (provider != null)
			new CTScreator(comp, provider.getCTSManager());
		provider.close();
	}
	
	
}
