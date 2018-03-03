package br.com.jpo;

import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;

import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.sql.ProcedureCaller;
import br.com.jpo.utils.JdbcUtils;

public class TesteProcedureCaller {

	private static Properties properties;

	static {
		properties = new Properties();

		properties.setProperty(ConnectionProvider.USER, "root");
		properties.setProperty(ConnectionProvider.URL, "jdbc:mysql://localhost:3306/CONTAS");
		properties.setProperty(ConnectionProvider.DRIVER, "com.mysql.jdbc.Driver");
		properties.setProperty(ConnectionProvider.PASSWORD, "javaseven");
	}

	@Test
	public void teste() throws Exception {
		executeOnReuseStatement();
		executeOffReuseStatement();
	}

	@Test
	public void executeOnReuseStatement() throws Exception {
		log("Iniciando reuseStatement ON: ");

		Connection con = null;
		ProcedureCaller caller = null;

		try{
			 con = JdbcUtils.getConnection(properties);

			 caller = new ProcedureCaller("sp_calcula_preco");
			 caller.setReuseStatements(true);

			 for (int i = 0; i <= 5000; i++) {
				 caller.addDBInputParameter(ProcedureCaller.DB_PARAM_NUMDEC, "VLRUNIT", i);

				 caller.addInputParameter(caller.getExecutionID());
				 caller.addOutputParameter(Types.FLOAT, "preco");

				 caller.execute(con);
				 caller.reset();

				 //System.out.println(caller.resultAsBigDecimal("preco"));
			 }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ProcedureCaller.releaseResources(caller);
			JdbcUtils.close(con);		
			log("Finalizando reuseStatement ON: ");
		}
	}

	@Test
	public void executeOffReuseStatement() throws Exception {
		log("Iniciando reuseStatement OFF: ");

		Connection con = null;

		try{
			 con = JdbcUtils.getConnection(properties);

			 for (int i = 0; i <= 5000; i++) {
				 ProcedureCaller caller = new ProcedureCaller("sp_calcula_preco");

				 caller.addDBInputParameter(ProcedureCaller.DB_PARAM_NUMDEC, "VLRUNIT", i);

				 caller.addInputParameter(caller.getExecutionID());
				 caller.addOutputParameter(Types.FLOAT, "preco");

				 caller.execute(con);

				 //System.out.println(caller.resultAsBigDecimal("preco"));
			 }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);
			log("Finalizando reuseStatement OFF: ");
		}
	}

	private void log(String msg) {
		long time = System.currentTimeMillis();

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		System.out.println(String.format(msg+" %s",format.format(new Date(time))));
	}
}
