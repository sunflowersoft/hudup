package net.hudup.core;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.parser.DatasetParser;


/**
 * 
 * Plug-in manager is responsible for discovering and managing registered recommendation algorithms.
 * The class that implements {@code PluginManager} is {@code Firer}.
 * {@code Alg} is the most abstract interface for all algorithms.
 * Every algorithm has a unique name. Every algorithm is registered in system register table and identified by its name.
 * Such system register table is modeled as {@code RegisterTable} class.
 * Actually, plug-in manager discovers automatically all algorithms via their names at the booting time and requires {@link PluginStorage} to add such algorithms into register tables represented by {@link RegisterTable} classes.
 * After Hudup framework started, it is easy to retrieve any any algorithms by looking up algorithms in register tables from {@link PluginStorage}.
 * Please pay attention that system register tables are stored in a so-called {@code plug-in storage} which is represented by this {@code PluginStorage} class.
 * <br><br>
 * Note, general algorithm is represented by {@link Alg} interface. {@link Alg} has five typical inherited interfaces as follows:
 * <ul>
 * <li>Recommendation algorithm {@code Recommender}.</li> 
 * <li>Context template schema manager {@code CTSManager}.</li> 
 * <li>Data set parser {@code DatasetParser}.</li>
 * <li>External query {@code ExternalQuery}.</li>
 * <li>Metric {@code Metric}.</li>
 * </ul>
 * In general, this {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For examples, the internal variable {@link #recommenderReg} which is a register table manages recommendation algorithms (recommenders).
 * The internal variable {@link #metricReg} which is a register table manages metrics for evaluating recommenders.
 * {@link PluginStorage} also manages a list of next-update algorithms. Next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
 * Currently, next-update algorithms are not used.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class PluginStorage {

	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code Recommender} (s).
	 */
	private static final String RECOMMENDER    = "Recommender";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code DatasetParser} (s).
	 */
	private static final String PARSER         = "Parser";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code Metric} (s).
	 */
	private static final String METRIC         = "Metric";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code ExternalQuery} (s).
	 */
	private static final String EXTERNAL_QUERY = "External query";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code CTSManager} (s).
	 */
	private static final String CTS_MANAGER    = "CTS manager";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of unknown algorithms.
	 */
	private static final String UNKNOWN        = "Unknown";

	
	/**
	 * Recommendation algorithm register. Note, this is public and static variable.
	 */
	private final static RegisterTable recommenderReg = new RegisterTable();

	
	/**
	 * {@link DatasetParser} register. Note, this is public and static variable.
	 */
	private final static RegisterTable parserReg = new RegisterTable();

	
	/**
	 * {@link Metric} register. Note, this is public and static variable.
	 */
	private final static RegisterTable metricReg = new RegisterTable();

	
	/**
	 * {@link ExternalQuery} register. Note, this is public and static variable.
	 */
	private final static RegisterTable externalQueryReg = new RegisterTable();

	
	/**
	 * {@link CTSManager} register. Note, this is public and static variable.
	 */
	private final static RegisterTable ctsmReg = new RegisterTable();

	
	/**
	 * List of next-update algorithms. Next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * Currently, next-update algorithms are not used. Note, this is public and static variable.
	 */
	private final static AlgList nextUpdateList = new AlgList();
	
	
	/**
	 * This static method gets recommendation algorithm register.
	 * @return register table of {@link Alg}.
	 */
	public final static RegisterTable getRecommenderReg() {
		return recommenderReg;
	}
	
	
	/**
	 * This static method gets {@link DatasetParser} register.
	 * @return register table of {@link DatasetParser}.
	 */
	public final static RegisterTable getParserReg() {
		return parserReg;
	}

	
	/**
	 * This static method gets {@link Metric} register.
	 * @return {@link Metric} register.
	 */
	public final static RegisterTable getMetricReg() {
		return metricReg;
	}

	
	/**
	 * This static method gets {@link ExternalQuery} register.
	 * @return {@link ExternalQuery} register.
	 */
	public final static RegisterTable getExternalQueryReg() {
		return externalQueryReg;
	}

	
	/**
	 * This static method gets {@link CTSManager} register.
	 * @return {@link CTSManager} register.
	 */
	public final static RegisterTable getCTSManagerReg() {
		return ctsmReg;
	}

	
	/**
	 * This static method gets list of next-update algorithms.
	 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * Currently, next-update algorithms are not used.
	 * @return list of next-update algorithms.
	 */
	public final static AlgList getNextUpdateList() {
		return nextUpdateList;
	}

	
	/**
	 * This static method clears this storage, which means that all register tables become empty.
	 */
	public final static void clear() {
		recommenderReg.clear();
		parserReg.clear();
		metricReg.clear();
		externalQueryReg.clear();
		ctsmReg.clear();
		nextUpdateList.clear();
	}
	
	
	/**
	 * This static method gets all register tables as a {@link RegisterTableList}.
	 * @return all register tables as a {@link RegisterTableList}.
	 */
	public final static RegisterTableList getRegisterTableList() {
		return new RegisterTableList(
				new RegisterTableList.RegisterTableItem(RECOMMENDER, recommenderReg), 
				new RegisterTableList.RegisterTableItem(PARSER, parserReg), 
				new RegisterTableList.RegisterTableItem(METRIC, metricReg), 
				new RegisterTableList.RegisterTableItem(EXTERNAL_QUERY, externalQueryReg), 
				new RegisterTableList.RegisterTableItem(CTS_MANAGER, ctsmReg)); 
	}
	
	
	/**
	 * Looking up the register table whose algorithms have the class which is equal to specified algorithm class.
	 * @param algClass specified algorithm class.
	 * @return {@link RegisterTable} whose algorithms have the class which is equal to specified algorithm class.
	 */
	public final static RegisterTable lookupTable(Class<? extends Alg> algClass) {
		if (Recommender.class.isAssignableFrom(algClass))
			return recommenderReg;
		else if (DatasetParser.class.isAssignableFrom(algClass))
			return parserReg;
		else if (Metric.class.isAssignableFrom(algClass))
			return metricReg;
		else if (ExternalQuery.class.isAssignableFrom(algClass))
			return externalQueryReg;
		else if (CTSManager.class.isAssignableFrom(algClass))
			return ctsmReg;
		else
			return null;
	}
	
	
	/**
	 * Looking up the register table whose name is equal to specified name
	 * @param algTypeName specified name. This is the name implying the type of algorithm.
	 * <ul>
	 * <li>Register table of {@link Recommender} (s) has name specified by the constant {@link #RECOMMENDER}.</li>
	 * <li>Register table of {@link DatasetParser} (s) has name specified by the constant {@link #PARSER}.</li>
	 * <li>Register table of {@link Metric} (s) has name specified by the constant {@link #METRIC}.</li>
	 * <li>Register table of {@link ExternalQuery} (s) has name specified by the constant {@link #EXTERNAL_QUERY}.</li>
	 * <li>Register table of {@link CTSManager} (s) has name specified by the constant {@link #CTS_MANAGER}.</li>
	 * </ul>
	 * @return {@link RegisterTable} whose name is equal to specified name.
	 */
	public final static RegisterTable lookupTable(String algTypeName) {
		if (algTypeName.equals(RECOMMENDER))
			return recommenderReg;
		else if (algTypeName.equals(PARSER))
			return parserReg;
		else if (algTypeName.equals(METRIC))
			return metricReg;
		else if (algTypeName.equals(EXTERNAL_QUERY))
			return externalQueryReg;
		else if (algTypeName.equals(CTS_MANAGER))
			return ctsmReg;
		else
			return null;
	}
	
	
	/**
	 * Looking up the type name of specified class of algorithm.
	 * @param algClass specified class of algorithm.
	 * @return type name of specified class of algorithm.
	 * <ul>
	 * <li>Class of {@link Recommender} (s) has type name specified by the constant {@link #RECOMMENDER}.</li>
	 * <li>Class of {@link DatasetParser} (s) has type name specified by the constant {@link #PARSER}.</li>
	 * <li>Class of {@link Metric} (s) has type name specified by the constant {@link #METRIC}.</li>
	 * <li>Class of {@link ExternalQuery} (s) has type name specified by the constant {@link #EXTERNAL_QUERY}.</li>
	 * <li>Class of {@link CTSManager} (s) has type name specified by the constant {@link #CTS_MANAGER}.</li>
	 * </ul>
	 */
	public final static String lookupAlgTypeName(Class<? extends Alg> algClass) {
		if (Recommender.class.isAssignableFrom(algClass))
			return RECOMMENDER;
		else if (DatasetParser.class.isAssignableFrom(algClass))
			return PARSER;
		else if (Metric.class.isAssignableFrom(algClass))
			return METRIC;
		else if (ExternalQuery.class.isAssignableFrom(algClass))
			return EXTERNAL_QUERY;
		else if (CTSManager.class.isAssignableFrom(algClass))
			return CTS_MANAGER;
		else
			return UNKNOWN;
	
	}
	
	
}
