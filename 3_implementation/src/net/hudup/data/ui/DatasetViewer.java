package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ctx.ui.CTSviewer;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetViewer extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	protected final static String VIEW_RATING_MATRIX = "Rating matrix";
	
	
	protected final static String VIEW_USER_PROFILE = "User profile";

	
	protected final static String VIEW_USER_EXTERNAL_RECORD = "User external";

	
	protected final static String VIEW_ITEM_PROFILE = "Item profile";

	
	protected final static String VIEW_ITEM_EXTERNAL_RECORD = "Item external";

	
	protected final static String VIEW_CTS = "Context template";

	
	/**
	 * 
	 */
	protected Dataset dataset = null;
	
	
	private RatingMatrixPane paneRatingMatrix = null;
	
	private JPanel paneUserProfile = null;

	private JPanel paneUserExternal = null;
	
	private JPanel paneItemProfile = null;

	private JPanel paneItemExternal = null;

	private JPanel paneCTS = null;
	
	/**
	 * 
	 * @param comp
	 * @param dataset
	 */
	public DatasetViewer(Component comp, Dataset dataset) {
		super(UIUtil.getFrameForComponent(comp), "Dataset viewer", true);
		
		this.dataset = dataset;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 600);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosed(e);
				clear();
			}
			
		});
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		
		JLabel datasetId = new JLabel("Dataset \"" + dataset.getConfig().getUriId() + "\"");
		header.add(datasetId);
		
		final JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		paneRatingMatrix = createRatingMatrixPane();
		paneUserProfile = createUserProfilePane();
		paneUserExternal = createUserExternalPane();
		paneItemProfile = createItemProfilePane();
		paneItemExternal = createItemExternalPane();
		paneCTS = createCTSPane();
		
		final JList<String> unitList = new JList<String>(new String[] {
			VIEW_RATING_MATRIX,
			VIEW_USER_PROFILE,
			VIEW_USER_EXTERNAL_RECORD,
			VIEW_ITEM_PROFILE,
			VIEW_ITEM_EXTERNAL_RECORD,
			VIEW_CTS
		});
		unitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		unitList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				
				String unit = unitList.getSelectedValue();
				body.removeAll();
				
				if (unit.equals(VIEW_RATING_MATRIX)) {
					body.add(paneRatingMatrix, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_USER_PROFILE)) {
					body.add(paneUserProfile, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_USER_EXTERNAL_RECORD)) {
					body.add(paneUserExternal, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_ITEM_PROFILE)) {
					body.add(paneItemProfile, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_ITEM_EXTERNAL_RECORD)) {
					body.add(paneItemExternal, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_CTS)) {
					body.add(paneCTS, BorderLayout.CENTER);
				}
				
				body.updateUI();
			}
			
		});
		add(new JScrollPane(unitList), BorderLayout.WEST);

		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		JLabel summary = new JLabel("Summary");
		footer.add(summary, BorderLayout.NORTH);

		DatasetMetadataTable tblMetadata = new DatasetMetadataTable();
		DatasetMetadata metadata = DatasetMetadata.create(dataset);
		tblMetadata.update(metadata);
		tblMetadata.setPreferredScrollableViewportSize(new Dimension(50, 50));
		footer.add(new JScrollPane(tblMetadata), BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		footer.add(buttons, BorderLayout.SOUTH);
		
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clear();
				
				paneRatingMatrix = createRatingMatrixPane();
				paneUserProfile = createUserProfilePane();
				paneUserExternal = createUserExternalPane();
				paneItemProfile = createItemProfilePane();
				paneItemExternal = createItemExternalPane();
				
				body.removeAll();
				unitList.getSelectionModel().addSelectionInterval(0, 0);
			}
		});
		buttons.add(refresh);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		buttons.add(close);
		
		
		body.removeAll();
		unitList.getSelectionModel().addSelectionInterval(0, 0);
		
		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return rating matrix panel
	 */
	private RatingMatrixPane createRatingMatrixPane() {
		return new RatingMatrixPane(dataset);
	}
	
	
	/**
	 * 
	 * @return user profile panel
	 */
	private JPanel createUserProfilePane() {
		JPanel main = new JPanel(new BorderLayout());
		
		ProfileTable tblProfile = new ProfileTable(true);
		tblProfile.update(dataset);
		main.add(new JScrollPane(tblProfile), BorderLayout.CENTER);

		return main;
	}
	

	/**
	 * 
	 * @return user external record panel
	 */
	private JPanel createUserExternalPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		ExternalRecordTable tblExternalRecord = new ExternalRecordTable(true);
		tblExternalRecord.update(dataset);
		main.add(new JScrollPane(tblExternalRecord), BorderLayout.CENTER);
		
		return main;
	}


	/**
	 * 
	 * @return item profile panel
	 */
	private JPanel createItemProfilePane() {
		JPanel main = new JPanel(new BorderLayout());
		
		ProfileTable tblProfile = new ProfileTable(false);
		tblProfile.update(dataset);
		main.add(new JScrollPane(tblProfile), BorderLayout.CENTER);
		
		return main;
	}
	

	/**
	 * 
	 * @return item external panel
	 */
	private JPanel createItemExternalPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		ExternalRecordTable tblExternalRecord = new ExternalRecordTable(false);
		tblExternalRecord.update(dataset);
		main.add(new JScrollPane(tblExternalRecord), BorderLayout.CENTER);
		
		return main;
	}


	/**
	 * 
	 * @return context template schema viewer
	 */
	private JPanel createCTSPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		CTSviewer ctsViewer = new CTSviewer(dataset.getCTSchema());
		main.add(new JScrollPane(ctsViewer), BorderLayout.CENTER);
		
		return main;
	}
	
	
	private void clear() {
		if (paneRatingMatrix != null)
			paneRatingMatrix.clear();
	}
	
	
}
