package com.noe.apinoe2.controller.base;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.noe.apinoe2.dto.ApiResponse;
import com.noe.apinoe2.mapper.BaseMapper;
import com.noe.apinoe2.service.BaseService;

import jakarta.validation.Valid;

/**
 * Controlador base genérico que proporciona operaciones CRUD estándar
 * @param <E> Tipo de la entidad
 * @param <D> Tipo del DTO
 * @param <ID> Tipo del identificador
 */
public abstract class BaseController<E, D, ID> {

    protected final BaseService<E, ID> service;
    protected final BaseMapper<E, D> mapper;
    
    public BaseController(BaseService<E, ID> service, BaseMapper<E, D> mapper) {
        this.service = service;
        this.mapper = mapper;
    }
    
    protected String getEntityName() {
        return "Entidad";
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<D>>> getAll() {
        List<E> entities = service.findAll();
        List<D> dtos = entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<D>> getById(@PathVariable ID id) {
        Optional<E> entity = service.findById(id);
        
        if (entity.isPresent()) {
            D dto = mapper.toDto(entity.get());
            return ResponseEntity.ok(ApiResponse.success(dto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(getEntityName() + " no encontrado con id: " + id));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<D>> create(@Valid @RequestBody D dto) {
        try {
            validateBeforeCreate(dto);
            
            E entity = mapper.toEntity(dto);
            E savedEntity = service.save(entity);
            D savedDto = mapper.toDto(savedEntity);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(getEntityName() + " creado exitosamente", savedDto));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear " + getEntityName().toLowerCase() + ": " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<D>> update(@PathVariable ID id, @Valid @RequestBody D dto) {
        try {
            validateBeforeUpdate(id, dto);
            
            Optional<E> existingEntity = service.findById(id);
            
            if (existingEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(getEntityName() + " no encontrado con id: " + id));
            }
            
            E entity = existingEntity.get();
            mapper.updateEntityFromDto(entity, dto);
            
            E updatedEntity = service.update(id, entity);
            D updatedDto = mapper.toDto(updatedEntity);
            
            return ResponseEntity.ok(ApiResponse.success(getEntityName() + " actualizado exitosamente", updatedDto));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(getEntityName() + " no encontrado con id: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al actualizar " + getEntityName().toLowerCase() + ": " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable ID id) {
        try {
            validateBeforeDelete(id);
            
            service.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success(getEntityName() + " eliminado exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(getEntityName() + " no encontrado con id: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar " + getEntityName().toLowerCase() + ": " + e.getMessage()));
        }
    }
    
    // Métodos hook para validaciones personalizadas
    protected void validateBeforeCreate(D dto) {}
    
    protected void validateBeforeUpdate(ID id, D dto) {}
    
    protected void validateBeforeDelete(ID id) {}
}
