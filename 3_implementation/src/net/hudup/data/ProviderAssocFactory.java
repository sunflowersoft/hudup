package net.hudup.data;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.ProviderAssoc;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProviderAssocFactory {

	
	/**
	 * 
	 * @param config
	 * @return {@link ProviderAssoc}
	 */
	public static ProviderAssoc create(DataConfig config) {
		
		DataDriver dataDriver = DataDriver.create(config.getStoreUri());
		if (dataDriver == null)
			return null;
		else if (dataDriver.isFlatServer())
			return new FlatProviderAssoc(config);
		else if (dataDriver.isDbServer())
			return new DbProviderAssoc(config);
		else 
			return null;
	}
	
	
}
