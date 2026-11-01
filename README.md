# 💅 Salón de Belleza AREMI - Sistema de Gestión

Sistema Integral de Gestión de Citas, Clientes, Servicios y Liquidación de Comisiones desarrollado en **Java (Swing)** y persistencia dual en **H2 Embebido / MySQL Workbench**.

Proyecto Integrador de 4° Semestre - **Institución Universitaria Antonio José Camacho (UNIAJC)**.

---

## 📋 Tabla de Contenido
- [Descripción General](#-descripción-general)
- [Características y Módulos](#-características-y-módulos)
- [Arquitectura del Sistema](#-arquitectura-del-sistema)
- [Usuarios y Credenciales](#-usuarios-y-credenciales)
- [Base de Datos](#-base-de-datos)
- [Estructura de Ramas (GitFlow)](#-estructura-de-ramas-gitflow)
- [Instrucciones de Ejecución](#-instrucciones-de-ejecución)
- [Estructura del Proyecto](#-estructura-del-proyecto)

---

## 🌟 Descripción General

El **Sistema de Gestión Salón de Belleza AREMI** automatiza los procesos operativos y financieros de una estética o salón de belleza:
- Registro centralizado de clientes y catálogo de servicios.
- Programación de citas con validación de horarios y solapamiento de agenda por empleada.
- Registro de servicios efectivamente prestados.
- Liquidación periódica de comisiones por porcentajes personalizados (20%, 30%, 50%) según especialidad (Uñas, Cejas, Depilación).
- Generación y exportación de comprobantes de liquidación en formato **PDF**.
- Seguridad y autenticación con contraseñas cifradas en **SHA-256** y control de acceso basado en roles (**Administrador** y **Empleada**).

---

## 🚀 Características y Módulos

### 1. 🔐 Autenticación y Seguridad
- Control de acceso por credenciales con hash SHA-256.
- Menú modular dinámico según el rol (`administrador` o `empleada`).
- Cierre de sesión y protección de módulos administrativos.

### 2. 👥 Gestión de Clientes (CRUD)
- Registro, consulta, edición y eliminación de clientes.
- Historial de contacto y datos de contacto rápido (teléfono, email, dirección).

### 3. 💅 Empleadas y Configuración de Comisiones
- Gestión de profesionales con especialidad.
- Configuración de porcentaje de comisión individual:
  - **Uñas y Manicure**: 20%
  - **Cejas**: 30%
  - **Depilación**: 50%

### 4. ✨ Catálogo de Servicios
- Tarifas, duración estimada en minutos y categorías de atención.

### 5. 📅 Agenda de Citas y Disponibilidad
- Programación de citas seleccionando cliente, servicio, empleada, fecha y hora.
- **Validación inteligente de conflictos**: impide agendar una empleada en una franja horaria ya ocupada.
- Transición directa de cita a "Servicio Realizado".

### 6. ✂ Registro de Servicios Realizados
- Confirmación de atención prestada con cálculo automático de comisiones generadas.
- Vista para empleadas donde consultan sus servicios y saldo acumulado en el mes.

### 7. 💰 Liquidación de Comisiones y Reportes PDF
- Consulta de servicios pendientes de liquidar por empleada y rango de fechas.
- Resumen financiero: total facturado, comisión a pagar y ganancia neta para el salón.
- Generación de comprobante formal en **PDF** con librerías iText.

### 8. ⚙ Conexión Dual a Base de Datos
- **Modo Local Embebido (H2)**: Funciona automáticamente sin requerir servidor externo ni contraseñas.
- **Modo Servidor MySQL**: Permite conectar a MySQL Workbench local o remoto ingresando host, puerto, base de datos y contraseña mediante el asistente gráfico de configuración.

---

## 👥 Usuarios y Credenciales

El sistema viene preconfigurado con usuarios para pruebas inmediatas:

| Usuario | Contraseña | Rol | Especialidad / Permisos |
| :--- | :--- | :--- | :--- |
| **`fernanda`** | `fernanda123` | **Administrador** | Acceso total al panel de control |
| **`admin`** | `admin123` | **Administrador** | Administrador general |
| **`ashley`** | `ashley123` | **Empleada** | Uñas y Manicure (Comisión: 20%) |
| **`monika`** | `monika123` | **Empleada** | Cejas (Comisión: 30%) |
| **`veronika`** | `veronika123` | **Empleada** | Depilación (Comisión: 50%) |
| **`empleada1`** | `emp123` | **Empleada** | Perfil demostrativo de empleada |

---

## 🌿 Estructura de Ramas (GitFlow)

El repositorio mantiene un flujo de trabajo ordenado por características:

```
main (Producción estable)
 ├── develop (Integración de desarrollo)
 ├── feature/gestion-citas-servicios
 ├── feature/liquidacion-comisiones-pdf
 ├── feature/seguridad-base-datos
 └── feature/interfaz-grafica-aremi
```

---

## 💻 Instrucciones de Ejecución

### Requisitos Previos:
- **Java JDK 17 o superior** (probado y compatible con JDK 21 y JDK 25).
- Compatible con Windows 10 / 11.

### Paso a paso:

1. **Clonar o descargar el repositorio**:
   ```bash
   git clone https://github.com/jsalazarc-tech/Proyecto_Estetica_Aremi.git
   cd Proyecto_Estetica_Aremi
   ```

2. **Compilar el proyecto**:
   Ejecuta el archivo:
   ```cmd
   compilar.bat
   ```

3. **Ejecutar la aplicación**:
   Doble clic o desde consola:
   ```cmd
   ejecutar.bat
   ```

> 💡 **Nota:** La aplicación arranca por defecto en modo local de base de datos embebida, inicializando automáticamente las tablas y usuarios de prueba. Si deseas conectar a tu servidor MySQL local, pulsa el botón **"⚙ Configurar Conexión a Base de Datos"** en la pantalla de Login.

---

## 📁 Estructura del Proyecto

```
Proyecto_Estetica_Aremi/
├── .gitignore
├── README.md
├── compilar.bat
├── ejecutar.bat
└── proyecto-integrador-main/
    └── proyecto-integrador-main/
        ├── compilar.bat
        ├── ejecutar.bat
        ├── lib/
        │   ├── h2-2.2.224.jar
        │   ├── itextpdf-5.5.13.3.jar
        │   └── mysql-connector-j-8.0.33.jar
        ├── dumps/
        │   └── Dump_SalonAremi_Completo.sql
        └── src/
            ├── MainApplication.java
            ├── LoginSystemAremi.java
            ├── SeguridadManager.java
            ├── DatabaseConfig.java
            ├── DatabaseConnection.java
            ├── ConnectionPool.java
            ├── ConfiguracionDBDialog.java
            ├── AgendarCitaGUI.java
            ├── InterfazClientes.java
            ├── GestionEmpleadasGUI.java
            ├── GestionServiciosGUI.java
            ├── ServiciosRealizadosGUI.java
            ├── LiquidacionComisionesGUI.java
            ├── ReportePDFGenerator.java
            ├── Pago.java
            ├── PagoDetailsDialog.java
            ├── Facturas.java
            ├── Cliente.java
            ├── Usuario.java
            ├── Servicio.java
            ├── Empleado.java
            ├── Cita.java
            ├── ServicioRealizado.java
            └── LiquidacionComision.java
```

---

## 📜 Licencia y Créditos
Proyecto académico desarrollado para **Salón de Belleza Aremi**.
UNIAJC © 2026.
