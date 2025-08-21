package com.noe.apinoe2.service;

import java.util.List;
import java.util.Optional;

/**
 * Interface base para servicios que proporciona operaciones CRUD básicas
 * @param <E> Tipo de la entidad
 * @param <ID> Tipo del identificador
 */
public interface BaseService<E, ID> {
    
    List<E> findAll();
    
    Optional<E> findById(ID id);
    
    E save(E entity);
    
    E update(ID id, E entity);
    
    void deleteById(ID id);
    
    boolean existsById(ID id);
}
