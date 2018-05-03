/**
 * 
 */
package net.hudup.listener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherMetadata;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 * @param <E>
 */
public class MemFetcher<E> implements Fetcher<E> {
	
	
	/**
	 * 
	 */
	private Collection<E> data = null;
	
	
	/**
	 * 
	 */
	private Iterator<E> iterator = null;
	
	
	/**
	 * 
	 */
    private E current = null;

    
    /**
     * 
     */
    private FetcherMetadata metadata = null;
    
    
	/**
	 * 
	 * @param data
	 */
	public MemFetcher(Collection<E> data) {
		update(data);
	}
	
	
	
	/**
	 * 
	 */
	public MemFetcher() {
		List<E> data = Util.newList();
		update(data);
	}
	
	
	/**
	 * 
	 * @param fetcher
	 * @param autoClose
	 */
	public MemFetcher(Fetcher<E> fetcher, boolean autoClose) {
		List<E> data = Util.newList();
		FetcherUtil.fillCollection(data, fetcher, false);
		try {
			if (autoClose)
				fetcher.close();
			else
				fetcher.reset();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		update(data);
	}
	
	
	
	/**
	 * 
	 */
	private void update(Collection<E> data) {
		this.data = data;
		this.iterator = data.iterator();
		this.current = null;
		
		this.metadata = new FetcherMetadata();
		this.metadata.setSize(this.data.size());
	}

	
	
	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		if (iterator.hasNext()) {
			current = iterator.next();
			return true;
		}
		else {
			current = null;
			return false;
		}
	}


	@Override
	public E pick() {
		// TODO Auto-generated method stub
		return current;
	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		current = null;
		iterator = data.iterator();
	}
	
	
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		data = null;
		current = null;
		iterator = null;
		metadata = null;
	}


	@Override
	public FetcherMetadata getMetadata() {
		return metadata;
	}
	
	
	/**
	 * 
	 * @return empty memory fetcher
	 */
	public static <E> MemFetcher<E> createEmpty() {
		Set<E> set = Util.newSet();
		return new MemFetcher<E>(set);
	}



	@Override
	public String toText() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		
		int i = 0;
		for (E el : data) {
			if (i > 0)
				buffer.append("\n");
			
			String row = el.getClass().toString() + TextParserUtil.LINK_SEP;
			if (el instanceof TextParsable)
				buffer.append( row + ((TextParsable)el).toText());
			else
				buffer.append(row + el.toString());
					
			i++;
		}
		
		
		return buffer.toString();
	}



	@SuppressWarnings("unchecked")
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		try {
			List<E> dataList = Util.newList();
			List<String> textList = TextParserUtil.split(spec, "\n", null);
			for (String text : textList) {
				int index = text.indexOf(TextParserUtil.LINK_SEP);
				String className = text.substring(0, index);
				String value = text.substring(index + 1);
				Object element = TextParserUtil.parseObjectByClass(value, className);
				
				if (element != null)
					dataList.add((E)element);
			}
			
			update(dataList);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	
	
}
