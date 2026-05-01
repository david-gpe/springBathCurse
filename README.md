# ⚙️ Spring Batch – Procesamiento de Datos

## 🧠 Descripción

Proyecto de práctica desarrollado con **Spring Batch** enfocado en el procesamiento por lotes de información.

El objetivo es demostrar la implementación de un flujo ETL básico:

* Lectura de datos
* Procesamiento
* Escritura de resultados

---

## 🚀 Funcionalidades

* 📥 Lectura de datos desde archivo o fuente configurada
* 🔄 Procesamiento de información mediante `ItemProcessor`
* 📤 Escritura de resultados procesados
* ⚙️ Configuración de Jobs y Steps con Spring Batch

---

## 🧱 Arquitectura

El flujo del batch sigue el patrón:

```plaintext
Reader → Processor → Writer
```

---

## 🛠️ Tecnologías

* Java
* Spring Boot
* Spring Batch
* Maven

---

## ⚙️ Ejecución del proyecto

### 1. Clonar repositorio

```bash
git clone https://github.com/david-gpe/springBathCurse.git
cd springBathCurse
```

---

### 2. Compilar

```bash
mvn clean install
```

---

### 3. Ejecutar

```bash
mvn spring-boot:run
```

---

## 📂 Estructura relevante

* `JobConfig` → Configuración del Job
* `StepConfig` → Definición de pasos
* `Reader` → Lectura de datos
* `Processor` → Transformación
* `Writer` → Escritura

---

## 🧠 Aprendizajes

* Configuración de jobs en Spring Batch
* Procesamiento por chunks
* Manejo de flujos batch
* Separación de responsabilidades (Reader/Processor/Writer)

---

## 🚧 Mejoras futuras

* Integración con base de datos
* Manejo de errores y reintentos
* Logging detallado
* Dashboard de ejecución

---

## 📌 Nota

Este proyecto fue desarrollado como parte de un curso de Spring Batch con fines educativos.
