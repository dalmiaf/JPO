package br.com.jpo.dao.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.connection.impl.JdbcWrapper;
import br.com.jpo.dao.EntityDAO;
import br.com.jpo.dao.KeyGenerateEvent;
import br.com.jpo.dao.KeyGenerator;
import br.com.jpo.dao.PersistenceException;
import br.com.jpo.metadata.entity.EntityColumnMetadata;
import br.com.jpo.metadata.entity.EntityKeyMetadata;
import br.com.jpo.metadata.entity.EntityMetadata;
import br.com.jpo.transaction.JPOTransactionLock;
import br.com.jpo.transaction.JPOTransactionLockContext;
import br.com.jpo.transaction.impl.JPOTransactionLockContextImpl;
import br.com.jpo.utils.BigDecimalUtils;
import br.com.jpo.utils.JdbcUtils;
import br.com.jpo.utils.StringUtils;

public class TableKeyGenerator implements KeyGenerator {

	private StringBuffer sqlCommandLastKey;
	private StringBuffer sqlCommandPreviousKey;
	private StringBuffer sqlCommandUpdateKey;

	@Override
	public Object generateKey(KeyGenerateEvent event) throws PersistenceException {
		Object key = null;
		PreparedStatement pstmLastKey = null;
		PreparedStatement pstmPreviousKey = null;
		ResultSet rset = null;

		try {
			registryLock(event);

			DynamicBean dynamicBean = event.getDynamicBean();
			EntityDAO entityDAO = event.getEntityDAO();
			JdbcWrapper jdbcWrapper = event.getJdbcWrapper();

			initializeSQLCommand(entityDAO);

			EntityMetadata entityMetadata = entityDAO.getInstanceMetadata().getEntityMetadata();
			EntityKeyMetadata entityKeyMetadata =  entityMetadata.getEntityKeyMetadata();
			EntityColumnMetadata columnMetadata = entityMetadata.getEntityColumnMetadata(entityKeyMetadata.getKeyField());

			pstmLastKey = jdbcWrapper.getPreparedStatementForSearch(sqlCommandLastKey.toString());
			pstmLastKey.setString(1, entityMetadata.getName());

			BigDecimal previousKey = BigDecimal.ZERO;
			BigDecimal lastKey = BigDecimal.ZERO;

			rset = pstmLastKey.executeQuery();

            if(rset.next()) {
                lastKey = BigDecimalUtils.getBigDecimalOrZero(rset.getBigDecimal(EntityKeyMetadata.ULTIMACHAVE));
            }

            previousKey = lastKey.add(BigDecimal.ONE);

            pstmPreviousKey = jdbcWrapper.getPreparedStatementForSearch(sqlCommandPreviousKey.toString());

            key = getPreviousKey(previousKey, pstmPreviousKey);

            updateLastKey(jdbcWrapper, entityDAO, (BigDecimal) key);

            dynamicBean.setAttribute(columnMetadata.getName(), key);

		} catch(Exception ex) {
			PersistenceException.throwMe(ex);
		} finally {
			JdbcUtils.close(rset);
			JdbcUtils.close(pstmLastKey);
			JdbcUtils.close(pstmPreviousKey);
		}

		return key;
	}

	private void initializeSQLCommand(EntityDAO entityDAO) throws Exception {
		EntityMetadata entityMetadata = entityDAO.getInstanceMetadata().getEntityMetadata();
		EntityKeyMetadata entityKeyMetadata =  entityMetadata.getEntityKeyMetadata();
		EntityColumnMetadata columnMetadata = entityMetadata.getEntityColumnMetadata(entityKeyMetadata.getKeyField());

		sqlCommandLastKey = new StringBuffer();
		sqlCommandLastKey.append("SELECT ULTIMACHAVE FROM TDDCHAVE WHERE NOMETABELA = ?");

		sqlCommandPreviousKey = new StringBuffer();
		sqlCommandPreviousKey.append("SELECT DISTINCT ${nomeCampo} FROM ${nomeTabela} WHERE ${nomeCampo} IS NOT NULL AND ${nomeCampo} >= ?");
		StringUtils.replaceString("${nomeTabela}", entityMetadata.getName(), sqlCommandPreviousKey, true);
		StringUtils.replaceString("${nomeCampo}", columnMetadata.getName(), sqlCommandPreviousKey, true);

		sqlCommandUpdateKey = new StringBuffer();
		sqlCommandUpdateKey.append("UPDATE TDDCHAVE SET ULTIMACHAVE = ? WHERE NOMETABELA = ?");
	}

	private BigDecimal getPreviousKey(BigDecimal previous, PreparedStatement pstm) throws Exception {
		laco: do {
			pstm.setBigDecimal(1, previous);
			pstm.setMaxRows(100);

			ResultSet rsetConfere = pstm.executeQuery();

			if (rsetConfere.next()) {
				do {
					if (rsetConfere.getBigDecimal(1).compareTo(previous) != 0) {
						break laco;
					}
					previous = previous.add(BigDecimal.ONE);
				} while (rsetConfere.next());
			} else {
				break laco;
			}

			rsetConfere.close();
		} while (true);

		return previous;
	}

	private void updateLastKey(JdbcWrapper jdbcWrapper, EntityDAO entityDAO, BigDecimal key) throws Exception {
		PreparedStatement pstm = null;

		try {
			EntityMetadata entityMetadata = entityDAO.getInstanceMetadata().getEntityMetadata();

			pstm = jdbcWrapper.getPreparedStatementForSearch(sqlCommandUpdateKey.toString());
			pstm.setBigDecimal(1, key);
			pstm.setString(2, entityMetadata.getName());

			pstm.executeUpdate();
		} finally {
			JdbcUtils.close(pstm);
		}
	}

	private void registryLock(KeyGenerateEvent event) throws Exception {
		String resourceName = "TableKeyGenerator:TDDCHAVE:"+event.getDynamicBean().getName();

		JPOTransactionLock transactionLock = event.getTransactionLock();
		JPOTransactionLockContext context = new JPOTransactionLockContextImpl(resourceName, true);

		transactionLock.lockResource(context);
	}

}