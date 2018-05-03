package net.hudup.core.client;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.ExternalItemInfo;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.parser.HtmlParsable;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * Recommendlet is a service or a small graphic user interface (GUI) that allows users to see and interact with a list of recommended items.
 * Recommendlet is now deprecated.
 * Recommendlet contains information about server and a list of recommended list.
 * The main method of recommendlet, {@link #toHtml()}, is responsible for generating HTML code which is shown as a small GUI on web page that allows users to see and interact with a list of recommended items.
 * In other words, in current implementation, recommendlet only supports web GUI. It will be replaced by more powerful component in the future. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class Recommendlet implements Serializable, TextParsable, HtmlParsable, Cloneable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Maximum number of items in the recommendation list.
	 */
	protected final static int MAX_RECOMMEND = 10;

	
	/**
	 * External user identifier (user ID) which is stored in outside database different from Hudup database.
	 */
	protected String externalUserId = null;
	
	
	/**
	 * External item identifier (item ID) which is stored in outside database different from Hudup database.
	 */
	protected String externalItemId = null;
	
	
	/**
	 * List of recommended items.
	 */
	protected RatingVector recommendedItems = null;
	
	
	/**
	 * Meta-data of dataset stores essential information about dataset, for example: minimum rating value, maximum rating value, number of items, number of users, etc.
	 */
	protected DatasetMetadata metadata = null;
	
	
	/**
	 * Information about Hudup server specified by class {@link ServerInfo}.
	 */
	protected ServerInfo serverInfo = null;
	
	
	/**
	 * Client stub plays a role of server assistant. It connects with server but it is deployed at client.
	 */
	protected ClientStub clientStub = null;
	
	
	/**
	 * Default constructor.
	 */
	public Recommendlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified external user identifier, external item identifier, list of recommended items, dataset meta-data, server information, and client stub.
	 * @param externalUserId specified external user identifier (user ID) which is stored in outside database different from Hudup database.
	 * @param externalItemId specified external item identifier (item ID) which is stored in outside database different from Hudup database.
	 * @param recommendedItems specified list of recommended items.
	 * @param metadata specified dataset meta-data. Meta-data of dataset stores essential information about dataset, for example: minimum rating value, maximum rating value, number of items, number of users, etc.
	 * @param serverInfo specified server information  which is Information about Hudup server specified by class {@link ServerInfo}.
	 * @param clientStub specified client stub. Client stub plays a role of server assistant. It connects with server but it is deployed at client.
	 */
	public Recommendlet(
			String externalUserId, 
			String externalItemId, 
			RatingVector recommendedItems, 
			DatasetMetadata metadata,
			ServerInfo serverInfo,
			ClientStub clientStub) {
		
		this.externalUserId = externalUserId;
		this.externalItemId = externalItemId;
		this.recommendedItems = recommendedItems;
		this.metadata = metadata;
		this.serverInfo = serverInfo;
		this.clientStub = clientStub;
	}

	
	/**
	 * Setting external user identifier by specified identifier.
	 * Note, external user identifier (user ID) is stored in outside database different from Hudup database.
	 * @param externalUserId specified by identifier.
	 */
	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}
	
	
	/**
	 * Getting external user identifier (user ID).
	 * Note, external user identifier (user ID) is stored in outside database different from Hudup database.
	 * @return external user id.
	 */
	public String getExternalUserId() {
		return externalUserId;
	}
	
	
	/**
	 * Setting external item identifier by specified identifier.
	 * Note, external item identifier (item ID) is stored in outside database different from Hudup database.
	 * @param externalItemId specified identifier.
	 */
	public void setExternalItemId(String externalItemId) {
		this.externalItemId = externalItemId;
	}

	
	/**
	 * Getting external item identifier (item ID).
	 * Note, external item identifier (item ID) is stored in outside database different from Hudup database.
	 * @return external item id
	 */
	public String getExternalItemId() {
		return externalItemId;
	}
	
	
	/**
	 * Setting the list of recommended items.
	 * @param recommendedItems specified list of recommended items, which represented by {@link RatingVector} class.
	 */
	public void setRecommendedItems(RatingVector recommendedItems) {
		this.recommendedItems = recommendedItems;
	}
	
	
	/**
	 * Getting the list of recommended items.
	 * @return list of recommended items, represented by {@link RatingVector}.
	 */
	public RatingVector getRecommendedItems() {
		return recommendedItems;
	}
	
	
	/**
	 * Setting meta-data of server dataset.
	 * Note, meta-data of dataset stores essential information about dataset, for example: minimum rating value, maximum rating value, number of items, number of users, etc.
	 * @param metadata specified dataset meta-data.
	 */
	public void setDatasetMetadata(DatasetMetadata metadata) {
		this.metadata = metadata;
	}
	
	
	/**
	 * Getting meta-data of server dataset.
	 * Note, meta-data of dataset stores essential information about dataset, for example: minimum rating value, maximum rating value, number of items, number of users, etc.
	 * @return meta-data of server dataset.
	 */
	public DatasetMetadata getDatasetMetadata() {
		return metadata;
	}
	
	
	/**
	 * Setting server information by specified information.
	 * Note, information about Hudup server specified by class {@link ServerInfo}.
	 * @param serverInfo specified server information.
	 */
	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}
	
	
	/**
	 * Getting server information.
	 * Note, information about Hudup server specified by class {@link ServerInfo}.
	 * @return server information, specified by {@link ServerInfo}.
	 */
	public ServerInfo getServerInfo() {
		return serverInfo;
	}
	
	
	/**
	 * Setting client stub to this recommendlet.
	 * Client stub plays a role of server assistant. It connects with server but it is deployed at client.
	 * @param clientStub specified client stub specified by {@link ClientStub} class.
	 */
	public void setClientStub(ClientStub clientStub) {
		this.clientStub = clientStub;
	}
	
	
	/**
	 * Testing whether or not this recommendlet is valid.
	 * Note, this recommendlet is valid if all its internal variables (internal information) are not not (or not empty). 
	 * @return whether or not this recommendlet is valid.
	 */
	public boolean validate() {
		return 
			externalUserId != null && !externalUserId.isEmpty() && 
			metadata != null && 
			serverInfo != null && serverInfo.validate() && 
			clientStub != null;
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return 	(externalUserId == null ? 
					TextParserUtil.NULL_STRING : TextParserUtil.encryptReservedChars(externalUserId)
				) + TextParserUtil.NEW_LINE +
				
				(externalItemId == null || externalItemId.isEmpty() ? 
					TextParserUtil.NULL_STRING :  
					TextParserUtil.encryptReservedChars(externalItemId)
				) + TextParserUtil.NEW_LINE  + 
				
				(recommendedItems == null || recommendedItems.size() == 0 ? 
					TextParserUtil.NULL_STRING :  
					recommendedItems.toText()
				) + TextParserUtil.NEW_LINE  + 
				
				(metadata == null ? 
					TextParserUtil.NULL_STRING : metadata.toText()
				) + TextParserUtil.NEW_LINE +
				
				(serverInfo == null ? 
					TextParserUtil.NULL_STRING : serverInfo.toText()
				) + TextParserUtil.NEW_LINE +
				
				(clientStub == null ? 
					TextParserUtil.NULL_STRING :  
					clientStub.toText()
				);
				
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> textList = TextParserUtil.split(spec, TextParserUtil.NEW_LINE, null);
		
		externalUserId = TextParserUtil.decryptReservedChars(textList.get(0));
		
		externalItemId = 
			textList.get(1).equals(TextParserUtil.NULL_STRING) ? null : TextParserUtil.decryptReservedChars(textList.get(1));
		
		if (textList.get(2).equals(TextParserUtil.NULL_STRING))
			recommendedItems = null;
		else
			recommendedItems.parseText(textList.get(2));
		
		if (textList.get(3).equals(TextParserUtil.NULL_STRING))
			metadata = null;
		else
			metadata.parseText(textList.get(3));
		
		if (textList.get(4).equals(TextParserUtil.NULL_STRING))
			serverInfo = null;
		else
			serverInfo.parseText(textList.get(4));
		
		if (textList.get(5).equals(TextParserUtil.NULL_STRING))
			clientStub = null;
		else
			clientStub.parseText(textList.get(5));
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toText();
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new Recommendlet(
				externalUserId == null ? null : externalUserId.toString(), 
				externalItemId == null ? null : externalItemId.toString(), 
				recommendedItems == null ? null : (RatingVector)recommendedItems.clone(), 
				metadata == null ? null : (DatasetMetadata) metadata.clone(), 
				serverInfo == null ? null : (ServerInfo) serverInfo.clone(), 
				clientStub == null ? null : (ClientStub) clientStub.clone());
	}

	
	@Override
	public String toHtml() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<div>");
		
		if (externalUserId != null && !externalUserId.isEmpty() && 
				externalItemId != null && !externalItemId.isEmpty() && 
				clientStub != null && metadata != null) {
			
			buffer.append("<div>");
			int minRating = (int) metadata.minRating;
			int maxRating = (int) metadata.maxRating;
			for (int rating = minRating; rating <= maxRating; rating++) {
				buffer.append("<a href=\"" + 
						 createHttpUpdateRatingRequestText(
								 clientStub.getRegName(), 
								 externalUserId, 
								 externalItemId, 
								 MAX_RECOMMEND, 
								 rating)+ 
						 "\" >");
				buffer.append(
						"<img src=\"" + Constants.Q_DIRECTORY + "/images/rating_" + rating + ".png\" />");
				buffer.append("</a>");
			}
			buffer.append("</div>");
		}
		
		if (recommendedItems != null && recommendedItems.size() > 0 && clientStub != null) {
			
			buffer.append("<div>");
			buffer.append("<table>");
			List<Pair> pairList = Pair.toPairList(recommendedItems);
			Pair.sort(pairList, true);
			buffer.append("<tr>");
			for (Pair pair : pairList) {
				int itemId = pair.key();
				ExternalItemInfo itemInfo = clientStub.getExternalItemInfo(itemId); 
				if (itemInfo == null)
					continue;
				
				buffer.append("<td>");
				buffer.append("<table align=\"center\">");
				
				buffer.append("<tr>");
				buffer.append("<td>");
				buffer.append("<a href = \"" + itemInfo.link + "\">");
				buffer.append("<img src = \"" + itemInfo.largeImagePath + "\" />");
				buffer.append("</a>");
				buffer.append("</td>");
				buffer.append("</tr>");
				
				buffer.append("<tr>");
				buffer.append("<td>");
				buffer.append("<a href = \"" + itemInfo.link + "\">");
				buffer.append(itemInfo.title);
				buffer.append("</a>");
				buffer.append("</td>");
				buffer.append("</tr>");
				
				buffer.append("</table>");
				buffer.append("</td>");
				
				
			}
			buffer.append("</tr>");
			buffer.append("</table>");
			buffer.append("</div>");
		}
		
		
		buffer.append("</div>");
		
		return buffer.toString();
	}
	
	
	/**
	 * This method creates a request for updating the specified rating value that the specified user gives on the specified item.
	 * Such request will be sent to Hudup server via HTTP protocol.
	 * The request is in text form with support of HTML and Javascript.
	 * 
	 * @param regName registered name. Each recommendlet need to register a name with Hudup server.
	 * @param externalUserId external user identifier (user ID) is stored in outside database different from Hudup database.
	 * @param externalItemId external item identifier (item ID) is stored in outside database different from Hudup database.
	 * @param maxRecommend the maximum number of items in recommended list.
	 * @param rating rating value that will be updated or inserted into Hudup database.
	 * @return text-form request for updating the specified rating value that the specified user gives on the specified item.
	 */
	private String createHttpUpdateRatingRequestText(
			String regName, 
			String externalUserId, 
			String externalItemId, 
			int maxRecommend, 
			float rating) {
		
		String host = serverInfo.getHost();
		int port = serverInfo.getPort();
		String params = 
				"'" + host + "', " + 
				port + ", " + 
				"'" + regName + "', " + 
				"'" + externalUserId + "', " + 
				"'" + externalItemId + "', " + 
				maxRecommend + ", " + 
				+ rating;
		return "javascript:hdpAjaxCall(" + params + ")";
	}
	
	
}
