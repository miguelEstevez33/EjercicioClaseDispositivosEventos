package com.example.entidades.eventos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventoComando extends Evento {
    
    // Atributo específico
    // Al leer el CSV, aquí guardaremos lo que venga en la columna "estadoNuevo"
    private String accion;

    // Constructor completo
    public EventoComando(LocalDateTime timestamp, String deviceId, String tipoDispositivo, String ubicacion, String nombre, String accion) {
        super(timestamp, deviceId, tipoDispositivo, ubicacion, nombre);
        this.accion = accion;
    }

    @Override
    public String descripcion() {
        return "comando " + this.accion;
    }

    @Override
    public boolean esCritico() {
        return false; // Los comandos no son críticos
    }
}
