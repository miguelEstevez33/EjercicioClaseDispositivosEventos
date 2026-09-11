package com.example.entidades.dispositivos;

public class DispositivoFactory {

    public static Dispositivo crearDesdeCsv(String[] columnas) {
        // Extraemos los atributos básicos que todo dispositivo necesita
        String deviceId = columnas[1];
        String tipoDispositivo = columnas[2].toUpperCase();
        String ubicacion = columnas[3];
        String nombre = columnas[4];

        // Retornamos la instancia correspondiente según la columna discriminadora[cite: 1]
        return switch (tipoDispositivo) {
            case "TERMOSTATO" -> new Termostato(deviceId, nombre, ubicacion);
            case "LUZ" -> new LuzInteligente(deviceId, nombre, ubicacion);
            case "CERRADURA" -> new Cerradura(deviceId, nombre, ubicacion);
            case "ENCHUFE" -> new EnchufeInteligente(deviceId, nombre, ubicacion);
            case "SENSOR_MOVIMIENTO" -> new SensorMovimiento(deviceId, nombre, ubicacion);
            case "SENSOR_APERTURA" -> new SensorApertura(deviceId, nombre, ubicacion);
            default -> throw new IllegalArgumentException("Dispositivo desconocido: " + tipoDispositivo); // Lanza excepción si el tipo no existe[cite: 1]
        };
    }
}