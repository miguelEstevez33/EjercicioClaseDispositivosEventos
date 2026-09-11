package com.example;

import com.example.entidades.dispositivos.Dispositivo;
import com.example.entidades.dispositivos.Termostato;
import com.example.entidades.eventos.Evento;

import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        
        // 1. Obtenemos la URL exacta de dónde Maven empaquetó nuestro CSV
        // Se usa la barra inicial '/' para indicarle que busque desde la raíz de resources
        URL resourceUrl = App.class.getResource("/data/eventos.csv");
        
        if (resourceUrl == null) {
            System.err.println("¡No se pudo encontrar el archivo CSV! Revisá la carpeta resources.");
            return;
        }

        // 2. Procesamos el CSV y obtenemos el mapa
        Map<String, Dispositivo> dispositivos = CargadorDatos.cargarDesdeCsv(resourceUrl);

        // 3. Inicializamos el Scanner para el menú interactivo
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        // 4. Bucle principal del programa
        do {
            System.out.println("\n===========================================================");
            System.out.println("        MENÚ DE ESTADÍSTICAS - RED IOT INTELIGENTE         ");
            System.out.println("===========================================================");
            System.out.println("1 — Eventos por tipo de dispositivo");
            System.out.println("2 — Consumo estimado por habitación, de mayor a menor");
            System.out.println("3 — Temperatura promedio por termostato");
            System.out.println("4 — Dispositivo con más alertas críticas");
            System.out.println("5 — Hora del día (0–23) con más eventos");
            System.out.println("6 — Los 3 dispositivos más activos");
            System.out.println("7 — Línea de tiempo de las alertas críticas (cronológico)");
            System.out.println("0 — Salir del sistema");
            System.out.print("Ingrese su opción: ");
            
            try {
                // Usamos parseInt(nextLine()) para evitar el clásico "bug" de salto de línea del Scanner
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1; // Forzamos a que entre al default si el usuario tipea una letra
            }

            // Usamos el switch moderno introducido en Java 14
            switch (opcion) {
                case 1 -> {
                    System.out.println("\n--- 1. Eventos por tipo de dispositivo ---");
                    // 1. Tomamos los dispositivos de nuestro mapa y abrimos el flujo
                    Map<String, Long> eventosPorTipo = dispositivos.values().stream()
                        // 2. Extraemos y "aplanamos" las listas de eventos de todos los dispositivos
                        .flatMap(d -> d.getEventos().stream()) 
                        // 3. Los agrupamos por la columna tipoDispositivo y los contamos
                        .collect(Collectors.groupingBy(
                            Evento::getTipoDispositivo, 
                            Collectors.counting() 
                        ));

                    // 4. Imprimimos el resultado iterando el mapa con forEach (azúcar sintáctico moderno)
                    eventosPorTipo.forEach((tipo, cantidad) -> 
                        System.out.println("Tipo: " + tipo + " -> " + cantidad + " eventos")
                    );
                }
                case 2 -> {
                    System.out.println("\n--- 2. Consumo estimado por habitación (Mayor a menor) ---");
                    // FASE 1: Agrupar por ubicación y sumar el consumo
                    Map<String, Double> consumoPorHabitacion = dispositivos.values().stream()
                        .collect(Collectors.groupingBy(
                            Dispositivo::getUbicacion, 
                            Collectors.summingDouble(Dispositivo::consumoEstimadoWh) // Downstream collector
                        )); 

                    // FASE 2: Ordenar el mapa de mayor a menor e imprimirlo
                    consumoPorHabitacion.entrySet().stream()
                        // Ordenamos comparando por el valor (consumo) y lo invertimos (reversed) para que sea descendente
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) 
                        .forEach(entry -> 
                            System.out.println("Habitación: " + entry.getKey() + " -> " + entry.getValue() + " Wh")
                        );
                }
                case 3 -> {
                    System.out.println("\n--- 3. Temperatura promedio por termostato ---");
                    dispositivos.values().stream()
                        // 1. Filtramos el flujo: dejamos pasar SOLO a los objetos que sean Termostatos
                        .filter(d -> d instanceof Termostato)
                        // 2. Transformamos el flujo de Dispositivo genérico a Termostato específico (Casting)
                        .map(d -> (Termostato) d)
                        // 3. Recorremos los termostatos resultantes y mostramos su promedio
                        .forEach(t -> 
                            System.out.println("Termostato: " + t.getNombre() + " -> " + t.temperaturaPromedio() + " C")
                        );
                }
                case 4 -> {
                    System.out.println("\n--- 4. Dispositivo con más alertas críticas ---");
                    dispositivos.values().stream()
                        // 1. Buscamos el dispositivo que tenga el valor máximo según su cantidad de alertas críticas
                        .max(Comparator.comparingLong(d -> 
                            d.getEventos().stream()
                                .filter(Evento::esCritico) // Filtramos solo los eventos críticos de ese dispositivo
                                .count()                  // Los contamos
                        ))
                        // 2. Si encontramos uno, lo imprimimos en consola
                        .ifPresentOrElse(
                            disp -> System.out.println("Dispositivo con más alertas: " + disp.getNombre() + 
                                " (" + disp.getClass().getSimpleName() + ")"),
                            () -> System.out.println("No se encontraron dispositivos con alertas críticas.")
                        );
                }
                case 5 -> {
                    System.out.println("\n--- 5. Hora del día (0–23) con más eventos ---");
                    dispositivos.values().stream()
                        // 1. Extraemos y "aplanamos" todos los eventos de todos los dispositivos en un solo flujo continuo
                        .flatMap(d -> d.getEventos().stream())
                        // 2. Agrupamos usando la hora exacta del evento (0 a 23) como clave, y contamos cuántos caen ahí
                        .collect(Collectors.groupingBy(
                            evento -> evento.getTimestamp().getHour(), 
                            Collectors.counting()
                        ))
                        // 3. Convertimos el mapa resultante en un flujo de pares Clave-Valor (Entry)
                        .entrySet().stream()
                        // 4. Buscamos la entrada (la hora) que tenga el valor (el conteo) más alto
                        .max(Map.Entry.comparingByValue())
                        // 5. Imprimimos el resultado de forma segura
                        .ifPresentOrElse(
                            entry -> System.out.println("Hora pico: " + entry.getKey() + " hs con " + entry.getValue() + " eventos."),
                            () -> System.out.println("No se encontraron eventos para calcular la hora pico.")
                        );
                }
                case 6 -> {
                    System.out.println("\n--- 6. Los 3 dispositivos más activos ---");
                    dispositivos.values().stream()
                        // 1. Ordenamos usando un comparador basado en la cantidad de eventos (de menor a mayor)
                        .sorted(Comparator.comparingInt(d -> d.getEventos().size()))
                        // 2. Invertimos el orden para que quede de mayor a menor (descendente)
                        .map(d -> (Dispositivo) d) // Casteo opcional de seguridad si el compilador se confunde con el reversed
                        .sorted(Comparator.comparingInt((Dispositivo d) -> d.getEventos().size()).reversed())
                        // 3. Cortamos el flujo para quedarnos solo con los primeros 3
                        .limit(3)
                        // 4. Imprimimos el podio
                        .forEach(d -> 
                            System.out.println("Dispositivo: " + d.getNombre() + " -> " + d.getEventos().size() + " eventos")
                        );
                }
                case 7 -> {
                    System.out.println("\n--- 7. Línea de tiempo de alertas críticas ---");
                    dispositivos.values().stream()
                        // 1. Unificamos todos los eventos en un solo flujo continuo
                        .flatMap(d -> d.getEventos().stream())
                        // 2. Filtramos dejando pasar únicamente los eventos críticos
                        .filter(Evento::esCritico)
                        // 3. Ordenamos de forma natural (cronológica) usando la fecha y hora
                        .sorted(Comparator.comparing(Evento::getTimestamp))
                        // 4. Imprimimos el resultado formateado como una línea de tiempo visual
                        .forEach(e -> 
                            System.out.println("[ " + e.getTimestamp() + " ] - " + 
                                            e.getTipoDispositivo() + " (" + e.getDeviceId() + ") : " + 
                                            e.getNombre())
                        );
                }
                case 0 -> System.out.println("\n¡Gracias por usar el sistema! Saliendo...");
                default -> System.out.println("\n⚠️ Opción no válida. Por favor, ingrese un número del 0 al 7.");
            }
        } while (opcion != 0);

        // Cerramos el scanner para liberar el recurso
        scanner.close();
    }
}