package net.hudup.core.client;

import java.io.Serializable;

import net.hudup.core.Cloneable;
import net.hudup.core.data.ExternalItemInfo;
import net.hudup.core.parser.TextParsable;



/**
 * As a convention, this class is called {@code client stub}.
 * Client stub plays a role of server assistant. It connects with server but it is deployed at client.
 * The recommendlet represented by {@code Recommendlet} class stores client stub to interact with server.
 * In current implementation, client stub is very simple.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ClientStub extends Serializable, TextParsable, Cloneable {
	
	
	/**
	 * Items represent many kinds of goods such as books, clothes. The Hudup database cannot store all information about various goods.
	 * So the class {@link ExternalItemInfo} called {@code external item information} stores additional information about items that are not stored in Hudup database.
	 * This method retrieves {@code external item information} of an item specified by internal item identifier.
	 * @param itemId internal item identified (stored in Hudup database).
	 * @return {@code external item information} of an item specified by internal item identifier.
	 */
	ExternalItemInfo getExternalItemInfo(int itemId);
	
	
	/**
	 * Each client stub has a name which is registered with the server.
	 * This method returns such registered name.
	 * @return registered name of this client stub.
	 */
	String getRegName();
	
	
}
