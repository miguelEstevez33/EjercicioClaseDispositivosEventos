package com.example.entidades.eventos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public abstract class Evento {
    
    // Atributos comunes a TODAS las filas del CSV
    private LocalDateTime timestamp;
    private String deviceId;
    private String tipoDispositivo;
    private String ubicacion;
    private String nombre;

    // Constructor con los atributos comunes
    public Evento(LocalDateTime timestamp, String deviceId, String tipoDispositivo, String ubicacion, String nombre) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.tipoDispositivo = tipoDispositivo;
        this.ubicacion = ubicacion;
        this.nombre = nombre;
    }

    public abstract String descripcion();
    public abstract boolean esCritico();
}