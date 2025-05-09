package com.example.tiki.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    /*
    swagger에서 multipart/form-data로 넘겼을 때, dto와 file 두가지를 보낼 때 Content-type을 따로 설정해주지 못 하여
    application/octet-stream is not supported 에러가 발생하는 것을 막아주는 컨버터

    참조 : https://devsungwon.tistory.com/entry/Spring-MultipartFile%EC%9D%B4-%ED%8F%AC%ED%95%A8%EB%90%9C-DTO-requestBody%EB%A1%9C-%EC%9A%94%EC%B2%AD%EB%B0%9B%EA%B8%B0-swagger-%EC%9A%94%EC%B2%AD
     */

    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}