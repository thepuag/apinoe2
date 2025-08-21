package com.noe.apinoe2.mapper;

import com.noe.apinoe2.dto.UsuarioDto;
import com.noe.apinoe2.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversiones entre Usuario y UsuarioDto
 */
@Component
public class UsuarioMapper implements BaseMapper<Usuario, UsuarioDto> {

    @Override
    public UsuarioDto toDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        UsuarioDto dto = new UsuarioDto();
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setGoogleId(usuario.getGoogleId());
        dto.setImagenUrl(usuario.getImagenUrl());
        
        return dto;
    }

    @Override
    public Usuario toEntity(UsuarioDto dto) {
        if (dto == null) {
            return null;
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setGoogleId(dto.getGoogleId());
        usuario.setImagenUrl(dto.getImagenUrl());
        
        // Los campos id, fechaCreacion, fechaActualizacion y activo 
        // se setean automáticamente en la entidad
        
        return usuario;
    }

    @Override
    public void updateEntityFromDto(Usuario usuario, UsuarioDto dto) {
        if (usuario == null || dto == null) {
            return;
        }
        
        // Solo actualiza los campos que pueden ser modificados
        if (dto.getNombre() != null && !dto.getNombre().trim().isEmpty()) {
            usuario.setNombre(dto.getNombre());
        }
        
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            usuario.setEmail(dto.getEmail());
        }
        
        // GoogleId puede ser null o vacío (usuario sin Google Auth)
        usuario.setGoogleId(dto.getGoogleId());
        
        // ImagenUrl puede ser null o vacía
        usuario.setImagenUrl(dto.getImagenUrl());
        
        // IMPORTANTE: No actualizamos:
        // - id (nunca debe cambiar)
        // - fechaCreacion (se setea una sola vez)
        // - fechaActualizacion (se actualiza automáticamente con @PreUpdate)
        // - activo (tiene sus propios métodos: activar/desactivar)
    }
    
    // =============== MÉTODOS AUXILIARES ===============
    
    /**
     * Convierte una lista de usuarios a DTOs
     */
    public List<UsuarioDto> toDtoList(List<Usuario> usuarios) {
        if (usuarios == null) {
            return null;
        }
        
        return usuarios.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte una lista de DTOs a usuarios
     */
    public List<Usuario> toEntityList(List<UsuarioDto> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea un DTO con información básica (para respuestas públicas)
     */
    public UsuarioDto toDtoBasico(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        UsuarioDto dto = new UsuarioDto();
        dto.setNombre(usuario.getNombre());
        // No incluye email ni otros datos sensibles
        dto.setImagenUrl(usuario.getImagenUrl());
        
        return dto;
    }
}