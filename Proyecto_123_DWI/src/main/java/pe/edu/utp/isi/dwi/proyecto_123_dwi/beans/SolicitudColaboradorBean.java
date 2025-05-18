package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Solicitud;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.SolicitudFacade;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class SolicitudColaboradorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SolicitudFacade solicitudFacade;

    private List<Solicitud> solicitudesPendientes;
    private List<Solicitud> solicitudesEnProceso;
    private List<Solicitud> solicitudesFinalizadas;

    private Solicitud solicitudSeleccionada; // Solicitud actualmente seleccionada
    
    @PostConstruct
    public void init() {
        cargarSolicitudes();
    }
    
    public void cargarSolicitudes() {
        solicitudesPendientes = solicitudFacade.obtenerPorEstado("Pendiente");
        solicitudesEnProceso = solicitudFacade.obtenerPorEstado("En Proceso");
        solicitudesFinalizadas = solicitudFacade.obtenerPorEstado("Finalizada");

        if (solicitudesPendientes == null) solicitudesPendientes = new ArrayList<>();
        if (solicitudesEnProceso == null) solicitudesEnProceso = new ArrayList<>();
        if (solicitudesFinalizadas == null) solicitudesFinalizadas = new ArrayList<>();
    }
    
    public void finalizarSolicitud(Solicitud solicitud) {
        solicitud.setEstado("Finalizada");
        solicitud.setFechaFinalizacion(LocalDateTime.now()); // Establece la fecha de finalización
        solicitudFacade.actualizarSolicitud(solicitud);
        cargarSolicitudes(); // Actualiza las listas después de finalizar
    }

    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) {
            return "-";
        }
        return fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public List<Solicitud> getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public List<Solicitud> getSolicitudesEnProceso() {
        return solicitudesEnProceso;
    }

    public List<Solicitud> getSolicitudesFinalizadas() {
        return solicitudesFinalizadas;
    }

    public Solicitud getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    public void setSolicitudSeleccionada(Solicitud solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
    }
}
