package com.example.entidades.eventos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventoAlerta extends Evento {
    
    // Atributos específicos
    // Nota: Cuando leas el CSV, enviarás el contenido de 'estadoNuevo' al parámetro 'motivo'
    private String motivo; 
    private String severidad;

    // Constructor completo
    public EventoAlerta(LocalDateTime timestamp, String deviceId, String tipoDispositivo, String ubicacion, String nombre, String motivo, String severidad) {
        super(timestamp, deviceId, tipoDispositivo, ubicacion, nombre);
        this.motivo = motivo;
        this.severidad = severidad;
    }

    @Override
    public String descripcion() {
        return "ALERTA " + this.motivo + " (" + this.severidad + ")";
    }

    @Override
    public boolean esCritico() {
        // equalsIgnoreCase compara ignorando si está en mayúsculas o minúsculas
        // Es más seguro poner la constante "ALTA" primero para evitar errores si 'severidad' llega a ser nulo
        return "ALTA".equalsIgnoreCase(this.severidad);
    }
}