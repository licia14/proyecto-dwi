package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Cliente;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Solicitud;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.SolicitudFacade;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Named
@SessionScoped
public class SolicitudClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SolicitudFacade solicitudFacade;

    @Inject
    private ClienteSesionBean clienteSesionBean;

    private Solicitud solicitud = new Solicitud();
    private List<Solicitud> solicitudesCliente;

    private Part imagenFile; // Archivo cargado por el cliente

    private final String uploadDir = "C:/uploads/solicitudes/"; // Directorio de carga

    /**
     * Registra una nueva solicitud para el cliente autenticado.
     */
    public void registrarSolicitud() {
        try {
            Cliente cliente = clienteSesionBean.getCliente();
            if (cliente == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No estás autenticado."));
                return;
            }
            
            // Guardar la imagen si se cargó
            if (imagenFile != null) {
                String imagenPath = guardarImagen(imagenFile);
                solicitud.setImagen(imagenPath);
            }
            
            // Asignar número de solicitud
            int nroSolicitud = solicitudFacade.calcularNroSolicitud(cliente.getIdCliente());
            solicitud.setNroSolicitud(nroSolicitud);
            
            // Configurar los datos de la solicitud
            solicitud.setCliente(cliente);
            solicitud.setFechaRegistro(LocalDateTime.now());
            solicitud.setEstado("Pendiente");

            // Guardar la solicitud
            solicitudFacade.guardarSolicitud(solicitud);

            // Mensaje de éxito
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Solicitud registrada correctamente."));

            // Limpiar el formulario
            solicitud = new Solicitud();
            imagenFile = null;
            cargarSolicitudes(); // Actualizar la lista
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar la solicitud: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Guarda la imagen cargada en el directorio especificado, evitando duplicados.
     */
    private String guardarImagen(Part file) throws IOException {
        String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName; // Evitar duplicados
        String filePath = uploadDir + uniqueFileName;

        Files.createDirectories(Paths.get(uploadDir)); // Crear directorio si no existe

        try (InputStream input = file.getInputStream()) {
            Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }

        return filePath;
    }

    /**
     * Carga las solicitudes asociadas al cliente autenticado.
     */
    public void cargarSolicitudes() {
        Cliente cliente = clienteSesionBean.getCliente();
        if (cliente != null) {
            solicitudesCliente = solicitudFacade.listarSolicitudesPorCliente(cliente.getIdCliente());
        }
    }
    
    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) {
            return "-";
        }
        return fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    // Getters y Setters
    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    public List<Solicitud> getSolicitudesCliente() {
        if (solicitudesCliente == null) {
            cargarSolicitudes();
        }
        return solicitudesCliente;
    }

    public void setSolicitudesCliente(List<Solicitud> solicitudesCliente) {
        this.solicitudesCliente = solicitudesCliente;
    }

    public Part getImagenFile() {
        return imagenFile;
    }

    public void setImagenFile(Part imagenFile) {
        this.imagenFile = imagenFile;
    }
}
