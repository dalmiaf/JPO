package br.com.jpo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;

import br.com.jpo.connection.ConnectionProvider;
import br.com.jpo.metadata.entity.Metadata;
import br.com.jpo.utils.JdbcUtils;

public class TesteConectionJDBC {

	private static Properties properties;

	static {
		properties = new Properties();

		properties.setProperty(ConnectionProvider.USER, "root");
		properties.setProperty(ConnectionProvider.URL, "jdbc:mysql://localhost:3306/CONTAS");
		properties.setProperty(ConnectionProvider.DRIVER, "com.mysql.jdbc.Driver");
		properties.setProperty(ConnectionProvider.PASSWORD, "javaseven");
	}

	public static void main(String args[]) {
		TesteConectionJDBC teste = new TesteConectionJDBC();
		teste.testeConnectionJDBC();
	}

	@Test
	public void testeIsDataBaseConnection() {
		Connection con = null;

		try{
			 con = JdbcUtils.getConnection(properties);

			if(con != null){
				System.out.println("Connecting is MySql: "+JdbcUtils.isDataBaseConnection(con, Metadata.MYSQL_DIALECT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);			
		}
	}

	@Test
	public void testeConnectionJDBC() {
		Connection con = null;

		try{
			 con = JdbcUtils.getConnection(properties);

			if(con != null){
				String query = "SELECT * FROM GRUPOUSUARIO";
				PreparedStatement statement = con.prepareStatement(query);

				ResultSet rs = statement.executeQuery();

				while (rs.next()) {
					System.out.println(rs.getString("DESCRGRUPO"));
				}

				statement.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);			
		}
	}

	@Test
	public void testeResultSetMetadataForQuery() {
		Connection con = null;

		try{
			con = JdbcUtils.getConnection(properties);

			if(con != null){
				PreparedStatement tddTabStatement 	= con.prepareStatement("SELECT CODGRUPOUSUARIO AS CODIGO, DESCRGRUPO AS DESCRICAO FROM GRUPOUSUARIO");
				ResultSet rs						= tddTabStatement.executeQuery();

				ResultSetMetaData resultSetMetaData = rs.getMetaData();

				while (rs.next()) {
					for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
						imprimirMetadados(rs, resultSetMetaData, i);
					}
				}

				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);			
		}
	}

	@Test
	public void testeResultSetMetaData() {
		Connection con = null;

		try{
			con = JdbcUtils.getConnection(properties);

			if(con != null){
				PreparedStatement tddTabStatement 	= con.prepareStatement("SELECT * FROM TDDTABELA");
				ResultSet tddTabResultSet			= tddTabStatement.executeQuery();

				PreparedStatement tddCamStatement 	= null;
				ResultSet tddCamResultSet			= null;
				ResultSetMetaData tddCamMetaData 	= null;

				while (tddTabResultSet.next()) {
					String nomeTab = tddTabResultSet.getString("NOMETABELA");

					tddCamStatement = con.prepareStatement("SELECT * FROM TDDCAMPO WHERE NOMETABELA = ?");
					tddCamStatement.setObject(1, nomeTab);

					tddCamResultSet = tddCamStatement.executeQuery();
					tddCamMetaData	= tddCamResultSet.getMetaData();

					for(int i = 1; i <= tddCamMetaData.getColumnCount(); i++){
						imprimirMetaDados(tddCamMetaData, i);
					}
				}

				tddTabResultSet.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);			
		}
	}

	@Test
	public void testMetadata() {
		Connection con = null;

		try{
			con = JdbcUtils.getConnection(properties);

			if(con != null){
				DatabaseMetaData databaseMetadata = con.getMetaData();
				
				ResultSet tablesResultSet = databaseMetadata.getTables(null, null, "GRUPOUSUARIO", new String[]{"TABLE"});
				ResultSet columnsResultSet = databaseMetadata.getColumns(null, null, "GRUPOUSUARIO", null);
				ResultSet primaryKeysResultSet = databaseMetadata.getPrimaryKeys(null, null, "GRUPOUSUARIO");
				
				/*Tables*/
				System.out.println();
				System.out.println("### TABLES ###");
				if (tablesResultSet.next()) {
					ResultSetMetaData resultSetMetaData = tablesResultSet.getMetaData();
					
					for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
						imprimirMetadados(tablesResultSet, resultSetMetaData, i);
					}
				}
				
				/*Columns*/
				System.out.println();
				System.out.println("### COLUMNS ###");
				while (columnsResultSet.next()) {
					ResultSetMetaData resultSetMetaData = columnsResultSet.getMetaData();
					
					for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
						imprimirMetadados(columnsResultSet, resultSetMetaData, i);
					}
					System.out.println();
				}
				
				/*PrimaryKeys*/
				System.out.println();
				System.out.println("### PrimaryKeys ###");
				while (primaryKeysResultSet.next()) {
					ResultSetMetaData resultSetMetaData = primaryKeysResultSet.getMetaData();
					
					for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
						imprimirMetadados(primaryKeysResultSet, resultSetMetaData, i);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);			
		}
	}

	private void imprimirMetaDados(ResultSetMetaData metaData, int reg) throws SQLException {
		System.out.println(
				"getColumnName: "+ metaData.getColumnName(reg) +" - "+
				"getColumnClassName: "+ metaData.getColumnClassName(reg) +" - "+
				"getColumnDisplaySize: "+ metaData.getColumnDisplaySize(reg) +" - "+
				"getColumnLabel: "+ metaData.getColumnLabel(reg) +" - "+
				"getPrecision: "+ metaData.getPrecision(reg) +" - "+
				"getScale: "+ metaData.getScale(reg) +" - "+
				"getColumnType: "+ metaData.getColumnType(reg) +" - "+
				"isAutoIncrement: "+ metaData.isAutoIncrement(reg)
				);
	}

	private void imprimirMetadados(ResultSet resultSet, ResultSetMetaData resultSetMetaData, int index) throws SQLException {
		System.out.println(
				"Name: "+resultSetMetaData.getColumnName(index) +" - "+
				"Label: "+resultSetMetaData.getColumnLabel(index) +" - "+
				"Value: "+resultSet.getObject(resultSetMetaData.getColumnLabel(index))
				);
	}
}
