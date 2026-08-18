# Simulador de Limnígrafo por Presión Hidrostática

Aplicación de escritorio en Java (Swing) que simula el comportamiento de un
limnígrafo sumergible de presión relativa: genera datos sintéticos de nivel
de agua y temperatura, los convierte en las magnitudes que efectivamente
mide el hardware (presión hidrostática y temperatura), les agrega ruido de
sensor y muestra todo en tiempo real mediante gráficos y controles
interactivos.

Para el detalle conceptual (qué es un limnígrafo, para qué se usan sus
datos, qué hardware se está emulando) ver [`limnigrafos/README.md`](limnigrafos/README.md).

## Requisitos

- JDK 17 o superior (probado con JDK 21).
- Maven 3.6+.
- Entorno con soporte de interfaz gráfica (Swing), ya que la app abre una ventana de escritorio.

## Cómo ejecutarlo

Desde la raíz del proyecto:

```bash
mvn compile exec:java
```

Esto compila las clases bajo `limnigrafos/**/*.java` y ejecuta
`limnigrafos.Main`, que abre la ventana principal del simulador.

También se puede compilar y empaquetar por separado:

```bash
mvn compile
mvn exec:java -Dexec.mainClass=limnigrafos.Main
```

## Cómo funciona

### 1. Configuración del limnígrafo

Al iniciar, `Main` lee la configuración del limnígrafo `0xA10F2C` desde
`limnigrafos/configuraciones/0xA10F2C.properties` mediante
`RepositorioConfiguracionesLimnigrafos`. Cada limnígrafo se define con:

```properties
codigo=0xA10F2C
ubicacion=Rio Olivia
tiempoRecoleccionSegundos=1
tiempoEnvioSegundos=60
bateria=12.4
```

- `tiempoRecoleccionSegundos`: cada cuántos segundos se genera una medición.
- `tiempoEnvioSegundos`: cada cuántos segundos se "envían" las mediciones acumuladas.
- `bateria`: voltaje inicial simulado.

Se pueden agregar más limnígrafos creando otros archivos `<codigo>.properties`
en esa carpeta (ver [`limnigrafos/configuraciones/README.md`](limnigrafos/configuraciones/README.md)).

### 2. Motor de simulación (`limnigrafos.simulacion`)

El `Simulador` corre en un hilo propio (`ScheduledExecutorService`) con dos
tareas periódicas:

1. **Generar una medición** cada `tiempoRecoleccionSegundos`:
   - `GeneradorSimulacion` calcula el nivel de agua "real" y la temperatura
     "real" para el instante actual, en modo **automático** (funciones
     seno/coseno moduladas por `Estacion` y `Clima`) o **manual** (valor
     fijado por el usuario desde la interfaz).
   - `ModeloFisico` traduce nivel + temperatura a presión hidrostática
     usando la ecuación $p = \rho \cdot g \cdot h$, calculando la densidad
     del agua en función de la temperatura (incluye el caso de congelamiento,
     que simula una falla del sensor).
   - `Sensor` agrega ruido gaussiano a la presión y la temperatura para
     emular la imprecisión de un sensor físico real.
   - Con la presión y temperatura "medidas" (con ruido), se vuelve a
     despejar el nivel de agua, tal como lo haría el firmware real, dando
     como resultado el nivel "medido" que ve el usuario final.
   - Cada medición resta batería (`Medicion`/`bateria`); si la batería llega
     a 0, se corta la generación de datos.
2. **Enviar el lote de mediciones pendientes** cada `tiempoEnvioSegundos`,
   simulado por `ClienteApiScarh` (solo imprime en consola cuántas
   mediciones se "enviaron").

`Estacion` (Invierno/Otoño/Primavera/Verano) y `Clima` (Soleado/Nublado/
Lluvioso/Tormenta) son enums que parametrizan la temperatura base, el nivel
base del río y su variabilidad, permitiendo simular distintos escenarios
ambientales desde la interfaz.

### 3. Persistencia

- `RepositorioMedicionesCsv` guarda cada medición en
  `limnigrafos/db/mediciones/<codigo>-mediciones.csv` (columnas:
  `fechaHora,temperatura,presion,nivelAgua,bateria`). Esta carpeta no se
  versiona.
- `RepositorioConfiguracionesLimnigrafos` lee/escribe los `.properties` de
  configuración descritos arriba.

### 4. Interfaz gráfica (`limnigrafos.interfaz`)

`VentanaPrincipal` (con tema oscuro vía FlatLaf) muestra:

- **Panel de controles (izquierda):**
  - Selector de **Estación** y **Climatología**, que afectan al generador
    automático.
  - Sliders de **Nivel** y **Temperatura**, cada uno con un interruptor para
    alternar entre modo automático (sigue la función matemática) y modo
    manual (el usuario fuerza el valor).
  - Slider de **Batería**, mostrando y permitiendo ajustar el voltaje.
- **Panel de gráficos (derecha)**, con JFreeChart: series temporales de
  **Nivel del agua**, **Presión hidrostática** y **Temperatura**, cada una
  comparando el valor "Real" contra el "Medido" (con ruido), para visualizar
  la exactitud del instrumento simulado. Si la batería llega a 0 se marca un
  corte en los gráficos.

### 5. Firmware de referencia

`limnigrafos/limnigrafo-firmware.ino` es el firmware real (Arduino) del
hardware que este simulador emula: lectura del sensor por puerto serie,
cálculo de peso específico según temperatura, almacenamiento en EEPROM y
envío de datos por GSM/HTTP. Se incluye como referencia del dispositivo
físico, no forma parte de la compilación Java ni se ejecuta desde este
proyecto.

## Estructura del proyecto

```
limnigrafos/
├── Main.java                          punto de entrada
├── configuraciones/                   configuración de limnígrafos (.properties, versionado)
├── interfaz/                          componentes Swing (ventana, gráficos, sliders, tema)
├── modelos/                           entidades de dominio (Limnigrafo, Medicion, MedicionReal)
├── servicios/                         persistencia (CSV, properties) y envío simulado a API
├── simulacion/                        motor de simulación (Simulador, ModeloFisico, Sensor, ...)
│   └── entorno/                       enums Estacion y Clima
└── limnigrafo-firmware.ino            firmware real de referencia (no se compila con Maven)
```
