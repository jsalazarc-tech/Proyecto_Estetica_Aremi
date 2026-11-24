-- ====================================================================
-- SISTEMA DE GESTIÓN DE CITAS Y LIQUIDACIÓN DE COMISIONES
-- SALÓN DE BELLEZA AREMI
-- Base de Datos: salon_aremi (MySQL 8.0+)
-- ====================================================================

CREATE DATABASE IF NOT EXISTS `salon_aremi` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `salon_aremi`;

-- --------------------------------------------------------
-- 1. Tabla de Usuarios (RF-01, RNF-01, RNF-05)
-- --------------------------------------------------------
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(64) NOT NULL, -- Contraseñas encriptadas con SHA-256
  `nombre_completo` VARCHAR(100) NOT NULL,
  `rol` ENUM('administrador', 'empleada') NOT NULL DEFAULT 'empleada',
  `fecha_creacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Credenciales iniciales:
-- admin: admin123 -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- fernanda: fernanda123 -> a277c3ba4d39d7a49cc6507e1818e0d6ebc3d8621ccc63daec59829a6ae52ad9
-- ashley: ashley123 -> 2336e2ec1b97338056c1e64172acf13a6461d977ecbcb23cbec31d25de98e2ad
-- monika: monika123 -> 07e6f0ebf1ecce152c491cb7ac864e5ad8fa9e11d8be9127e18da867e2c42fa7
-- veronika: veronika123 -> d6f1c8895277b7e7133bd4aaba2feb61d9b69f2f3a159670cd44b8dbc39a6973
INSERT INTO `usuarios` (`username`, `password`, `nombre_completo`, `rol`) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador General', 'administrador'),
('fernanda', 'a277c3ba4d39d7a49cc6507e1818e0d6ebc3d8621ccc63daec59829a6ae52ad9', 'Fernanda Administradora', 'administrador'),
('ashley', '2336e2ec1b97338056c1e64172acf13a6461d977ecbcb23cbec31d25de98e2ad', 'Ashley', 'empleada'),
('monika', '07e6f0ebf1ecce152c491cb7ac864e5ad8fa9e11d8be9127e18da867e2c42fa7', 'Monika', 'empleada'),
('veronika', 'd6f1c8895277b7e7133bd4aaba2feb61d9b69f2f3a159670cd44b8dbc39a6973', 'Veronika', 'empleada');

-- --------------------------------------------------------
-- 2. Tabla de Empleadas (RF-03)
-- Ashley 20% en Uñas y Manicure
-- Monika 30% en Cejas
-- Veronika 50% en Depilación
-- --------------------------------------------------------
DROP TABLE IF EXISTS `empleadas`;
CREATE TABLE `empleadas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `telefono` VARCHAR(20) DEFAULT NULL,
  `direccion` VARCHAR(200) DEFAULT '',
  `email` VARCHAR(100) DEFAULT '',
  `especialidad` VARCHAR(100) NOT NULL,
  `porcentaje_comision` DECIMAL(5,2) NOT NULL,
  `username` VARCHAR(50) DEFAULT NULL,
  `activo` TINYINT(1) DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `fk_empleadas_usuario` (`username`),
  CONSTRAINT `fk_empleadas_usuario` FOREIGN KEY (`username`) REFERENCES `usuarios` (`username`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `empleadas` (`nombre`, `telefono`, `direccion`, `email`, `especialidad`, `porcentaje_comision`, `username`, `activo`) VALUES
('Ashley', '3101234567', 'Calle 10 # 45-20', 'ashley@salonaremi.com', 'Uñas y Manicure', 20.00, 'ashley', 1),
('Monika', '3119876543', 'Av. 6N # 28-15', 'monika@salonaremi.com', 'Cejas', 30.00, 'monika', 1),
('Veronika', '3125556677', 'Carrera 56 # 14-80', 'veronika@salonaremi.com', 'Depilación', 50.00, 'veronika', 1);

-- --------------------------------------------------------
-- 3. Tabla de Clientes (RF-02, CU-03, CU-06)
-- --------------------------------------------------------
DROP TABLE IF EXISTS `clientes`;
CREATE TABLE `clientes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `telefono` VARCHAR(20) NOT NULL,
  `direccion` VARCHAR(200) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `clientes` (`nombre`, `telefono`, `direccion`, `email`) VALUES
('Valentina Gómez', '3154829103', 'Calle 5 # 34-12', 'valentina.gomez@gmail.com'),
('Camila Torres', '3187214950', 'Av. Roosevelt # 28-40', 'camila.torres@hotmail.com'),
('Mariana Restrepo', '3119043218', 'Carrera 66 # 10-05', 'mariana.restrepo@yahoo.com');

-- --------------------------------------------------------
-- 4. Tabla de Servicios (RF-04, CU-05)
-- --------------------------------------------------------
DROP TABLE IF EXISTS `servicios`;
CREATE TABLE `servicios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(255) DEFAULT NULL,
  `categoria` VARCHAR(50) NOT NULL,
  `precio` DECIMAL(10,2) NOT NULL,
  `duracion_minutos` INT DEFAULT 60,
  `activo` TINYINT(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `servicios` (`nombre`, `descripcion`, `categoria`, `precio`, `duracion_minutos`, `activo`) VALUES
('Manicure Tradicional', 'Limpieza, limado, cutícula y esmaltado tradicional', 'Uñas y Manicure', 25000.00, 45, 1),
('Uñas Acrílicas Esculpidas', 'Extensión y diseño artístico con acrílico', 'Uñas y Manicure', 65000.00, 90, 1),
('Pedicure Spa', 'Exfoliación, hidratación profunda y esmaltado', 'Uñas y Manicure', 35000.00, 60, 1),
('Diseño de Cejas con Henna', 'Diseño personalizado y pigmentación con henna natural', 'Cejas', 30000.00, 45, 1),
('Micropigmentación de Cejas', 'Técnica pelo a pelo semipermanente', 'Cejas', 120000.00, 120, 1),
('Depilación Facial Completa', 'Cera suave para cejas, bozo y mentón', 'Depilación', 25000.00, 30, 1),
('Depilación Piernas Completas', 'Depilación con cera miel nutritiva', 'Depilación', 45000.00, 45, 1),
('Depilación Bikini Completo', 'Cera especializada para zona íntima', 'Depilación', 40000.00, 40, 1);

-- --------------------------------------------------------
-- 5. Tabla de Citas (RF-05, RF-06, RF-07, CU-02, CU-07)
-- --------------------------------------------------------
DROP TABLE IF EXISTS `citas`;
CREATE TABLE `citas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `id_cliente` INT DEFAULT NULL,
  `nombre_cliente` VARCHAR(100) NOT NULL,
  `telefono_cliente` VARCHAR(20) NOT NULL,
  `id_servicio` INT DEFAULT NULL,
  `nombre_servicio` VARCHAR(100) NOT NULL,
  `id_empleada` INT DEFAULT NULL,
  `nombre_empleada` VARCHAR(100) NOT NULL,
  `fecha` DATE NOT NULL,
  `hora` VARCHAR(10) NOT NULL,
  `estado` ENUM('Pendiente', 'Completada', 'Cancelada') NOT NULL DEFAULT 'Pendiente',
  `notas` VARCHAR(255) DEFAULT NULL,
  `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_citas_cliente` (`id_cliente`),
  KEY `fk_citas_servicio` (`id_servicio`),
  KEY `fk_citas_empleada` (`id_empleada`),
  CONSTRAINT `fk_citas_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_citas_servicio` FOREIGN KEY (`id_servicio`) REFERENCES `servicios` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_citas_empleada` FOREIGN KEY (`id_empleada`) REFERENCES `empleadas` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `citas` (`id_cliente`, `nombre_cliente`, `telefono_cliente`, `id_servicio`, `nombre_servicio`, `id_empleada`, `nombre_empleada`, `fecha`, `hora`, `estado`, `notas`) VALUES
(1, 'Valentina Gómez', '3154829103', 1, 'Manicure Tradicional', 1, 'Ashley', CURDATE(), '10:00', 'Pendiente', 'Cliente regular'),
(2, 'Camila Torres', '3187214950', 4, 'Diseño de Cejas con Henna', 2, 'Monika', CURDATE(), '11:00', 'Pendiente', 'Primera sesión');

-- --------------------------------------------------------
-- 6. Tabla de Servicios Realizados (RF-08, CU-08)
-- Base para la liquidación de comisiones
-- --------------------------------------------------------
DROP TABLE IF EXISTS `servicios_realizados`;
CREATE TABLE `servicios_realizados` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `id_cita` INT DEFAULT NULL,
  `id_empleada` INT NOT NULL,
  `nombre_empleada` VARCHAR(100) NOT NULL,
  `id_servicio` INT NOT NULL,
  `nombre_servicio` VARCHAR(100) NOT NULL,
  `id_cliente` INT DEFAULT NULL,
  `nombre_cliente` VARCHAR(100) NOT NULL,
  `fecha` DATE NOT NULL,
  `valor_cobrado` DECIMAL(10,2) NOT NULL,
  `porcentaje_comision` DECIMAL(5,2) NOT NULL,
  `comision_calculada` DECIMAL(10,2) NOT NULL,
  `estado_liquidacion` ENUM('Pendiente', 'Liquidado') NOT NULL DEFAULT 'Pendiente',
  `id_liquidacion` INT DEFAULT NULL,
  `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_sr_cita` (`id_cita`),
  KEY `fk_sr_empleada` (`id_empleada`),
  KEY `fk_sr_servicio` (`id_servicio`),
  KEY `fk_sr_cliente` (`id_cliente`),
  CONSTRAINT `fk_sr_cita` FOREIGN KEY (`id_cita`) REFERENCES `citas` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_sr_empleada` FOREIGN KEY (`id_empleada`) REFERENCES `empleadas` (`id`),
  CONSTRAINT `fk_sr_servicio` FOREIGN KEY (`id_servicio`) REFERENCES `servicios` (`id`),
  CONSTRAINT `fk_sr_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 7. Tabla de Liquidaciones (RF-09, RF-10, CU-04)
-- Histórico de liquidaciones de comisiones guardadas
-- --------------------------------------------------------
DROP TABLE IF EXISTS `liquidaciones`;
CREATE TABLE `liquidaciones` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `id_empleada` INT NOT NULL,
  `nombre_empleada` VARCHAR(100) NOT NULL,
  `fecha_inicio` DATE NOT NULL,
  `fecha_fin` DATE NOT NULL,
  `total_servicios` INT NOT NULL,
  `total_ventas` DECIMAL(10,2) NOT NULL,
  `porcentaje_aplicado` DECIMAL(5,2) NOT NULL,
  `total_comision` DECIMAL(10,2) NOT NULL,
  `total_salon` DECIMAL(10,2) NOT NULL,
  `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `notas` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_liq_empleada` (`id_empleada`),
  CONSTRAINT `fk_liq_empleada` FOREIGN KEY (`id_empleada`) REFERENCES `empleadas` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- 8. Tabla de Facturas (Compatibilidad de facturación)
-- --------------------------------------------------------
DROP TABLE IF EXISTS `facturas`;
CREATE TABLE `facturas` (
  `id_factura` VARCHAR(20) NOT NULL,
  `usuario` VARCHAR(100) DEFAULT NULL,
  `telefono` VARCHAR(20) DEFAULT NULL,
  `valor_venta` DECIMAL(10,2) DEFAULT NULL,
  `nombre_vendedor` VARCHAR(100) DEFAULT NULL,
  `fecha_venta` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_factura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
