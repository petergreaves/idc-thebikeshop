package com.ibm.commerce.ue.rest;

import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonCustomProvider implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	public JacksonCustomProvider() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}
}