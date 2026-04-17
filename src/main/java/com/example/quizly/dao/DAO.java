package com.example.quizly.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T, ID> {

    void save(T t);

    Optional<T> findById(ID id);

    List<T> findAll();

    void update(T t);

    void delete(Long id);
}