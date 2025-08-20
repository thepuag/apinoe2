# ApiNoe - Arquitectura Spring Boot

## Estructura del Proyecto
```
src/main/java/com/noe/apinoe/
├── config/           # Configuraciones (CORS, Security, DB, Excepciones)
├── controller/       # REST Controllers + BaseController genérico
├── dto/             # Data Transfer Objects + ApiResponse<T>
├── mapper/          # Entity ↔ DTO + BaseMapper genérico  
├── model/           # Entidades JPA
├── repository/      # JPA Repositories
├── service/         # Interfaces + impl/ (lógica negocio)
└── ApinoeApplication.java
```

## Arquitectura Base Genérica

### BaseService<E, ID>
```java
public interface BaseService<E, ID> {
    List<E> findAll();
    Optional<E> findById(ID id);
    E save(E entity);
    E update(ID id, E entity);
    void deleteById(ID id);
    boolean existsById(ID id);
}
```

### BaseMapper<E, D>
```java
public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
    void updateEntityFromDto(E entity, D dto);
}
```

### BaseController<E, D, ID>
Controlador genérico que proporciona:
- `GET /api/entidades` → getAll()
- `GET /api/entidades/{id}` → getById()
- `POST /api/entidades` → create()
- `PUT /api/entidades/{id}` → update()
- `DELETE /api/entidades/{id}` → delete()

### ApiResponse<T>
```java
{
    "success": true/false,
    "message": "Mensaje descriptivo",
    "data": T | null,
    "timestamp": "2024-01-15T10:30:00"
}
```

## Flujo de Datos
```
Cliente → Controller → Service → Repository → BD
        ← DTO ← Mapper ← Entity ←           ←
```

## Implementación por Entidad

### Ejemplo: Usuario
1. **UsuarioService extends BaseService<Usuario, Integer>**
2. **UsuarioServiceImpl implements UsuarioService** 
3. **UsuarioMapper implements BaseMapper<Usuario, UsuarioDto>**
4. **UsuarioController extends BaseController<Usuario, UsuarioDto, Integer>**

### Personalización
- **Servicios**: Métodos específicos (findByEmail, activar, etc.)
- **Controladores**: Endpoints adicionales (/activos, /buscar)
- **Validaciones**: Hooks en BaseController (validateBeforeCreate)

## Configuraciones Clave
- **CORS**: Permite frontend
- **Security**: OAuth2/JWT + roles
- **GlobalExceptionHandler**: Manejo centralizado errores
- **@Transactional**: En servicios

## Entidades del Dominio
- Usuario, Producto, Proyecto, Tecnologia, Almacen
- Relaciones: OneToMany, ManyToOne, ManyToMany
- Campos base: id, fechaCreacion, fechaActualizacion, activo

## Ventajas
- **Reutilización**: 80% código genérico
- **Consistencia**: Misma estructura para todas las entidades
- **Escalabilidad**: Fácil agregar nuevas entidades
- **Mantenimiento**: Cambios centralizados

## Para Implementar
1. Definir entidades (columnas + relaciones)
2. Crear clases base genéricas
3. Implementar primera entidad completa
4. Replicar patrón para las demás