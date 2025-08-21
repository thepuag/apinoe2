package com.noe.apinoe2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para transferencia de datos de Usuario
 * No incluye: id, fechaCreacion, fechaActualizacion, activo
 */

@Getter
@Setter
public class UsuarioDto {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;
    
    @Size(max = 255, message = "El GoogleId no puede exceder 255 caracteres")
    private String googleId;
    
    @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
    private String imagenUrl;
    
    // Constructor por defecto
    public UsuarioDto() {}
    
    // Constructor con parámetros principales
    public UsuarioDto(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }
    
    // Constructor completo
    public UsuarioDto(String nombre, String email, String googleId, String imagenUrl) {
        this.nombre = nombre;
        this.email = email;
        this.googleId = googleId;
        this.imagenUrl = imagenUrl;
    }

    @Override
    public String toString() {
        return "UsuarioDto{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
