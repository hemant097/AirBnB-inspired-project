package com.example.project.airbnbapp.Advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        System.out.println("supports: " + converterType.getName());
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,ServerHttpRequest request, ServerHttpResponse response) {

//        System.out.println(">>> beforeBodyWrite called with body = "+body);

    //Oneliner Summary : ResponseEntity is consumed before ResponseBodyAdvice; only the body survives.
        if(body instanceof APIResponse<?>)
            return body;

        if (body instanceof APIError error) {
            return new APIResponse<>(error);
        }

        return new APIResponse<>(body);
    }
}
