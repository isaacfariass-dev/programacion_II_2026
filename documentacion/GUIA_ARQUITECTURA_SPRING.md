# GUIA COMPLETA: Arquitectura MVC con Spring Boot - Proyecto SICRA

Manual paso a paso para construir el Sistema de Gestion de Correlatividades de Materias.
Incluye el "por que" de cada decision, que hace cada capa, que archivos van en cada una y como se conectan entre si.

---

## INDICE

1. [Prerequisitos y configuracion inicial](#1-prerequisitos-y-configuracion-inicial)
2. [Que son las dependencias y por que se eligieron estas](#2-que-son-las-dependencias-y-por-que-se-eligieron-estas)
3. [Configurar la conexion a la base de datos](#3-configurar-la-conexion-a-la-base-de-datos)
4. [Estructura de carpetas del proyecto](#4-estructura-de-carpetas-del-proyecto)
5. [PASO 1 - Capa Model (Los JavaBeans)](#5-paso-1---capa-model-los-javabeans)
6. [PASO 2 - Capa Model/Enums (Los valores fijos)](#6-paso-2---capa-modelenums-los-valores-fijos)
7. [PASO 3 - Capa Exception (Excepciones de dominio)](#7-paso-3---capa-exception-excepciones-de-dominio)
8. [PASO 4 - Capa Util (Constantes y helpers)](#8-paso-4---capa-util-constantes-y-helpers)
9. [PASO 5 - Capa Repository (Acceso a datos)](#9-paso-5---capa-repository-acceso-a-datos)
10. [PASO 6 - Capa Service (Logica de negocio)](#10-paso-6---capa-service-logica-de-negocio)
11. [PASO 7 - Capa Controller (Orquestacion HTTP)](#11-paso-7---capa-controller-orquestacion-http)
12. [PASO 8 - Capa View (Plantillas Thymeleaf)](#12-paso-8---capa-view-plantillas-thymeleaf)
13. [Como se conecta todo: el flujo completo de una peticion](#13-como-se-conecta-todo-el-flujo-completo-de-una-peticion)
14. [Diccionario de anotaciones de Spring Boot](#14-diccionario-de-anotaciones-de-spring-boot)
15. [Inyeccion de Dependencias: que es y por que importa](#15-inyeccion-de-dependencias-que-es-y-por-que-importa)
16. [Orden recomendado para implementar cada entidad](#16-orden-recomendado-para-implementar-cada-entidad)
17. [Checklist antes de cada commit](#17-checklist-antes-de-cada-commit)
18. [Errores frecuentes y como resolverlos](#18-errores-frecuentes-y-como-resolverlos)

---

## 1. Prerequisitos y configuracion inicial

Antes de escribir codigo necesitas tener instalado y funcionando:

| Herramienta | Para que sirve | Version minima |
|---|---|---|
| **JDK** | Compilar y ejecutar Java | 17+ (LTS) |
| **Maven** | Gestionar dependencias y compilar el proyecto (ya viene incluido con el wrapper `mvnw`) | 3.9+ |
| **MySQL** | Base de datos relacional donde se persisten los datos | 8.0+ |
| **IntelliJ IDEA / VS Code** | Editar codigo, ejecutar la app | Cualquiera |
| **Git** | Control de versiones | 2.x |

**Paso de verificacion:**
1. Abri una terminal y ejecuta `java -version` (debe decir 17 o superior).
2. Ejecuta tu `database.sql` en MySQL Workbench o desde la terminal con `mysql -u root -p < database.sql` para crear el esquema `sicra_db` con todas las tablas.
3. Verifica que puedas conectarte: `mysql -u root -p -e "USE sicra_db; SHOW TABLES;"`.

---

## 2. Que son las dependencias y por que se eligieron estas

Las dependencias son librerias externas que tu proyecto necesita para funcionar. Se declaran en `pom.xml` y Maven las descarga automaticamente de internet.

### 2.1 `spring-boot-starter-web`

**Que trae:** Apache Tomcat embebido + Spring MVC (el framework web).

**Por que la necesitas:** Sin esta dependencia no podrias crear controladores que escuchen peticiones HTTP. Cuando vos entras a `http://localhost:8080/carreras` en el navegador, es Tomcat quien recibe esa peticion y Spring MVC quien la rutea al metodo correcto de tu `CarreraController`.

**Que pasa si la sacas:** La app arranca pero no escucha en ningun puerto. No hay servidor web.

### 2.2 `spring-boot-starter-thymeleaf`

**Que trae:** El motor de plantillas Thymeleaf integrado con Spring.

**Por que la necesitas:** Thymeleaf es lo que reemplaza a JSP en Spring Boot. Te permite escribir archivos `.html` normales pero con atributos especiales (como `th:each`, `th:text`) que se resuelven del lado del servidor antes de enviar el HTML al navegador. El controller le pasa datos al `Model`, y Thymeleaf los inyecta en el HTML.

**Ejemplo concreto:** Si el controller hace `model.addAttribute("nombre", "Isaac")`, en el HTML podes escribir `<p th:text="${nombre}"></p>` y el usuario vera `<p>Isaac</p>`.

**Que pasa si la sacas:** Los controllers no pueden retornar vistas HTML. Solo podrias retornar JSON (API REST), que no es lo que pide la consigna.

### 2.3 `spring-boot-starter-jdbc`

**Que trae:** `DataSource` autoconfigurado (pool de conexiones HikariCP) + `JdbcTemplate` + manejo de transacciones.

**Por que la necesitas:** Esta dependencia le dice a Spring Boot "voy a trabajar con una base de datos relacional via JDBC". Spring Boot lee tu `application.properties`, crea un pool de conexiones (HikariCP) y te lo deja disponible para inyectarlo en tus Repositorios.

**Que es un pool de conexiones y por que importa:** Abrir y cerrar conexiones a MySQL es costoso (tarda milisegundos cada vez). HikariCP mantiene un grupo de conexiones ya abiertas y las reutiliza. Cuando tu Repository pide una `Connection`, HikariCP le presta una que ya esta abierta. Cuando la "cerras" con `try-with-resources`, en realidad no se cierra sino que vuelve al pool. Esto hace la app mucho mas rapida.

**Que pasa si la sacas:** No tenes `DataSource` inyectable. Tendrias que crear las conexiones a mano con `DriverManager.getConnection(...)` cada vez, que es lento y propenso a errores.

### 2.4 `mysql-connector-j`

**Que trae:** El driver JDBC especifico de MySQL (clase `com.mysql.cj.jdbc.Driver`).

**Por que la necesitas:** JDBC es una especificacion generica de Java para hablar con bases de datos. Pero cada motor de base de datos (MySQL, PostgreSQL, Oracle) necesita su propio "traductor" que sepa hablar su protocolo de red. Este JAR es ese traductor para MySQL.

**Que pasa si la sacas:** Al arrancar la app, Spring Boot va a intentar conectarse a MySQL y va a fallar con `No suitable driver found`.

**Scope `runtime`:** Esta marcada como `<scope>runtime</scope>` porque tu codigo Java nunca importa clases de este JAR directamente. Solo la usa JDBC internamente en tiempo de ejecucion.

### 2.5 `spring-boot-starter-test`

**Que trae:** JUnit 5 + Mockito + utilidades de testing de Spring.

**Por que la necesitas:** Para escribir tests unitarios y de integracion. Esta marcada con `<scope>test</scope>`, asi que no se incluye en el JAR final de produccion.

---

## 3. Configurar la conexion a la base de datos

Edita el archivo `src/main/resources/application.properties`:

```properties
spring.application.name=servicio-correlativas

# -- Conexion a MySQL --
spring.datasource.url=jdbc:mysql://localhost:3306/sicra_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Explicacion linea por linea:**

| Propiedad | Que hace |
|---|---|
| `spring.datasource.url` | URL JDBC. `localhost:3306` es tu MySQL local. `sicra_db` es el esquema. Los parametros despues del `?` desactivan SSL (no es necesario en local) y fijan la zona horaria. |
| `spring.datasource.username` | Usuario de MySQL. |
| `spring.datasource.password` | Contrasenya de ese usuario. Si no tenes password, dejalo vacio. |
| `spring.datasource.driver-class-name` | Clase del driver. Spring Boot la detecta automaticamente por el `mysql-connector-j`, pero es buena practica dejarlo explicito. |

**Por que no se pone la contrasenya directamente en el repositorio:**
En un proyecto real usarias variables de entorno o un archivo `.env` ignorado por git. Para el parcial, dejalo hardcodeado pero tene en cuenta que no es una practica de produccion.

---

## 4. Estructura de carpetas del proyecto

Dentro de `src/main/java/com/arduna/farias/servicio_correlativas/` vas a crear estos paquetes:

```
servicio_correlativas/
|
|-- ServicioCorrelativasApplication.java   (ya existe, punto de entrada)
|
|-- model/                    PASO 1 - JavaBeans (entidades)
|   |-- enums/                PASO 2 - Enumeraciones
|   |-- Carrera.java
|   |-- Alumno.java
|   |-- Usuario.java
|   |-- Materia.java
|   |-- HistorialAcademico.java
|   +-- Correlatividad.java
|
|-- exception/                PASO 3 - Excepciones propias del dominio
|   |-- RecursoNoEncontradoException.java
|   |-- CorrelatividadCiclicaException.java
|   |-- AccesoDenegadoException.java
|   +-- GlobalExceptionHandler.java
|
|-- util/                     PASO 4 - Constantes y helpers
|   +-- Constantes.java
|
|-- repository/               PASO 5 - Interfaces + Implementaciones de acceso a datos
|   |-- ICarreraRepository.java
|   |-- IAlumnoRepository.java
|   |-- IUsuarioRepository.java
|   |-- IMateriaRepository.java
|   |-- IHistorialAcademicoRepository.java
|   |-- ICorrelatividadRepository.java
|   +-- impl/
|       |-- CarreraRepositoryImpl.java
|       |-- AlumnoRepositoryImpl.java
|       |-- UsuarioRepositoryImpl.java
|       |-- MateriaRepositoryImpl.java
|       |-- HistorialAcademicoRepositoryImpl.java
|       +-- CorrelatividadRepositoryImpl.java
|
|-- service/                  PASO 6 - Interfaces + Implementaciones de logica de negocio
|   |-- ICarreraService.java
|   |-- IMateriaService.java
|   |-- ICorrelatividadService.java
|   |-- IHistorialService.java
|   |-- IUsuarioService.java
|   +-- impl/
|       |-- CarreraServiceImpl.java
|       |-- MateriaServiceImpl.java
|       |-- CorrelatividadServiceImpl.java
|       |-- HistorialServiceImpl.java
|       +-- UsuarioServiceImpl.java
|
+-- controller/               PASO 7 - Controladores web
    |-- AuthController.java
    |-- CarreraController.java
    |-- MateriaController.java
    +-- AdminController.java
```

Y en `src/main/resources/`:

```
resources/
|-- application.properties
|-- templates/                PASO 8 - Plantillas Thymeleaf (la Vista)
|   |-- fragments/
|   |   |-- head.html         (CSS, meta tags compartidos)
|   |   |-- navbar.html       (barra de navegacion)
|   |   +-- footer.html
|   |-- auth/
|   |   |-- login.html
|   |   +-- registro.html
|   |-- carrera/
|   |   |-- lista.html
|   |   +-- formulario.html
|   |-- materia/
|   |   |-- lista.html
|   |   +-- formulario.html
|   +-- alumno/
|       +-- arbol.html
+-- static/
    |-- css/
    |   +-- estilos.css
    +-- js/
        +-- app.js
```

---

## 5. PASO 1 - Capa Model (Los JavaBeans)

### Que es un JavaBean

Un JavaBean es una clase Java que cumple tres reglas:
1. Tiene un **constructor vacio** (sin parametros).
2. Todos sus atributos son **privados**.
3. Cada atributo tiene un **getter** y un **setter** publico.

### Por que se necesita esta capa

Las tablas de tu base de datos guardan datos en filas y columnas. Pero en Java trabajas con objetos. Los JavaBeans son la traduccion de una fila de la tabla a un objeto de Java. Cuando tu Repository ejecuta un `SELECT * FROM carrera`, lee las columnas del `ResultSet` y las vuelca en los atributos de un objeto `Carrera`.

### Que archivos crear y su correspondencia con la BD

| Archivo Java | Tabla en MySQL | Atributos (mapeo columna a atributo) |
|---|---|---|
| `Carrera.java` | `carrera` | `idCarrera` (id_carrera), `codigo`, `nombre`, `institucion`, `duracionAnios` (duracion_anios) |
| `Alumno.java` | `alumno` | `idAlumno`, `idCarrera`, `legajo`, `nombre`, `apellido`, `anioIngreso` |
| `Usuario.java` | `usuario` | `idUsuario`, `idAlumno` (nullable), `nombreUsuario`, `clave`, `rol` (usar enum `Rol`), `fechaCreacion` |
| `Materia.java` | `materia` | `idMateria`, `idCarrera`, `codigo`, `nombre`, `anioPlan`, `cuatrimestre` (usar enum `Cuatrimestre`), `cargaHoraria` |
| `HistorialAcademico.java` | `historial_academico` | `idHistorial`, `idAlumno`, `idMateria`, `estado` (usar enum `EstadoMateria`), `notaFinal` (usar `Double`, nullable), `ultimaModificacion` |
| `Correlatividad.java` | `correlatividad` | `idMateriaDestino`, `idMateriaRequisito`, `tipoRequisito` (usar enum `TipoRequisito`) |

### Modelo de un JavaBean (ejemplo con Carrera)

```java
package com.arduna.farias.servicio_correlativas.model;

/**
 * Representa una carrera/plan de estudios.
 * Mapea la tabla carrera de la base de datos.
 */
public class Carrera {

    private int idCarrera;
    private String codigo;
    private String nombre;
    private String institucion;
    private int duracionAnios;

    /** Constructor vacio requerido por el estandar JavaBean. */
    public Carrera() {
    }

    /** Constructor completo para crear instancias con todos los datos. */
    public Carrera(int idCarrera, String codigo, String nombre,
                   String institucion, int duracionAnios) {
        this.idCarrera = idCarrera;
        this.codigo = codigo;
        this.nombre = nombre;
        this.institucion = institucion;
        this.duracionAnios = duracionAnios;
    }

    // Getters y Setters para cada atributo...
    public int getIdCarrera() { return idCarrera; }
    public void setIdCarrera(int idCarrera) { this.idCarrera = idCarrera; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    // ... (completar para nombre, institucion, duracionAnios)

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + institucion + ")";
    }
}
```

**Regla importante:** Los nombres de los atributos en Java usan **camelCase** (`idCarrera`), mientras que las columnas en MySQL usan **snake_case** (`id_carrera`). La traduccion la haces vos manualmente en el Repository cuando lees el `ResultSet`.

### Por que se necesitan dos constructores

- El **vacio** es necesario porque muchos frameworks (incluido Thymeleaf para formularios) crean objetos vacios y despues les setean los valores uno a uno con los setters.
- El **completo** es una conveniencia para cuando vos ya tenes todos los datos (por ejemplo, al leer del `ResultSet`).

---

## 6. PASO 2 - Capa Model/Enums (Los valores fijos)

### Que es un Enum y por que usarlo en vez de Strings

Un `enum` es un tipo de dato que solo puede tener un conjunto fijo de valores. En la BD, las columnas `ENUM('ADMIN', 'ALUMNO')` ya restringen los valores posibles. En Java hacemos lo mismo con `enum` para evitar errores de tipeo.

Si usaras `String rol = "ADMIN"` en vez de `Rol.ADMIN`, un dia podrias escribir `"Admin"` o `"admin"` por error y la comparacion fallaria silenciosamente. Con enums el compilador te avisa si escribis mal.

### Archivos a crear en `model/enums/`

| Archivo | Valores | Corresponde a columna |
|---|---|---|
| `Rol.java` | `ADMIN`, `ALUMNO` | `usuario.rol` |
| `EstadoMateria.java` | `CURSANDO`, `REGULAR`, `APROBADA`, `DESAPROBADA` | `historial_academico.estado` |
| `Cuatrimestre.java` | `ANUAL`, `PRIMER_CUATRIMESTRE`, `SEGUNDO_CUATRIMESTRE` | `materia.cuatrimestre` |
| `TipoRequisito.java` | `REGULAR`, `APROBADA` | `correlatividad.tipo_requisito` |

### Ejemplo de enum

```java
package com.arduna.farias.servicio_correlativas.model.enums;

/**
 * Roles de usuario del sistema.
 * ADMIN: gestiona carreras, materias y correlatividades.
 * ALUMNO: consulta su historial y materias habilitadas.
 */
public enum Rol {
    ADMIN,
    ALUMNO
}
```

### Como convertir entre enum y String (lo vas a necesitar en el Repository)

```java
// De String (viene del ResultSet) a Enum:
Rol rol = Rol.valueOf(rs.getString("rol"));          // "ADMIN" -> Rol.ADMIN

// De Enum a String (para escribir en un PreparedStatement):
stmt.setString(1, Rol.ADMIN.name());                 // Rol.ADMIN -> "ADMIN"
```

---

## 7. PASO 3 - Capa Exception (Excepciones de dominio)

### Por que crear excepciones propias

Las pautas dicen: "Crear excepciones propias del dominio cuando corresponda". Si un alumno intenta cursar una materia sin tener las correlativas, eso no es un `NullPointerException` ni una `SQLException`. Es un error de **negocio** que merece su propia clase para ser identificado y manejado correctamente.

### Archivos a crear en `exception/`

| Archivo | Extiende de | Cuando se lanza |
|---|---|---|
| `RecursoNoEncontradoException.java` | `RuntimeException` | Cuando se busca una carrera/materia/alumno por ID y no existe en la BD. |
| `CorrelatividadCiclicaException.java` | `RuntimeException` | Cuando el admin intenta crear una correlatividad que genera un ciclo (A necesita B, B necesita A). |
| `AccesoDenegadoException.java` | `RuntimeException` | Cuando un alumno intenta hacer algo que solo puede hacer un admin (o viceversa). |
| `GlobalExceptionHandler.java` | (ninguna, usa `@ControllerAdvice`) | Captura las excepciones anteriores y redirige a una pagina de error amigable en vez de mostrar un stacktrace. |

### Ejemplo de excepcion propia

```java
package com.arduna.farias.servicio_correlativas.exception;

/**
 * Se lanza cuando se busca un recurso por ID y no existe en la base de datos.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
```

### Ejemplo del GlobalExceptionHandler

```java
package com.arduna.farias.servicio_correlativas.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Intercepta excepciones lanzadas por cualquier Controller
 * y redirige a una vista de error amigable.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String manejarNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("mensajeError", ex.getMessage());
        return "error";  // busca templates/error.html
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public String manejarAccesoDenegado(AccesoDenegadoException ex, Model model) {
        model.addAttribute("mensajeError", ex.getMessage());
        return "error";
    }
}
```

**Por que `@ControllerAdvice`:** Es una anotacion de Spring que le dice "esta clase aplica a TODOS los controllers". Cualquier excepcion que un controller no atrape, la atrapa este handler. Asi no tenes que repetir `try/catch` en cada controller.

---

## 8. PASO 4 - Capa Util (Constantes y helpers)

### Por que centralizar constantes

Las pautas dicen: "Nada de numeros magicos ni strings sueltos repetidos". Si en 5 lugares de tu codigo escribis `"ADMIN"` como string y un dia lo cambias, tenes que cambiarlo en 5 lugares. Si usas `Constantes.ROL_ADMIN`, lo cambias en un solo lugar.

### Archivo a crear: `util/Constantes.java`

```java
package com.arduna.farias.servicio_correlativas.util;

/**
 * Centraliza valores constantes usados en toda la aplicacion.
 * Evita strings magicos y numeros sueltos.
 */
public final class Constantes {

    private Constantes() {
        // Clase utilitaria, no se instancia.
    }

    // -- Sesion --
    public static final String SESION_USUARIO = "usuarioLogueado";
    public static final String SESION_ROL = "rolUsuario";

    // -- Mensajes de error --
    public static final String ERROR_CARRERA_NO_ENCONTRADA = "No se encontro la carrera con el ID indicado.";
    public static final String ERROR_MATERIA_NO_ENCONTRADA = "No se encontro la materia con el ID indicado.";
    public static final String ERROR_CREDENCIALES = "Usuario o clave incorrectos.";
    public static final String ERROR_ACCESO_DENEGADO = "No tenes permisos para realizar esta accion.";
    public static final String ERROR_CICLO_CORRELATIVIDAD = "La correlatividad genera un ciclo y no puede crearse.";

    // -- Carga horaria minima --
    public static final int CARGA_HORARIA_MINIMA = 1;
}
```

---

## 9. PASO 5 - Capa Repository (Acceso a datos)

### Que es y que hace esta capa

Es la **unica capa autorizada a escribir SQL**. Su responsabilidad es traducir entre el mundo de la base de datos (filas, columnas, SQL) y el mundo de Java (objetos, listas). Recibe peticiones como "dame todas las carreras" y devuelve una `List<Carrera>`.

### Por que se usa una Interfaz + Implementacion

Esto cumple la **D de SOLID** (Dependency Inversion): las capas superiores (Service, Controller) dependen de la **interfaz** `ICarreraRepository`, no de la implementacion `CarreraRepositoryImpl`. Si manyiana quisieras cambiar de MySQL a PostgreSQL, solo cambiarias la implementacion sin tocar el Service ni el Controller.

### Archivos a crear

Para **cada entidad** necesitas dos archivos:

1. **Interfaz** en `repository/` - Define que operaciones existen (contrato).
2. **Implementacion** en `repository/impl/` - Escribe las queries SQL reales.

### Ejemplo completo: Interfaz

```java
package com.arduna.farias.servicio_correlativas.repository;

import com.arduna.farias.servicio_correlativas.model.Carrera;
import java.util.List;

/**
 * Contrato de acceso a datos para la entidad Carrera.
 */
public interface ICarreraRepository {

    /**
     * Obtiene todas las carreras de la base de datos.
     * @return lista de carreras (puede estar vacia, nunca null).
     */
    List<Carrera> obtenerTodas();

    /**
     * Busca una carrera por su ID.
     * @param id identificador de la carrera.
     * @return la carrera encontrada, o null si no existe.
     */
    Carrera buscarPorId(int id);

    /**
     * Persiste una nueva carrera en la base de datos.
     * @param carrera objeto con los datos a guardar.
     */
    void guardar(Carrera carrera);

    /**
     * Actualiza una carrera existente.
     * @param carrera objeto con los datos actualizados (debe tener idCarrera seteado).
     */
    void actualizar(Carrera carrera);

    /**
     * Elimina una carrera por su ID.
     * @param id identificador de la carrera a eliminar.
     */
    void eliminar(int id);
}
```

### Ejemplo completo: Implementacion

```java
package com.arduna.farias.servicio_correlativas.repository.impl;

import com.arduna.farias.servicio_correlativas.model.Carrera;
import com.arduna.farias.servicio_correlativas.repository.ICarreraRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion JDBC de acceso a datos para Carrera.
 * Usa try-with-resources obligatorio segun pautas.
 */
@Repository
public class CarreraRepositoryImpl implements ICarreraRepository {

    private final DataSource dataSource;

    /**
     * Spring inyecta automaticamente el DataSource configurado
     * en application.properties (pool HikariCP).
     */
    public CarreraRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Carrera> obtenerTodas() {
        String sql = "SELECT id_carrera, codigo, nombre, institucion, duracion_anios FROM carrera";
        List<Carrera> resultado = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                resultado.add(mapearCarrera(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener carreras", e);
        }

        return resultado;
    }

    @Override
    public Carrera buscarPorId(int id) {
        String sql = "SELECT id_carrera, codigo, nombre, institucion, duracion_anios FROM carrera WHERE id_carrera = ?";
        Carrera resultado = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultado = mapearCarrera(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar carrera por ID", e);
        }

        return resultado;
    }

    @Override
    public void guardar(Carrera carrera) {
        String sql = "INSERT INTO carrera (codigo, nombre, institucion, duracion_anios) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, carrera.getCodigo());
            stmt.setString(2, carrera.getNombre());
            stmt.setString(3, carrera.getInstitucion());
            stmt.setInt(4, carrera.getDuracionAnios());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar carrera", e);
        }
    }

    @Override
    public void actualizar(Carrera carrera) {
        String sql = "UPDATE carrera SET codigo = ?, nombre = ?, institucion = ?, duracion_anios = ? WHERE id_carrera = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, carrera.getCodigo());
            stmt.setString(2, carrera.getNombre());
            stmt.setString(3, carrera.getInstitucion());
            stmt.setInt(4, carrera.getDuracionAnios());
            stmt.setInt(5, carrera.getIdCarrera());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar carrera", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM carrera WHERE id_carrera = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar carrera", e);
        }
    }

    // -- Metodo auxiliar privado --

    /**
     * Convierte una fila del ResultSet en un objeto Carrera.
     * Aqui se hace la traduccion snake_case (BD) -> camelCase (Java).
     */
    private Carrera mapearCarrera(ResultSet rs) throws SQLException {
        Carrera c = new Carrera();
        c.setIdCarrera(rs.getInt("id_carrera"));
        c.setCodigo(rs.getString("codigo"));
        c.setNombre(rs.getString("nombre"));
        c.setInstitucion(rs.getString("institucion"));
        c.setDuracionAnios(rs.getInt("duracion_anios"));
        return c;
    }
}
```

### Patrones clave a notar en el Repository

1. **`DataSource` en el constructor:** Spring lo inyecta automaticamente. No haces `new`.
2. **`try-with-resources`:** Cada `Connection`, `PreparedStatement` y `ResultSet` se abre dentro del `try(...)`. Al salir del bloque, Java los cierra automaticamente (en realidad los devuelve al pool).
3. **`PreparedStatement` con `?`:** Nunca concatenes variables en el SQL (`"WHERE id = " + id`). Siempre usa `?` y `stmt.setInt()`. Esto previene inyeccion SQL.
4. **Metodo `mapearCarrera` privado:** Evita repetir la misma logica de lectura del ResultSet en cada metodo. Reutilizacion (DRY - Don't Repeat Yourself).
5. **Single return:** El metodo `buscarPorId` usa una variable `resultado` y retorna al final, cumpliendo las pautas.

### Que interfaces y que metodos crear para cada entidad

| Interfaz | Metodos sugeridos |
|---|---|
| `ICarreraRepository` | `obtenerTodas()`, `buscarPorId(int)`, `guardar(Carrera)`, `actualizar(Carrera)`, `eliminar(int)` |
| `IMateriaRepository` | Los mismos CRUD + `obtenerPorCarrera(int idCarrera)` |
| `IAlumnoRepository` | CRUD basico + `buscarPorLegajo(String)` |
| `IUsuarioRepository` | `buscarPorNombreUsuario(String)`, `guardar(Usuario)` |
| `IHistorialAcademicoRepository` | `obtenerPorAlumno(int idAlumno)`, `guardarOActualizar(HistorialAcademico)` |
| `ICorrelatividadRepository` | `obtenerRequisitos(int idMateriaDestino)`, `obtenerHabilitadas(int idMateriaRequisito)`, `guardar(Correlatividad)`, `eliminar(int dest, int req)`, `existeCorrelatividad(int dest, int req)` |

---

## 10. PASO 6 - Capa Service (Logica de negocio)

### Que es y que hace esta capa

Es donde viven las **reglas del juego**. El Repository solo sabe leer y escribir datos. El Service sabe **que significan** esos datos y que se puede o no hacer con ellos.

### Ejemplos concretos de logica de negocio de este proyecto

- **Puede el alumno cursar Analisis II?** El Service consulta al `CorrelatividadRepository` que correlativas tiene Analisis II, y al `HistorialRepository` que materias aprobo el alumno. Si todas las correlativas estan aprobadas/regulares segun el tipo, retorna true.
- **Se puede crear la correlatividad A -> B?** El Service verifica que no exista ya el camino inverso B -> ... -> A (deteccion de ciclos con DFS/BFS).
- **Se puede eliminar la materia X?** El Service verifica que no sea correlativa de ninguna otra antes de permitir el borrado.
- **Login:** El Service recibe usuario y clave, busca en el Repository, compara la clave y retorna el `Usuario` si es correcta o lanza excepcion.

### Por que se usa Interfaz + Implementacion (igual que en Repository)

Misma razon: desacoplamiento (SOLID - D). El Controller depende de `IMateriaService`, no de `MateriaServiceImpl`. Esto permite cambiar la implementacion sin tocar el Controller.

### Ejemplo: Interfaz

```java
package com.arduna.farias.servicio_correlativas.service;

import com.arduna.farias.servicio_correlativas.model.Carrera;
import java.util.List;

/**
 * Logica de negocio para la entidad Carrera.
 */
public interface ICarreraService {

    List<Carrera> obtenerTodas();

    Carrera obtenerPorId(int id);

    void crear(Carrera carrera);

    void actualizar(Carrera carrera);

    void eliminar(int id);
}
```

### Ejemplo: Implementacion

```java
package com.arduna.farias.servicio_correlativas.service.impl;

import com.arduna.farias.servicio_correlativas.exception.RecursoNoEncontradoException;
import com.arduna.farias.servicio_correlativas.model.Carrera;
import com.arduna.farias.servicio_correlativas.repository.ICarreraRepository;
import com.arduna.farias.servicio_correlativas.service.ICarreraService;
import com.arduna.farias.servicio_correlativas.util.Constantes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraServiceImpl implements ICarreraService {

    private final ICarreraRepository carreraRepository;

    public CarreraServiceImpl(ICarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Override
    public List<Carrera> obtenerTodas() {
        return carreraRepository.obtenerTodas();
    }

    @Override
    public Carrera obtenerPorId(int id) {
        Carrera resultado = carreraRepository.buscarPorId(id);
        if (resultado == null) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_CARRERA_NO_ENCONTRADA);
        }
        return resultado;
    }

    @Override
    public void crear(Carrera carrera) {
        // Aqui van las VALIDACIONES DE NEGOCIO:
        // - El nombre no puede estar vacio.
        // - La duracion debe ser mayor a 0.
        // - El codigo no puede estar repetido (el Repository lanzara excepcion de BD,
        //   pero es mejor validar antes para dar un mensaje amigable).
        carreraRepository.guardar(carrera);
    }

    @Override
    public void actualizar(Carrera carrera) {
        Carrera existente = carreraRepository.buscarPorId(carrera.getIdCarrera());
        if (existente == null) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_CARRERA_NO_ENCONTRADA);
        }
        carreraRepository.actualizar(carrera);
    }

    @Override
    public void eliminar(int id) {
        Carrera existente = carreraRepository.buscarPorId(id);
        if (existente == null) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_CARRERA_NO_ENCONTRADA);
        }
        // Validar que no tenga materias asociadas antes de eliminar
        // (la FK de la BD lo impide, pero es mejor avisar con mensaje claro)
        carreraRepository.eliminar(id);
    }
}
```

### Diferencia clave entre Repository y Service

| Aspecto | Repository | Service |
|---|---|---|
| **Conoce SQL** | Si, es su unica responsabilidad | Nunca. No importa de donde vienen los datos |
| **Valida reglas de negocio** | No. Solo lee y escribe | Si. Decide si una operacion es valida |
| **Lanza excepciones de dominio** | No (solo relanza `SQLException` como `RuntimeException`) | Si (`RecursoNoEncontradoException`, `CorrelatividadCiclicaException`) |
| **Conoce HTTP** | No | No (esta es la regla que lo diferencia del Controller) |
| **Anotacion** | `@Repository` | `@Service` |

### Que servicios crear y su logica especifica

| Servicio | Logica clave que debe contener |
|---|---|
| `CarreraServiceImpl` | Validar que la duracion sea > 0, que el codigo no este vacio. Verificar que no tenga materias antes de eliminar. |
| `MateriaServiceImpl` | Validar carga horaria >= 1. Verificar que la carrera exista antes de crear la materia. |
| `CorrelatividadServiceImpl` | **Deteccion de ciclos** (RF-13): al crear A -> B, recorrer el grafo desde B para verificar que B no llega a A. Calcular materias habilitadas para un alumno (RF-08). |
| `HistorialServiceImpl` | Validar que la nota este entre 0 y 10. Al actualizar un estado, recalcular habilitaciones. |
| `UsuarioServiceImpl` | Autenticacion (comparar clave). Validacion de permisos por rol en capa service, no solo en vista (segun pautas). |

---

## 11. PASO 7 - Capa Controller (Orquestacion HTTP)

### Que es y que hace esta capa

El Controller es el **intermediario entre el navegador del usuario y la logica de negocio**. Su trabajo es:
1. Recibir la peticion HTTP (GET, POST).
2. Extraer los datos que mando el usuario (parametros de URL, datos del formulario).
3. Llamar al Service correspondiente.
4. Poner los datos resultantes en el `Model` de Spring.
5. Retornar el nombre de la plantilla HTML que Thymeleaf debe renderizar.

**Regla fundamental (segun pautas):** El Controller NO contiene logica de negocio ni SQL. Solo "orquesta".

### Ejemplo completo: CarreraController

```java
package com.arduna.farias.servicio_correlativas.controller;

import com.arduna.farias.servicio_correlativas.model.Carrera;
import com.arduna.farias.servicio_correlativas.service.ICarreraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/carreras")
public class CarreraController {

    private final ICarreraService carreraService;

    public CarreraController(ICarreraService carreraService) {
        this.carreraService = carreraService;
    }

    /** GET /carreras -> muestra la lista de todas las carreras */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("carreras", carreraService.obtenerTodas());
        return "carrera/lista";  // busca templates/carrera/lista.html
    }

    /** GET /carreras/nueva -> muestra el formulario vacio para crear */
    @GetMapping("/nueva")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("carrera", new Carrera());
        return "carrera/formulario";
    }

    /** POST /carreras -> recibe los datos del formulario y guarda */
    @PostMapping
    public String crear(@ModelAttribute Carrera carrera) {
        carreraService.crear(carrera);
        return "redirect:/carreras";  // redirige al listado
    }

    /** GET /carreras/editar/5 -> muestra el formulario con datos de la carrera 5 */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("carrera", carreraService.obtenerPorId(id));
        return "carrera/formulario";
    }

    /** POST /carreras/editar/5 -> recibe los datos editados y actualiza */
    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Carrera carrera) {
        carrera.setIdCarrera(id);
        carreraService.actualizar(carrera);
        return "redirect:/carreras";
    }

    /** GET /carreras/eliminar/5 -> elimina la carrera 5 */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id) {
        carreraService.eliminar(id);
        return "redirect:/carreras";
    }
}
```

### Explicacion de cada anotacion usada en el Controller

| Anotacion | Donde va | Que hace |
|---|---|---|
| `@Controller` | Sobre la clase | Le dice a Spring "registra esta clase como un controlador web". |
| `@RequestMapping("/carreras")` | Sobre la clase | Todas las rutas de esta clase empiezan con `/carreras`. |
| `@GetMapping` | Sobre un metodo | Este metodo responde a peticiones HTTP GET. |
| `@PostMapping` | Sobre un metodo | Este metodo responde a peticiones HTTP POST (envio de formularios). |
| `@PathVariable` | Sobre un parametro | Extrae un valor de la URL. En `/carreras/editar/5`, extrae el `5`. |
| `@ModelAttribute` | Sobre un parametro | Spring toma los datos del formulario HTML y los carga automaticamente en un objeto Java. Si el formulario tiene un campo `name="nombre"`, Spring llama a `carrera.setNombre(valor)`. |
| `Model` | Parametro del metodo | Objeto que se usa para pasar datos del Controller a la Vista. `model.addAttribute("clave", valor)` hace que `valor` este disponible en el HTML como `${clave}`. |

### Que controllers crear

| Controller | Rutas principales | Responsabilidad |
|---|---|---|
| `AuthController` | `/login`, `/registro`, `/logout` | Manejo de sesion y autenticacion. |
| `CarreraController` | `/carreras`, `/carreras/nueva`, `/carreras/editar/{id}`, `/carreras/eliminar/{id}` | CRUD completo de carreras. |
| `MateriaController` | `/materias`, `/materias/nueva`, etc. | CRUD de materias, vista del arbol. |
| `AdminController` | `/admin/correlatividades` | Gestion de correlatividades (solo ADMIN). |

---

## 12. PASO 8 - Capa View (Plantillas Thymeleaf)

### Donde van los archivos

Todos los HTML van en `src/main/resources/templates/`. Spring Boot busca ahi automaticamente cuando un Controller retorna un String como `"carrera/lista"` (busca `templates/carrera/lista.html`).

Los archivos estaticos (CSS, JS, imagenes) van en `src/main/resources/static/`. Son accesibles desde el navegador como `/css/estilos.css`.

### Sintaxis basica de Thymeleaf que vas a necesitar

```html
<!-- Mostrar un texto dinamico -->
<p th:text="${carrera.nombre}">Nombre placeholder</p>

<!-- Iterar una lista (equivalente a un for-each) -->
<tr th:each="carrera : ${carreras}">
    <td th:text="${carrera.codigo}"></td>
    <td th:text="${carrera.nombre}"></td>
</tr>

<!-- Condicional (mostrar algo solo si se cumple una condicion) -->
<button th:if="${sesion.rol == 'ADMIN'}">Eliminar</button>

<!-- Link dinamico -->
<a th:href="@{/carreras/editar/{id}(id=${carrera.idCarrera})}">Editar</a>

<!-- Formulario que envia datos por POST -->
<form th:action="@{/carreras}" th:object="${carrera}" method="post">
    <input type="text" th:field="*{nombre}" />
    <input type="text" th:field="*{codigo}" />
    <button type="submit">Guardar</button>
</form>

<!-- Incluir un fragmento reutilizable (navbar, footer) -->
<div th:replace="~{fragments/navbar :: navbar}"></div>
```

### Ejemplo: templates/carrera/lista.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Carreras - SICRA</title>
    <link rel="stylesheet" th:href="@{/css/estilos.css}" />
</head>
<body>

    <h1>Listado de Carreras</h1>

    <a th:href="@{/carreras/nueva}">Nueva Carrera</a>

    <table>
        <thead>
            <tr>
                <th>Codigo</th>
                <th>Nombre</th>
                <th>Institucion</th>
                <th>Duracion</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="carrera : ${carreras}">
                <td th:text="${carrera.codigo}"></td>
                <td th:text="${carrera.nombre}"></td>
                <td th:text="${carrera.institucion}"></td>
                <td th:text="${carrera.duracionAnios} + ' anios'"></td>
                <td>
                    <a th:href="@{/carreras/editar/{id}(id=${carrera.idCarrera})}">Editar</a>
                    <a th:href="@{/carreras/eliminar/{id}(id=${carrera.idCarrera})}">Eliminar</a>
                </td>
            </tr>
        </tbody>
    </table>

</body>
</html>
```

### Ejemplo: templates/carrera/formulario.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Carrera - SICRA</title>
</head>
<body>

    <h1 th:text="${carrera.idCarrera == 0} ? 'Nueva Carrera' : 'Editar Carrera'"></h1>

    <form th:action="${carrera.idCarrera == 0}
                      ? @{/carreras}
                      : @{/carreras/editar/{id}(id=${carrera.idCarrera})}"
          th:object="${carrera}"
          method="post">

        <label>Codigo:</label>
        <input type="text" th:field="*{codigo}" required />

        <label>Nombre:</label>
        <input type="text" th:field="*{nombre}" required />

        <label>Institucion:</label>
        <input type="text" th:field="*{institucion}" required />

        <label>Duracion (anios):</label>
        <input type="number" th:field="*{duracionAnios}" min="1" required />

        <button type="submit">Guardar</button>
    </form>

</body>
</html>
```

### Fragmentos reutilizables

Crea `templates/fragments/navbar.html`:
```html
<nav th:fragment="navbar">
    <a th:href="@{/carreras}">Carreras</a>
    <a th:href="@{/materias}">Materias</a>
    <a th:href="@{/logout}">Cerrar sesion</a>
</nav>
```
Y en cualquier otra pagina, incluilos con:
```html
<div th:replace="~{fragments/navbar :: navbar}"></div>
```

---

## 13. Como se conecta todo: el flujo completo de una peticion

Ejemplo: el usuario entra a `http://localhost:8080/carreras/editar/3`

```
NAVEGADOR                        SPRING BOOT
=========                        ===========

GET /carreras/editar/3
        |
        v
   [Tomcat recibe la peticion]
        |
        v
   [Spring MVC busca que metodo tiene @GetMapping("/editar/{id}")
    dentro de un @Controller con @RequestMapping("/carreras")]
        |
        v
   CarreraController.mostrarFormularioEditar(id=3, model)
        |  - NO tiene logica de negocio
        |  - Solo llama al service
        v
   carreraService.obtenerPorId(3)
        |  - Verifica que exista
        |  - Si no existe: lanza RecursoNoEncontradoException
        |  - Si existe: retorna el objeto Carrera
        v
   carreraRepository.buscarPorId(3)
        |  - Abre Connection del pool (HikariCP)
        |  - Ejecuta: SELECT ... FROM carrera WHERE id_carrera = 3
        |  - Lee el ResultSet, crea un objeto Carrera
        |  - Cierra recursos (try-with-resources)
        |  - Retorna el objeto Carrera al Service
        v
   [Service retorna la Carrera al Controller]
        |
        v
   [Controller hace model.addAttribute("carrera", carrera)]
   [Controller retorna "carrera/formulario"]
        |
        v
   [Thymeleaf toma templates/carrera/formulario.html]
   [Reemplaza th:field="*{nombre}" por el valor real]
   [Genera HTML puro]
        |
        v
   [Tomcat envia el HTML al navegador]
        |
        v
   NAVEGADOR muestra el formulario con los datos de la carrera 3
```

---

## 14. Diccionario de anotaciones de Spring Boot

| Anotacion | Donde se pone | Que hace | Por que existe |
|---|---|---|---|
| `@SpringBootApplication` | Clase principal | Combina `@Configuration`, `@EnableAutoConfiguration` y `@ComponentScan`. Arranca todo. | Sin ella, Spring Boot no sabe donde empezar a buscar tus clases. |
| `@Controller` | Clase | Marca la clase como un controlador web MVC. | Spring la registra para que reciba peticiones HTTP. |
| `@Service` | Clase | Marca la clase como un servicio de negocio. | Spring crea una instancia unica (Singleton) y la hace disponible para inyeccion. |
| `@Repository` | Clase | Marca la clase como componente de acceso a datos. | Igual que `@Service`, pero ademas traduce excepciones de BD a excepciones de Spring. |
| `@GetMapping("/ruta")` | Metodo | Responde a HTTP GET en esa ruta. | Mapea una URL a un metodo Java. |
| `@PostMapping("/ruta")` | Metodo | Responde a HTTP POST en esa ruta. | Para recibir datos de formularios. |
| `@RequestMapping("/base")` | Clase | Prefijo de ruta comun para todos los metodos de esa clase. | Evita repetir `/carreras` en cada metodo. |
| `@PathVariable` | Parametro | Extrae un valor de la URL (`/editar/{id}` -> `id`). | Para rutas dinamicas. |
| `@ModelAttribute` | Parametro | Carga datos del formulario en un objeto Java automaticamente. | Evita leer cada campo del request a mano. |
| `@ControllerAdvice` | Clase | Aplica logica transversal a todos los controllers. | Para manejo global de excepciones. |
| `@ExceptionHandler` | Metodo dentro de `@ControllerAdvice` | Atrapa un tipo especifico de excepcion. | Muestra paginas de error amigables en vez de stacktraces. |

---

## 15. Inyeccion de Dependencias: que es y por que importa

### El problema sin Inyeccion de Dependencias

```java
// MAL: El Controller crea sus propias dependencias
public class CarreraController {
    private CarreraServiceImpl service = new CarreraServiceImpl(
        new CarreraRepositoryImpl(/* y el DataSource de donde lo saco? */)
    );
}
```

Problemas:
- El Controller esta **acoplado** a `CarreraServiceImpl`. Si queres cambiar la implementacion, tenes que tocar el Controller.
- Tenes que resolver a mano toda la cadena de dependencias (Service necesita Repository, Repository necesita DataSource...).
- No podes testear el Controller con un Service falso (mock).

### La solucion con Inyeccion de Dependencias

```java
// BIEN: El Controller solo conoce la interfaz
@Controller
public class CarreraController {
    private final ICarreraService carreraService;

    // Spring ve que este constructor pide un ICarreraService,
    // busca en su registro interno que clase anotada con @Service
    // implementa esa interfaz, y la inyecta automaticamente.
    public CarreraController(ICarreraService carreraService) {
        this.carreraService = carreraService;
    }
}
```

**Que hace Spring por detras:**
1. Al arrancar, escanea todos los paquetes buscando clases con `@Controller`, `@Service`, `@Repository`.
2. Crea una instancia unica de cada una (Singleton).
3. Resuelve las dependencias: si `CarreraServiceImpl` necesita un `ICarreraRepository` en su constructor, busca la clase anotada con `@Repository` que implemente esa interfaz y se la pasa.
4. Todo esto sucede **automaticamente**. Vos solo declaras interfaces y constructores.

---

## 16. Orden recomendado para implementar cada entidad

Cuando vayas a implementar el CRUD de una entidad (ej: Materia), segui este orden estricto para no bloquearte:

### Fase 1: Modelo
1. Crea el `enum` si la entidad tiene columnas ENUM en la BD (ej: `Cuatrimestre`).
2. Crea el JavaBean en `model/` (ej: `Materia.java`) con atributos, constructor vacio, constructor completo, getters/setters, `toString()`.

### Fase 2: Persistencia
3. Crea la interfaz `IMateriaRepository` en `repository/` definiendo los metodos CRUD.
4. Crea `MateriaRepositoryImpl` en `repository/impl/` implementando cada metodo con SQL.
5. **(Opcional pero recomendado):** Escribi un test rapido o un `@PostConstruct` temporal para verificar que los queries funcionan contra tu BD.

### Fase 3: Negocio
6. Crea la interfaz `IMateriaService` en `service/`.
7. Crea `MateriaServiceImpl` en `service/impl/` con las validaciones de negocio.

### Fase 4: Web
8. Crea `MateriaController` en `controller/` con las rutas GET y POST.
9. Crea los HTMLs en `templates/materia/` (lista.html, formulario.html).

### Fase 5: Verificacion
10. Levanta la app con `./mvnw spring-boot:run` (o desde IntelliJ).
11. Abri `http://localhost:8080/materias` y verifica que funciona.
12. Proba crear, editar y eliminar.

**Repeti este ciclo para cada entidad:** Carrera -> Materia -> Alumno -> Usuario -> HistorialAcademico -> Correlatividad.

---

## 17. Checklist antes de cada commit

Antes de hacer `git add` y `git commit`, repasa:

- [ ] Cada metodo publico tiene un unico `return` al final (salvo dentro de `try-with-resources`).
- [ ] La clase respeta una sola responsabilidad (SOLID - S).
- [ ] No hay strings ni numeros magicos; estan en `Constantes.java` o en un `enum`.
- [ ] La logica de negocio esta en el `Service`, no en el `Controller` ni en el `Repository`.
- [ ] Todos los `Connection`, `PreparedStatement` y `ResultSet` usan `try-with-resources`.
- [ ] No hay ningun `catch` vacio ni `catch(Exception e) {}`.
- [ ] Las clases y metodos publicos tienen Javadoc.
- [ ] Las validaciones de rol estan en el `Service`, no solo ocultas en la vista.
- [ ] Se usan `PreparedStatement` con `?` (nunca concatenacion de strings en SQL).

---

## 18. Errores frecuentes y como resolverlos

### "Whitelabel Error Page" al entrar a una URL

**Causa:** El Controller retorna un nombre de plantilla (ej: `"carrera/lista"`) pero no existe el archivo `templates/carrera/lista.html`.
**Solucion:** Verifica que el archivo HTML existe exactamente en esa ruta dentro de `src/main/resources/templates/`.

### "No qualifying bean of type 'ICarreraService'"

**Causa:** Spring no encontro ninguna clase anotada con `@Service` que implemente `ICarreraService`.
**Solucion:** Verifica que:
1. La clase `CarreraServiceImpl` tiene la anotacion `@Service`.
2. Esta dentro del paquete `com.arduna.farias.servicio_correlativas` (o un subpaquete). Spring solo escanea desde el paquete de la clase `@SpringBootApplication` hacia abajo.

### "Cannot determine a DataSource" al arrancar

**Causa:** Las propiedades de conexion en `application.properties` estan mal o MySQL no esta corriendo.
**Solucion:** Verifica que MySQL esta activo, que el esquema `sicra_db` existe, y que el usuario/clave son correctos.

### "SQLSyntaxErrorException: Table 'sicra_db.xxx' doesn't exist"

**Causa:** No ejecutaste el `database.sql` para crear las tablas.
**Solucion:** Ejecuta el script SQL en MySQL antes de arrancar la app.

### "Request method 'POST' not supported"

**Causa:** El formulario HTML envia un POST pero el Controller solo tiene `@GetMapping` para esa ruta.
**Solucion:** Agrega un metodo con `@PostMapping` en el Controller.

### La app arranca pero se queda colgada sin mostrar nada

**Causa:** Si solo tenes `spring-boot-starter` sin `spring-boot-starter-web`, no se levanta el servidor Tomcat.
**Solucion:** Verifica que `spring-boot-starter-web` esta en tu `pom.xml` (ya esta configurado).

---

## Resumen visual: que capa conoce a cual

```
VISTA (Thymeleaf HTML)
    ^  solo recibe datos del Model, nunca llama a Service ni Repository
    |
CONTROLLER (@Controller)
    |  recibe HTTP, delega en Service, retorna nombre de vista
    v
SERVICE (@Service)
    |  valida reglas de negocio, delega en Repository
    v
REPOSITORY (@Repository)
    |  ejecuta SQL contra MySQL, retorna objetos Java
    v
BASE DE DATOS (MySQL - sicra_db)
```

**Regla de oro:** Cada capa solo conoce a la capa inmediatamente inferior. El Controller no sabe que existe MySQL. La Vista no sabe que existe el Service. El Repository no sabe que existe HTTP.
