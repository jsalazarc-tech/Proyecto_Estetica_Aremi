-- Script para cambiar el usuario admin por fernanda
-- Base de datos: salon_aremi

USE salon_aremi;

-- Opción 1: Actualizar el usuario existente 'admin' a 'fernanda'
-- Contraseña: fernanda123
-- Hash SHA-256 de 'fernanda123': 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92

UPDATE usuarios
SET username = 'fernanda',
    nombre_completo = 'Fernanda Administrador',
    password = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92'
WHERE username = 'admin';

-- Verificar el cambio
SELECT username, nombre_completo, rol FROM usuarios WHERE username = 'fernanda';

-- Si prefieres eliminar el admin viejo y crear uno nuevo:
-- DELETE FROM usuarios WHERE username = 'admin';
-- INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES
-- ('fernanda', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Fernanda Administrador', 'administrador');