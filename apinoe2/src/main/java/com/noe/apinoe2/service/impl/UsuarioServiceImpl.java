package com.noe.apinoe2.service.impl;

import com.noe.apinoe2.model.Usuario;
import com.noe.apinoe2.repository.UsuarioRepository;
import com.noe.apinoe2.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // =============== MÉTODOS DE BaseService ===============

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        // Las fechas se setean automáticamente con @PrePersist
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario update(Integer id, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        // Actualizar campos (sin tocar id, fechaCreacion)
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setGoogleId(usuario.getGoogleId());
        usuarioExistente.setImagenUrl(usuario.getImagenUrl());
        // La fechaActualizacion se actualiza automáticamente con @PreUpdate

        return usuarioRepository.save(usuarioExistente);
    }

    @Override
    public void deleteById(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return usuarioRepository.existsById(id);
    }

    // =============== BÚSQUEDAS ESPECÍFICAS ===============

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByGoogleId(String googleId) {
        return usuarioRepository.findByGoogleId(googleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findInactivos() {
        return usuarioRepository.findByActivoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByNombreContaining(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByEmailContaining(String email) {
        return usuarioRepository.findByEmailContainingIgnoreCase(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findActivosRecientes() {
        return usuarioRepository.findActivosOrderByFechaCreacionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByNombreAndActivo(String nombre, boolean activo) {
        return usuarioRepository.findByNombreAndActivo(nombre, activo);
    }

    // =============== VALIDACIONES ===============

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByGoogleId(String googleId) {
        return usuarioRepository.existsByGoogleId(googleId);
    }

    // =============== OPERACIONES ESPECÍFICAS ===============

    @Override
    public void activar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Override
    public void desactivar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // =============== ESTADÍSTICAS ===============

    @Override
    @Transactional(readOnly = true)
    public long contarActivos() {
        return usuarioRepository.countByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarInactivos() {
        return usuarioRepository.countByActivoFalse();
    }

    // =============== VALIDACIONES DE NEGOCIO ===============

    @Override
    @Transactional(readOnly = true)
    public void validarEmailUnico(String email, Integer idUsuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(email);
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(idUsuario)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + email);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validarGoogleIdUnico(String googleId, Integer idUsuario) {
        if (googleId != null && !googleId.isEmpty()) {
            Optional<Usuario> usuarioExistente = usuarioRepository.findByGoogleId(googleId);
            if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(idUsuario)) {
                throw new IllegalArgumentException("Ya existe un usuario con el Google ID: " + googleId);
            }
        }
    }
}