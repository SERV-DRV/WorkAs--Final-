# WorkAs (Final)
|Proyecto Final Del Año de Quinto Perito Informatica|   Programadores necesitan registrar tareas y pagos por proyecto. Implementar un sistema para clientes, contratos, tareas y facturación básica.

# 💼 WorkAs — Gestor para Freelancers

**WorkAs** es una aplicación de escritorio desarrollada en **JavaFX** con conexión a **MySQL**, diseñada para facilitar la comunicación y gestión de proyectos entre **freelancers** y **clientes**.  
Su objetivo es centralizar la administración de tareas, contratos y pagos dentro de una interfaz intuitiva, optimizando el flujo de trabajo del profesional independiente.

---

## 📖 Descripción general

WorkAs permite a los **freelancers** registrar clientes, crear proyectos o contratos, asignar tareas, controlar avances y registrar pagos recibidos.  
También facilita la interacción con los clientes, ofreciendo un entorno organizado para el seguimiento de cada trabajo.

La aplicación está orientada a **usuarios finales**, sin necesidad de conocimientos técnicos avanzados.

---

## 🚀 Características principales

- 👤 **Gestión de clientes** — Agrega, edita y elimina información de tus clientes fácilmente.  
- 📄 **Contratos / Proyectos** — Crea contratos con detalles de fechas, costos y alcances.  
- 🧩 **Tareas** — Divide los proyectos en tareas individuales y asigna su estado de progreso.  
- 💰 **Control de pagos** — Registra los pagos recibidos y consulta saldos pendientes.  
- 📊 **Reportes** — Visualiza el progreso general de tus proyectos y tus ingresos totales.  
- 🔐 **Inicio de sesión seguro** — Acceso protegido con credenciales.  
- 🗃️ **Base de datos MySQL** — Almacena toda la información de forma estructurada y persistente.  

---

## 🧰 Tecnologías utilizadas

| Tecnología | Descripción |
|-------------|-------------|
| **JavaFX** | Interfaz gráfica de usuario moderna y adaptable |
| **MySQL** | Base de datos relacional para almacenar la información |
| **Java SE 21+** | Lenguaje de programación principal |
| **NetBeans / IntelliJ IDEA / Eclipse** | IDEs compatibles |
| **Scene Builder** | (Opcional) para diseño visual de las vistas FXML |

---

## 🖥️ Requisitos del sistema

- ☕ **JDK 21 o superior**  
- 🗄️ **Servidor MySQL 8.0+**  
- 💾 **Conexión estable a la base de datos local o remota**  
- 🪟 Compatible con **Windows**, **Linux** y **macOS**

---

## ⚙️ Instalación y configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/SERV-DRV/WorkAs--Final-.git
   cd WorkAs--Final-
   ```

2. **Configurar la base de datos:**
   - Crear una base de datos MySQL llamada `workas_db` (o el nombre definido en la clase de conexión).
   - Importar el script SQL que se encuentra en la carpeta `/db`.
   - Verificar las credenciales en la clase de conexión:
     ```java
     private final String URL = "jdbc:mysql://localhost/workas_db";
     private final String USER = "root";
     private final String PASSWORD = "admin";
     ```

3. **Abrir el proyecto en tu IDE favorito** (NetBeans, IntelliJ, Eclipse).  
   - Asegúrate de tener configurado el **JDK 21** correctamente.  
   - Si usas NetBeans y aparece el error:
     ```
     The J2SE Platform is not correctly set up.
     ```
     abre *Propiedades del proyecto → Librerías → Plataforma Java* y selecciona la versión correcta del JDK.

4. **Ejecutar la aplicación:**
   - Desde el IDE o mediante:
     ```bash
     mvn javafx:run
     ```
     (si está configurado como proyecto Maven).

---

## 🧭 Guía rápida de uso

1. Inicia la aplicación.  
2. Ingresa tus credenciales (si ya tienes una cuenta) o regístrate.  
3. Desde el menú principal podrás:
   - Crear un nuevo cliente.  
   - Registrar un contrato o proyecto.  
   - Agregar tareas dentro del proyecto.  
   - Marcar tareas como completadas.  
   - Registrar pagos recibidos del cliente.  
4. Consulta el historial de proyectos y reportes financieros.  

---

## 🧑‍💼 Público objetivo

- **Freelancers** (diseñadores, programadores, redactores, etc.)  
- Profesionales independientes que trabajan por proyecto.  
- Usuarios que buscan una herramienta sencilla para organizar su trabajo con clientes.

---

## 🧩 Estructura del proyecto (simplificada)

```
WorkAs--Final-/
├── src/
│   ├── org/workas/controller/     # Controladores JavaFX
│   ├── org/workas/model/          # Clases de modelo (Clientes, Contratos, etc.)
│   ├── org/workas/db/             # Conexión a base de datos MySQL
│   ├── org/workas/system/         # Clase Main y manejo de escenas
│   └── org/workas/view/           # Archivos FXML y recursos visuales
├── database/                      # Script SQL (si aplica)
├── nbproject/                     # Configuración NetBeans
└── README.md
```

---

## 💡 Consejos

- Realiza **copias de seguridad** de la base de datos con frecuencia.  
- Asegúrate de tener el **driver JDBC de MySQL** agregado en las librerías.  
- Usa la vista de consola para verificar posibles errores de conexión.  
- Puedes personalizar los estilos con **CSS** desde las hojas de estilo JavaFX.

---

## 🧑‍💻 Autores

Desarrollado por el equipo **SERV-DRV**  
Con la colaboración de:  
- [@eGalv3zDev](https://github.com/eGalv3zDev)

---

## 📫 Contacto

- 📧 **acamposeco-2024018@kinal.edu.gt**  
- 📧 **sderosa-2023220@kinal.edu.gt**

---

## 📜 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.  
Eres libre de usar, modificar y distribuir el código, siempre y cuando se mantenga el crédito a los autores originales.

---

## 🌟 Agradecimientos

Gracias a todos los docentes y compañeros que apoyaron el desarrollo de este proyecto, así como a la comunidad JavaFX por su documentación y herramientas de código abierto.