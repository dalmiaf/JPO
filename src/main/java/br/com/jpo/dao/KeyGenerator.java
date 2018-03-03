package br.com.jpo.dao;


public interface KeyGenerator {

	String KEY_GENERATOR = "KeyGenerator";

	Object generateKey(KeyGenerateEvent event) throws PersistenceException;
}
