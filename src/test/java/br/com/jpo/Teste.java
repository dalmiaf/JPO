package br.com.jpo;

import java.lang.reflect.Field;

import org.junit.Test;

import br.com.jpo.service.DataSetService;
import br.com.jpo.service.ServiceManager;
import br.com.jpo.service.ServiceParameter;
import br.com.jpo.Documento.DadosAdicionais;
import br.com.jpo.Documento.Ocorrencia;
import br.com.jpo.transaction.annotation.JPOTransaction;

public class Teste {
	
	@Test
	public void testeReflectionInnerClass() {
		Documento doc = new Documento();
		Ocorrencia oco = new Ocorrencia();
		DadosAdicionais d = new DadosAdicionais();

		imprimirReflect(doc, "Documento:");
		imprimirReflect(oco, "Ocorrência:");
		imprimirReflect(d, "Dados adicionais:");
	}

	private void imprimirReflect(Object o, String txt) {
		// dados adicionais
		System.out.println();
		System.out.println(txt);
		Class objClass = o.getClass();
		Field[] fields = objClass.getDeclaredFields();

		for(int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			System.out.println(fields[i].getName());
		}
	}

	@Test
	public void testeReflection() throws Exception {
		ServiceParameter param = new ServiceParameter(DataSetService.class.getSimpleName(), "getMetadata", null, null);

		if (ServiceManager.containsAnnotation(param, JPOTransaction.class)) {
			System.out.println("SIM");
		}

	}

	@Test
	public void testeReplace() throws Exception {
		String where = "this.ATIVO = 'S'";

		where = where.replaceAll("this.", "Parceiro.");

		System.out.println(where);
	}
}
