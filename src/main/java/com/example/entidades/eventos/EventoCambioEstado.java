package com.example.entidades.eventos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventoCambioEstado extends Evento {
    
    // Atributos específicos de esta subclase
    private String estadoPrevio;
    private String estadoNuevo;

    // Constructor que recibe los atributos de la superclase y los específicos
    public EventoCambioEstado(LocalDateTime timestamp, String deviceId, String tipoDispositivo, String ubicacion, String nombre, String estadoPrevio, String estadoNuevo) {
        // Llamada al constructor de la superclase Evento
        super(timestamp, deviceId, tipoDispositivo, ubicacion, nombre);
        
        this.estadoPrevio = estadoPrevio;
        this.estadoNuevo = estadoNuevo;
    }

    // Implementación de los métodos abstractos
    @Override
    public String descripcion() {
        // Reconstruye el estado uniendo el previo y el nuevo con una flecha
        return this.estadoPrevio + " → " + this.estadoNuevo;
    }

    @Override
    public boolean esCritico() {
        // Como indicaste, este evento no es crítico por defecto
        return false;
    }
}