package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetMetadataTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public DatasetMetadataTable() {
		// TODO Auto-generated constructor stub
		super(new DatasetMetadataTM());
	}

	
	/**
	 * 
	 * @param metadata
	 */
	public void update(DatasetMetadata metadata) {
		getDatasetMetadataTM().update(metadata);
	}

	
	/**
	 * 
	 * @return {@link DatasetMetadataTM}
	 */
	public DatasetMetadataTM getDatasetMetadataTM() {
		return (DatasetMetadataTM)getModel();
	}
	
	
	
	/**
	 * 
	 * @param comp
	 * @param dataset
	 */
	public static void showDlg(Component comp, Dataset dataset) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Dataset metadata", true);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		dlg.setSize(400, 200);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		dlg.setLayout(new BorderLayout());

		JLabel lblUriId = new JLabel("Metadata of dataset \"" + dataset.getConfig().getUriId() + "\"");
		dlg.add(lblUriId, BorderLayout.NORTH);
		
		DatasetMetadataTable tblMetadata = new DatasetMetadataTable();
		DatasetMetadata metadata = DatasetMetadata.create(dataset);
		tblMetadata.update(metadata);
		dlg.add(new JScrollPane(tblMetadata), BorderLayout.CENTER);
		
		dlg.setVisible(true);
	}
	
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class DatasetMetadataTM extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	public DatasetMetadataTM() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * 
	 * @param metrics
	 */
	public void update(DatasetMetadata metadata) {
		if (metadata == null) {
			clear();
			return;
		}
		
		setDataVector(new Object[][] {
				{"Min rating", metadata.minRating},
				{"Max rating", metadata.maxRating},
				{"Number of users", metadata.numberOfUsers},
				{"Number of user rating vectors", metadata.numberOfUserRatings},
				{"Number of items", metadata.numberOfItems},
				{"Number of item rating vectors", metadata.numberOfItemRatings},
			}, 
			createColumns());
		
	}
	
	
	
	/**
	 * 
	 */
	public void clear() {
		
		setDataVector(new Object[][] {}, createColumns());
	}
	
	
	/**
	 * 
	 * @return vector of column names
	 */
	private Object[] createColumns() {
		return new Object[] { "Attribute", "Value"};
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}

