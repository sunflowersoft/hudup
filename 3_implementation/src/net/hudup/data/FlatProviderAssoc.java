package net.hudup.data;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Condition;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderAssocAbstract;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FlatProviderAssoc extends ProviderAssocAbstract {

	
	/**
	 * 
	 */
	public final static char DELIMITER = ',';
	
	
	/**
	 * 
	 */
	protected UriAdapter adapter = null;
	
	
	/**
	 * 
	 * @param config
	 */
	public FlatProviderAssoc(DataConfig config) {
		super(config);
		this.adapter = new UriAdapter(config); 
	}
	

	@Override
	public boolean createUnit(String unit, AttributeList attList) {
		Writer writer = null;
		CsvWriter csvWriter = null;
		boolean result = true;
		
		try {
			xURI unitURI = getUnitUri(unit);
			if (adapter.exists(unitURI))
				adapter.delete(unitURI, null);
			adapter.create(unitURI, false);
			
			writer = adapter.getWriter(unitURI, false);
			csvWriter = new CsvWriter(writer, DELIMITER);
			writeHeader(csvWriter, attList);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			
			try {
				if (csvWriter != null)
					csvWriter.close();
				
				if (writer != null)
					writer.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	@Override
	public UnitList getUnitList() {
		UnitList tblList = new UnitList();
		try {
			UnitList defaultUnitList = DataConfig.getDefaultUnitList();
			
			List<xURI> uriList = adapter.getUriList(config.getStoreUri(), null);
			for (xURI u : uriList) {
				if (adapter.isStore(u))
					continue;
				
				String unitName = u.getLastName();
				Unit unit = new Unit(unitName);
				if (!defaultUnitList.contains(unitName))
					unit.setExtra(true);
					
				tblList.add(unit);
			}

		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Get database metadata error: " + e.getMessage());
		}
		
		return tblList;
	}

	
	@Override
	public boolean deleteUnitData(String unitName) {
		xURI unitURI = getUnitUri(unitName);
		if (unitURI == null || !adapter.exists(unitURI))
			return false;
		else {
			AttributeList attributes = getAttributes(unitName);
			if (attributes.size() == 0)
				return false;
			else
				return createUnit(unitName, attributes);
				
		}
	}

	
	@Override
	public boolean dropUnit(String unitName) {
		xURI unitURI = getUnitUri(unitName);
		if (unitURI == null)
			return true;
		else if (adapter.exists(unitURI))
			return adapter.delete(unitURI, null);
		else
			return true;
	}
	
	
	@Override
	public NominalList getNominalList(String filterUnit, String attName) {
		NominalList nominalList = new NominalList();
		UnitList unitList = getUnitList();
		if (!unitList.contains(getConfig().getNominalUnit()))
			return nominalList;
		
		Profile profile = new Profile(getAttributes(getConfig().getNominalUnit()));
		profile.setValue(DataConfig.NOMINAL_REF_UNIT_FIELD, filterUnit);
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, attName);
		
		Fetcher<Profile> fetcher = getProfiles(getConfig().getNominalUnit(), profile);
		try {
			while (fetcher.next()) {
				Profile nprofile = fetcher.pick();
				if (nprofile == null)
					continue;
				
				String nominalValue = nprofile.getValueAsString(DataConfig.NOMINAL_VALUE_FIELD);
				int nominalindex = nprofile.getValueAsInt(DataConfig.NOMINAL_INDEX_FIELD);
				if (nominalValue == null || nominalindex < 0)
					continue;
				
				int parentindex = nprofile.getValueAsInt(DataConfig.NOMINAL_PARENT_INDEX_FIELD);
				if (parentindex < 0)
					parentindex = Nominal.NULL_INDEX;
				
				nominalList.add(new Nominal(nominalValue, nominalindex, parentindex));
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				fetcher.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return nominalList;
	}

	
	@Override
	public AttributeList getAttributes(String profileUnit) {
		AttributeList list = new AttributeList();
		
		Reader reader = getReader(profileUnit);
		if (reader == null)
			return list;
		
		CsvReader csvReader = new CsvReader(reader);
		try {
			csvReader.readHeaders();
			String[] headers = csvReader.getHeaders();
			csvReader.close();
			csvReader = null;
			reader.close();
			reader = null;
			
			for (int i = 0; i < headers.length; i++) {
				Attribute attribute = new Attribute();
				attribute.parseText(headers[i]);
				
				if (attribute.getType() == Type.integer && !profileUnit.equals(getConfig().getNominalUnit())) {
					NominalList nominalList = getNominalList(profileUnit, attribute.getName());
					if (nominalList.size() > 0) {
						Attribute newAtt = new Attribute(attribute.getName(), nominalList);
						newAtt.setAutoInc(attribute.isAutoInc());
						newAtt.setIndex(attribute.getIndex());
						newAtt.setKey(attribute.isKey());
						
						attribute = newAtt;
					}
				}
				
				list.add(attribute);
			}
			
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (csvReader != null)
					csvReader.close();
				
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
		}
		
		return list;
		
	}

	
	@Override
	public AttributeList getAttributes(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement yet");
	}


	@Override
	public boolean containsProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		
		Reader reader = null;
		reader = getReader(profileUnit);
		if (reader == null)
			return false;

		CsvReader csvReader = null;
		boolean found = false;
		try {
			csvReader = new CsvReader(reader);
			csvReader.readHeaders();
			
			while (csvReader.readRecord()) {
				String[] record = csvReader.getValues();
				if (record.length == 0)
					continue;
				
				if (recordEqualsProfileValues(csvReader, profile)) {
					found = true;
					break;
				}
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			found = false;
		}
		finally {
			try {
				if (csvReader != null)
					csvReader.close();
				
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return found;
	}


	@Override
	public Profile getProfile(String profileUnit, Profile condition) {
		
		AttributeList attributes = getAttributes(profileUnit);
		Reader reader = getReader(profileUnit);
		if (reader == null)
			return null;
		
		Profile returnProfile = null;
		CsvReader csvReader = null;
		try {
			csvReader = new CsvReader(reader);
			csvReader.readHeaders();
			
			while (csvReader.readRecord()) {
				String[] record = csvReader.getValues();
				if (record.length == 0)
					continue;
				
				if (recordEqualsProfileValues(csvReader, condition)) {
					returnProfile = getProfile(csvReader, attributes);
					break;
				}
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			returnProfile = null;
		}
		finally {
			try {
				if (csvReader != null)
					csvReader.close();
				
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return returnProfile;
	}
	
	
	@Override
	public Fetcher<Profile> getProfiles(String profileUnit,
			Profile condition) {
		// TODO Auto-generated method stub

		List<Profile> list = Util.newList();
		
		AttributeList attributes = getAttributes(profileUnit);
		Reader reader = null;
		CsvReader csvReader = null;
		try {
			reader = getReader(profileUnit);
			csvReader = new CsvReader(reader);
			csvReader.readHeaders();
			
			while (csvReader.readRecord()) {
				String[] record = csvReader.getValues();
				if (record.length == 0)
					continue;
				
				Profile csvProfile = getProfile(csvReader, attributes);
				if (csvProfile == null)
					continue;
				else if (condition == null)
					list.add(csvProfile);
				else if (recordEqualsProfileValues(csvReader, condition))
					list.add(csvProfile);
				
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			
		}
		finally {
			try {
				if (csvReader != null)
					csvReader.close();

				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return new MemFetcher<Profile>(list);
	}


	@Override
	public Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement yet");
	}


	@Override
	public Fetcher<Integer> getProfileIds(String profileUnit) {
		// TODO Auto-generated method stub
		AttributeList attributes = getAttributes(profileUnit);
		final Attribute idAtt = attributes.getId();
		if (idAtt == null || idAtt.getType() != Type.integer)
			return new MemFetcher<Integer>();
		
		List<Integer> ids = Util.newList();
		Fetcher<Profile> fetcher = getProfiles(profileUnit, null);
		try {
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				int id = profile.getValueAsInt(idAtt.getName());
				if (id >= 0)
					ids.add(id);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fetcher != null)
					fetcher.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return new MemFetcher<Integer>(ids);
	}


	@Override
	public int getProfileMaxId(String profileUnit) {
		// TODO Auto-generated method stub
		
		AttributeList attributes = getAttributes(profileUnit);
		Reader reader = null;
		CsvReader csvReader = null;
		int maxId = -1;
		try {
			reader = getReader(profileUnit);
			csvReader = new CsvReader(reader);
			
			csvReader.readHeaders();
			Attribute id = attributes.getId();
			if (id == null)
				throw new Exception("Null id");
			
			while (csvReader.readRecord()) {
				String[] values = csvReader.getValues();
				if (values.length == 0)
					continue;
				
				Profile csvProfile = getProfile(csvReader, attributes);
				maxId = Math.max(maxId, csvProfile.getIdValueAsInt());
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			
			maxId = -1;
		}
		finally {
			try {
				if (csvReader != null)
					csvReader.close();
				
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return maxId < 0 ? -1 : maxId;
	}


	@Override
	public boolean insertProfile(String profileUnit, Profile profile) {
		if (profile == null || profile.getAttCount() == 0)
			return false;
		
		Writer writer = getWriter(profileUnit, true);
		if (writer == null)
			return false;
		
		boolean result = true;
		CsvWriter csvWriter = new CsvWriter(writer, DELIMITER);
		try {
			csvWriter.writeRecord(toStringArray(profile));
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			try {
				if (csvWriter != null)
					csvWriter.close();
				
				if (writer != null)
					writer.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	
	@NextUpdate
	@Override
	public boolean updateProfile(String profileUnit, Profile profile) {
		if (profile == null || profile.getAttCount() == 0)
			return false;
		
		Reader reader = null;
		CsvReader csvReader = null;
		Writer writer = null;
		CsvWriter csvWriter = null;
		
		boolean result = true;
		try {
			
			reader = getReader(profileUnit);
			csvReader = new CsvReader(reader);
			csvReader.readHeaders();
			String[] headers = csvReader.getHeaders(); 
			List<String[]> data = Util.newList();
			while (csvReader.readRecord()) {
				String[] values = csvReader.getValues();
				if (values.length == 0)
					continue;
				
				data.add(values);
			}
			csvReader.close();
			csvReader = null;
			reader.close();
			reader = null;
			
			deleteUnitData(profileUnit);
			writer = getWriter(profileUnit, true);
			csvWriter = new CsvWriter(writer, DELIMITER);

			boolean updated = false;
			for (String[] record : data) {
				if (profile != null && recordEqualsProfileKeyValues(headers, profile, record)) {
					csvWriter.writeRecord(toStringArray(profile));
				}
				else {
					updated = true;
					csvWriter.writeRecord(record);
				}
			}
			if (!updated)
				csvWriter.writeRecord(toStringArray(profile));
			
			csvWriter.close();
			csvWriter = null;
			writer.close();
			writer = null;
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			
			result = false;
		}
		finally {
			try {
				if (csvReader != null)
					csvReader.close();
					
				if (reader != null)
					reader.close();
				
				if (csvWriter != null)
					csvWriter.close();
					
				if (writer != null)
					writer.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	
	@NextUpdate
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) {
		if (condition == null || condition.getAttCount() == 0)
			return false;
		
		Reader reader = null;
		CsvReader csvReader = null;
		Writer writer = null;
		CsvWriter csvWriter = null;
		
		boolean result = true;
		try {
			
			reader = getReader(profileUnit);
			csvReader = new CsvReader(reader);
			csvReader.readHeaders();
			String[] headers = csvReader.getHeaders(); 
			List<String[]> data = Util.newList();
			while (csvReader.readRecord()) {
				String[] values = csvReader.getValues();
				if (values.length == 0)
					continue;
				
				data.add(values);
			}
			csvReader.close();
			csvReader = null;
			reader.close();
			reader = null;
			
			deleteUnitData(profileUnit);
			writer = getWriter(profileUnit, true);
			csvWriter = new CsvWriter(writer, DELIMITER);
			
			for (String[] record : data) {
				if (!recordEqualsProfileValues(headers, condition, record))
					csvWriter.writeRecord(record);
			}
			csvWriter.close();
			csvWriter = null;
			writer.close();
			writer = null;
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			try {
				if (csvReader != null)
					csvReader.close();
					
				if (reader != null)
					reader.close();
				
				if (csvWriter != null)
					csvWriter.close();
					
				if (writer != null)
					writer.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}


	/**
	 * 
	 * @param profile
	 * @return string array of profile values
	 */
	public static String[] toStringArray(Profile profile) {
		List<String> record = Util.newList();

		int n = profile.getAttCount();
		for (int i = 0; i < n; i++) {
			Object value = profile.getValue(i);
			if (value == null)
				record.add("");
			else if (value instanceof Date) {
				SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
				record.add(df.format(value));
			}
			else
				record.add(value.toString());
		}
		
		return record.toArray(new String[] { });
	}
	
	
	/**
	 * 
	 * @param unit
	 * @return unit {@link xURI}
	 */
	private xURI getUnitUri(String unit) {
		xURI store = config.getStoreUri();
		if (store == null)
			return null;
		else
			return store.concat(unit);
		
	}
	
	
	/**
	 * 
	 * @param reader
	 * @param profile
	 * @return whether csv record at current row equals profile key values
	 */
	@SuppressWarnings("unused")
	private boolean recordEqualsProfileKeyValues(CsvReader reader, Profile profile) {
		Condition keyValues = profile.getKeyValues();
		int n = keyValues.getAttCount();
		
		boolean equal = true;
		for (int i = 0; i < n; i++) {
			try {
				Object columnValue = getValue(reader, keyValues.getAtt(i).getName());
				
				if (columnValue == null || 
						!columnValue.equals(profile.getValue(i))) {
					equal = false;
					break;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				equal = false;
				break;
			}
		}
		
		return equal;
	}
	
	
	/**
	 * 
	 * @param headers
	 * @param profile
	 * @param csvRecord
	 * @return whether csv record at current row equals profile key values
	 */
	private boolean recordEqualsProfileKeyValues(String[] headers, Profile profile, String[] csvRecord) {
		Condition keyValues = profile.getKeyValues();
		int n = keyValues.getAttCount();
		
		boolean equal = true;
		for (int i = 0; i < n; i++) {
			try {
				Object columnValue = getValue(headers, keyValues.getAtt(i).getName(), csvRecord);
				
				if (columnValue == null || 
						!columnValue.equals(profile.getValue(i))) {
					equal = false;
					break;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				equal = false;
				break;
			}
		}
		
		return equal;
	}

	
	/**
	 * 
	 * @param reader
	 * @param profile
	 * @return whether csv record at current row equals profile values
	 */
	private boolean recordEqualsProfileValues(CsvReader reader, Profile profile) {
		int n = profile.getAttCount();
		
		boolean equal = true;
		for (int i = 0; i < n; i++) {
			try {
				Object profileValue = profile.getValue(i);
				if (profileValue == null)
					continue;
				
				Object columnValue = getValue(reader, profile.getAtt(i).getName());
				
				if (columnValue == null || 
						!columnValue.equals(profileValue)) {
					equal = false;
					break;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				equal = false;
				break;
			}
		}
		
		return equal;
	}

	
	/**
	 * 
	 * @param headers
	 * @param profile
	 * @param csvRecord
	 * @return whether csv record at current row equals profile values
	 */
	private boolean recordEqualsProfileValues(String[] headers, Profile profile, String[] csvRecord) {
		int n = profile.getAttCount();
		
		boolean equal = true;
		for (int i = 0; i < n; i++) {
			try {
				Object profileValue = profile.getValue(i);
				if (profileValue == null)
					continue;
				
				Object columnValue = getValue(headers, profile.getAtt(i).getName(), csvRecord);
				
				if (columnValue == null || 
						!columnValue.equals(profileValue)) {
					equal = false;
					break;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				equal = false;
				break;
			}
		}
		
		return equal;
	}

	
	/**
	 * 
	 * @param unit
	 * @param append
	 * @return {@link Writer}
	 */
	private Writer getWriter(String unit, boolean append) {
		xURI unitURI = getUnitUri(unit);
		if (unitURI == null || !adapter.exists(unitURI))
			return null;
		
		return adapter.getWriter(unitURI, append);
	}

	
	/**
	 * 
	 * @param unit
	 * @return {@link Reader}
	 */
	private Reader getReader(String unit) {
		xURI unitURI = getUnitUri(unit);
		if (unitURI == null || !adapter.exists(unitURI))
			return null;
		
		return adapter.getReader(unitURI);
	}

	
	/**
	 * 
	 * @param writer
	 * @param attributes
	 */
	private void writeHeader(CsvWriter writer, AttributeList attributes) {
		if (attributes.size() == 0)
			return;
		
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			Attribute newAtt = attribute;
			
			if (attribute.getType() == Type.nominal) {
				newAtt = new Attribute(attribute.getName(), Type.integer);
				newAtt.setAutoInc(attribute.isAutoInc());
				newAtt.setIndex(attribute.getIndex());
				newAtt.setKey(attribute.isKey());
			}
			
			try {
				writer.write(newAtt.toText());
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			writer.endRecord();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param reader
	 * @param attributeName
	 * @return csv attribute value
	 */
	private Object getValue(CsvReader reader, String attributeName) {
		try {
			String[] headers = reader.getHeaders();
			
			int foundIdx = -1;
			Attribute found = null;
			for (int i = 0; i < headers.length; i++) {
				Attribute attribute = new Attribute();
				attribute.parseText(headers[i]);
				if (attribute.getName().equals(attributeName)) {
					foundIdx = i;
					found = attribute;
					break;
				}
			}
			
			if (foundIdx != -1)
				return Profile.createValue(found, reader.get(foundIdx));
			else
				return null;
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param headers
	 * @param attributeName
	 * @param csvRecord
	 * @return csv attribute value
	 */
	private Object getValue(String[] headers, String attributeName, String[] csvRecord) {
		try {
			
			int foundIdx = -1;
			Attribute found = null;
			for (int i = 0; i < headers.length; i++) {
				Attribute attribute = new Attribute();
				attribute.parseText(headers[i]);
				if (attribute.getName().equals(attributeName)) {
					foundIdx = i;
					found = attribute;
					break;
				}
			}
			
			if (foundIdx != -1)
				return Profile.createValue(found, csvRecord[foundIdx]);
			else
				return null;
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param reader
	 * @param attributes
	 * @return {@link Profile}
	 */
	private Profile getProfile(CsvReader reader, AttributeList attributes) {
		if (attributes.size() == 0)
			return null;
		
		Profile profile = new Profile(attributes);
		for (int i = 0; i < attributes.size(); i++) {
			Object value = null;
			try {
				value = Profile.createValue(attributes.get(i), reader.get(i));
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			profile.setValue(i, value);
		}
		
		return profile;
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (adapter != null)
			adapter.close();
		adapter = null;
	}

	

	
	
}
