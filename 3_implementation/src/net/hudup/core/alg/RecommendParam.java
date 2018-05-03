package net.hudup.core.alg;
import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.UserRating;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * When a recommendation task is performed, a parameter of recommendation needs to be provided as an indicator.
 * As a convention, this class is called {@code recommendation parameter}.
 * For example, such parameter is input parameter of two main methods {@code estimate(...)} and {@code recommend(...)} of {@code Recommender} class to do actual tasks relevant to recommendation.
 * Concretely, method {@code estimate(...)} estimates rating values based on such recommendation parameter.
 * Method {@code recommend(...)} provides a list of items which are recommended to user based on recommendation parameter.
 * Recommendation parameter  contains important information as follows:
 * <ul>
 * <li>Initial rating vector which can be user rating vector or item rating vector, referred by internal variable {@link #ratingVector}.</li>
 * <li>A profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.), referred by internal variable {@link #profile}.</li>
 * <li>A context list relevant to how and where user (s) rates (rate) on item (s), referred by internal variable {@link #contextList}.</li>
 * <li>Additional information, referred by internal variable {@link #extra}, which must be serializable.</li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RecommendParam implements Serializable, Cloneable, TextParsable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Initial rating vector which can be user rating vector or item rating vector.
	 */
	public RatingVector ratingVector = null;
	
	
	/**
	 * A profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.).
	 */
	public Profile profile = null;
	
	
	/**
	 * A context list relevant to how and where user (s) rates (rate) on item (s).
	 */
	public ContextList contextList = null;
	
	
	/**
	 * Additional information.
	 */
	public Serializable extra = null;
	
	
	/**
	 * Constructor with specified rating vector, specified profile, and specified context list.
	 * @param vRating initial rating vector which can be user rating vector or item rating vector.
	 * @param profile a profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.).
	 * @param contextList a context list relevant to how and where user (s) rates (rate) on item (s).
	 */
	public RecommendParam(RatingVector vRating, Profile profile, ContextList contextList) {
		this.ratingVector = vRating;
		this.profile = profile;
		this.contextList = contextList;
	}

	
	/**
	 * Constructor with specified rating vector and specified profile. There is no context list.
	 * @param vRating initial rating vector which can be user rating vector or item rating vector.
	 * @param profile a profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.).
	 */
	public RecommendParam(RatingVector vRating, Profile profile) {
		this(vRating, profile, null);
	}
	
	
	/**
	 * Constructor with specified rating vector and specified context list. There is no profile.
	 * @param vRating initial rating vector which can be user rating vector or item rating vector.
	 * @param contextList a context list relevant to how and where user (s) rates (rate) on item (s).
	 */
	public RecommendParam(RatingVector vRating, ContextList contextList) {
		this(vRating, null, contextList);
	}

	
	/**
	 * Constructor with only specified rating vector.
	 * @param vRating initial rating vector which can be user rating vector or item rating vector.
	 */
	public RecommendParam(RatingVector vRating) {
		this (vRating, (Profile)null);
	}
	
	
	/**
	 * Constructor with user identification (user ID) and context list.
	 * This constructor aims to make a recommendation for a user but not knowing her/his profile and ratings.
	 * @param userId specified user identification (user ID).
	 * @param contextList a context list relevant to how and where user (s) rates (rate) on item (s).
	 */
	public RecommendParam(int userId, ContextList contextList) {
		this (new UserRating(userId), contextList);
	}

	
	/**
	 * Constructor with user identification (user ID).
	 * This constructor aims to make a recommendation for a user but not knowing her/his profile and ratings.
	 * @param userId specified user identification (user ID).
	 */
	public RecommendParam(int userId) {
		this (new UserRating(userId), (ContextList)null);
	}

	
	/**
	 * Default constructor.
	 */
	public RecommendParam() {
		this (null, null, null);
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		
		RecommendParam param = new RecommendParam(
				ratingVector == null ? null : (RatingVector)ratingVector.clone(), 
				profile == null ? null : (Profile)profile.clone(),
				contextList == null ? null : (ContextList)contextList.clone());
		param.extra = (Serializable) Util.clone(this.extra);
		
		return param;
	}


	/**
	 * Clearing this recommendation parameter. All internal variables are null.
	 */
	public void clear() {
		ratingVector = null;
		profile = null;
		contextList = null;
		extra = null;
	}
	
	
	@Deprecated
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		List<TextParsable> list = Util.newList();
		if (ratingVector != null && ratingVector.size() > 0)
			list.add(ratingVector);
		
		if (profile != null && profile.getAttCount() > 0)
			list.add(profile);
		
		if (extra != null && extra instanceof TextParsable)
			list.add((TextParsable)extra);
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				buffer.append(TextParserUtil.NEW_LINE);
			
			TextParsable field = list.get(i);
			buffer.append(field.getClass().getName() + "=" + field.toText());
			
		}
		
		return buffer.toString();
	}


	@Deprecated
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		List<String> textList = TextParserUtil.split(spec, TextParserUtil.NEW_LINE, null);
		if (textList.size() == 0) {
			return;
		}
		
		for (int i = 0; i < textList.size(); i++) {
			String text = textList.get(i);
			int index = text.indexOf("=");
			if (index == -1 || index == text.length() - 1)
				continue;
			
			String className = text.substring(0, index);
			String value = text.substring(index + 1);
			if (className.isEmpty() || value.isEmpty())
				continue;
			
			Object obj = TextParserUtil.parseObjectByClass(value, className);
			if (obj == null)
				continue;
			
			if (obj instanceof RatingVector)
				this.ratingVector = (RatingVector) obj;
			else if (obj instanceof Profile)
				this.profile = (Profile)obj;
			else if (obj instanceof Serializable)
				this.extra = (Serializable)obj;
		}
		
	}
	
	
}
