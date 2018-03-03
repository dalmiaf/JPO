package br.com.jpo.bean;

import java.io.Serializable;

public interface DynamicObject extends Serializable {

	String getName();

	void setName(String name);
}