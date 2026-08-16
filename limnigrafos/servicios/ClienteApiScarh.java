package limnigrafos.servicios;

import java.util.List;

import limnigrafos.modelos.Limnigrafo;
import limnigrafos.modelos.Medicion;

public class ClienteApiScarh {
    public void enviar(Limnigrafo limnigrafo, List<Medicion> mediciones) {
        System.out.printf(
                "Envio simulado: %d mediciones del limnigrafo %s (%s)%n",
                mediciones.size(),
                limnigrafo.getCodigo(),
                limnigrafo.getUbicacion());
    }
}
