FROM maven:3.9.9-eclipse-temurin-17 AS build
 
WORKDIR /app
COPY . .
 
RUN mvn clean package -DskipTests
 
# Etapa 2 - execução
FROM eclipse-temurin:17-jdk
 
WORKDIR /app
 
# Instalando o Tesseract
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-por \
    tesseract-ocr-eng \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
 
COPY --from=build /app/target/*.jar app.jar
 
 
EXPOSE 8080
 
# Limita heap para caber no plano gratuito do Render (512MB RAM total)
CMD ["java", "-Xms64m", "-Xmx320m", "-jar", "app.jar"]