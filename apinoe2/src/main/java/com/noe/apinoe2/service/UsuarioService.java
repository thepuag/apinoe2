package com.noe.apinoe2.service;

import com.noe.apinoe2.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Interface de servicio para Usuario
 * Extiende BaseService para operaciones CRUD básicas
 */
public interface UsuarioService extends BaseService<Usuario, Integer> {
    
    // =============== BÚSQUEDAS ESPECÍFICAS ===============
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByGoogleId(String googleId);
    
    List<Usuario> findActivos();
    
    List<Usuario> findInactivos();
    
    List<Usuario> findByNombreContaining(String nombre);
    
    List<Usuario> findByEmailContaining(String email);
    
    List<Usuario> findActivosRecientes();
    
    List<Usuario> findByNombreAndActivo(String nombre, boolean activo);
    
    // =============== VALIDACIONES ===============
    
    boolean existsByEmail(String email);
    
    boolean existsByGoogleId(String googleId);
    
    // =============== OPERACIONES ESPECÍFICAS ===============
    
    void activar(Integer id);
    
    void desactivar(Integer id);
    
    // =============== ESTADÍSTICAS ===============
    
    long contarActivos();
    
    long contarInactivos();
    
    // =============== MÉTODOS DE VALIDACIÓN DE NEGOCIO ===============
    
    /**
     * Valida que el email no esté en uso por otro usuario
     */
    void validarEmailUnico(String email, Integer idUsuario);
    
    /**
     * Valida que el Google ID no esté en uso por otro usuario
     */
    void validarGoogleIdUnico(String googleId, Integer idUsuario);
}
