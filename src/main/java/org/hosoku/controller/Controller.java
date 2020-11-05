package org.hosoku.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hosoku.util.Constants;
import org.hosoku.util.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class Controller<T, P extends Serializable> {

    private T model;
    private P modelId;
    private JpaRepository<T, P> modelRepository;
    private Function<T, ? extends Response> responseFunction;

    public Controller (JpaRepository<T,P> repository, Function<T, ? extends Response> responseFunction){
        this.modelRepository = repository;
        this.responseFunction  = responseFunction;
    }

    public List<T> getAllList(int currentPage, int size, String direction, String attribute ) {
        int copyOfCurrentPage = currentPage;
        int copyOfSize = size;
        String copyOfDirection = direction;
        String copyOfAttribute = attribute;


        if (copyOfSize == 0) {
            copyOfSize = Constants.PAGINATION_DEFAULT_SIZE;
        }

        if (direction == null || !StringUtils.hasText(direction)) {
            direction = "ASC";
        }

        List<String> attributeList = new LinkedList<>();
        attributeList.add(attribute);

        Optional<Pageable> pageable = Optional.empty();

        if (direction.equalsIgnoreCase("ASC")) {
            pageable = Optional.ofNullable(PageRequest.of(currentPage, size, Sort.by(attribute).ascending()));

        } else if (direction.equalsIgnoreCase("DESC"))
            pageable = Optional.ofNullable(PageRequest.of(currentPage, size, Sort.by(attribute).descending()));


        List<T> all;
        if (pageable.isPresent())
            all = (List<T>) modelRepository.findAll(pageable.get()).get().collect(Collectors.toList());
        else
            all = modelRepository.findAll();
        return all;
    }

    public ResponseEntity<?> getAllModel(int currrentPage, int size, String direction, String attribute ){
        List<T> all = getAllList(currrentPage, size, direction, attribute);
        if (all.isEmpty()){
            return ResponseEntity.noContent().build();
        } else{
            List<? extends Response> responses = all.stream()
                    .map(response ->  responseFunction.apply(response)).collect(Collectors.toList());
            return ResponseEntity.ok().body(responses);
        }
    }



    public ResponseEntity<?> getById( P id){
        Optional<T> optional = modelRepository.findById(id);

        if (optional.isPresent()){
            return ResponseEntity.ok(responseFunction.apply(optional.get()));
        } else{
            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<?> createModel(T object){

        T savedObject = (T) modelRepository.save(object);

        return new ResponseEntity<>(responseFunction.apply(savedObject), HttpStatus.CREATED);

    }

    public ResponseEntity<?> editModel(Map<String,Object> map, P id){

        if (modelRepository.existsById(id)){

            T objectToBeEdited = (T) modelRepository.findById(id).get();

            ObjectMapper objectMapper = new ObjectMapper();

            T copiedUser = (T) objectMapper.convertValue(map, model.getClass());

            copyProperties(copiedUser, objectToBeEdited, map.keySet());

            T editedObject = (T) modelRepository.saveAndFlush(objectToBeEdited);
            Map<String, Object> response = new HashMap<>();
            response.put("status","success");
            response.put("result", responseFunction.apply(editedObject));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> deleteObjectById(P id){

        if (modelRepository.existsById(id)){
            modelRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }


    public static void copyProperties(Object src, Object trg, Set<String> props) {

        String[] excludedProperties =
                Arrays.stream(BeanUtils.getPropertyDescriptors(src.getClass()))
                        .map(PropertyDescriptor::getName)
                        .filter(name -> !props.contains(name))
                        .toArray(String[]::new);
        System.out.println(Arrays.toString(excludedProperties));
        BeanUtils.copyProperties(src, trg, excludedProperties);

    }

}
