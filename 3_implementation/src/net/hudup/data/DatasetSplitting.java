/**
 * 
 */
package net.hudup.data;

import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Pair;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;

/**
 * @author Loc Nguyen
 * @version 10.0
 */
public final class DatasetSplitting {

	
	/**
	 * 
	 * @param datasetUri
	 * @param store
	 * @param testRatio
	 * @param registeredListener
	 * @throws Exception
	 */
	public static void split(
			xURI datasetUri, 
			xURI store, 
			float testRatio, 
			ProgressListener registeredListener) throws Exception {

		int numFold = (int)(1f / testRatio);
		if (numFold == 0)
			return;
		
		List<List<Integer>> foldList = Util.newList();
		for (int i = 0; i < numFold; i++) {
			List<Integer> list = Util.newList();
			foldList.add(list);
		}
		
		UriAdapter adapter = new UriAdapter(datasetUri);
		List<StringBuffer> content = adapter.readLines(datasetUri);
		adapter.close();
		if (content.size() == 0)
			return;
		
		// Removing CSV header
		String firstRecord = content.get(0).toString();
		AttributeList attributes = new AttributeList();
		try {
			attributes.parseText(firstRecord);
		}
		catch (Throwable e) {
			e.printStackTrace();
			attributes.clear();
		}
		if (attributes.size() > 0 && !attributes.get(0).getName().isEmpty()) {
			content.remove(0);
		}
		else
			firstRecord = null;
		
		List<Pair> lineNumber = Util.newList();
		for (int i = 0; i < content.size(); i++)
			lineNumber.add(new Pair(i, 0));
		
		Random rnd = new Random(); 
		while (true) {
			
			for (int i = 0; i < numFold; i++) {
				int n = lineNumber.size();
				if (n > 0) {
					int k = rnd.nextInt(n);
					Pair pair = lineNumber.get(k);
					lineNumber.remove(k);
					
					List<Integer> list = foldList.get(i); 
					list.add(pair.key());
				}
			}
			
			if (lineNumber.size() == 0)
				break;
		}
		

		adapter = new UriAdapter(datasetUri);
		store = (store == null ? 
				adapter.getStoreOf(datasetUri) : datasetUri);
		
		String lastName = datasetUri.getLastName();
		int dot = lastName.lastIndexOf(".");
		if (dot != -1)
			lastName = lastName.substring(0, dot);
		
		PrintWriter[] foldWriter = new PrintWriter[numFold * 2];
		for (int i = 0; i < numFold * 2; i += 2) {
			xURI base = store.concat(lastName + (i/2+1) + ".base");
			xURI test = store.concat(lastName + (i/2+1) + ".test");

			foldWriter[i] = new PrintWriter(adapter.getWriter(base, false));
			foldWriter[i + 1] = new PrintWriter(adapter.getWriter(test, false));
			
			if (firstRecord != null) {
				foldWriter[i].println(firstRecord);
				foldWriter[i + 1].println(firstRecord);
			}
		}

		int progressTotal = content.size();
		int progressStep = 0;
		for (int i = 0; i < content.size(); i++) {
			StringBuffer line = content.get(i);
			
			for (int j = 0; j < numFold; j++) {
				List<Integer> list = foldList.get(j);
				if (list.contains(i))
					foldWriter[2*j + 1].println(line); // test
				else
					foldWriter[2*j].println(line); // base
				
			}
			if (registeredListener != null)
				registeredListener.receiveProgress(
					new ProgressEvent(registeredListener, progressTotal, ++progressStep, "Written: " + line));
		}
		
	
		for (PrintWriter writer :  foldWriter) {
			writer.flush();
			writer.close();
		}
	
		adapter.close();
	}
	
	
	/**
	 * 
	 * @param datasetUri
	 * @param testRatio
	 * @param registeredListener
	 * @throws Exception
	 */
	public static void split(xURI datasetUri, float testRatio, ProgressListener registeredListener) throws Exception {
		split(datasetUri, null, testRatio, registeredListener);
	}
	
	
	
}
