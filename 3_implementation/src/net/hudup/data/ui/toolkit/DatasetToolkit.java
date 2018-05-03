package net.hudup.data.ui.toolkit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import net.hudup.core.data.SysConfig;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetToolkit extends JFrame {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected DatasetCreator datasetCreator = null;

	
	/**
	 * 
	 */
	protected DatasetInput datasetInput = null;

	
	/**
	 * 
	 */
	protected DatasetExporter datasetExporter = null;
	
	
	/**
	 * 
	 */
	protected ExternalImporter externalImporter = null;
	
	
	/**
	 * 
	 */
	protected DatasetSplitter datasetSplitter = null;

	
	/**
	 * 
	 */
	public DatasetToolkit() {
		super("Dataset tool");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null);
		
        Image image = UIUtil.getImage("datatool-32x32.png");
        if (image != null)
        	setIconImage(image);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				try {
					datasetCreator.dispose();
					datasetInput.dispose();
					datasetExporter.dispose();
					externalImporter.dispose();
					datasetSplitter.dispose();
				}
				catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
			
		});
		
		Container content = getContentPane();
		content.setLayout(new BorderLayout(2, 2));
		
		JTabbedPane body = new JTabbedPane();
		content.add(body, BorderLayout.CENTER);
		
		datasetCreator = new DatasetCreator(new SysConfig() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
			}
		});
		body.add("Dataset creator", datasetCreator);
		
		datasetInput = new DatasetInput();
		body.add("Dataset input", datasetInput);

		datasetExporter = new DatasetExporter();
		body.add("Dataset exporter", datasetExporter);
		
		externalImporter = new ExternalImporter();
		body.add("External importer", externalImporter);
		
		datasetSplitter = new DatasetSplitter();
		body.add("Dataset splitter", datasetSplitter);
		
		setVisible(true);
	}
	
	
}





