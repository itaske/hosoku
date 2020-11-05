package org.hosoku.controller;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.xml.ws.Response;
import java.io.Serializable;

@Data
public class Controller<T, P extends Serializable> {

    private T model;
    private P modelId;
    private JpaRepository<T, P> modelRepository;
    private Response<T> modelReponse;

}
