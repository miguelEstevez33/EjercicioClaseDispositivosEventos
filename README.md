# Procesador Analítico de Redes IoT: Documentación del Sistema

Este proyecto implementa una solución integral en Java para el procesamiento, análisis y visualización de eventos generados por una red de dispositivos inteligentes (IoT). El sistema ingesta un volumen de datos históricos desde un archivo CSV, reconstruye el estado lógico de cada dispositivo y ofrece una interfaz interactiva de consulta utilizando programación declarativa y funcional.

---

## 🏗️ Arquitectura del Proyecto

El desarrollo del sistema se fundamenta en principios sólidos de diseño de software y en el ecosistema moderno de herramientas de la plataforma Java.

* **Gestión y Construcción con Maven:** Maven actúa como el orquestador del ciclo completo de construcción del proyecto.


* El archivo `pom.xml` funciona como el corazón de la configuración, centralizando la administración de las dependencias externas y la versión del compilador.


* Maven automatiza fases críticas como la validación, compilación, ejecución de pruebas (`test`) y el empaquetado del artefacto binario final (`package`).




* **Reducción de Código Repetitivo (Boilerplate):** Se integró la biblioteca Lombok para mantener el código fuente limpio y enfocado en la lógica de negocio.
* Lombok genera automáticamente constructores, métodos de acceso (`getters`/`setters`), y las implementaciones de `equals()` y `hashCode()`.


* Este proceso ocurre de forma transparente al interceptar el árbol de sintaxis durante el tiempo de compilación.




* **Diseño Orientado a Objetos (POO):** La estructura del dominio se modela a partir de jerarquías de clases basadas en la herencia.
* Se definieron superclases abstractas que reúnen las características y comportamientos comunes de toda la jerarquía, evitando la redeclaración de atributos.


* El uso de métodos marcados con el modificador `abstract` obliga a cada subclase derivada a proveer su propia implementación específica, garantizando el comportamiento polimórfico.




* **Manejo de Excepciones:** La aplicación contempla la posibilidad de errores en tiempo de ejecución.
* Los errores generados por circunstancias anormales se representan como objetos de excepción que derivan de la clase base `Throwable`.


* Esta arquitectura de excepciones permite que el programa intervenga, capture el error y se recupere sin finalizar de manera abrupta.





---

## 📦 Entidades del Dominio

El modelado de datos refleja de manera exacta los componentes físicos e informativos de la red inteligente. Se definieron dos jerarquías de clases independientes que interactúan mediante relaciones de agregación:

### 1. Jerarquía de Dispositivos (Equipos Físicos)

La superclase abstracta `Dispositivo` actúa como la base de la jerarquía. Centraliza la identidad del equipo (ID, Nombre, Ubicación) y gestiona una colección interna de sus eventos históricos. Las subclases derivadas, que especializan el comportamiento y el cálculo de consumo eléctrico, son:

* `Termostato`
* `LuzInteligente`
* `EnchufeInteligente`
* `Cerradura`
* `SensorMovimiento`
* `SensorApertura`

### 2. Jerarquía de Eventos (Telemetría)

La superclase abstracta `Evento` captura la estampa de tiempo (`timestamp`), el origen y el tipo de dispositivo. Desde esta clase derivan los distintos tipos de telemetría emitidos:

* `EventoMedicion` (registra valores numéricos, como temperatura o voltaje).
* `EventoCambioEstado` (registra transiciones como ENCENDIDO/APAGADO o TRABADA/DESTRABADA).
* `EventoAlerta` (notifica situaciones anómalas con diferentes niveles de severidad).
* `EventoComando` (acciones ejecutadas remotamente por el usuario).

---

## ⚙️ Motor de Procesamiento y Lógica de Negocio

El núcleo analítico de la aplicación abandona los bucles imperativos tradicionales para abrazar el paradigma de programación funcional introducido en las últimas versiones de Java.

* **Ingesta de Datos:** La carga del archivo CSV se realiza optimizando el uso de memoria.
* Se utiliza `Files.lines()` para generar un flujo (`stream`) de cadenas de texto leyendo cada línea del archivo de forma individual.


* Este enfoque permite procesar grandes volúmenes de datos convirtiendo las cadenas en objetos sin necesidad de usar estructuras de control clásicas.




* **Almacenamiento en Memoria:** Las entidades procesadas se estructuran utilizando el Java Collections Framework.
* Se emplea la interfaz `Map` para asociar la identidad única de cada dispositivo con su objeto correspondiente, garantizando el acceso rápido y evitando duplicados.




* **Análisis con Stream API:** La lógica matemática y de consulta se ejecuta mediante flujos de datos.
* A diferencia de las colecciones tradicionales, los Streams procesan los elementos como una secuencia continua donde cada dato es interceptado y manipulado individualmente.


* Se aplican operaciones intermedias como `filter()` para descartar elementos según un predicado, y `map()` para transformar los datos de un tipo a otro.


* El motor evita el overhead de conversión de tipos (boxing/unboxing) en cálculos matemáticos usando flujos de primitivos como `IntStream` o `DoubleStream`.


* Se ejecutan ordenamientos descendentes combinando la interfaz `Comparator` con el método `reversed()`.


* Las consultas finalizan con operaciones terminales; en particular, se hace un uso extensivo de `Collectors.groupingBy()` para agrupar datos por claves, y *downstream collectors* como `Collectors.counting()` y `Collectors.summingDouble()` para acumular los resultados.





---

## 🖥️ Interfaz de Usuario: Menú Interactivo

Para facilitar la interacción con los reportes analíticos, el sistema presenta un menú de consola basado en texto.

* **Implementación Técnica:**
* La captura de la entrada del usuario se realiza mediante la instanciación de la clase `Scanner`, la cual lee los valores directamente desde el teclado a través de la consola estándar.


* El ciclo de vida del menú está gobernado por una estructura `do-while` que garantiza la ejecución continua y repetitiva del programa hasta que el usuario decida salir explícitamente.




* **Opciones Disponibles en el Menú:**
1. **Eventos por tipo de dispositivo:** Genera una estadística global agrupando todos los eventos procesados según la categoría del equipo emisor.
2. **Consumo estimado por habitación, de mayor a menor:** Ejecuta un agrupamiento múltiple que suma la energía (Wh) proyectada por todos los dispositivos en una misma ubicación, ordenando el resultado de forma descendente.
3. **Temperatura promedio por termostato:** Filtra la colección aislando únicamente los objetos `Termostato` y reduce su historial de métricas para obtener la temperatura media.
4. **Dispositivo con más alertas críticas:** Evalúa el nivel de severidad en los historiales para identificar de forma unívoca el nodo de la red con mayor cantidad de incidencias.
5. **Hora del día (0–23) con más eventos:** Analiza las estampas de tiempo (`timestamp`) de todo el parque de dispositivos para descubrir el horario pico de tráfico de la red.
6. **Los 3 dispositivos más activos:** Genera un ranking limitando el flujo de resultados (`limit(3)`) luego de ordenar el sistema en base a la longitud del historial de eventos de cada equipo.


7. **Línea de tiempo de las alertas críticas (cronológico):** Aísla los eventos de severidad alta de todos los equipos y los ordena mediante su orden natural temporal, emitiendo un reporte secuencial.
8. **Salir:** Finaliza el bucle de la aplicación de manera controlada.



---

## 🚀 Ejecución del Sistema

El proyecto está preparado para ser ejecutado directamente utilizando las herramientas de construcción integradas, sin necesidad de compilar las clases manualmente.

* Para ejecutar el sistema utilizando Maven y su resolución automática del *classpath*, utilice el siguiente comando desde el directorio raíz:
```bash
mvn exec:java -Dexec.mainClass="com.example.App"

```


*Este comando orquesta automáticamente la resolución de dependencias, compila el código fuente si hay cambios recientes y ejecuta el método principal de la aplicación en un entorno seguro.*