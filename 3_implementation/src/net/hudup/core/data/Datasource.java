package net.hudup.core.data;

import java.io.Serializable;

import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.xURI;


/**
 * Data source represented by {@link Datasource} class is a special class used to point to a dataset. Of course it contains a URI of such dataset. Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
 * As usual and for convenience, data source also contains dataset. We can considered data source as a wrapper of dataset with additional information about dataset.
 * It is possible that there are many data sources referring to the same dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate(note = "Testing whether variable dataset is serializable")
public class Datasource implements AutoCloseable, Serializable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The internal dataset.
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * URI of the internal dataset referred by variable {@link #dataset}.
	 */
	protected xURI uri = null;
	
	
	/**
	 * If {@code true} the dataset referred by variable {@link #dataset} is closed after this data source is closed (by calling {@link #close()} method).
	 * Otherwise, such dataset is not closed after this data source is closed (by calling {@link #close()} method).
	 */
	protected boolean autoClose = false;
	
	
	/**
	 * Default constructor.
	 */
	public Datasource() {
		
	}
	
	
	/**
	 * This method is used to connect this data source with the specified dataset.
	 * After this method is called, this data source is initialized; concretely, the internal variable {@link #dataset} is assigned by the specified dataset and
	 * the internal variable {@link #uri} is assigned by the URI of specified dataset and
	 * the internal variable {@link #autoClose} is assigned by whether the specified dataset is exclusive.
	 * <br>
	 * <code>
	 * this.dataset = dataset;<br>
	 * this.uri = dataset.getConfig().toUri();<br>
	 * this.autoClose = dataset.isExclusive();
	 * </code>
	 * @param dataset specified dataset.
	 */
	public void connect(Dataset dataset) {
		close();
		
		xURI uri = dataset.getConfig().toUri();
		if (dataset == null || uri == null)
			return;
		
		this.dataset = dataset;
		this.uri = dataset.getConfig().toUri();
		this.autoClose = dataset.isExclusive();
		
	}
	
	
	/**
	 * This method is used to connect this data source with the URI of specified dataset.
	 * After this method is called, this data source is initialized; concretely, the internal variable {@link #dataset} is loaded from the specified URI and
	 * the internal variable {@link #uri} is assigned by the specified URI and
	 * the internal variable {@link #autoClose} is {@code true}.
	 * @param uri URI of specified dataset.
	 */
	public void connect(xURI uri) {
		close();
		
		DataConfig config = DataConfig.create(uri);
		if (config == null)
			return;
		
		Dataset dataset = DatasetUtil.loadDataset(config);
		if (dataset != null) {
			this.dataset = dataset;
			this.uri = uri;
			this.autoClose = true;
		}
	}
	
	
	/**
	 * Getting the internal dataset of this data source.
	 * @return internal dataset of this data source.
	 */
	public Dataset getDataset() {
		return dataset;
	}
	
	
	/**
	 * Getting the URI of this data source.
	 * @return URI of this data source.
	 */
	public xURI getUri() {
		return uri;
	}
	
	
	/**
	 * Checking whether data source is valid. This data source is valid if its dataset is opened and its URI is not {@code null}.
	 * @return whether data source is valid.
	 */
	public boolean isValid() {
		return dataset != null && uri != null;
	}
	
	
	@Override
	public void close() {
		try {
			if (autoClose && dataset != null)
				dataset.clear();
			
			dataset = null;
			autoClose = false;
			uri = null;
		}
		catch (Throwable e) {
			e.printStackTrace();
			
			dataset = null;
			autoClose = false;
			uri = null;
		}
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}
