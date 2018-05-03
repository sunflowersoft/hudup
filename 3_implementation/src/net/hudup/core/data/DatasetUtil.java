package net.hudup.core.data;

import net.hudup.core.parser.DatasetParser;


/**
 * This utility class, called {@code dataset utility}, provides utility methods processing on {@link Dataset} such as loading dataset and saving dataset.
 * In current implementation, {@code dataset utility} gives very few utility methods.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class DatasetUtil {
	
	
	/**
	 * Loading (reading, parsing) dataset from specified configuration.
	 * Actually, this method calls the method {@link DatasetParser#parse(DataConfig)} of dataset parser.
	 * Note, {@code DatasetParser} is one of main interface responsible for reading and parsing coarse data such as CSV file, Excel file, database table into {@link Dataset}.
	 * 
	 * @param config specified configuration. 
	 * @return {@link Dataset} loaded from specified configuration.
	 */
	public final static Dataset loadDataset(DataConfig config) {
		try {
			DatasetParser parser = config.getParser();
			if (parser == null)
				return null;
			
			if (config.getDataDriverName() != null)
				parser.getConfig().setDataDriverName(config.getDataDriverName());
			
			return parser.parse(config);
		}
		catch (Throwable e) {
			e.printStackTrace();
			
		}
		return null;
	}

	
}
