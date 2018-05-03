package net.hudup.data;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

import net.hudup.alg.RmiRecommender;
import net.hudup.alg.SocketRecommender;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.CompositeRecommender;
import net.hudup.core.alg.MemoryBasedRecommender;
import net.hudup.core.alg.ModelBasedRecommender;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.ServiceRecommender;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.KBasePointer;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.ObjectPair;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.DbServerIndicator;
import net.hudup.core.parser.FlatServerIndicator;
import net.hudup.core.parser.Indicator;
import net.hudup.core.parser.KBaseIndicator;
import net.hudup.core.parser.RecommendRmiServerIndicator;
import net.hudup.core.parser.RecommendSocketServerIndicator;
import net.hudup.core.parser.ScannerParser;
import net.hudup.core.parser.SnapshotParser;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.ui.DatasetConfigurator;
import net.hudup.parser.MovielensParser;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class DatasetUtil2 {
	
	
	/**
	 * 
	 * @param defaultAlg
	 * @return {@link DataConfig}
	 */
	public static DataConfig createDefaultConfig(Alg defaultAlg) {
		if (defaultAlg == null)
			return null;
		
		DatasetParser parser = null;
		if (defaultAlg instanceof ModelBasedRecommender)
			parser = (DatasetParser) PluginStorage.getParserReg().query(new KBaseIndicator().getName());
		else if (defaultAlg instanceof SocketRecommender)
			parser = (DatasetParser) PluginStorage.getParserReg().query(new RecommendSocketServerIndicator().getName());
		else if (defaultAlg instanceof RmiRecommender)
			parser = (DatasetParser) PluginStorage.getParserReg().query(new RecommendRmiServerIndicator().getName());
		else
			parser = (DatasetParser) PluginStorage.getParserReg().query(new MovielensParser().getName());
		
		if (parser == null)
			return null;
		
		DataDriver dataDriver = null;
		if (parser instanceof KBaseIndicator)
			dataDriver = new DataDriver(DataType.file);
		else if (parser instanceof RecommendSocketServerIndicator)
			dataDriver = new DataDriver(DataType.hudup_socket);
		else if (parser instanceof RecommendRmiServerIndicator)
			dataDriver = new DataDriver(DataType.hudup_rmi);
		else
			dataDriver = new DataDriver(DataType.file);

		DataConfig config = (DataConfig) defaultAlg.getConfig().clone();
		config.setParser(parser.getName());
		config.setDataDriverName(dataDriver.getName());
		return config;
	}

	
	/**
	 * 
	 * @param comp
	 * @param parserList
	 * @param dataDriverList
	 * @param defaultConfig
	 * @return {@link DataConfig}
	 */
	public static DataConfig chooseConfig(
			Component comp, 
			List<Alg> parserList,
			DataDriverList dataDriverList,
			DataConfig defaultConfig) {
		
		DatasetConfigurator configurator = new DatasetConfigurator(
				comp, 
				parserList, 
				dataDriverList,
				defaultConfig);
		
		return configurator.getResult();
	}

	
	/**
	 * 
	 * @param comp
	 * @param initConfig
	 * @return toolkit {@link DataConfig}
	 */
	public static DataConfig chooseConfig(Component comp, DataConfig initConfig) {
		DataConfig defaultConfig = initConfig == null ? new DataConfig() : (DataConfig) initConfig.clone();
		
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		
		try {
			String dataDriverName = defaultConfig.getDataDriverName();
			if (dataDriverName == null) {
				xURI store = defaultConfig.getStoreUri();
				if (store != null) {
					DataDriver dataDriver = DataDriver.create(store);
					if (dataDriver != null)
						dataDriverName = dataDriver.getName(); 
				}
			}
			
			DataDriver dataDriver = null;
			if (dataDriverName != null) {
				defaultConfig.setDataDriverName(dataDriverName);
				dataDriver = DataDriver.createByName(dataDriverName);
			}
			
			DatasetParser parser = defaultConfig.getParser();
			if (parser == null && dataDriver != null) {
				if (dataDriver.isFlatServer())
					defaultConfig.setParser(new FlatServerIndicator());
				else if (dataDriver.isDbServer())
					defaultConfig.setParser(new DbServerIndicator());
				else if (dataDriver.isRecommendServer())
					defaultConfig.setParser(new RecommendRmiServerIndicator());
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return chooseConfig(
				comp, parserList, DataDriverList.list(), defaultConfig);
	}

	
	/**
	 * 
	 * @param comp
	 * @param defaultConfig
	 * @param alg
	 * @return training {@link DataConfig}
	 */
	public static DataConfig chooseTrainingConfig(Component comp, DataConfig defaultConfig, Alg alg) {
		if (alg == null || !(alg instanceof Recommender))
			return DatasetUtil2.chooseConfig(comp, defaultConfig);
		
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		List<Alg> newParserList = Util.newList();
		for (Alg parser : parserList) {
			if (alg instanceof MemoryBasedRecommender) {
				if (parser instanceof SnapshotParser ||
						parser instanceof ScannerParser)
					newParserList.add(parser);
			}
			else if (alg instanceof ModelBasedRecommender) {
				if (parser instanceof KBaseIndicator || 
						parser instanceof SnapshotParser ||
						parser instanceof ScannerParser)
					newParserList.add(parser);
			}
			else if (alg instanceof CompositeRecommender) {
				newParserList.add(parser);
			}
			else if (alg instanceof ServiceRecommender) {
				if (parser instanceof Indicator && !(parser instanceof KBaseIndicator))
					newParserList.add(parser);
			}
			else
				newParserList.add(parser);
		}
		
		return chooseConfig(comp, newParserList, DataDriverList.list(), defaultConfig);
	}

	
	/**
	 * 
	 * @param defaultConfig
	 * @return testing {@link DataConfig}
	 */
	public static DataConfig chooseTestingConfig(Component comp, DataConfig defaultConfig) {
		
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		List<Alg> newParserList = Util.newList();
		for (Alg parser : parserList) {
			if (parser instanceof SnapshotParser || parser instanceof ScannerParser)
				newParserList.add(parser);
		}
		
		return chooseConfig(comp, newParserList, DataDriverList.list(), defaultConfig);
	}

	
	/**
	 * 
	 * @param comp
	 * @param initConfig
	 * @return server {@link DataConfig}
	 */
	public static DataConfig chooseServerConfig(Component comp, DataConfig initConfig) {
		DataConfig defaultConfig = initConfig == null ? new DataConfig() : (DataConfig) initConfig.clone();
		
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		List<Alg> newParserList = Util.newList();
		for (Alg parser : parserList) {
			if (parser instanceof Indicator)
				newParserList.add(parser);
		}
		
		try {
			String dataDriverName = null;
			xURI store = defaultConfig.getStoreUri();
			if (store != null) {
				DataDriver dataDriver = DataDriver.create(store);
				if (dataDriver != null)
					dataDriverName = dataDriver.getName(); 
			}
			
			if (dataDriverName == null)
				return chooseConfig(
						comp, newParserList, DataDriverList.list(), defaultConfig);
				
			defaultConfig.setDataDriverName(dataDriverName);
			DataDriver dataDriver = DataDriver.createByName(dataDriverName);
			
			if (dataDriver.isFlatServer())
				defaultConfig.setParser(new FlatServerIndicator());
			else if (dataDriver.isDbServer())
				defaultConfig.setParser(new DbServerIndicator());
			else if (dataDriver.isRecommendServer())
				defaultConfig.setParser(new RecommendRmiServerIndicator());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return chooseConfig(
				comp, newParserList, DataDriverList.list(), defaultConfig);
	}

	
	/**
	 * 
	 * @param config
	 * @return {@link Properties}
	 */
	public static Properties loadFlatConfig(DataConfig config) {
		Properties props = new Properties();
		
		UriAdapter adapter = new UriAdapter(config);
		try {
			xURI configUri = config.getStoreUri().concat(config.getConfigUnit());
			
			Reader reader = adapter.getReader(configUri);
			
			props.load(reader);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.close();
		
		return props;
	}
	
	
	/**
	 * 
	 * @param comp
	 * @param training
	 * @param algList
	 * @return whether dataset conform with algorithms
	 */
	public static boolean validateTrainingset(Component comp, Dataset training, Alg[] algList) {
		if (training == null || algList.length == 0)
			return false;
		
		for (Alg alg : algList) {
			if (alg == null)
				return false;
		}
		
		if (training instanceof Pointer) {
			
			if (training instanceof KBasePointer) {
				boolean flag = false;
				
				for (Alg alg : algList) {
					if (!(alg instanceof ModelBasedRecommender || alg instanceof CompositeRecommender)) {
						flag = true;
						break;
					}
				}
				
				if (flag) {
					JOptionPane.showMessageDialog(
							comp, 
							"Training dataset is KBasePointer but there is not model based recommender nor composite recomender", 
							"Invalid training dataset", 
							JOptionPane.ERROR_MESSAGE);
					
					return false;
				}
			}
			else {
				boolean flag = false;
				
				for (Alg alg : algList) {
					if (!(alg instanceof ServiceRecommender || alg instanceof CompositeRecommender)) {
						flag = true;
						break;
					}
				}
				
				if (flag) {
					JOptionPane.showMessageDialog(
							comp, 
							"Training dataset is Pointer but algorithm isn't service recommender nor composite recommender", 
							"Invalid training dataset", 
							JOptionPane.ERROR_MESSAGE);
					
					return false;
				}
				
			} // if (training instance of KBasePointer)
			
			
		} // if (training instance of Pointer)
		else {
			
			boolean flag = false;
			
			for (Alg alg : algList) {
				if (alg instanceof ServiceRecommender) {
					flag = true;
					break;
				}
			}
			
			if (flag) {
				JOptionPane.showMessageDialog(
						comp, 
						"Training dataset is normal dataset but there is a service recommender", 
						"Invalid training dataset", 
						JOptionPane.ERROR_MESSAGE);
				
				return false;
			}

		}
		
		return true;
		
	}

	
	/**
	 * 
	 * @param nUsers
	 * @param nItems
	 * @param nRatings
	 * @return random {@link Dataset}
	 */
	public static Dataset createRandomDataset(int nUsers, int nItems, int nRatings) {
		return null;
	}
	
	
	/**
	 * 
	 * @param reader
	 * @return map of {@link Attribute}
	 */
	public static Map<String, Attribute> loadNominalAttributes(Reader reader, String filterUnit) {
		Map<String, List<ObjectPair<Nominal>>> map = Util.newMap();
		
		BufferedReader buffer = new BufferedReader(reader);
		String line = null;
		try {
			while ( (line = buffer.readLine()) != null ) {
				List<String> parts = TextParserUtil.split(line, TextParserUtil.NOSPACE_DEFAULT_SEP, null);
				if (parts.size() < 4)
					continue;
				
				String unit = parts.get(0);
				if (!unit.equals(filterUnit))
					continue;
				
				try {
					String att = parts.get(1);
					int nominalIndex = Integer.parseInt(parts.get(2));
					String nominalValue = parts.get(3);
					int parentIndex = -1;
					if (parts.size() > 4)
						parentIndex = Integer.parseInt(parts.get(4));
					
					List<ObjectPair<Nominal>> list = map.get(att);
					if (list == null) {
						list = Util.newList();
						map.put(att, list);
					}
					
					Nominal nominal = new Nominal(nominalValue, nominalIndex, parentIndex);
					ObjectPair<Nominal> pair = new ObjectPair<Nominal>(nominal, nominalIndex);
					list.add(pair);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		Set<String> attNames = map.keySet();
		Map<String, Attribute> attributeMap = Util.newMap();
		for (String attName : attNames) {
			List<ObjectPair<Nominal>> list = map.get(attName);
			ObjectPair.sort(list, false);
			
			List<Nominal> nominals = ObjectPair.getKeyList(list);
			Attribute att = new Attribute(attName, new NominalList(nominals));
			attributeMap.put(attName, att);
		}
		
		
		return attributeMap;
	}
	
	
	/**
	 * 
	 * @param adapter
	 * @param uri
	 * @return map of {@link Attribute}
	 */
	public static Map<String, Attribute> loadNominalAttributes(UriAdapter adapter, xURI uri, String filterUnit) {
		Reader reader = adapter.getReader(uri);
		
		if (reader == null)
			return Util.newMap();
		
		Map<String, Attribute> attributeMap = loadNominalAttributes(reader, filterUnit);
		
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return attributeMap;
	}
	
	
	/**
	 * 
	 * @param nominalAttributes
	 * @param writer
	 */
	public static void saveNominalAttributes(
			Collection<Attribute> nominalAttributes, 
			Writer writer,
			String filterUnit) {
		
		BufferedWriter buffer = new BufferedWriter(writer);
		for (Attribute att : nominalAttributes) {
			if (att.getType() != Type.nominal)
				continue;
			
			try {
				String attName = att.getName();
				NominalList nominals = att.getNominalList();
				
				for (int i = 0; i < nominals.size(); i++) {
					Nominal nominal = nominals.get(i);
					String line = 
							filterUnit + ", " + 
							attName + ", " + 
							nominal.getIndex() + ", " + 
							nominal.getValue() + ", " + 
							nominals.get(i) + "\n";
					writer.write(line);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			buffer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	/**
	 * 
	 * @param nominalAttributes
	 * @param adapter
	 * @param uri
	 */
	public static void saveNominalAttributes(
			Collection<Attribute> nominalAttributes,
			UriAdapter adapter,
			xURI uri,
			String filterUnit) {
		
		Writer writer = adapter.getWriter(uri, false);
		if (writer == null)
			return;
		saveNominalAttributes(nominalAttributes, writer, filterUnit);
		
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * 
	 * @param realValues
	 * @return zero-based integer array
	 */
	public static int[] zeroBasedRatingValueOf(float[] realValues, float minRating) {
		int[] inner = new int[realValues.length];
		for (int i = 0; i < realValues.length; i++) {
			int realValue = (int)((float)realValues[i] + 0.5);
			inner[i] = zeroBasedRatingValueOf(realValue, minRating);
		}
		
		return inner;
	}

	
	/**
	 * 
	 * @param realValue
	 * @return zero-based value
	 */
	public static int zeroBasedRatingValueOf(float realValue, float minRating) {
		return (int) (realValue - minRating);
	}

	
	/**
	 * 
	 * @param zeroBasedValue
	 * @return real rating value
	 */
	public static float realRatingValueOf(int zeroBasedValue, float minRating) {
		return zeroBasedValue + minRating;
	}

	
	
	
	
}
