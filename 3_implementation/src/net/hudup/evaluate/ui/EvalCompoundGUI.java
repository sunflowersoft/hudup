package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ui.SysConfigDlgExt;
import net.hudup.evaluate.EvaluatorConfig;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class EvalCompoundGUI extends JFrame implements PluginChangedListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	private EvaluatorConfig thisConfig = null;
	
	
	/**
	 * 
	 */
	private JTabbedPane body = null;
	
	
	/**
	 * 
	 */
	private EvaluateGUI evaluateGUI = null;
	
	
	/**
	 * 
	 */
	private BatchEvaluateGUI batchEvaluateGUI = null;
	
	
	/**
	 * 
	 */
	public EvalCompoundGUI(boolean estimateMode) {
		this(new EvaluatorConfig(xURI.create(EvaluatorConfig.evalConfig)), estimateMode);
	}
	
	
	/**
	 * 
	 * @param config
	 * @param estimateMode
	 */
	public EvalCompoundGUI(EvaluatorConfig config, boolean estimateMode) {
		super("Evaluator GUI");
		this.thisConfig = config;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				
				evaluateGUI.dispose();
				batchEvaluateGUI.dispose();
				
				thisConfig.save();
			}

			
		});
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null)
        	setIconImage(image);
		
		setSize(800, 600);
		setLocationRelativeTo(null);
	    setJMenuBar(createMenuBar());
	    
		Container content = getContentPane();
		content.setLayout(new BorderLayout(2, 2));
		
		body = new JTabbedPane();
		content.add(body, BorderLayout.CENTER);
		
		evaluateGUI = new EvaluateGUI(thisConfig, estimateMode);
		body.add(getMessage("evaluate"), evaluateGUI);
		
		batchEvaluateGUI = new BatchEvaluateGUI(thisConfig, estimateMode);
		body.add(getMessage("evaluate_batch"), batchEvaluateGUI);
		
		setTitle(getMessage("evaluator"));
		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return this GUI
	 */
	private EvalCompoundGUI getThis() {
		return this;
	}
	
	
	/**
	 * 
	 * @param key
	 * @return message according to key
	 */
	protected String getMessage(String key) {
		return I18nUtil.getMessage(thisConfig, key);
	}

	
	/**
	 * 
	 * @return {@link JMenuBar}
	 */
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTools = new JMenu(getMessage("tool"));
		mnBar.add(mnTools);
		
		JMenuItem mniSysConfig = new JMenuItem(
			new AbstractAction(getMessage("system_configure")) {

				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				
				@Override
				public void actionPerformed(ActionEvent e) {
					sysConfig();
				}
			
			});
		
		mnTools.add(mniSysConfig);

		JMenuItem mniSwitchEvaluator = new JMenuItem(
			new AbstractAction(getMessage("switch_evaluator")) {

				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				
				@Override
				public void actionPerformed(ActionEvent e) {
					switchEvaluator();
				}
			
			});
		mnTools.add(mniSwitchEvaluator);


		JMenu mnHelp = new JMenu(getMessage("help"));
		mnBar.add(mnHelp);
		
		JMenuItem mniHelpContent = new JMenuItem(
			new AbstractAction(getMessage("help_content")) {

				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				
				@Override
				public void actionPerformed(ActionEvent e) {
					new HelpContent(getThis());
				}
			
			});
		
		mnHelp.add(mniHelpContent);

		
		return mnBar;
	}

	
	/**
	 * 
	 * @return {@link JPopupMenu}
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem switchEvaluator = UIUtil.makeMenuItem((String)null, getMessage("switch_evaluator"), 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					switchEvaluator();
				}
			});
		
		contextMenu.add(switchEvaluator);
		
		return contextMenu;
	}

	
	/**
	 * 
	 */
	private void switchEvaluator() {
		if (!isIdle()) {
			int confirm = JOptionPane.showConfirmDialog(
					UIUtil.getFrameForComponent(getThis()), 
					"Evaluation task will be terminated if switching evaluator.\n" +
						"Are you sure to switch evaluator?", 
					"Switching evaluator", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
				
			if (confirm != JOptionPane.YES_OPTION)
				return;
		}
	
		AbstractEvaluateGUI comp = (AbstractEvaluateGUI) body.getSelectedComponent();
		if (comp == evaluateGUI)
			evaluateGUI.switchEvaluator();
		else if (comp == batchEvaluateGUI)
			batchEvaluateGUI.switchEvaluator();
	}
	
	
	/**
	 * 
	 */
	private void sysConfig() {
		SysConfigDlgExt cfg = new SysConfigDlgExt(this, getMessage("system_configure"), this);
		cfg.update(thisConfig);
		cfg.setVisible(true);
	}


	@Override
	public void pluginChanged(PluginChangedEvent evt) {
		// TODO Auto-generated method stub
		evaluateGUI.pluginChanged();
		batchEvaluateGUI.pluginChanged();
	}


	@Override
	public boolean isIdle() {
		// TODO Auto-generated method stub
		return !evaluateGUI.getEvaluator().isStarted() && !batchEvaluateGUI.getEvaluator().isStarted();
	}
	
	
	

}


