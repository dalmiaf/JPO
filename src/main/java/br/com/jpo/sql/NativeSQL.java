package br.com.jpo.sql;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.jpo.connection.impl.JdbcWrapper;
import br.com.jpo.utils.BigDecimalUtils;


public class NativeSQL {
	private static final Pattern regexNamedParams = Pattern.compile(":\\b\\w+(\\.\\w+)?\\b", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

	private Collection<Object>			pstms;
	private JdbcWrapper					jdbcWrapper;
	private List<Object>				parameters;
	private List<Object>				parametersSequence;
	private Map<String, Object>			namedParameters;
	private PreparedStatement			lastPrepareUsed;
	private String						lastSqlExecuted;
	private StringBuffer				sqlBuf;
	private int 						fetchSize = -1;
	private int							maxRows;
	private boolean						reuseStatements;
	private boolean						scrollableResult;
	private boolean 					fillNamedParametersWithNull;
	private static final int			EDIT_SQL	= 0;
	private static final int			QUERY_SQL	= 1;

	public NativeSQL(JdbcWrapper jdbcWrapper) {
		this.jdbcWrapper = jdbcWrapper;
		this.parameters = new ArrayList<Object>();
		this.pstms = new ArrayList<Object>();
		this.sqlBuf = new StringBuffer();
		this.parametersSequence = new ArrayList<Object>();
		this.namedParameters = new HashMap<String, Object>();
	}

	@SuppressWarnings("rawtypes")
	public NativeSQL(JdbcWrapper jdbcWrapper, Class baseClass, String resourcePath) throws Exception{
		this(jdbcWrapper);
		loadSql(baseClass, resourcePath);
	}

	public void addParameter(Object value) {
		parameters.add(value);
	}

	public void addParameter(int index, Object value) {
		parameters.set(index, value);
	}

	public void addParameter(Collection<Object> params) {
		parameters.addAll(params);
	}

	public void addNamedParameter(String name, Object value) {
		namedParameters.put(name.toUpperCase(), value);
	}

	public void addNamedParameter(Map<String, Object> params) {
		namedParameters.putAll(params);
	}

	public Map<String, Object> getNamedParameters() {
		return namedParameters;
	}

	public void cleanParameters() {
		parameters.clear();
		namedParameters.clear();
	}

	public ResultSet executeQuery(StringBuffer sql, Collection<Object> params) throws Exception {
		return executeQuery(sql.toString(), params);
	}

	public ResultSet executeQuery(StringBuffer sql) throws Exception {
		return executeQuery(sql.toString(), parameters);
	}

	public ResultSet executeQuery(String sql) throws Exception {
		return executeQuery(sql, parameters);
	}

	public ResultSet executeQuery() throws Exception {
		return executeQuery(sqlBuf.toString(), parameters);
	}

	public ResultSet executeQuery(String sql, Collection<Object> params) throws Exception {
		return (ResultSet) executeInternal(sql, params, QUERY_SQL);
	}

	public boolean executeUpdate(String sql) throws Exception {
		return executeUpdate(sql, parameters);
	}

	public boolean executeUpdate() throws Exception {
		return executeUpdate(sqlBuf.toString(), parameters);
	}

	public boolean executeUpdate(StringBuffer sql) throws Exception {
		return executeUpdate(sql.toString(), parameters);
	}

	public boolean executeUpdate(StringBuffer sql, Collection<Object> params) throws Exception {
		return executeUpdate(sql.toString(), params);
	}

	public boolean executeUpdate(String sql, Collection<Object> params) throws Exception {
		return ((Boolean) executeInternal(sql, params, EDIT_SQL)).booleanValue();
	}

	private Object executeInternal(String sql, Collection<Object> params, int commandType) throws Exception {
		PreparedStatement pstm = null;

		if (!reuseStatements || (lastPrepareUsed == null) || !lastSqlExecuted.equals(sql)) {

			if(lastSqlExecuted != null){
				lastPrepareUsed.close();
			}

			StringBuffer buf = new StringBuffer(sql);
			lastSqlExecuted = sql;

			parseNamedParameters(buf);

			sql = buf.toString();

			if (scrollableResult) {
				pstm = jdbcWrapper.getPreparedStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} else {
				pstm = jdbcWrapper.getPreparedStatement(sql);
			}

			if (maxRows > 0) {
				pstm.setMaxRows(maxRows);
			}

			lastPrepareUsed = pstm;

			pstms.add(pstm);
		} else {
			pstm = lastPrepareUsed;
		}

		int paramIndex = 1;

		Collection<Object> finalParametersList = params;

		if (!namedParameters.isEmpty() || fillNamedParametersWithNull) {
			finalParametersList = buildParameterList();
			// Adicionamos os parametros da lista de entrada no final da lista de parametros. Isso significa que se usarmos parametros nomeados
			// em conjunto com parametros não nomeados estes últimos devem ficar no final do SQL, exemplo:
			//
			// WHERE COLUNA_A = :PA AND COLUNA_B = :PB AND COLUNA_C = ?
			finalParametersList.addAll(params);
		}

		for (Iterator<Object> ite = finalParametersList.iterator(); ite.hasNext();) {
			Object o = ite.next();

			if (o instanceof BigDecimal) {
				pstm.setBigDecimal(paramIndex++, BigDecimalUtils.adjustBigDecimalValue((BigDecimal) o));
			} else if (o instanceof Timestamp) {
				pstm.setTimestamp(paramIndex++, (Timestamp) o);
			} else if (o instanceof Date) {
				pstm.setDate(paramIndex++, (Date) o);
			} else if (o instanceof String) {
				pstm.setString(paramIndex++, (String) o);
			} else {
				pstm.setObject(paramIndex++, o);
			}
		}

		if (!reuseStatements) {
			lastPrepareUsed = null;
			lastSqlExecuted = null;
		}

		if (commandType == QUERY_SQL) {
			if (fetchSize > -1) {
				pstm.setFetchSize(fetchSize);
			}

			return pstm.executeQuery();
		} else if (commandType == EDIT_SQL) {
			return Boolean.valueOf(pstm.executeUpdate() > 0);
		} else {
			return new Boolean(pstm.execute());
		}
	}

	public NativeSQL appendSql(String s) {
		sqlBuf.append(s);

		return this;
	}

	public StringBuffer getSqlBuf() {
		return sqlBuf;
	}

	public boolean isScrollableResult() {
		return scrollableResult;
	}

	public void resetSqlBuf() {
		sqlBuf = new StringBuffer();
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public void setReuseStatements(boolean b) {
		this.reuseStatements = b;
	}

	public void setScrollableResult(boolean scrollableResult) {
		this.scrollableResult = scrollableResult;
	}

	public void setFillNamedParametersWithNull(boolean value) throws Exception {
		fillNamedParametersWithNull = value;
	}

	public JdbcWrapper getJdbcWrapper() {
		return jdbcWrapper;
	}

	@SuppressWarnings("rawtypes")
	public void loadSql(Class baseClass, String resourcePath) throws Exception {
		InputStream inStream = baseClass.getResourceAsStream(resourcePath);

		if (inStream == null) {
			throw new IllegalArgumentException("Arquivo de SQL não encontrado: " + baseClass.getName() + " -> " + resourcePath);
		}

		byte[] buf = new byte[1024];

		while (true) {
			int readen = inStream.read(buf);

			if (readen <= 0) {
				break;
			}

			sqlBuf.append(new String(buf, 0, readen, "ISO-8859-1"));
		}
	}

	public void removeSQLBlock(String commentKey) {
        String startComment = "/*" + commentKey;
        String endComment = commentKey + "*/";

        int posStartComment = sqlBuf.toString().indexOf(startComment);
        int posEndComment = 0;

        while((posEndComment = sqlBuf.toString().indexOf(endComment)) != -1) {
        	sqlBuf.delete(posStartComment, posEndComment + endComment.length());
        	posStartComment = sqlBuf.toString().indexOf(startComment);
        }
	}

	public static void releaseResources(NativeSQL o) {
		if (o != null) {
			for (Iterator<Object> ite = o.pstms.iterator(); ite.hasNext();) {
				PreparedStatement pstm = (PreparedStatement) ite.next();

				try {
					pstm.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	private Collection<Object> buildParameterList() {
		Collection<Object> result = new ArrayList<Object>();

		for (Iterator<Object> ite = parametersSequence.iterator(); ite.hasNext();) {
			String pName = (String) ite.next();

			if (!namedParameters.containsKey(pName)) {
				if( ! fillNamedParametersWithNull) {
					throw new IllegalArgumentException("Parâmetro '" + pName + "' não informado.");
				}
			}

			Object value = namedParameters.get(pName);

			result.add(value);
		}

		return result;
	}

	private void parseNamedParameters(StringBuffer sql) throws Exception {
		if (!namedParameters.isEmpty() || fillNamedParametersWithNull) {
			parametersSequence.clear();

			Matcher comments = Pattern.compile("/\\*.*?\\*/", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(sql);

			while (comments.find()) {
				sql.replace(comments.start(), comments.end(), "");
				comments.reset();
			}

			Matcher m = regexNamedParams.matcher(sql);

			while (m.find()) {
				String pName = m.group();
				pName = pName.substring(1).toUpperCase(); // retira o ':'

				if (!namedParameters.containsKey(pName)) {
					if( ! fillNamedParametersWithNull) {
						throw new IllegalStateException("NativeSQL: Parâmetro '" + pName + "' não informado.");
					}
				}

				parametersSequence.add(pName);

				sql.replace(m.start(), m.end(), "?");

				m.reset();
			}
		}
	}
}