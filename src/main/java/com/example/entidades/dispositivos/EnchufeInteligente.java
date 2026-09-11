package com.example.entidades.dispositivos;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Comparator;

import com.example.entidades.eventos.Evento;
import com.example.entidades.eventos.EventoMedicion;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EnchufeInteligente extends Dispositivo {

    // Constructor que invoca a la superclase
    public EnchufeInteligente(String id, String nombre, String ubicacion) {
        super(id, nombre, ubicacion);
    }

    // --- Métodos propios del Enchufe Inteligente ---

    public double potenciaActual() {
        // Buscamos la última medición registrada para saber el consumo instantáneo
        return getEventos().stream()
                .filter(e -> e instanceof EventoMedicion)
                .map(e -> (EventoMedicion) e)
                .max(Comparator.comparing(Evento::getTimestamp)) // Obtenemos el evento más reciente
                .map(EventoMedicion::getValor)                   // Extraemos su valor
                .orElse(0.0);                                    // Si no hay mediciones, devuelve 0
    }

    // --- Implementación de los métodos abstractos obligatorios ---

    @Override
    public double consumoEstimadoWh() {
        // Sumamos todas las mediciones cuya unidad sea "Wh"
        return getEventos().stream()
                .filter(e -> e instanceof EventoMedicion)
                .map(e -> (EventoMedicion) e)
                .filter(em -> "Wh".equalsIgnoreCase(em.getUnidad())) 
                .mapToDouble(EventoMedicion::getValor)               // Convertimos a un flujo numérico
                .sum();                                              // Suma todos los valores encontrados
    }

    @Override
    public String descripcionEstado() {
        // Para este dispositivo, el estado más relevante a mostrar es su consumo total acumulado
        return "Consumo acumulado: " + consumoEstimadoWh() + " Wh";
    }
}