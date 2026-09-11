package com.example.entidades.dispositivos;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Comparator;

import com.example.entidades.eventos.Evento;
import com.example.entidades.eventos.EventoCambioEstado;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Cerradura extends Dispositivo {

    public Cerradura(String id, String nombre, String ubicacion) {
        super(id, nombre, ubicacion);
    }

    // --- Métodos propios de la Cerradura ---

    public boolean estaTrabada() {
        // Buscamos el último evento de cambio de estado para conocer su situación actual
        return getEventos().stream()
                .filter(e -> e instanceof EventoCambioEstado)
                .map(e -> (EventoCambioEstado) e)
                .max(Comparator.comparing(Evento::getTimestamp)) 
                .map(e -> "TRABADA".equalsIgnoreCase(e.getEstadoNuevo()))
                .orElse(true); // Asumimos que por defecto, si no hay eventos, una cerradura está trabada por seguridad
    }

    public long aperturas() {
        // Filtramos solo los eventos donde el estado nuevo sea "DESTRABADA" y los contamos
        return getEventos().stream()
                .filter(e -> e instanceof EventoCambioEstado)
                .map(e -> (EventoCambioEstado) e)
                .filter(e -> "DESTRABADA".equalsIgnoreCase(e.getEstadoNuevo()))
                .count(); // Retorna la cantidad de elementos del flujo que cumplieron la condición
    }

    // --- Implementación de los métodos abstractos obligatorios ---

    @Override
    public double consumoEstimadoWh() {
        // No consume energía relevante, devolvemos 0 como regla de negocio
        return 0.0;
    }

    @Override
    public String descripcionEstado() {
        return estaTrabada() ? "TRABADA" : "DESTRABADA";
    }
}
