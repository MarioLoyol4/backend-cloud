# Usar una imagen base ligera de Java
FROM eclipse-temurin:21-jdk-alpine

# Crear un directorio temporal
VOLUME /tmp

# Copiar el archivo JAR compilado al contenedor
COPY target/*.jar app.jar

# Comando para ejecutar el microservicio
ENTRYPOINT ["java","-jar","/app.jar"]