# inventarioSample

### Modelo de BD
La BD esta creada en mariaBD.
El script para la creacion de la BD y carga de datos necesarios y algunos de prueba se encuentran en el mismo script.

Se considera el empleo de catalogos y relaciones por medio de indice para reducir el tamaño de las tablas para almacenar los campos minimos requeridos, una tabla de relacion para asociar las acciones permitidas por rol y una tabla de bitacora que permite revisar los cambios de estado que ha tenido algun activo.
De igual manera se crea una vista para mejorar la extraccion de informacion y visualizarla en la capa de UI. 

El Script se encuentra en este proyecto en la carpeta rsrc con el nombre inventarioMariaDB.sql.

### Java
Este es un proyecto creado con maven, por lo que las dependecias y compilacion se manejan desde el pom.

Este proyecto es la capa de servicios del sistema de inventario, fue creado con spring-boot 4.1.1, emplea servicios REST para el control del aplicativo. Su creacion esta basada en principios SOLID.

Emplea datasource para conectarse a la BD y emplea la capa JPA para la interaccion con la BD, por medio de entidades.

