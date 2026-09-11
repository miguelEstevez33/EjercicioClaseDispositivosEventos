package com.example.entidades.dispositivos;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Comparator;

import com.example.entidades.eventos.Evento;
import com.example.entidades.eventos.EventoAlerta;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SensorMovimiento extends Dispositivo {

    public SensorMovimiento(String id, String nombre, String ubicacion) {
        super(id, nombre, ubicacion);
    }

    // --- Métodos propios del Sensor de Movimiento ---

    public long detecciones() {
        // Filtramos buscando alertas de movimiento y las contamos
        return getEventos().stream()
                .filter(e -> e instanceof EventoAlerta)
                .map(e -> (EventoAlerta) e)
                .filter(ea -> "MOVIMIENTO".equalsIgnoreCase(ea.getMotivo()))
                .count(); // Retorna la cantidad de elementos del flujo que cumplieron la condición[cite: 5]
    }

    public LocalDateTime ultimaDeteccion() {
        // Buscamos la alerta de movimiento con el timestamp más grande (más reciente)
        return getEventos().stream()
                .filter(e -> e instanceof EventoAlerta)
                .map(e -> (EventoAlerta) e)
                .filter(ea -> "MOVIMIENTO".equalsIgnoreCase(ea.getMotivo()))
                .max(Comparator.comparing(Evento::getTimestamp)) // Obtiene el mayor valor del flujo[cite: 5]
                .map(Evento::getTimestamp)                       // Extraemos solo la fecha/hora de ese evento
                .orElse(null);                                   // Si no hay detecciones aún, devolvemos null
    }

    // --- Implementación de los métodos abstractos obligatorios ---

    @Override
    public double consumoEstimadoWh() {
        // No consume energía relevante
        return 0.0;
    }

    @Override
    public String descripcionEstado() {
        // Redefinimos el comportamiento para adecuarlo a las características del sensor[cite: 1]
        LocalDateTime ultima = ultimaDeteccion();
        if (ultima == null) {
            return "Sin detecciones registradas";
        }
        return "Última detección: " + ultima.toString();
    }
}