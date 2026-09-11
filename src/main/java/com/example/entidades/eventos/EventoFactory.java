package com.example.entidades.eventos;

import java.time.LocalDateTime;

public class EventoFactory {

    // Método estático para poder llamarlo sin instanciar la fábrica
    public static Evento crearDesdeCsv(String[] columnas) {
        // Índices de columnas del CSV:
        // 0: timestamp, 1: deviceId, 2: tipoDispositivo, 3: ubicacion, 4: nombre
        // 5: tipoEvento, 6: valor, 7: unidad, 8: estadoPrevio, 9: estadoNuevo, 10: severidad
        
        LocalDateTime timestamp = LocalDateTime.parse(columnas[0]);
        String deviceId = columnas[1];
        String tipoDispositivo = columnas[2];
        String ubicacion = columnas[3];
        String nombre = columnas[4];
        String tipoEvento = columnas[5].toUpperCase();

        // Usamos el switch moderno con -> para instanciar la clase correcta[cite: 4]
        return switch (tipoEvento) {
            case "MEDICION" -> {
                double valor = Double.parseDouble(columnas[6]);
                String unidad = columnas[7];
                yield new EventoMedicion(timestamp, deviceId, tipoDispositivo, ubicacion, nombre, valor, unidad);
            }
            case "CAMBIO_ESTADO" -> {
                String estadoPrevio = columnas[8];
                String estadoNuevo = columnas[9];
                yield new EventoCambioEstado(timestamp, deviceId, tipoDispositivo, ubicacion, nombre, estadoPrevio, estadoNuevo);
            }
            case "ALERTA" -> {
                // Según tus reglas, el motivo viene en la columna estadoNuevo (9) y severidad en la 10
                String motivo = columnas[9]; 
                String severidad = columnas.length > 10 ? columnas[10] : "BAJA"; // Prevención por si la fila está incompleta
                yield new EventoAlerta(timestamp, deviceId, tipoDispositivo, ubicacion, nombre, motivo, severidad);
            }
            case "COMANDO" -> {
                // La acción viene en la columna estadoNuevo (9)
                String accion = columnas[9];
                yield new EventoComando(timestamp, deviceId, tipoDispositivo, ubicacion, nombre, accion);
            }
            default -> throw new IllegalArgumentException("Tipo de evento no soportado: " + tipoEvento); // Manejo de errores recomendado
        };
    }
}
