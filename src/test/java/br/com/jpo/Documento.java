package br.com.jpo;

import java.math.BigDecimal;

public class Documento {

	String nome;
	BigDecimal cpf;
	Ocorrencia ocorrencia;

	public static class Ocorrencia {
		int numOcorrencia;
		String descrOcorrencia;
		DadosAdicionais dadosAdicionais;
	}

	public static class DadosAdicionais {
		String endereco;
		String complemento;
	}
}
