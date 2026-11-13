package com.x.base.core.project.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class MessageBodyReaderImpl<T> implements MessageBodyReader<T> {

	private Gson gson = XGsonBuilder.instance();

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream stream)
			throws IOException, WebApplicationException {
		try {
			// UTF-8 only
			return gson.fromJson(new InputStreamReader(stream, "UTF-8"), type);
		} catch (Exception e) {
			// 이 메서드는 JAXRS 메서드 전에 실행되어 예외를 잡을 수 없음
			e.printStackTrace();
			return null;
		}
	}
}