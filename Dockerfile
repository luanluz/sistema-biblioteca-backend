# Stage 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copia os arquivos do projeto
COPY pom.xml .
COPY src ./src

# Executa o build da aplicação
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o JAR gerado no stage de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
