package com.example.entidades.dispositivos;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

import com.example.entidades.eventos.Evento;

@Data
public abstract class Dispositivo {
    
    // Atributos base
    private String id;
    private String nombre;
    private String ubicacion;
    
    // Lista para almacenar el historial de eventos de este dispositivo en particular
    private List<Evento> eventos;

    // Constructor
    public Dispositivo(String id, String nombre, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        // Es vital inicializar la colección vacía para evitar errores (NullPointerException)
        // al querer agregar eventos más adelante.
        this.eventos = new ArrayList<>(); 
    }

    // Método concreto para añadir eventos a la lista del dispositivo
    public void agregarEvento(Evento evento) {
        this.eventos.add(evento);
    }

    // Nota: El método getEventos() que pide el UML ya se genera 
    // automáticamente gracias a la anotación @Data de Lombok.

    // Métodos abstractos que cada subclase deberá redefinir obligatoriamente
    public abstract String descripcionEstado();
    public abstract double consumoEstimadoWh();
}