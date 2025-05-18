package pe.edu.utp.isi.dwi.proyecto_123_dwi.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "asignacion")
public class Asignacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion") // Nombre exacto de la columna en la BD
    private int idAsignacion;

    @ManyToOne
    @JoinColumn(name = "id_solicitud", nullable = false) // FK hacia Solicitud
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "id_colaborador", nullable = false) // FK hacia Colaborador
    private Colaborador colaborador;

    @Column(name = "es_coordinador", nullable = false) // Aseguramos que no sea nulo
    private boolean esCoordinador;

    @Column(name = "fecha_asignacion", nullable = false) // Fecha de la asignación
    private LocalDate fechaAsignacion;

    // Getters y setters
    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public boolean isEsCoordinador() {
        return esCoordinador;
    }

    public void setEsCoordinador(boolean esCoordinador) {
        this.esCoordinador = esCoordinador;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    // Método toString
    @Override
    public String toString() {
        return "Asignacion{" +
                "idAsignacion=" + idAsignacion +
                ", solicitud=" + (solicitud != null ? solicitud.getIdSolicitud() : "null") +
                ", colaborador=" + (colaborador != null ? colaborador.getIdColaborador() : "null") +
                ", esCoordinador=" + esCoordinador +
                ", fechaAsignacion=" + fechaAsignacion +
                '}';
    }
}
