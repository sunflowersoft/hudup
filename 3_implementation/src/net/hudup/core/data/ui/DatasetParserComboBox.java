/**
 * 
 */
package net.hudup.core.data.ui;

import java.util.List;

import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.ui.AlgComboBox;

/**
 * This graphic user interface (GUI) component shows a combo box for users to select a dataset parser {@code DatasetParser}.
 * Note, {@code DatasetParser} is also an algorithm {@link Alg} and so {@code DatasetParser} is discovered automatically and stored in an {@link RegisterTable} of {@link PluginStorage}.
 * Therefore, this {@link DatasetParserComboBox} asks {@link PluginStorage} to retrieves all registered {@code DatasetParser} (s) to list.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetParserComboBox extends AlgComboBox {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Name of default dataset parser to be selected at the first time when {@link DatasetParserComboBox} is shown.
	 */
	public final static String DEFAULT_PARSER = "movielens_parser";
	
	
	/**
	 * Default constructor.
	 */
	public DatasetParserComboBox() {
		super();
		
	}
	

	/**
     * Constructor with specified list of algorithms. Such {@link Alg} (s) are really {@code DatasetParser} (s).
     * @param algList specified list of algorithms.
     */
    public DatasetParserComboBox(List<Alg> algList) {
    	super(algList);
    }

    
    @Override
	protected void setDefaultSelected() {
		RegisterTable parserReg = PluginStorage.getParserReg();
		Alg defaultParser = parserReg.query(DEFAULT_PARSER);
		if (defaultParser != null) {
			int idx = findItem(defaultParser.getName());
			if (idx != -1)
				setSelectedIndex(idx);
		}
    }
    
	
}
