package net.hudup.server;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.client.ClientStub;
import net.hudup.core.data.ExternalItemInfo;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.parser.TextParserUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ClientStubImpl implements ClientStub {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	
	
	/**
	 * 
	 */
	protected String regName = "";
	
	
	/**
	 * 
	 */
	protected Map<Integer, ExternalItemInfo> itemInfo = Util.newMap();
	
	
	/**
	 * 
	 */
	public ClientStubImpl() {
		if (!(itemInfo instanceof Serializable))
			throw new RuntimeException("Invalid field");
	}
	
	
	/**
	 * @param regName
	 * @param itemInfos
	 */
	public ClientStubImpl(String regName, Map<Integer, ExternalItemInfo> itemInfos) {
		if (!(itemInfos instanceof Serializable))
			throw new RuntimeException("Invalid field");
		
		this.regName = regName;
		this.itemInfo = itemInfos;
	}
	
	
	/**
	 * 
	 */
	protected void clear() {
		regName = "";
		itemInfo.clear();
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return TextParserUtil.encryptReservedChars(regName) + TextParserUtil.MAIN_SEP + TextParserUtil.toText(itemInfo, ",");
	}

	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		List<String> textList = TextParserUtil.split(spec, TextParserUtil.MAIN_SEP, null);
		if (textList.size() == 0)
			return;
		
		if (textList.size() < 2) {
			regName = TextParserUtil.decryptReservedChars(textList.get(0));
			return;
		}
		
		String itemSpec = textList.get(1);
		Map<String, String> itemSpecMap = TextParserUtil.parseTextMap(itemSpec, ",");
		Set<String> keys = itemSpecMap.keySet();
		for (String key : keys) {
			try {
				String itemInfoText = itemSpecMap.get(key);
				ExternalItemInfo itemInfo = new ExternalItemInfo();
				itemInfo.parseText(itemInfoText);
				
				int itemId = Integer.parseInt(key);
				
				this.itemInfo.put(itemId, itemInfo);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		
	}

	
	@Override
	public Object clone() {
		Map<Integer, ExternalItemInfo> itemInfos = Util.newMap();
		Set<Integer> itemIds = this.itemInfo.keySet();
		for (int itemId : itemIds) {
			ExternalItemInfo itemInfo = this.itemInfo.get(itemId);
			itemInfos.put(itemId, itemInfo);
		}
		
		return new ClientStubImpl(regName.toString(), itemInfos);
	}
	
	
	
	@Override
	public String toString() {
		return toText();
	}


	@Override
	public String getRegName() {
		// TODO Auto-generated method stub
		return regName;
	}


	@Override
	public ExternalItemInfo getExternalItemInfo(int itemId) {
		// TODO Auto-generated method stub
		return itemInfo.get(itemId);
	}


	/**
	 * 
	 * @param regName
	 * @param itemIds
	 * @return {@link ClientStubImpl}
	 */
	public static ClientStubImpl create(String regName, Collection<Integer> itemIds, ExternalQuery externalQuery) {
		Map<Integer, ExternalItemInfo> itemInfos = Util.newMap();

		for (int itemId : itemIds) {
			try {
				ExternalItemInfo itemInfo = externalQuery != null ? externalQuery.getItemInfo(itemId) : null;
				if (itemInfo == null) { // for testing, should be removed
					itemInfo = new ExternalItemInfo();
					itemInfo.externalId = itemId + "";
					itemInfo.title = "There is no link for recommended item '" + itemId + "'";
				}
				itemInfos.put(itemId, itemInfo);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return new ClientStubImpl(regName, itemInfos);
	}
	
	
	
}
