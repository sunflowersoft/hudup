/**
 * 
 */
package net.hudup.data;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil;


/**
 * This contains a pair of dataset such as training dataset and testing dataset.
 * Note, training dataset is the dataset used to build up some model like knowledge database ({@code KBase}) whereas testing dataset is the dataset used to test or evaluate such model.
 * Both training dataset and testing dataset are extracted from a so-called whole dataset or entire dataset.
 * However it also contains the whole dataset (entire dataset) from which training dataset and testing dataset are extracted.
 * As a convention, it is called {@code dataset pair}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class DatasetPair {
	
	
	/**
	 * Training dataset.
	 */
	protected Dataset training = null;
	
	
	/**
	 * Testing dataset.
	 */
	protected Dataset testing = null;
	
	
	/**
	 * Whole dataset (entire dataset) from which training dataset and testing dataset are extracted.
	 */
	protected Dataset whole = null;

	
	/**
	 * Constructor with specified training dataset, testing dataset, and whole (entire) dataset.
	 * @param training specified training dataset.
	 * @param testing specified testing dataset.
	 * @param whole specified whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public DatasetPair(Dataset training, Dataset testing, Dataset whole) {
		this.training = training;
		this.testing = testing;
		this.whole = whole;
	}
	
	
	/**
	 * Constructor with specified training dataset and testing dataset. The whole (entire) dataset is null.
	 * @param training specified training dataset.
	 * @param testing specified testing dataset.
	 */
	public DatasetPair(Dataset training, Dataset testing) {
		this (training, testing, null);
	}

	
	/**
	 * Get training dataset.
	 * @return training dataset.
	 */
	public Dataset getTraining() {
		return training;
	}
	

	/**
	 * Setting training dataset.
	 * @param training specified training dataset.
	 */
	public void setTraining(Dataset training) {
		this.training = training;
	}
	
	
	/**
	 * Getting testing dataset.
	 * @return testing dataset.
	 */
	public Dataset getTesting() {
		return testing;
	}

	
	/**
	 * Setting testing dataset.
	 * @param testing specified testing dataset.
	 */
	public void setTesting(Dataset testing) {
		this.testing = testing;
	}

	
	/**
	 * Getting the whole (entire) dataset.
	 * @return whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public Dataset getWhole() {
		return whole;
	}
	
	
	/**
	 * Setting the whole (entire) dataset.
	 * @param whole specified whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public void setWhole(Dataset whole) {
		this.whole = whole;
	}

	
	/**
	 * Testing whether this dataset pair is valid. The dataset pair is valid if its both training dataset and testing dataset are not null. 
	 * @return whether this dataset pair is valid.
	 */
	public boolean validate() {
		return training != null && testing != null;
	}
	
	
	/**
	 * Getting the total number of users in training dataset.
	 * @return the total number of users in training dataset
	 */
	public int getTrainingUserNumber() {
		return getUserNumber(training);
	}

	
	/**
	 * Getting the total number of users in testing dataset.
	 * @return the total number of users in testing dataset.
	 */
	public int getTestingUserNumber() {
		return getUserNumber(testing);
	}
	
	
	/**
	 * Getting the total number of users in the whole (entire) dataset.
	 * @return the total number of users in the whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public int getWholeUserNumber() {
		return getUserNumber(whole);
	}


	/**
	 * Getting the total number of items in training dataset.
	 * @return the total number of items in training dataset.
	 */
	public int getTrainingItemNumber() {
		return getItemNumber(training);
	}

	
	/**
	 * Getting the total number of items in testing dataset.
	 * @return the total number of items in testing dataset.
	 */
	public int getTestingItemNumber() {
		return getItemNumber(testing);
	}
	
	
	/**
	 * Getting the total number of items in the whole (entire) dataset.
	 * @return the total number of items in the whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public int getWholeItemNumber() {
		return getItemNumber(whole);
	}

	
	/**
	 * Getting total users in specified dataset.
	 * @param dataset specified dataset.
	 * @return total users in specified dataset.
	 */
	private static int getUserNumber(Dataset dataset) {
		if (dataset == null)
			return 0;
		
		try {
			return dataset.fetchUserIds().getMetadata().getSize();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	
	/**
	 * Getting total items in specified dataset.
	 * @param dataset specified dataset.
	 * @return total items in specified dataset.
	 */
	private static int getItemNumber(Dataset dataset) {
		if (dataset == null)
			return 0;
		
		try {
			return dataset.fetchItemIds().getMetadata().getSize();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	/**
	 * Clearing this dataset pair, which means that training dataset, testing dataset, and the whole (entire) dataset are cleared. 
	 */
	public void clear() {
		if (training != null)
			training.clear();
		training = null;
		
		if (testing != null)
			testing.clear();
		testing = null;
		
		if (whole != null)
			whole.clear();
		whole = null;
	}
	

	/**
	 * Reloading training dataset, testing dataset, and the whole (entire) dataset in this dataset pair.
	 */
	public void reload() {
		if (training != null) {
			training.clear();
			training = DatasetUtil.loadDataset(training.getConfig());
		}
		
		if (testing != null) {
			testing.clear();
			testing = DatasetUtil.loadDataset(testing.getConfig());
		}
		
		if (whole != null) {
			whole.clear();
			whole = DatasetUtil.loadDataset(whole.getConfig());
		}
		
	}

	
}
