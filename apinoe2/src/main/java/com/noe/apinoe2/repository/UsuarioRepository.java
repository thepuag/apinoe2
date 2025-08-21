package com.noe.apinoe2.repository;

import com.noe.apinoe2.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /**
     * Busca usuario por email
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Busca usuario por Google ID
     */
    Optional<Usuario> findByGoogleId(String googleId);
    
    /**
     * Busca usuarios activos
     */
    List<Usuario> findByActivoTrue();
    
    /**
     * Busca usuarios inactivos
     */
    List<Usuario> findByActivoFalse();
    
    /**
     * Busca usuarios por nombre (contiene, ignorando mayúsculas)
     */
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Busca usuarios por email (contiene, ignorando mayúsculas)
     */
    List<Usuario> findByEmailContainingIgnoreCase(String email);
    
    /**
     * Verifica si existe usuario con el email dado
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe usuario con el Google ID dado
     */
    boolean existsByGoogleId(String googleId);
    
    /**
     * Cuenta usuarios activos
     */
    long countByActivoTrue();
    
    /**
     * Cuenta usuarios inactivos
     */
    long countByActivoFalse();
    
    /**
     * Busca usuarios activos ordenados por fecha de creación descendente
     */
    @Query("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.fechaCreacion DESC")
    List<Usuario> findActivosOrderByFechaCreacionDesc();
    
    /**
     * Busca usuarios por nombre y activos
     */
    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre% AND u.activo = :activo")
    List<Usuario> findByNombreAndActivo(@Param("nombre") String nombre, @Param("activo") boolean activo);
}
