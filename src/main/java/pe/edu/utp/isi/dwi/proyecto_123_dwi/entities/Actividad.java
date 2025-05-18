package pe.edu.utp.isi.dwi.proyecto_123_dwi.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "actividad") // Nombre exacto de la tabla en la BD
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad") // Nombre exacto de la columna en la BD
    private int idActividad;

    @ManyToOne
    @JoinColumn(name = "id_asignacion", nullable = false) // FK hacia Asignacion
    private Asignacion asignacion;

    @Column(name = "descripcion", nullable = false) // Descripción de la actividad
    private String descripcion;

    @Column(name = "fecha_registro", nullable = false) // Fecha y hora de registro
    private LocalDateTime fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "id_colaborador", nullable = false) // FK hacia Colaborador
    private Colaborador colaborador;

    @Column(name = "tiempo_requerido", nullable = false) // Tiempo requerido en minutos
    private long tiempoRequerido; // Almacenado como minutos totales

    // Métodos utilitarios para tiempo
    @Transient
    public String getTiempoFormatoHoras() {
        long horas = tiempoRequerido / 60;
        long minutos = tiempoRequerido % 60;
        return horas + " horas " + minutos + " minutos";
    }

    // Getters y Setters
    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public Asignacion getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignacion asignacion) {
        this.asignacion = asignacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public long getTiempoRequerido() {
        return tiempoRequerido;
    }

    public void setTiempoRequerido(long tiempoRequerido) {
        this.tiempoRequerido = tiempoRequerido;
    }

    @Override
    public String toString() {
        return "Actividad{" +
                "idActividad=" + idActividad +
                ", asignacion=" + (asignacion != null ? asignacion.getIdAsignacion() : null) +
                ", descripcion='" + descripcion + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", colaborador=" + (colaborador != null ? colaborador.getNombreColaborador() : null) +
                ", tiempoRequerido=" + tiempoRequerido +
                '}';
    }
}
