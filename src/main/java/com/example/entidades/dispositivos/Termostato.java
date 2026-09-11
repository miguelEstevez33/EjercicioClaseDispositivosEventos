package com.example.entidades.dispositivos;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.Comparator;

import com.example.entidades.eventos.Evento;
import com.example.entidades.eventos.EventoMedicion;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Termostato extends Dispositivo {

    // Constructor que invoca al de la superclase Dispositivo
    public Termostato(String id, String nombre, String ubicacion) {
        super(id, nombre, ubicacion);
    }

    // --- Métodos propios de Termostato ---

    public double temperaturaActual() {
        return getEventos().stream()
                .filter(e -> e instanceof EventoMedicion) // Nos quedamos solo con las mediciones
                .map(e -> (EventoMedicion) e)             // Hacemos el cast (downcasting)
                .filter(em -> "C".equalsIgnoreCase(em.getUnidad())) // Filtramos por unidad "C"
                .max(Comparator.comparing(Evento::getTimestamp))    // Buscamos el evento más reciente
                .map(EventoMedicion::getValor)            // Extraemos su valor numérico
                .orElse(0.0);                             // Si no hay mediciones, devuelve 0.0
    }

    public double temperaturaPromedio() {
        return getEventos().stream()
                .filter(e -> e instanceof EventoMedicion)
                .map(e -> (EventoMedicion) e)
                .filter(em -> "C".equalsIgnoreCase(em.getUnidad()))
                .mapToDouble(EventoMedicion::getValor)    // Convertimos el flujo a un flujo numérico primitivo
                .average()                                // Calcula el promedio automáticamente
                .orElse(0.0);                             // Valor por defecto si la lista está vacía[cite: 2]
    }

    // --- Implementación de los métodos abstractos heredados ---

    @Override
    public double consumoEstimadoWh() {
        return getEventos().stream()
                .filter(e -> e instanceof EventoMedicion)
                .map(e -> (EventoMedicion) e)
                .filter(em -> "Wh".equalsIgnoreCase(em.getUnidad())) // Ahora filtramos por energía
                .mapToDouble(EventoMedicion::getValor)
                .sum();                                   // Suma todos los valores encontrados[cite: 2]
    }

    @Override
    public String descripcionEstado() {
        // Redefinimos el comportamiento para que al pedir su estado, muestre la temperatura
        return "Temperatura actual: " + temperaturaActual() + " C";
    }
}