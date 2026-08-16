# Configuraciones de limnigrafos

Cada limnigrafo se guarda en un archivo `<codigo>.properties`. Estos archivos forman
parte del proyecto y deben versionarse.

Ejemplo:

```properties
codigo=LIM-001
ubicacion=Rio Grande
tiempoRecoleccionSegundos=300
tiempoEnvioSegundos=3600
bateria=12.4
```

Las mediciones generadas se guardan por separado en `limnigrafos/db/mediciones/`
y no se versionan.
