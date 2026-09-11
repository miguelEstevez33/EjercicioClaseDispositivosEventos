package com.example.entidades.dispositivos;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.entidades.eventos.Evento;
import com.example.entidades.eventos.EventoCambioEstado;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LuzInteligente extends Dispositivo {

    // Definimos una potencia fija en Watts para el cálculo de consumo
    private static final double POTENCIA_FIJA_W = 15.0;

    public LuzInteligente(String id, String nombre, String ubicacion) {
        super(id, nombre, ubicacion); // Llamada al constructor de la superclase Dispositivo[cite: 1]
    }

    public boolean estaEncendida() {
        // Filtramos buscando el último cambio de estado
        return getEventos().stream()
                .filter(e -> e instanceof EventoCambioEstado)
                .map(e -> (EventoCambioEstado) e)
                .max(Comparator.comparing(Evento::getTimestamp)) // Obtenemos el más reciente[cite: 7]
                .map(e -> "ENCENDIDA".equalsIgnoreCase(e.getEstadoNuevo()))
                .orElse(false); // Si no hay eventos, asumimos que está apagada
    }

    public double horasEncendida() {
        // 1. Obtenemos solo los cambios de estado, ordenados por fecha de más antiguo a más nuevo
        List<EventoCambioEstado> cambios = getEventos().stream()
                .filter(e -> e instanceof EventoCambioEstado)
                .map(e -> (EventoCambioEstado) e)
                .sorted(Comparator.comparing(Evento::getTimestamp))
                .collect(Collectors.toList());

        double horasTotales = 0.0;
        LocalDateTime tiempoEncendido = null;

        // 2. Recorremos la línea de tiempo para medir los tramos
        for (EventoCambioEstado evento : cambios) {
            if ("ENCENDIDA".equalsIgnoreCase(evento.getEstadoNuevo())) {
                if (tiempoEncendido == null) {
                    tiempoEncendido = evento.getTimestamp(); // Marcamos el inicio del tramo
                }
            } else if ("APAGADA".equalsIgnoreCase(evento.getEstadoNuevo())) {
                if (tiempoEncendido != null) {
                    // Calculamos los minutos transcurridos y los pasamos a horas
                    long minutos = ChronoUnit.MINUTES.between(tiempoEncendido, evento.getTimestamp());
                    horasTotales += minutos / 60.0;
                    tiempoEncendido = null; // Reseteamos para el próximo tramo
                }
            }
        }

        // 3. Si terminó el ciclo y la luz sigue encendida, sumamos el tiempo hasta AHORA
        if (tiempoEncendido != null) {
            long minutos = ChronoUnit.MINUTES.between(tiempoEncendido, LocalDateTime.now());
            horasTotales += minutos / 60.0;
        }

        return horasTotales;
    }

    // --- Implementación de los métodos abstractos obligatorios ---

    @Override
    public double consumoEstimadoWh() {
        // Multiplicamos la cantidad de horas por la potencia de la lámpara
        return horasEncendida() * POTENCIA_FIJA_W;
    }

    @Override
    public String descripcionEstado() {
        // Usamos el operador ternario para definir el texto de forma concisa
        return estaEncendida() ? "ENCENDIDA" : "APAGADA";
    }
}