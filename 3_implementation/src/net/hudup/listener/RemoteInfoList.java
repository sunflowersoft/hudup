package net.hudup.listener;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfoList implements Serializable, Cloneable, TextParsable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected List<RemoteInfo> rInfoList = Util.newList();
	
	
	/**
	 * 
	 */
	public RemoteInfoList() {
		
	}
	
	
	/**
	 * 
	 * @param index
	 * @return {@link RemoteInfo}
	 */
	public RemoteInfo get(int index) {
		return rInfoList.get(index);
	}
	
	
	/**
	 * 
	 * @param rInfo
	 * @return whether add {@link RemoteInfo} successfully
	 */
	public boolean add(RemoteInfo rInfo) {
		int index = indexOf(rInfo.host, rInfo.port);
		if (index != -1)
			return false;
		return rInfoList.add(rInfo);
	}
	
	
	/**
	 * 
	 * @param index
	 * @return removed {@link RemoteInfo}
	 */
	public RemoteInfo remove(int index) {
		return rInfoList.remove(index);
	}
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @return removed {@link RemoteInfo}
	 */
	public RemoteInfo remove(String host, int port) {
		int index = indexOf(host, port);
		if (index == -1)
			return null;
		else
			return remove(index);
	}
	
	
	/**
	 * 
	 * @param rInfo
	 * @return whether remove successfully
	 */
	public boolean remove(RemoteInfo rInfo) {
		return rInfoList.remove(rInfo);
	}
	
	
	/**
	 * 
	 * @param index
	 * @param rInfo
	 */
	public RemoteInfo set(int index, RemoteInfo rInfo) {
		int idx = indexOf(rInfo.host, rInfo.port);
		if (idx != -1 && idx != index)
			return null;
		else
			return rInfoList.set(index, rInfo);
	}
	
	
	/**
	 * 
	 * @param rInfo
	 * @return set {@link RemoteInfo}
	 */
	public RemoteInfo set(RemoteInfo rInfo) {
		int idx = indexOf(rInfo.host, rInfo.port);
		if (idx == -1)
			return null;
		else
			return set(idx, rInfo);
	}
	
	
	
	/**
	 * 
	 * @return size of this list
	 */
	public int size() {
		return rInfoList.size();
	}
	
	
	/**
	 * Clearing this list
	 */
	public void clear() {
		rInfoList.clear();
	}
	
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @return index of host
	 */
	public int indexOf(String host, int port) {
		for (int i = 0; i < rInfoList.size(); i++) {
			RemoteInfo rInfo = rInfoList.get(i);
			if (rInfo.host.compareToIgnoreCase(host) == 0 && rInfo.port == port)
				return i;
		}
		
		return -1;
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return TextParserUtil.toText(rInfoList, ",");
	}

	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		RemoteInfoList list = new RemoteInfoList();
		for (RemoteInfo rInfo : rInfoList) {
			list.add((RemoteInfo)rInfo.clone());
		}
		
		return list;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toText();
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		if (spec == null || spec.isEmpty())
			return;
		
		List<RemoteInfo> rInfoList = TextParserUtil.parseTextParsableList(
				spec, RemoteInfo.class, ",");
		for (RemoteInfo rInfo : rInfoList)
			this.rInfoList.add(rInfo);
	}

	
	
	
}
