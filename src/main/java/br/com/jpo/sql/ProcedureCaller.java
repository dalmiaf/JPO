package br.com.jpo.sql;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.jpo.utils.JdbcUtils;
import br.com.jpo.utils.UIDGenerator;

public class ProcedureCaller {

	public static final String					DB_PARAM_TEXT					= "S";
	public static final String					DB_PARAM_NUMINT					= "I";
	public static final String					DB_PARAM_NUMDEC					= "F";
	public static final String					DB_PARAM_DATE					= "D";

	private static final String 				PROCEDURE_CALLER_EXECUTE        ="ProcedureCaller.execute";
	private static final String 				PROCEDURE_CALLER_INSERT		    ="ProcedureCaller.insertDBParameters";
	private static final String 				PROCEDURE_CALLER_DELETE         ="ProcedureCaller.deleteDBParameters";

	private String								name;
	private String								execucutionId;
	private StringBuffer						placeHolders;
	private Collection<ProcedureParam>			params;
	private Map<String, Object>					returns;
	private Collection<DBParam>					dbParams;
	private Map<String, Statement>				cache;
	private boolean								reuseStatements;

	public ProcedureCaller(String name) {
		this.name = name;
		this.execucutionId = UIDGenerator.getNextID();
		this.placeHolders = new StringBuffer();
		this.params = new ArrayList<ProcedureParam>();
		this.returns = new HashMap<String, Object>();
		this.dbParams = new ArrayList<DBParam>();
		this.cache = new HashMap<String, Statement>();
	}

	public void addOutputParameter(int type, String name) {
		addParam(new ProcedureParam(params.size() + 1, true, null, type, name));
	}

	public void addDBInputParameter(String type, String name, Object value) throws Exception {
		addDBInputParameter(type, 0, name, value);
	}

	public void addDBInputParameter(String type, int row, String name, Object value) throws Exception {
		dbParams.add(new DBParam(type, row, name, value));
	}

	public void changeParamValue(int index, Object newValue) {
		if(index >= 0 && index < dbParams.size()){
			ProcedureParam p = (ProcedureParam) params.toArray()[index];
			p.value = newValue;
		}
	}

	public void addInputParameter(Object p) {
		addParam(new ProcedureParam(params.size() + 1, false, p, 0, null));
	}

	public Object result(String paramName) {
		return returns.get(paramName);
	}

	public String resultAsString(String paramName) {
		return (String) result(paramName);
	}

	public BigDecimal resultAsBigDecimal(String paramName) {
		return (BigDecimal) result(paramName);
	}

	public Timestamp resultAsTimestamp(String paramName) {
		return (Timestamp) result(paramName);
	}

	public ResultSet resultAsResultSet(String paramName) {
		return (ResultSet) result(paramName);
	}

	private void addParam(ProcedureParam p) {
		params.add(p);

		if (placeHolders.length() > 0) {
			placeHolders.append(", ");
		}

		placeHolders.append("?");
	}

	public boolean hasDBParams() {
		return dbParams.size() > 0;
	}

	public void setReuseStatements(boolean reuseStatements) {
		this.reuseStatements = reuseStatements;
	}

	public void reset() {
		this.execucutionId = UIDGenerator.getNextID();
		this.returns = new HashMap<String, Object>();
		this.dbParams = new ArrayList<DBParam>();
		this.params = new ArrayList<ProcedureParam>();
	}

	public static void releaseResources(ProcedureCaller caller) {
		for (Iterator<Entry<String,Statement>> it = caller.cache.entrySet().iterator(); it.hasNext();) {
			Statement st = it.next().getValue();

			JdbcUtils.close(st);
		}
	}

	public CallableStatement execute(Connection c) throws Exception {
		CallableStatement cstmt = null;

		try {
			insertDBParameters(c);

			if (reuseStatements) {
				cstmt = (CallableStatement) cache.get(PROCEDURE_CALLER_EXECUTE);

				if (cstmt == null) {
					cstmt = c.prepareCall(String.format("{call %s(%s)}", new Object[] { name, placeHolders.toString() }));

					cache.put(PROCEDURE_CALLER_EXECUTE, cstmt);
				} else {
					cstmt.clearParameters();
					cstmt.clearWarnings();
				}
			} else {
				cstmt = c.prepareCall(String.format("{call %s(%s)}", new Object[] { name, placeHolders.toString() }));
			}

			for (Iterator<ProcedureParam> ite = params.iterator(); ite.hasNext();) {
				ProcedureParam p = ite.next();

				if (p.output) {
					cstmt.registerOutParameter(p.index, p.type);
				} else {
					if (p.value == null) {
						cstmt.setObject(p.index, p.value);
					} else {
						if (p.value.getClass() == BigDecimal.class) {
							cstmt.setBigDecimal(p.index, (BigDecimal) p.value);
						} else if (p.value.getClass() == String.class) {
							cstmt.setString(p.index, (String) p.value);
						} else if (p.value.getClass() == Timestamp.class) {
							cstmt.setTimestamp(p.index, (Timestamp) p.value);
						} else {
							cstmt.setObject(p.index, p.value);
						}
					}
				}
			}

			cstmt.execute();

			for (Iterator<ProcedureParam> ite = params.iterator(); ite.hasNext();) {
				ProcedureParam p = ite.next();

				if (p.output) {
					returns.put(p.name, p.getValue(cstmt));
				}
			}

			return cstmt;

		} finally {
			if (!reuseStatements) {
				JdbcUtils.close(cstmt);
			}

			deleteDBParameters(c);
		}
	}

	private void insertDBParameters(Connection c) throws Exception {
		PreparedStatement pstm = null;

		try {
			if (reuseStatements) {
				pstm = (PreparedStatement) cache.get(PROCEDURE_CALLER_INSERT);

				if (pstm == null) {
					pstm = c.prepareStatement("INSERT INTO EXECPARAMS(IDSESSAO, SEQUENCIA, NOME, TIPO, NUMINT, NUMDEC, TEXTO, DTA) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");

					cache.put(PROCEDURE_CALLER_INSERT, pstm);
				} else {
					pstm.clearParameters();
					pstm.clearWarnings();
				}
			} else {
				pstm = c.prepareStatement("INSERT INTO EXECPARAMS(IDSESSAO, SEQUENCIA, NOME, TIPO, NUMINT, NUMDEC, TEXTO, DTA) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
			}

			for (Iterator<DBParam> ite = dbParams.iterator(); ite.hasNext();) {
				DBParam dbParam = ite.next();

				pstm.setString(1, execucutionId);
				pstm.setBigDecimal(2, new BigDecimal(dbParam.row));
				pstm.setString(3, dbParam.name);
				pstm.setString(4, dbParam.type);
				pstm.setNull(5, Types.NUMERIC);
				pstm.setNull(6, Types.NUMERIC);
				pstm.setNull(7, Types.VARCHAR);
				pstm.setNull(8, Types.TIMESTAMP);
				
				if (DB_PARAM_NUMINT.equals(dbParam.type)) {
					pstm.setInt(5, ((Integer) dbParam.value).intValue());
				} else if (DB_PARAM_NUMDEC.equals(dbParam.type)) {
					Object value = dbParam.value;
					pstm.setBigDecimal(6, value instanceof BigDecimal ? (BigDecimal) value : BigDecimal.valueOf(((Number) value).doubleValue()));
				} else if (DB_PARAM_TEXT.equals(dbParam.type)) {
					pstm.setString(7, (String) dbParam.value);
				} else if (DB_PARAM_DATE.equals(dbParam.type)) {
					pstm.setTimestamp(8, (Timestamp) dbParam.value);
				}
				
				pstm.executeUpdate();
			}
		} finally {
			if (!reuseStatements) {
				JdbcUtils.close(pstm);
			}
		}
	}

	private void deleteDBParameters(Connection c) throws Exception {
		PreparedStatement pstm = null;

		try {
			if (reuseStatements) {
				pstm = (PreparedStatement) cache.get(PROCEDURE_CALLER_DELETE);

				if (pstm == null) {
					pstm = c.prepareStatement("DELETE FROM EXECPARAMS WHERE IDSESSAO = ?");

					cache.put(PROCEDURE_CALLER_DELETE, pstm);
				} else {
					pstm.clearParameters();
					pstm.clearWarnings();
				}
			} else {
				pstm = c.prepareStatement("DELETE FROM EXECPARAMS WHERE IDSESSAO = ?");
			}

			pstm.setString(1, execucutionId);
			pstm.executeUpdate();

		} finally {
			if (!reuseStatements) {
				pstm.close();
			}
		}
	}

	public String getExecutionID() {
		return execucutionId;
	}

	private class ProcedureParam {
		int		index;
		int		type;
		boolean	output;
		Object	value;
		String	name;

		ProcedureParam(int index, boolean output, Object value, int type, String name) {
			this.index = index;
			this.type = type;
			this.output = output;
			this.value = value;
			this.name = name;
		}

		Object getValue(CallableStatement cstmt) throws Exception {
			switch (type) {
				case Types.INTEGER:
				case Types.FLOAT:
				case Types.DOUBLE:
				case Types.NUMERIC:
				case Types.DECIMAL:
					return cstmt.getBigDecimal(index);
				case Types.CHAR:
				case Types.VARCHAR:
					return cstmt.getString(index);
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					return cstmt.getTimestamp(index);
			}

			return cstmt.getObject(index);
		}
	}

	private class DBParam {
		String	type;
		int		row;
		String	name;
		Object	value;

		public DBParam(String type, int row, String name, Object value) {
			this.type = type;
			this.row = row;
			this.name = name;
			this.value = value;
		}
	}
}
