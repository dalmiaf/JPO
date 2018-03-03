package br.com.jpo.service;

import br.com.jpo.bean.DynamicBean;
import br.com.jpo.metadata.entity.event.InstanceListenerAdapter;
import br.com.jpo.metadata.entity.event.PersistenceInstanceEvent;
import br.com.jpo.service.helper.DataSetHelper;
import br.com.jpo.service.interfaces.ServiceBase;
import br.com.jpo.session.JPOSession;
import br.com.jpo.transaction.annotation.JPOTransaction;

import com.google.gson.JsonObject;

public class DataSetService extends ServiceBase {

	public DataSetService(JPOSession session) {
		super(session);
	}

	public JsonObject getMetadata(JsonObject request) {
		DataSetHelper helper = new DataSetHelper(session);
		return helper.getMetadata(request);
	}

	public JsonObject loadRecords(JsonObject request) {
		DataSetHelper helper = new DataSetHelper(session);
		return helper.getLoadRecords(request);
	}

	@JPOTransaction
	public JsonObject saveOrUpdateRecord(JsonObject request) throws Exception {
		return null;
	}

	@JPOTransaction
	public JsonObject deleteRecord(JsonObject request) {
		return null;
	}

	@JPOTransaction
	public void teste(JsonObject request) throws Exception {
		testeTransaction2();
	}

	@JPOTransaction
	public void teste2(JsonObject request) throws Exception {
		testeTransaction3();
	}

	private void testeTransaction() throws Exception {
		DynamicBean bean = session.getDefaultBean("Parceiro");

		bean.setAttribute("NOMEPARCEIRO", "Teste Session Save");
		bean.setAttribute("CLIENTE", "N");
		bean.setAttribute("FORNECEDOR", "S");
		bean.setAttribute("CPFCNPJ", "66666666666");
		bean.setAttribute("ATIVO", "S");

		session.save(bean);

		boolean pararTransacao = false;

		if (pararTransacao) {
			throw new Exception("Parando transação!!!!");
		}
	}

	private void testeTransaction2() throws Exception {
		DynamicBean bean = session.getDefaultBean("GrupoUsuario");

		bean.setAttribute("DESCRGRUPO", "Teste Session Save 01");

		session.save(bean);

		boolean pararTransacao = false;

		if (pararTransacao) {
			throw new Exception("Parando transação!!!!");
		}
	}

	private void testeTransaction3() throws Exception {
		DynamicBean bean = session.getDefaultBean("GrupoUsuario");

		bean.setAttribute("DESCRGRUPO", "Teste Session Save 02");

		session.save(bean);

		boolean pararTransacao = false;

		if (pararTransacao) {
			throw new Exception("Parando transação!!!!");
		}
	}

	private static class ParceiroInstanceListener extends InstanceListenerAdapter  {

		@Override
		public void beforeInsert(PersistenceInstanceEvent event) throws Exception {
			DynamicBean bean = event.getBean();

			bean.setAttribute("CLIENTE", "S");
			bean.setAttribute("ATIVO", "N");
		}
	}
}