# Salón de Belleza Aremi - Arquitectura del Sistema

Sistema de información de escritorio para la administración de citas, registro de servicios realizados y liquidación de comisiones del personal para el Salón de Belleza Aremi.

---

## 1. Arquitectura de Software: Modelo - Vista - Controlador (MVC) + DAO

El sistema está estructurado bajo la arquitectura en capas Modelo-Vista-Controlador (MVC), complementada con el patrón de acceso a datos **DAO (Data Access Object)**:

```
src/
├── model/                  --> Entidades del dominio (POJOs puros)
│   ├── Cliente.java
│   ├── Empleado.java
│   ├── Servicio.java
│   ├── Cita.java
│   ├── ServicioRealizado.java
│   ├── LiquidacionComision.java
│   └── Usuario.java
│
├── dao/                    --> Capa de persistencia exclusiva (JDBC)
│   ├── ConexionBD.java     --> Conexión Singleton
│   ├── ClienteDAO.java
│   ├── EmpleadoDAO.java
│   ├── ServicioDAO.java
│   ├── CitaDAO.java
│   ├── ServicioRealizadoDAO.java
│   ├── LiquidacionDAO.java
│   └── UsuarioDAO.java
│
├── controller/             --> Lógica de negocio y validaciones
│   ├── ClienteController.java
│   ├── EmpleadoController.java
│   ├── ServicioController.java
│   ├── CitaController.java
│   ├── ServicioRealizadoController.java
│   ├── LiquidacionController.java
│   └── AuthController.java
│
├── view/                   --> Vistas e interfaces gráficas Swing
│   ├── LoginSystemAremi.java
│   ├── MainApplication.java
│   ├── InterfazClientes.java
│   ├── GestionEmpleadasGUI.java
│   ├── GestionServiciosGUI.java
│   ├── AgendarCitaGUI.java
│   ├── ServiciosRealizadosGUI.java
│   ├── LiquidacionComisionesGUI.java
│   └── ConfiguracionDBDialog.java
│
└── util/                   --> Componentes transversales
    ├── SeguridadManager.java
    └── ReportePDFGenerator.java
```

---

## 2. Diagrama de Clases UML del Dominio

```mermaid
classDiagram
    direction TB

    class Usuario {
        -int id
        -String username
        -String rol
        -boolean activo
        +esAdministrador() boolean
        +esEmpleada() boolean
    }

    class Cliente {
        -int id
        -String nombre
        -String documento
        -String telefono
        -boolean activo
    }

    class Empleado {
        -int id
        -String nombre
        -String especialidad
        -double porcentajeComision
        -boolean activo
        +calcularComision(double valor) double
    }

    class Servicio {
        -int id
        -String nombre
        -String descripcion
        -double precio
        -boolean activo
    }

    class Cita {
        -int id
        -int clienteId
        -int empleadoId
        -int servicioId
        -LocalDateTime fechaHora
        -int duracionMinutos
        -String estado
        -String notas
    }

    class ServicioRealizado {
        -int id
        -int citaId
        -int empleadoId
        -int servicioId
        -int clienteId
        -LocalDate fechaAtencion
        -double valorCobrado
        -double porcComisionHist
        -String estado
        +calcularComision() double
    }

    class LiquidacionComision {
        -int id
        -int empleadoId
        -LocalDate fechaInicio
        -LocalDate fechaFin
        -double totalVentas
        -double totalComision
        -int numeroServicios
        -LocalDateTime fechaLiquidacion
        -String estado
    }

    Cliente "1" <-- "0..*" Cita : agenda
    Empleado "1" <-- "0..*" Cita : asignada a
    Servicio "1" <-- "0..*" Cita : reserva

    Cita "0..1" <-- "1" ServicioRealizado : se liquida a partir de
    Empleado "1" <-- "0..*" ServicioRealizado : realizado por
    Servicio "1" <-- "0..*" ServicioRealizado : tipo de servicio
    Cliente "1" <-- "0..*" ServicioRealizado : atendido a

    Empleado "1" <-- "0..*" LiquidacionComision : recibe pago
    LiquidacionComision "1" o-- "1..*" ServicioRealizado : consolida
```

---

## 3. Patrones de Diseño Implementados

### 3.1 Singleton (`ConexionBD`)
Garantiza una única instancia de conexión activa a la base de datos, optimizando el uso de recursos y leyendo los parámetros de configuración de forma desacoplada desde `db.properties`.

### 3.2 Data Access Object (DAO)
Encapsula todas las sentencias SQL y operaciones JDBC dentro de clases especializadas (`ClienteDAO`, `CitaDAO`, `LiquidacionDAO`, etc.). La capa de presentación (`view`) desconoce los detalles de persistencia y no contiene sentencias SQL.

### 3.3 Modelo - Vista - Controlador (MVC)
Separa claramente los datos de la aplicación (`model`), las pantallas de usuario (`view`) y la coordinación de flujo y reglas de negocio (`controller`).
