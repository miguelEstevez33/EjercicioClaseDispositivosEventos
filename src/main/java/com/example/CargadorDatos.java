package com.example;

import com.example.entidades.dispositivos.Dispositivo;
import com.example.entidades.dispositivos.DispositivoFactory;
import com.example.entidades.eventos.Evento;
import com.example.entidades.eventos.EventoFactory;

import java.io.BufferedReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CargadorDatos {

    public static Map<String, Dispositivo> cargarDesdeCsv(URL resourceUrl) {
        // Inicializamos el mapa que nos pide el enunciado
        Map<String, Dispositivo> dispositivos = new HashMap<>();

        try {
            // Convertimos la URL en un Path seguro para leer el archivo
            Path archivo = Path.of(resourceUrl.toURI());
            
            // Usamos try-with-resources para no dejar el archivo abierto en memoria[cite: 3]
            try (BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
                
                // Leemos y descartamos la primera línea porque es la cabecera[cite: 1]
                String linea = br.readLine();

                // Recorremos el resto de las líneas
                while ((linea = br.readLine()) != null) {
                    
                    // Si la línea está vacía, la salteamos
                    if (linea.trim().isEmpty()) continue;
                    
                    String[] columnas = linea.split(";");
                    String deviceId = columnas[1]; // El deviceId está en la columna 1 (índice 1)

                    // MAGIA AQUÍ: computeIfAbsent hace todo el trabajo pesado.
                    // Busca si el deviceId ya está en el mapa. Si NO está, ejecuta la función 
                    // (llamando al DispositivoFactory) y lo guarda. Si YA ESTÁ, simplemente te lo devuelve.
                    Dispositivo dispositivo = dispositivos.computeIfAbsent(deviceId, id -> 
                        DispositivoFactory.crearDesdeCsv(columnas)
                    );

                    // Ahora usamos el EventoFactory para crear el evento de esta línea
                    Evento evento = EventoFactory.crearDesdeCsv(columnas);

                    // Finalmente, guardamos el evento en la lista interna del dispositivo
                    dispositivo.agregarEvento(evento);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando el archivo CSV: " + e.getMessage());
            e.printStackTrace();
        }

        return dispositivos;
    }
}