package net.hudup.parser;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.ItemRating;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.UserRating;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LineProcessor;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.SnapshotParser;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.MemProfiles;
import net.hudup.data.SnapshotImpl;
import net.hudup.data.ctx.CTSMemMultiProfiles;
import net.hudup.data.ctx.ContextTemplateSchemaImpl;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MovielensParser extends SnapshotParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public MovielensParser() {
		
	}
	
	
	@Override
	public Dataset parse(DataConfig config) {
		
		Map<Integer, RatingVector> userRatingMap = Util.newMap();
		Map<Integer, RatingVector> itemRatingMap = Util.newMap();
		loadRatingMatrix(userRatingMap, itemRatingMap, config);
		
		MemProfiles users = MemProfiles.createEmpty();
		MemProfiles items = MemProfiles.createEmpty();

		Properties props = DatasetUtil2.loadFlatConfig(config);
		String type = props.getProperty("type").toLowerCase();
		if (type.equalsIgnoreCase("100k")) {
			users = load100KUserProfiles(config);
			items = load100KItemProfiles(config);
		}
		else if (type.equalsIgnoreCase("1m")) {
			users = load1MUserProfiles(config);
			items = load1MItemProfiles(config);
		}
		
		if (users.size() == 0)
			users = MemProfiles.createEmpty(DataConfig.USERID_FIELD, Type.integer);
		users.fillAs(userRatingMap.keySet());
		
		if (items.size() == 0)
			items = MemProfiles.createEmpty(DataConfig.ITEMID_FIELD, Type.integer);
		items.fillAs(itemRatingMap.keySet());
		
		float minRating = Float.parseFloat(props.getProperty(DataConfig.MIN_RATING_FIELD));
		float maxRating = Float.parseFloat(props.getProperty(DataConfig.MAX_RATING_FIELD));
		DatasetMetadata metadata = this.config.getMetadata();
		metadata.minRating = minRating;
		metadata.maxRating = maxRating;
		config.setMetadata(metadata);
		
		SnapshotImpl dataset = new SnapshotImpl();
		Map<Integer, ExternalRecord> externalUserRecordMap = Util.newMap();
		Map<Integer, ExternalRecord> externalItemRecordMap = Util.newMap();
		
		ContextTemplateSchemaImpl ctschema = ContextTemplateSchemaImpl.create();
		ctschema.defaultCTSchema();
		config.setParser(this);
		dataset.assign(
				config, 
				externalUserRecordMap, 
				userRatingMap, 
				users, 
				externalItemRecordMap, 
				itemRatingMap, 
				items,
				ctschema,
				CTSMemMultiProfiles.create());
		
		return dataset;
	}

	
	@Override
	public boolean support(DataDriver driver) {
		// TODO Auto-generated method stub
		
		return driver.isFlatServer();
	}


	/**
	 * 
	 * @param outUserMap
	 * @param outItemMap
	 */
	private void loadRatingMatrix(
			final Map<Integer, RatingVector> outUserMap,
			final Map<Integer, RatingVector> outItemMap,
			DataConfig config) {
		
		outUserMap.clear();
		outItemMap.clear();
		
		UriAdapter adapter = null;
		BufferedReader reader = null;
		try {
			adapter = new UriAdapter(config);
			
			xURI store = config.getStoreUri();
			String ratingsUnit = config.getRatingUnit();
			xURI ruUri = store.concat(ratingsUnit);
			
			reader = new BufferedReader(adapter.getReader(ruUri));
			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					// TODO Auto-generated method stub
					int rowId = -1;
					int colId = -1;
					float value = Constants.UNUSED;
					
					List<String> arr = TextParserUtil.split(line, TextParserUtil.DEFAULT_SEP, null);
					try {
						// check row is valid ? column must better than 3
						if(arr.size() >= 3) {
							rowId = Integer.parseInt(arr.get(0));	
							colId = Integer.parseInt(arr.get(1));								
							value = Float.parseFloat(arr.get(2));
						}
						else {
							logger.error("Rating column count is < 3");
						}
						
						if (rowId == -1 || colId == -1)
							return;
					}
					catch (NumberFormatException e) {
						e.printStackTrace();
						logger.error("Processing rating line causes error " + e.getMessage());
						return;
					}
					
					Rating rating = new Rating(value);
					
					RatingVector user = null;
					if (outUserMap.containsKey(rowId))
						user = outUserMap.get(rowId);
					else {
						user = new UserRating(rowId);
						outUserMap.put(rowId, user);
					}
					user.put(colId, rating);
					
					RatingVector item = null;
					if (outItemMap.containsKey(colId))
						item = outItemMap.get(colId);
					else {
						item = new ItemRating(colId);
						outItemMap.put(colId, item);
					}
					item.put(rowId, rating);
					
				}
			});
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (adapter != null)
				adapter.close();
		}
		
		
	}
	
	
	/**
	 * 
	 * @return {@link MemProfiles}
	 */
	private MemProfiles load100KUserProfiles(DataConfig config) {
		UriAdapter adapter = null;
		BufferedReader reader = null;
		MemProfiles memProfiles = MemProfiles.createEmpty();
		
		try {
			adapter = new UriAdapter(config);
			xURI store = config.getStoreUri();
			
			xURI nominalUri = store.concat(config.getNominalUnit());
			Map<String, Attribute> attributeMap = 
					DatasetUtil2.loadNominalAttributes(adapter, nominalUri, config.getUserUnit());
			
			Attribute userid = new Attribute(DataConfig.USERID_FIELD, Type.integer);

			Attribute age = new Attribute("age", Type.integer);
			if (attributeMap.containsKey("age"))
				age = attributeMap.get("age");

			Attribute gender = new Attribute("gender", Type.string);
			if (attributeMap.containsKey("gender"))
				gender = attributeMap.get("gender");

			Attribute occupation = new Attribute("occupation", Type.string);
			if (attributeMap.containsKey("occupation"))
				occupation = attributeMap.get("occupation");
			
			Attribute zipcode = new Attribute("zipcode", Type.string);

			final AttributeList attList = AttributeList.create(
					new Attribute[] {userid, age, gender, occupation, zipcode});
			
			final Map<Integer, Profile> profileMap = Util.newMap();
			
			String usersUnit = config.getUserUnit();
			xURI usersUri = store.concat(usersUnit);
			reader = new BufferedReader(adapter.getReader(usersUri));
			
			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> array = DSUtil.splitAllowEmpty(line, "|", null);
					if (array.size() < 5)
						return;
					
					Profile profile = new Profile(attList);
					profile.setKey(0);
					
					int id = Integer.parseInt(array.get(0));
					profile.setValue(0, id);
					profile.setValue(1, array.get(1));
					profile.setValue(2, array.get(2));
					profile.setValue(3, array.get(3));
					profile.setValue(4, array.get(4));
					
					profileMap.put(id, profile);
				}
			});
			
			memProfiles = MemProfiles.assign(attList, profileMap);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return memProfiles;
		
	}
	
	
	/**
	 * 
	 * @return {@link MemProfiles}
	 */
	private MemProfiles load1MUserProfiles(DataConfig config) {
		UriAdapter adapter = null;
		BufferedReader reader = null;
		MemProfiles memProfiles = MemProfiles.createEmpty();

		try {
			adapter = new UriAdapter(config);
			xURI store = config.getStoreUri();
			
			xURI nominalUri = store.concat(config.getNominalUnit());
			Map<String, Attribute> attributeMap = 
					DatasetUtil2.loadNominalAttributes(adapter, nominalUri, config.getUserUnit());
			
			Attribute userid = new Attribute(DataConfig.USERID_FIELD, Type.integer);

			Attribute age = new Attribute("age", Type.integer);
			if (attributeMap.containsKey("age"))
				age = attributeMap.get("age");

			Attribute gender = new Attribute("gender", Type.string);
			if (attributeMap.containsKey("gender"))
				gender = attributeMap.get("gender");

			Attribute occupation = new Attribute("occupation", Type.string);
			if (attributeMap.containsKey("occupation"))
				occupation = attributeMap.get("occupation");
			
			final Attribute zipcode = new Attribute("zipcode", Type.string);

			final AttributeList attList = AttributeList.create(
					new Attribute[] {userid, age, gender, occupation, zipcode});
			final Map<Integer, Profile> profileMap = Util.newMap();

			String usersUnit = config.getUserUnit();
			xURI usersUri = store.concat(usersUnit);
			reader = new BufferedReader(adapter.getReader(usersUri));
			
			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> array = DSUtil.splitAllowEmpty(line, "::", null);
					if (array.size() < 5)
						return;
					
					Profile profile = new Profile(attList);
					profile.setKey(0);
					
					int id = Integer.parseInt(array.get(0));
					profile.setValue(0, id);
					profile.setValue(1, array.get(2));
					profile.setValue(2, array.get(1));
					profile.setValue(3, Integer.parseInt(array.get(3)));
					profile.setValue(4, array.get(4));
					
					profileMap.put(id, profile);
					
				}
			});
			
			memProfiles = MemProfiles.assign(attList, profileMap);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return memProfiles;
		
	}

	
	/**
	 * 
	 * @return {@link MemProfiles}
	 */
	private MemProfiles load100KItemProfiles(DataConfig config) {
		UriAdapter adapter = null;
		BufferedReader reader = null;
		MemProfiles memProfiles = MemProfiles.createEmpty();
		
		try {
			
			Attribute itemid = new Attribute(DataConfig.ITEMID_FIELD, Type.integer);
			
			Attribute title = new Attribute("title", Type.string);
			
			Attribute release_date = new Attribute("release_date", Type.date);
			
			Attribute video_release_date = new Attribute("video_release_date", Type.date);

			Attribute imdb_url = new Attribute("imdb_url", Type.string);
			
			Attribute unknown = new Attribute("unknown", Type.bit);
			
			Attribute action = new Attribute("action", Type.bit);
			
			Attribute adventure = new Attribute("adventure", Type.bit);
			
			Attribute animation = new Attribute("animation", Type.bit);
			
			Attribute children = new Attribute("children", Type.bit);
			
			Attribute comedy = new Attribute("comedy", Type.bit);
			
			Attribute crime = new Attribute("crime", Type.bit);
			
			Attribute documentary = new Attribute("documentary", Type.bit);

			Attribute drama = new Attribute("drama", Type.bit);

			Attribute fantasy = new Attribute("fantasy", Type.bit);
			
			Attribute film_noir = new Attribute("film_noir", Type.bit);

			Attribute horror = new Attribute("horror", Type.bit);
			
			Attribute musical = new Attribute("musical", Type.bit);
			
			Attribute mystery = new Attribute("mystery", Type.bit);
			
			Attribute romance = new Attribute("romance", Type.bit);

			Attribute sci_fi = new Attribute("sci_fi", Type.bit);
			
			Attribute thriller = new Attribute("thriller", Type.bit);

			Attribute war = new Attribute("war", Type.bit);
			
			Attribute western = new Attribute("western", Type.bit);

			final AttributeList attList = AttributeList.create(new Attribute[] {
					itemid,
					title, release_date, video_release_date, imdb_url,
					unknown, action, adventure, animation, children, 
					comedy, crime, documentary, drama, fantasy, film_noir,
					horror, musical, mystery, romance, sci_fi, thriller, 
					war, western});
			final Map<Integer, Profile> profileMap = Util.newMap();
			
			adapter = new UriAdapter(config);
			
			xURI store = config.getStoreUri();
			String itemsUnit = config.getItemUnit();
			xURI itemsUri = store.concat(itemsUnit);
			reader = new BufferedReader(adapter.getReader(itemsUri));

			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> array = DSUtil.splitAllowEmpty(line, "|", null);
					if (array.size() < 24)
						return;
					
					Profile profile = new Profile(attList);
					profile.setKey(0);
					
					SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
					
					int id = Integer.parseInt(array.get(0));
					profile.setValue(0, id);
					profile.setValue(1, array.get(1));
					profile.setValue(2, array.get(2), df);
					profile.setValue(3, array.get(3), df);
					profile.setValue(4, array.get(4));
					profile.setValue(5, array.get(5));
					profile.setValue(6, array.get(6));
					profile.setValue(7, array.get(7));
					profile.setValue(8, array.get(8));
					profile.setValue(9, array.get(9));
					profile.setValue(10, array.get(10));
					profile.setValue(11, array.get(11));
					profile.setValue(12, array.get(12));
					profile.setValue(13, array.get(13));
					profile.setValue(14, array.get(14));
					profile.setValue(15, array.get(15));
					profile.setValue(16, array.get(16));
					profile.setValue(17, array.get(17));
					profile.setValue(18, array.get(18));
					profile.setValue(19, array.get(19));
					profile.setValue(20, array.get(20));
					profile.setValue(21, array.get(21));
					profile.setValue(22, array.get(22));
					profile.setValue(23, array.get(23));

					profileMap.put(id, profile);
					
				}
			});
			
			memProfiles = MemProfiles.assign(attList, profileMap);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return memProfiles;
		
	}

	
	/**
	 * 
	 * @return {@link MemProfiles}
	 */
	private MemProfiles load1MItemProfiles(DataConfig config) {
		UriAdapter adapter = null;
		BufferedReader reader = null;
		MemProfiles memProfiles = MemProfiles.createEmpty();

		try {
			
			final List<String> movies = Util.newList();
			movies.addAll(Arrays.asList(new String[] {
				"Action", "Adventure", "Animation", "Children's", "Comedy", 
				"Crime", "Documentary", "Drama", "Fantasy", "Film-Noir", 
				"Horror", "Musical", "Mystery", "Romance", "Sci-Fi", 
				"Thriller", "War", "Western" }));

			Attribute itemid = new Attribute(DataConfig.ITEMID_FIELD, Type.integer);

			Attribute title = new Attribute("title", Type.string);
			
			Attribute release_date = new Attribute("release_date", Type.date);
			
			Attribute video_release_date = new Attribute("video_release_date", Type.date);

			Attribute imdb_url = new Attribute("imdb_url", Type.string);
			
			Attribute unknown = new Attribute("unknown", Type.bit);

			Attribute action = new Attribute("action", Type.bit);
			
			Attribute adventure = new Attribute("adventure", Type.bit);
			
			Attribute animation = new Attribute("animation", Type.bit);
			
			Attribute children = new Attribute("children", Type.bit);
			
			Attribute comedy = new Attribute("comedy", Type.bit);
			
			Attribute crime = new Attribute("crime", Type.bit);
			
			Attribute documentary = new Attribute("documentary", Type.bit);

			Attribute drama = new Attribute("drama", Type.bit);

			Attribute fantasy = new Attribute("fantasy", Type.bit);
			
			Attribute film_noir = new Attribute("film_noir", Type.bit);

			Attribute horror = new Attribute("horror", Type.bit);
			
			Attribute musical = new Attribute("musical", Type.bit);
			
			Attribute mystery = new Attribute("mystery", Type.bit);
			
			Attribute romance = new Attribute("romance", Type.bit);

			Attribute sci_fi = new Attribute("sci_fi", Type.bit);
			
			Attribute thriller = new Attribute("thriller", Type.bit);

			Attribute war = new Attribute("war", Type.bit);
			
			Attribute western = new Attribute("western", Type.bit);
			
			final AttributeList attList = AttributeList.create(new Attribute[] {
					itemid,
					title, release_date, video_release_date, imdb_url,
					unknown, action, adventure, animation, children, 
					comedy, crime, documentary, drama, fantasy, film_noir,
					horror, musical, mystery, romance, sci_fi, thriller, 
					war, western});
			
			final Map<Integer, Profile> profileMap = Util.newMap();
			
			adapter = new UriAdapter(config);
			
			xURI store = config.getStoreUri();
			String itemsUnit = config.getItemUnit();
			xURI itemsUri = store.concat(itemsUnit);
			reader = new BufferedReader(adapter.getReader(itemsUri));

			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> array = DSUtil.splitAllowEmpty(line, "::", null);
					if (array.size() < 3)
						return;
					
					Profile profile = new Profile(attList);
					profile.setKey(0);
					
					int id = Integer.parseInt(array.get(0));
					profile.setValue(0, id);
					profile.setValue(1, array.get(1));
					
					profile.setMissing(2);
					profile.setMissing(3);
					profile.setMissing(4);
					
					if (array.get(2).isEmpty())
						profile.setValue(5, 1);
					
					List<String> idxsList = TextParserUtil.split(array.get(2), "[\\|]", null);
					List<Integer> idxList = Util.newList();
					for (String idxs : idxsList) {
						int idx = movies.indexOf(idxs);
						if (idx != -1)
							idxList.add(idx);
					}
					
					for (int idx : idxList) {
						profile.setValue(idx + 6, 1);
					}

					profileMap.put(id, profile);
					
				}
			});
			
			memProfiles = MemProfiles.assign(attList, profileMap);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return memProfiles;
		
	}

	
	@Override
	public String getName() {
		return "movielens_parser";
	}
	

	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MovielensParser();
	}

	
	
	
}
