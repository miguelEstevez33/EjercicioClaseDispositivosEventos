package com.example.entidades.eventos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventoMedicion extends Evento {
    
    private double valor;
    private String unidad;

    // El constructor ahora recibe los nuevos campos comunes del CSV
    public EventoMedicion(LocalDateTime timestamp, String deviceId, String tipoDispositivo, String ubicacion, String nombre, double valor, String unidad) {
        super(timestamp, deviceId, tipoDispositivo, ubicacion, nombre);
        this.valor = valor;
        this.unidad = unidad;
    }

    @Override
    public String descripcion() {
        return this.valor + " " + this.unidad;
    }

    @Override
    public boolean esCritico() {
        return false;
    }
}