/**
 * 
 */
package net.hudup.core.alg;

import java.awt.Component;

import javax.swing.JOptionPane;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Datasource;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriFilter;
import net.hudup.core.logistic.xURI;

import org.apache.log4j.Logger;


/**
 * This abstract implements partially {@link KBase} interface by adding a configuration {@link #config} to configure it and
 * a data source {@link #datasource} to point to where to locate the dataset from which this knowledge base is learned (trained).
 * Moreover, this class also implements methods {@link #load()} and {@link #save()} to load (read) and save (write) knowledge base.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class KBaseAbstract implements KBase {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(KBase.class);

	
	/**
	 * The configuration used to customize this knowledge base.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * The data source points to where to locate the dataset from which this knowledge base is learned (trained).
	 * Please see {@link Datasource} for more details.
	 */
	protected Datasource datasource = new Datasource();
	
	
	/**
	 * Default constructor.
	 */
	protected KBaseAbstract() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
		xURI store = config.getStoreUri();
		xURI cfgUri = store.concat(KBASE_CONFIG);
		config.load(cfgUri);
		
		datasource.close();
		String datasourceUriText = config.getAsString(DATASOURCE_URI);
		if (datasourceUriText == null || datasourceUriText.isEmpty())
			return;
		xURI datasourceUri = xURI.create(datasourceUriText);
		if (datasourceUri != null)
			datasource.connect(datasourceUri);
		
	}


	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		config.setMetadata(dataset.getConfig().getMetadata());
		config.put(KBASE_NAME, getName());
		
		config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		config.addReadOnly(DataConfig.MAX_RATING_FIELD);
		config.addReadOnly(KBASE_NAME);
		
		datasource.close();
		if (dataset instanceof Pointer)
			return;
		
		datasource.connect(dataset);
		if (datasource.isValid())
			config.put(DATASOURCE_URI, datasource.getUri().toString());
		else
			datasource.close();
			
	}


	@Override
	public void save() {
		export(config);
	}
	
	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub

		UriAdapter adapter = new UriAdapter(storeConfig);

		xURI store = storeConfig.getStoreUri();
		adapter.clearContent(store, new UriFilter() {
			
			@Override
			public boolean accept(xURI uri) {
				// TODO Auto-generated method stub
				return uri.getLastName().startsWith(getName());
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "No description";
			}
			
			
		});
		adapter.create(store, true);
		adapter.close();
		
		xURI cfgUri = store.concat(KBASE_CONFIG);
		config.save(cfgUri);
		
	}


	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
		xURI store = config.getStoreUri();
		if (store != null) {
			UriAdapter adapter = new UriAdapter(config);
			adapter.clearContent(store, new UriFilter() {
				
				@Override
				public boolean accept(xURI uri) {
					// TODO Auto-generated method stub
					return uri.getLastName().startsWith(getName());
				}
				
				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return "No description";
				}
				
			});
			adapter.close();
		}
		
		close();
	}


	@Override
	public void close() {
		// TODO Auto-generated method stub
		datasource.close();
		
		config.setMetadata(null);
		config.remove(KBASE_NAME);
		config.remove(DATASOURCE_URI);
		
		config.removeReadOnly(DataConfig.MIN_RATING_FIELD);
		config.removeReadOnly(DataConfig.MAX_RATING_FIELD);
		config.removeReadOnly(KBASE_NAME);
	}


	@Override
	public DataConfig getConfig() {
		
		return config;
	}
	
	
	@Override
	public void setConfig(DataConfig config) {
		this.config = config;
	}


	@Override
	public Datasource getDatasource() {
		// TODO Auto-generated method stub
		return datasource;
	}


	@Override
	public void view(Component comp) {
		// TODO Auto-generated method stub
		
		JOptionPane.showMessageDialog(
				comp, 
				"Not implemented yet", 
				"Not implemented yet", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	
}
