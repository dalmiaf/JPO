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
import br.com.jpo.utils.BigDecimalUtils;
import br.com.jpo.utils.JdbcUtils;
import br.com.jpo.utils.SQLUtils;
import br.com.jpo.utils.StringUtils;

public class SequenceKeyGenerator implements KeyGenerator {

	@Override
	public Object generateKey(KeyGenerateEvent event) throws PersistenceException {
		Object key = null;
		PreparedStatement pstm = null;
		ResultSet rset = null;

		try {
			DynamicBean dynamicBean = event.getDynamicBean();
			EntityDAO entityDAO = event.getEntityDAO();
			JdbcWrapper jdbcWrapper = event.getJdbcWrapper();

			EntityMetadata entityMetadata = entityDAO.getInstanceMetadata().getEntityMetadata();
			EntityKeyMetadata entityKeyMetadata =  entityMetadata.getEntityKeyMetadata();
			EntityColumnMetadata columnMetadata = entityMetadata.getEntityColumnMetadata(entityKeyMetadata.getKeyField());

			String command = buildSQLCommand(entityDAO);

			pstm = jdbcWrapper.getPreparedStatementForSearch(command);

			if (entityKeyMetadata.getKeyMembers().size() > 1) {
				int paramIndex = 1;

				for (String keyMember: entityKeyMetadata.getKeyMembers()) {
					if (columnMetadata.getName().equals(keyMember)) {
						continue;
					}

					Object value = dynamicBean.getAttribute(keyMember);
					SQLUtils.setParameters(pstm, value, paramIndex++);
				}
			}

			rset = pstm.executeQuery();

            if(rset.next()) {
                key = BigDecimalUtils.getBigDecimalOrZero(rset.getBigDecimal(columnMetadata.getName())).add(BigDecimal.ONE);
            } else {
                throw new PersistenceException("ResultSet vazio ao gerar sequência para a entidade '" + entityMetadata.getName() + "'");
            }

            dynamicBean.setAttribute(columnMetadata.getName(), key);

		} catch(Exception ex) {
			PersistenceException.throwMe(ex);
		} finally {
			JdbcUtils.close(rset);
			JdbcUtils.close(pstm);
		}

		return key;
	}

	private String buildSQLCommand(EntityDAO entityDAO) throws Exception {
		EntityMetadata entityMetadata = entityDAO.getInstanceMetadata().getEntityMetadata();
		EntityKeyMetadata entityKeyMetadata =  entityMetadata.getEntityKeyMetadata();
		EntityColumnMetadata columnMetadata = entityMetadata.getEntityColumnMetadata(entityKeyMetadata.getKeyField());

		StringBuffer command = new StringBuffer();

		command.append("SELECT MAX(${nomeCampo}) AS ${nomeCampo} FROM ${nomeTabela}");

		StringUtils.replaceString("${nomeTabela}", entityMetadata.getName(), command, true);
		StringUtils.replaceString("${nomeCampo}", columnMetadata.getName(), command, true);

		if (entityKeyMetadata.getKeyMembers().size() > 1) {
			command.append(" WHERE ");

			boolean isFirstMember = true;

			for (String keyMember: entityKeyMetadata.getKeyMembers()) {
				if (columnMetadata.getName().equals(keyMember)) {
					continue;
				}

				if (!isFirstMember) {
					command.append(" AND ");
				}

				command.append(keyMember);
				command.append(" = ?");

				isFirstMember = false;
			}
		}

		return command.toString();
	}
}