package org.hosoku.controller.util;

import org.springframework.http.ResponseEntity;

public interface Response<T> {

    ResponseEntity<T> createResponse(T model);
}
