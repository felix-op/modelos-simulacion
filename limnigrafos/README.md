# Simulador de Limnígrafo por Presión Hidrostática

## 1. ¿Qué es este simulador?
Es un programa diseñado para generar datos sintéticos del comportamiento de un cuerpo de agua y la respuesta de un sensor sumergido. 

A partir de variables físicas, calcula los cambios en el nivel del agua, los traduce a las métricas que leería el hardware (presión hidrostática y temperatura) y añade un margen de error (ruido) para emular la inexactitud de un sensor físico real. Finalmente, permite visualizar la evolución del nivel del agua a lo largo del tiempo y genera un flujo de datos continuo listo para ser procesado o transmitido.

## 2. ¿Qué es un limnígrafo?
Un limnígrafo es un instrumento de medición diseñado para registrar de forma continua y automática la cota o nivel superficial del agua (altura) en un punto específico. Su campo de acción abarca tanto entornos naturales (ríos, lagos, canales) como infraestructuras artificiales (tanques, cisternas, embalses). 

## 3. ¿Para qué se utilizan sus datos?
El dato crudo que captura el dispositivo (el nivel del agua en centímetros) es **necesario pero no suficiente por sí solo**. La medición de la altura adquiere utilidad real cuando se la procesa matemáticamente utilizando la geometría del entorno (como el perfil transversal de un río o el área de un tanque). Esto permite calcular el **caudal** (volumen de agua por unidad de tiempo) o el **volumen almacenado**.

Estos datos derivados se emplean en diversas aplicaciones críticas:

*   **Sistemas de alerta temprana:** Monitoreo en tiempo real del crecimiento de ríos y arroyos para proyectar desbordes y emitir alertas de inundación a poblaciones vulnerables.
*   **Gestión de infraestructura y reservorios:** En la industria y obras sanitarias, los datos evitan que los tanques se rebalsen o se vacíen. La lectura del nivel permite automatizar el encendido o corte del suministro de agua mediante bombas y válvulas.
*   **Administración y auditoría de recursos hídricos:** Medición precisa para la asignación de cuotas de agua. Permite controlar y facturar el volumen exacto de agua que se desvía mediante canales o bombas hacia empresas agrícolas para sistemas de riego, o hacia plantas industriales para su uso en manufactura.

## 5. Tipo de dispositivo a utilizar
El sistema simula un **limnígrafo sumergible de presión relativa (manométrica)**. Consta de un cabezal fondeado en el lecho del cuerpo de agua que integra:
* Un sensor de presión relativa.
* Un termistor (sensor de temperatura).

Al utilizar un sensor de presión relativa, el dispositivo toma la presión atmosférica local como punto cero, ignorando sus variaciones. Por lo tanto, la medición del hardware corresponde exclusivamente a la **presión hidrostática** ejercida por la columna de agua.

El firmware deduce la altura del agua en centímetros despejando $h$ de la ecuación fundamental de la hidrostática ($p = \rho \cdot g \cdot h$). El peso específico del fluido ($\rho \cdot g$) se calcula dinámicamente en función de la lectura de temperatura entregada por el termistor, permitiendo derivar el nivel sin requerir módulos barométricos externos.

## 6. Alcance de la Simulación
Esta sección abarca exclusivamente el motor matemático y su representación visual. Se simularán los siguientes componentes:
*   **Generación de Entradas:** Representación de las variables físicas base del entorno (temperatura y nivel del agua real). Operará en dos modos: automático (mediante funciones matemáticas continuas) o control manual.
*   **Respuesta del Hardware:** Conversión de la altura del agua a presión hidrostática relativa, calculando dinámicamente el peso específico del agua en función de la temperatura de entrada.
*   **Exactitud del Instrumento:** Inyección de ruido aleatorio (distribución normal) sobre los vectores resultantes para emular el margen de error de un sensor físico real.

## 7. Interfaz y Visualización
El simulador expondrá una interfaz gráfica interactiva para operar el modelo y visualizar la ejecución en tiempo real, dividida en dos paneles principales:
*   **Controles de Sistema (Inputs):** Controles deslizantes (*sliders*) que permitirán forzar cambios manuales en la temperatura del agua y en la altura de la columna. Contará con un interruptor para alternar entre estos controles manuales y el modo de forzante automático (donde las variables siguen su curso matemático predeterminado).
*   **Monitoreo de Datos (Outputs):** Representación gráfica de los vectores discretos generados. Se graficarán series temporales mostrando tres indicadores clave: Temperatura capturada, Presión relativa calculada y Nivel de agua deducido por el sensor (mostrando visualmente la dispersión generada por el ruido de exactitud).