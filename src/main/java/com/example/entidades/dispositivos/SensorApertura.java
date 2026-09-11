package com.example.entidades.dispositivos;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Comparator;

import com.example.entidades.eventos.Evento;
import com.example.entidades.eventos.EventoCambioEstado;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SensorApertura extends Dispositivo {

    public SensorApertura(String id, String nombre, String ubicacion) {
        super(id, nombre, ubicacion);
    }

    // --- Métodos propios del Sensor de Apertura ---

    public boolean estaAbierto() {
        // Buscamos el evento de cambio de estado más reciente
        return getEventos().stream()
                .filter(e -> e instanceof EventoCambioEstado)
                .map(e -> (EventoCambioEstado) e)
                .max(Comparator.comparing(Evento::getTimestamp)) // Buscamos el máximo (el más nuevo)[cite: 1]
                .map(e -> "ABIERTO".equalsIgnoreCase(e.getEstadoNuevo()))
                .orElse(false); // Por seguridad, asumimos que si no hay eventos está cerrado
    }

    public long vecesAbierto() {
        // Filtramos solo los eventos donde el estado nuevo sea "ABIERTO" y los contamos
        return getEventos().stream()
                .filter(e -> e instanceof EventoCambioEstado)
                .map(e -> (EventoCambioEstado) e)
                .filter(e -> "ABIERTO".equalsIgnoreCase(e.getEstadoNuevo()))
                .count(); // Retorna la cantidad de elementos del flujo que cumplieron la condición[cite: 1]
    }

    // --- Implementación de los métodos abstractos obligatorios ---

    @Override
    public double consumoEstimadoWh() {
        // Un sensor de apertura a pila o magnético no tiene un consumo de red relevante
        return 0.0;
    }

    @Override
    public String descripcionEstado() {
        // Evaluamos el método propio para decidir qué texto mostrar
        return estaAbierto() ? "ABIERTO" : "CERRADO";
    }
}
