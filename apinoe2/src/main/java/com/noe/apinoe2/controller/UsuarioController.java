package com.noe.apinoe2.controller;

import com.noe.apinoe2.controller.base.BaseController;
import com.noe.apinoe2.dto.ApiResponse;
import com.noe.apinoe2.dto.UsuarioDto;
import com.noe.apinoe2.mapper.UsuarioMapper;
import com.noe.apinoe2.model.Usuario;
import com.noe.apinoe2.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestión de usuarios
 * Extiende BaseController para operaciones CRUD básicas
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController extends BaseController<Usuario, UsuarioDto, Integer> {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        super(usuarioService, usuarioMapper);
        this.usuarioService = usuarioService;
    }

    @Override
    protected String getEntityName() {
        return "Usuario";
    }

    // =============== ENDPOINTS ESPECÍFICOS DE USUARIO ===============
    
    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<UsuarioDto>>> getUsuariosActivos() {
        List<Usuario> usuarios = usuarioService.findActivos();
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(mapper::toDto)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(usuariosDto));
    }
    
    @GetMapping("/inactivos")
    public ResponseEntity<ApiResponse<List<UsuarioDto>>> getUsuariosInactivos() {
        List<Usuario> usuarios = usuarioService.findInactivos();
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(mapper::toDto)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(usuariosDto));
    }
    
    @GetMapping("/recientes")
    public ResponseEntity<ApiResponse<List<UsuarioDto>>> getUsuariosRecientes() {
        List<Usuario> usuarios = usuarioService.findActivosRecientes();
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(mapper::toDto)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(usuariosDto));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UsuarioDto>> getUsuarioByEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioService.findByEmail(email);
        
        if (usuario.isPresent()) {
            UsuarioDto usuarioDto = mapper.toDto(usuario.get());
            return ResponseEntity.ok(ApiResponse.success(usuarioDto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Usuario no encontrado con email: " + email));
        }
    }

    @GetMapping("/google/{googleId}")
    public ResponseEntity<ApiResponse<UsuarioDto>> getUsuarioByGoogleId(@PathVariable String googleId) {
        Optional<Usuario> usuario = usuarioService.findByGoogleId(googleId);
        
        if (usuario.isPresent()) {
            UsuarioDto usuarioDto = mapper.toDto(usuario.get());
            return ResponseEntity.ok(ApiResponse.success(usuarioDto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Usuario no encontrado con Google ID: " + googleId));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<UsuarioDto>>> buscarUsuarios(
            @RequestParam String nombre,
            @RequestParam(required = false, defaultValue = "true") boolean activo) {
        
        List<Usuario> usuarios = usuarioService.findByNombreAndActivo(nombre, activo);
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(mapper::toDto)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(usuariosDto));
    }
    
    @GetMapping("/buscar/email")
    public ResponseEntity<ApiResponse<List<UsuarioDto>>> buscarUsuariosPorEmail(
            @RequestParam String email) {
        
        List<Usuario> usuarios = usuarioService.findByEmailContaining(email);
        List<UsuarioDto> usuariosDto = usuarios.stream()
                .map(mapper::toDto)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(usuariosDto));
    }

    // =============== ENDPOINTS DE ACTIVACIÓN/DESACTIVACIÓN ===============
    
    @PutMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<String>> activarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.activar(id);
            return ResponseEntity.ok(ApiResponse.success("Usuario activado exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Usuario no encontrado con id: " + id));
        }
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<String>> desactivarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.desactivar(id);
            return ResponseEntity.ok(ApiResponse.success("Usuario desactivado exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Usuario no encontrado con id: " + id));
        }
    }
    
    // =============== ENDPOINTS DE ESTADÍSTICAS ===============
    
    @GetMapping("/estadisticas/activos")
    public ResponseEntity<ApiResponse<Long>> contarUsuariosActivos() {
        long count = usuarioService.contarActivos();
        return ResponseEntity.ok(ApiResponse.success("Total de usuarios activos", count));
    }
    
    @GetMapping("/estadisticas/inactivos")
    public ResponseEntity<ApiResponse<Long>> contarUsuariosInactivos() {
        long count = usuarioService.contarInactivos();
        return ResponseEntity.ok(ApiResponse.success("Total de usuarios inactivos", count));
    }

    // =============== VALIDACIONES PERSONALIZADAS ===============
    
    @Override
    protected void validateBeforeCreate(UsuarioDto dto) {
        // Validar email único
        if (usuarioService.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        
        // Validar Google ID único (si se proporciona)
        if (dto.getGoogleId() != null && !dto.getGoogleId().isEmpty() && 
            usuarioService.existsByGoogleId(dto.getGoogleId())) {
            throw new IllegalArgumentException("Ya existe un usuario con el Google ID: " + dto.getGoogleId());
        }
    }

    @Override
    protected void validateBeforeUpdate(Integer id, UsuarioDto dto) {
        // Validar email único para actualización
        usuarioService.validarEmailUnico(dto.getEmail(), id);
        
        // Validar Google ID único para actualización
        usuarioService.validarGoogleIdUnico(dto.getGoogleId(), id);
    }

    @Override
    protected void validateBeforeDelete(Integer id) {
        // Aquí puedes agregar validaciones antes de eliminar
        // Por ejemplo, verificar si el usuario tiene proyectos asociados
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            // TODO: Verificar dependencias (proyectos, comentarios, etc.)
            // if (usuario.get().tieneProyectos()) {
            //     throw new IllegalArgumentException("No se puede eliminar el usuario porque tiene proyectos asociados");
            // }
        }
    }
}
