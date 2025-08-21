package com.noe.apinoe2.mapper;

import java.util.List;

/**
 * Interface base para mappers que convierten entre entidades y DTOs
 * @param <E> Tipo de la entidad
 * @param <D> Tipo del DTO
 */

public interface BaseMapper <E, D> {

    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDtoList(List<E> entities);

    List<E> toEntityList(List<D> dtos);
    
}
