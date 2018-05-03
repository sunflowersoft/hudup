/**
 * 
 */
package net.hudup.data.bit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.parser.MovielensParser;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BitDataTest {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(BitDataTest.class);

	
	/**
	 * 
	 */
	private Dataset dataset = null;
	
	
	/**
	 * 
	 */
	@Test
	@Deprecated
	public void testCreateBitData() throws RemoteException {
		BitData bitData = BitData.create(dataset);
		Dataset newDataset = BitDataUtil.transform(bitData);
		
		Fetcher<RatingVector> sessions = dataset.fetchUserRatings();
		Fetcher<RatingVector> newSessions = newDataset.fetchUserRatings();
		assertEquals("Different the number of sessions", 
				newSessions.getMetadata().getSize(), 
				newSessions.getMetadata().getSize());
		//
		while (sessions.next()) {
			RatingVector session = sessions.pick();
			if (session == null)
				continue;
			
			RatingVector newSession = newDataset.getUserRating(session.id());
			assertNotNull("New session is null", newSession);
			assertTrue("New session " + newSession.id() + " differs from old session " + session.id(), 
					checkValueEquals(session, newSession));
		}
		sessions.close();
		newSessions.close();
		
		Fetcher<RatingVector> items = dataset.fetchItemRatings();
		Fetcher<RatingVector> newItems = newDataset.fetchItemRatings();
		assertEquals("Different the number of items", 
				newItems.getMetadata().getSize(), 
				items.getMetadata().getSize());
		//
		while (items.next()) {
			RatingVector item = items.pick();
			if (item == null)
				continue;
			
			RatingVector newItem = newDataset.getItemRating(item.id());
			assertNotNull("New item is null", newItem);
			assertTrue("New item " + newItem.id() + " differs from old item " + item.id(), 
					checkValueEquals(item, newItem));
		}
		items.close();
		newItems.close();
	}
	

	/**
	 * 
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		MovielensParser parser = new MovielensParser();
		
		xURI ratingUri = xURI.create("datasets/rating001.base");
		UriAdapter adapter = new UriAdapter(ratingUri); 
		DataConfig config = adapter.makeFlatDataConfig(ratingUri);
		adapter.close();
		
		dataset = parser.parse(config);
	}
	
	
	/**
	 * 
	 */
	@After
	public void tearDown() {
		if (dataset != null)
			dataset.clear();
		dataset = null;
	}
	
	
	/**
	 * This method is used to check RatingVetor
	 * @param v1
	 * @param v2
	 * @return whether two vectors are equal
	 */
	private static boolean checkValueEquals(RatingVector v1, RatingVector v2) {
		if (v1.id() != v2.id()) return false;
		
		Set<Integer> columnIds1 = v1.fieldIds();
		Set<Integer> columnIds2 = v2.fieldIds();
		if (columnIds1.size() != columnIds2.size())
			return false;
		
		for (int columnId : columnIds1) {
			if(!columnIds2.contains(columnId))
				return false;
			
			float value1 = v1.get(columnId).value;
			float value2 = v2.get(columnId).value;
			if (value1 != value2)
				return false;
		}
		return true;
	}

	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(BitDataTest.class);
		for (Failure failure : result.getFailures()) {
			logger.error(failure.toString());
		}
	}
	
	
	
}
