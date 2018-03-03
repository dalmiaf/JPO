package br.com.jpo.connection.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class PreparedStatementProxy implements InvocationHandler {

	private Map<Object, Object>               	parameters;
    private PreparedStatement 	delegatePstm;
    private String            	sql;
    private boolean           	reportSQLErros;

    private PreparedStatementProxy(PreparedStatement delegatePstm, String sql) {
        this.delegatePstm       = delegatePstm;
        this.parameters         = new TreeMap<Object, Object>();
        this.sql                = sql;
        this.reportSQLErros     = true;
    }

    public static PreparedStatement wrapStatement(Connection c, String sql, int resultSetType, int resultSetConcurrency) throws Exception {
        PreparedStatement          pstm = c.prepareStatement(sql, resultSetType, resultSetConcurrency);

        return wrapStatement(pstm, sql);
    }

    public static PreparedStatement wrapStatement(PreparedStatement pstm, String sql) throws Exception {
    	PreparedStatementProxy proxy = new PreparedStatementProxy(pstm, sql);

    	return ( PreparedStatement ) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class [] { PreparedStatement.class }, proxy);
    }

    public Object invoke(Object proxy, Method method, Object [] args) throws Throwable {
        try {
            Object result = null;

            if(method.getDeclaringClass().equals(PreparedStatementProxy.class)) {
                result = method.invoke(this, args);
            } else {
                boolean clearParam = false;

                if("setNull".equals(method.getName())){
                	parameters.put(args[ 0 ], null);
                } else if("setBigDecimal-setString-setTimestamp-setObject-setDate".indexOf(method.getName()) > -1) {
                    parameters.put(args[ 0 ], args[ 1 ]);
                } else if(method.getName().indexOf("execute") > -1) {
                    clearParam = true;
                }

                result = method.invoke(delegatePstm, args);

                if(clearParam) {
                    parameters.clear();
                }
            }

            return result;
        } catch(InvocationTargetException e) {
            reportError(method, e);
            throw e.getCause();
        }
    }

    public void setReportSQLErros(boolean b) {
        this.reportSQLErros = b;
    }

    private void reportError(Method m, Throwable error) {
        if(reportSQLErros) {
            System.out.println("* >> **************************************************************************");
            System.out.println("Erro ao executar método '" + m.getName() + "' em um PreparedStatement.");
            System.out.println("SQL: " + sql);
            System.out.println("Parâmetros usados:");

            for(Iterator<Entry<Object, Object>> ite = parameters.entrySet().iterator(); ite.hasNext();) {
                Map.Entry<Object, Object> entry = ( Entry<Object, Object> ) ite.next();
                Object    value = entry.getValue();

                System.out.print("param " + entry.getKey() + " -> " + ((value != null) ? (value.getClass() + " -> " + value) : "null"));
            }

            System.out.println("\nStacktrace:\n");

            error.printStackTrace();

            System.out.println("* << **************************************************************************");
        }
    }
}
