package net.hudup.data;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.PropList;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BatchScript {

	/**
	 * 
	 */
	protected DatasetPool pool = new DatasetPool();
	
	
	/**
	 * 
	 */
	protected List<String> algNameList = Util.newList();
	
	
	/**
	 * 
	 * @param pool
	 * @param algNameList
	 */
	private BatchScript(DatasetPool pool, List<String> algNameList) {
		this.pool = pool;
		this.algNameList = algNameList;
	}
	
	
	/**
	 * 
	 * @param writer
	 * @return whether save successfully
	 */
	public boolean save(Writer writer) {
		PropList propList = new PropList();
		
		propList.put("algorithms", TextParserUtil.toText(algNameList, ","));
		
		
		List<String> trainings = Util.newList();
		List<String> testings = Util.newList();
		List<String> wholes = Util.newList();
		
		for (int i = 0; i < pool.size(); i++) {
			DatasetPair pair = pool.get(i);
			Dataset trainingSet = pair.getTraining();
			Dataset testingSet = pair.getTesting();
			
			if (trainingSet == null)
				continue;
			if (testingSet == null)
				continue;
			
			trainings.add(
					trainingSet.getConfig().getRatingUri().toString() + TextParserUtil.EXTRA_SEP +
					trainingSet.getConfig().getParser().getName()  + 
					(trainingSet.getConfig().getDataDriverName() != null ? TextParserUtil.EXTRA_SEP + trainingSet.getConfig().getDataDriverName() : "")
				);
			testings.add(
					testingSet.getConfig().getRatingUri().toString() + TextParserUtil.EXTRA_SEP +
					testingSet.getConfig().getParser().getName() +
					(testingSet.getConfig().getDataDriverName() != null ? TextParserUtil.EXTRA_SEP + testingSet.getConfig().getDataDriverName() : "")
				);
			
			Dataset wholeSet = pair.getWhole();
			if (wholeSet != null)
				wholes.add(
					wholeSet.getConfig().getRatingUri().toString() + TextParserUtil.EXTRA_SEP +
					wholeSet.getConfig().getParser().getName() +
					(wholeSet.getConfig().getDataDriverName() != null ? TextParserUtil.EXTRA_SEP + wholeSet.getConfig().getDataDriverName() : "")
				);
		}
		
		if (trainings.size() == 0 || trainings.size() != testings.size())
			return false;
		if (wholes.size() != 0 && wholes.size() != trainings.size())
			return false;
		
		propList.put("trainingsets", TextParserUtil.toText(trainings, ","));
		propList.put("testingsets", TextParserUtil.toText(testings, ","));
		
		if (wholes.size() > 0)
			propList.put("wholesets", TextParserUtil.toText(wholes, ","));
		
		return propList.saveProperties(writer);
		
	}
	
	
	/**
	 * 
	 * @param reader
	 * @return {@link BatchScript}
	 */
	public static BatchScript parse(Reader reader) {
		PropList propList = new PropList();
		propList.loadProperties(reader);
		
		if (!propList.containsKey("algorithms") ||
			!propList.containsKey("trainingsets") ||
			!propList.containsKey("testingsets")) {
			
			return null;
		}
		
		String algNames = propList.getAsString("algorithms");
		List<String> algNameList = TextParserUtil.split(algNames, ",", null);
		if (algNameList.size() == 0)
			return null;
		
		DatasetPool pool = readPropList(propList);
		if (pool == null)
			return null;
		
		return new BatchScript(pool, algNameList);
	}
	
	
	/**
	 * 
	 * @return {@link DatasetPool}
	 */
	public DatasetPool getPool() {
		return pool;
	}
	

	/**
	 * 
	 * @return list of algorithm name list
	 */
	public List<String> getAlgNameList() {
		return algNameList;
	}
	
	
	/**
	 * 
	 * @param pool
	 * @param algNameList
	 * @return {@link BatchScript}
	 */
	public static BatchScript assign(DatasetPool pool, List<String> algNameList) {
		return new BatchScript(pool, algNameList);
	}
	

	/**
	 * 
	 * @param propList
	 * @return {@link DatasetPool}
	 */
    private static DatasetPool readPropList(PropList propList) {
		
		if (!propList.containsKey("trainingsets") ||
			!propList.containsKey("testingsets")) {
			
			return null;
		}
		
		String trainings = propList.getAsString("trainingsets");
		List<String> trainingList = TextParserUtil.split(trainings, ",", null);
		if (trainingList.size() == 0)
			return null;
		
		String testings = propList.getAsString("testingsets");
		List<String> testingList = TextParserUtil.split(testings, ",", null);
		if (testingList.size() == 0 || testingList.size() != trainingList.size())
			return null;
		
		List<String> wholeList = Util.newList();
		if (propList.containsKey("wholesets")) {
			String wholes = propList.getAsString("wholesets");
			wholeList = TextParserUtil.split(wholes, ",", null);
			if (wholeList.size() == 0 || wholeList.size() != testingList.size())
				return null;
		}

		
		DatasetPool pool = new DatasetPool();
		
    	for (int i = 0; i < trainingList.size(); i++) {
    		
    		String training = trainingList.get(i);
    		String testing = testingList.get(i);
    		
    		Dataset trainingSet = null;
    		Dataset testingSet = null;
    		Dataset wholeSet = null;
    		
    		try {
    			List<String> trainingParts = TextParserUtil.split(training, TextParserUtil.EXTRA_SEP, null);
    			if (trainingParts.size() < 2)
    				continue;
    			
    			xURI trainingUri = xURI.create(trainingParts.get(0)); 
    			UriAdapter trainingAdapter = new UriAdapter(trainingUri);
    			DataConfig trainingCfg = trainingAdapter.makeFlatDataConfig(trainingUri);
    			trainingAdapter.close();
    			trainingCfg.setParser(trainingParts.get(1));
    			if (trainingParts.size() > 2)
    				trainingCfg.setDataDriverName(trainingParts.get(2));
	    		trainingSet = DatasetUtil.loadDataset(trainingCfg);
	    		if (trainingSet == null)
	    			continue;
	    		
    			List<String> testingParts = TextParserUtil.split(testing, TextParserUtil.EXTRA_SEP, null);
    			if (testingParts.size() < 2)
    				continue;
    			xURI testingUri = xURI.create(testingParts.get(0)); 
    			UriAdapter testingAdapter = new UriAdapter(testingUri);
    			DataConfig testingCfg = testingAdapter.makeFlatDataConfig(testingUri);
    			testingAdapter.close();
    			testingCfg.setParser(testingParts.get(1));
    			if (testingParts.size() > 2)
    				testingCfg.setDataDriverName(testingParts.get(2));
	    		testingSet = DatasetUtil.loadDataset(testingCfg);
	    		if (testingSet == null)
	    			continue;
	    		
	    		if (wholeList.size() > 0) {
	    			String whole = wholeList.get(i);
	    			
	    			List<String> wholeParts = TextParserUtil.split(whole, TextParserUtil.EXTRA_SEP, null);
	    			if (wholeParts.size() < 2)
	    				continue;
	        		
	    			xURI wholeUri = xURI.create(wholeParts.get(0)); 
	    			UriAdapter wholeAdapter = new UriAdapter(wholeUri);
	    			DataConfig wholeCfg = wholeAdapter.makeFlatDataConfig(wholeUri);
	    			wholeAdapter.close();
	    			DatasetPair pair = pool.findWholeSet(wholeCfg.getUriId());
	    			
	    			if (pair != null)
	    				wholeSet = pair.getWhole();
	    			else {
	        			wholeCfg.setParser(wholeParts.get(1));
	        			if (wholeParts.size() > 2)
	        				wholeCfg.setDataDriverName(wholeParts.get(2));
	    				wholeSet = DatasetUtil.loadDataset(wholeCfg);
	    			}
	    			
	    			
	    		} // if (wholeList.size() > 0)
	    		
	    		DatasetPair pair = new DatasetPair(
						trainingSet, 
				
						testingSet, 
				
						wholeSet == null ? null : wholeSet); 
		
	    		pool.add(pair);
	    		
	    		
    		}
    		catch (Throwable e) {
    			
    			e.printStackTrace();
    		}
    		
    	}
    	
    	if (pool.size() == 0)
    		return null;
    	
    	return pool;
    }
	
    
}
