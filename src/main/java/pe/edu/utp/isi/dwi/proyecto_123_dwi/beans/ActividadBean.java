package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Actividad;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Asignacion;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Solicitud;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ActividadFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.AsignacionFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.SolicitudFacade;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Cliente;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SendGridService;

@Named
@SessionScoped
public class ActividadBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ActividadFacade actividadFacade;

    @Inject
    private SolicitudFacade solicitudFacade;

    @Inject
    private AsignacionFacade asignacionFacade;

    private Solicitud solicitudSeleccionada; // Solicitud seleccionada para operaciones
    private Asignacion asignacionSeleccionada; // Asignación seleccionada (del colaborador)
    private Asignacion asignacionCoordinador; // Asignación del coordinador (calculada)
    private Actividad nuevaActividad; // Nueva actividad a registrar
    private List<Actividad> actividades; // Lista de actividades relacionadas

    @PostConstruct
    public void init() {
        nuevaActividad = new Actividad();
        actividades = new ArrayList<>();
    }

    // Cargar actividades relacionadas con la solicitud seleccionada.
    public void cargarActividades() {
        if (solicitudSeleccionada == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se seleccionó ninguna solicitud."));
            actividades = new ArrayList<>();
            return;
        }
        try {
            actividades = actividadFacade.listarActividadesPorSolicitud(solicitudSeleccionada.getIdSolicitud());
            asignacionCoordinador = asignacionFacade.obtenerCoordinadorPorSolicitud(solicitudSeleccionada.getIdSolicitud());

            if (actividades.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", "No se encontraron actividades para esta solicitud."));
            }
        } catch (Exception e) {
            actividades = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar las actividades."));
            e.printStackTrace();
        }
    }

    // Verificar si el usuario logueado es coordinador.
    public boolean esCoordinador() {
        if (asignacionSeleccionada == null) {
            return false;
        }
        return asignacionSeleccionada.isEsCoordinador();
    }
    
    // Método para obtener el correo del cliente a partir de la solicitud seleccionada
    public String obtenerCorreoCliente(Solicitud solicitudSeleccionada) {
        if (solicitudSeleccionada != null && solicitudSeleccionada.getCliente() != null) {
            Cliente cliente = solicitudSeleccionada.getCliente();
            if (cliente.getCorreo() != null && !cliente.getCorreo().isEmpty()) {
                return cliente.getCorreo();
            } else {
                return "Correo no disponible";
            }
        }
        return "Solicitud o cliente no válido";
    }

    // Guardar una nueva actividad asociada a la asignación.
    public void guardarActividad() {
        try {
            if (asignacionSeleccionada == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe seleccionar una asignación."));
                return;
            }
            nuevaActividad.setAsignacion(asignacionSeleccionada);
            nuevaActividad.setFechaRegistro(LocalDateTime.now());
            nuevaActividad.setColaborador(asignacionSeleccionada.getColaborador());

            actividadFacade.guardarActividad(nuevaActividad);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Actividad registrada correctamente."));
            cargarActividades();
            nuevaActividad = new Actividad();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la actividad."));
            e.printStackTrace();
        }
    }

    //Finalizar la solicitud asociada.
    public void finalizarSolicitud() {
        try {
            if (!esCoordinador()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Solo el coordinador puede finalizar la solicitud."));
                return;
            }

            solicitudSeleccionada.setEstado("Finalizada");
            solicitudSeleccionada.setFechaFinalizacion(LocalDateTime.now());
            solicitudFacade.actualizarSolicitud(solicitudSeleccionada);
            
            // Enviar correo de finalizacion
           enviarCorreoFinalizacion();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Solicitud finalizada exitosamente."));
            FacesContext.getCurrentInstance().getExternalContext().redirect("control_solicitud.xhtml?faces-redirect=true");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo finalizar la solicitud."));
            e.printStackTrace();
        }
    }
    
    private void enviarCorreoFinalizacion() {
        try {
            // Verificar que la solicitud seleccionada no sea nula
            if (solicitudSeleccionada == null) {
                throw new IllegalArgumentException("La solicitud seleccionada es nula.");
            }

            // Usar el método obtenerCorreoCliente para obtener el correo
            String destinatario = obtenerCorreoCliente(solicitudSeleccionada);

            // Verificar si el correo obtenido es válido
            if ("Solicitud o cliente no válido".equals(destinatario) || "Correo no disponible".equals(destinatario)) {
                throw new IllegalArgumentException("No se pudo obtener un correo válido para esta solicitud.");
            }

            // Configurar los datos para el correo
            String templateId = "d-b4d5aa9e84e0470aadc24b00921371ad"; // Sustituye con el ID de tu plantilla
            Map<String, String> dynamicData = Map.of(
                    "nombre", solicitudSeleccionada.getCliente().getNombreCliente(),
                    "id_solicitud", String.valueOf(solicitudSeleccionada.getIdSolicitud()),
                    "mensaje", "Tu solicitud ha sido finalizada con éxito. Gracias por confiar en nosotros."
            );

            // Llamar al servicio de envío de correos (SendGrid)
            SendGridService.enviarCorreoConPlantilla(destinatario, templateId, dynamicData);

            // Confirmar el envío del correo en la interfaz
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Correo de finalización enviado al cliente."));
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", e.getMessage()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo enviar el correo de finalización."));
            e.printStackTrace();
        }
    }

        public void verificarDatos() {
        System.out.println("Solicitud seleccionada: " + solicitudSeleccionada);
        if (solicitudSeleccionada != null) {
            System.out.println("ID: " + solicitudSeleccionada.getIdSolicitud());
            System.out.println("Estado: " + solicitudSeleccionada.getEstado());
            if (solicitudSeleccionada.getCliente() != null) {
                System.out.println("Cliente: " + solicitudSeleccionada.getCliente().getNombreCliente());
                System.out.println("Correo: " + solicitudSeleccionada.getCliente().getCorreo());
            } else {
                System.out.println("Cliente asociado es nulo.");
            }
        } else {
            System.out.println("Solicitud seleccionada es nula.");
        }
    }



    // Formatear fechas para mostrar en la vista
    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) {
            return "-";
        }
        return fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    // Getters y Setters
    public Solicitud getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    public void setSolicitudSeleccionada(Solicitud solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;

        // Obtener colaborador logueado
        Colaborador colaboradorLogueado = (Colaborador) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get("colaboradorLogueado");

        if (colaboradorLogueado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se encontró un colaborador logueado."));
            asignacionSeleccionada = null;
            return;
        }

        // Buscar asignación del colaborador logueado para esta solicitud
        asignacionSeleccionada = asignacionFacade.buscarAsignacionPorColaboradorYSolicitud(
                colaboradorLogueado.getIdColaborador(),
                solicitudSeleccionada.getIdSolicitud());

        if (asignacionSeleccionada == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se encontró una asignación válida para este colaborador y solicitud."));
        }

        // Cargar actividades relacionadas con la solicitud seleccionada
        cargarActividades();
    }


    public Asignacion getAsignacionSeleccionada() {
        return asignacionSeleccionada;
    }

    public void setAsignacionSeleccionada(Asignacion asignacionSeleccionada) {
        this.asignacionSeleccionada = asignacionSeleccionada;
    }

    public Asignacion getAsignacionCoordinador() {
        return asignacionCoordinador;
    }

    public void setAsignacionCoordinador(Asignacion asignacionCoordinador) {
        this.asignacionCoordinador = asignacionCoordinador;
    }

    public Actividad getNuevaActividad() {
        return nuevaActividad;
    }

    public void setNuevaActividad(Actividad nuevaActividad) {
        this.nuevaActividad = nuevaActividad;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
    }
}

/*
package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Actividad;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Asignacion;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Solicitud;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ActividadFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.AsignacionFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.SolicitudFacade;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SendGridService;

@Named
@SessionScoped
public class ActividadBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ActividadFacade actividadFacade;

    @Inject
    private SolicitudFacade solicitudFacade;

    @Inject
    private AsignacionFacade asignacionFacade;

    private Solicitud solicitudSeleccionada; // Solicitud seleccionada para operaciones
    private Asignacion asignacionSeleccionada; // Asignación seleccionada (del colaborador)
    private Asignacion asignacionCoordinador; // Asignación del coordinador (calculada)
    private Actividad nuevaActividad; // Nueva actividad a registrar
    private List<Actividad> actividades; // Lista de actividades relacionadas

    @PostConstruct
    public void init() {
        nuevaActividad = new Actividad();
        actividades = new ArrayList<>();
    }

    // Cargar actividades relacionadas con la solicitud seleccionada.
    public void cargarActividades() {
        if (solicitudSeleccionada == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se seleccionó ninguna solicitud."));
            actividades = new ArrayList<>();
            return;
        }
        try {
            actividades = actividadFacade.listarActividadesPorSolicitud(solicitudSeleccionada.getIdSolicitud());
            asignacionCoordinador = asignacionFacade.obtenerCoordinadorPorSolicitud(solicitudSeleccionada.getIdSolicitud());

            if (actividades.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", "No se encontraron actividades para esta solicitud."));
            }
        } catch (Exception e) {
            actividades = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar las actividades."));
            e.printStackTrace();
        }
    }

    // Guardar una nueva actividad asociada a la asignación.
    public void guardarActividad() {
        try {
            if (asignacionSeleccionada == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe seleccionar una asignación."));
                return;
            }
            nuevaActividad.setAsignacion(asignacionSeleccionada);
            nuevaActividad.setFechaRegistro(LocalDateTime.now());
            nuevaActividad.setColaborador(asignacionSeleccionada.getColaborador());

            actividadFacade.guardarActividad(nuevaActividad);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Actividad registrada correctamente."));
            cargarActividades();
            nuevaActividad = new Actividad();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la actividad."));
            e.printStackTrace();
        }
    }

    //Finalizar la solicitud asociada.
    public void finalizarSolicitud() {
        try {
            if (!esCoordinador()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Solo el coordinador puede finalizar la solicitud."));
                return;
            }

            solicitudSeleccionada.setEstado("Finalizada");
            solicitudSeleccionada.setFechaFinalizacion(LocalDateTime.now());
            solicitudFacade.actualizarSolicitud(solicitudSeleccionada);
            
            // Obtiene correo cliente
            if (solicitudSeleccionada != null && solicitudSeleccionada.getCliente() != null) {
            String destinatario = solicitudSeleccionada.getCliente().getCorreo();
            
            // Usar el correo para enviar la notificación
            String asunto = "Tu solicitud #" + solicitudSeleccionada.getIdSolicitud() + " ha sido concluida";
            String mensajeHtml = "<html>"
                    + "<body>"
                    + "<h1>Tu solicitud ha sido concluida</h1>"
                    + "<p>Hola <strong>" + solicitudSeleccionada.getCliente().getNombreCliente() + "</strong>,</p>"
                    + "<p>Gracias por confiar en nosotros.</p>"
                    + "</body>"
                    + "</html>";

        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo obtener el correo del cliente."));
        }

            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Solicitud finalizada exitosamente."));
            FacesContext.getCurrentInstance().getExternalContext().redirect("control_solicitud.xhtml?faces-redirect=true");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo finalizar la solicitud."));
            e.printStackTrace();
        }
    }

    // Verificar si el usuario logueado es coordinador.
    public boolean esCoordinador() {
        if (asignacionSeleccionada == null) {
            return false;
        }
        return asignacionSeleccionada.isEsCoordinador();
    }


    // Formatear fechas para mostrar en la vista
    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) {
            return "-";
        }
        return fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    // Getters y Setters
    public Solicitud getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    public void setSolicitudSeleccionada(Solicitud solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;

        // Obtener colaborador logueado
        Colaborador colaboradorLogueado = (Colaborador) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get("colaboradorLogueado");

        if (colaboradorLogueado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se encontró un colaborador logueado."));
            asignacionSeleccionada = null;
            return;
        }

        // Buscar asignación del colaborador logueado para esta solicitud
        asignacionSeleccionada = asignacionFacade.buscarAsignacionPorColaboradorYSolicitud(
                colaboradorLogueado.getIdColaborador(),
                solicitudSeleccionada.getIdSolicitud());

        if (asignacionSeleccionada == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se encontró una asignación válida para este colaborador y solicitud."));
        }

        // Cargar actividades relacionadas con la solicitud seleccionada
        cargarActividades();
    }


    public Asignacion getAsignacionSeleccionada() {
        return asignacionSeleccionada;
    }

    public void setAsignacionSeleccionada(Asignacion asignacionSeleccionada) {
        this.asignacionSeleccionada = asignacionSeleccionada;
    }

    public Asignacion getAsignacionCoordinador() {
        return asignacionCoordinador;
    }

    public void setAsignacionCoordinador(Asignacion asignacionCoordinador) {
        this.asignacionCoordinador = asignacionCoordinador;
    }

    public Actividad getNuevaActividad() {
        return nuevaActividad;
    }

    public void setNuevaActividad(Actividad nuevaActividad) {
        this.nuevaActividad = nuevaActividad;
    }

    public List<Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<Actividad> actividades) {
        this.actividades = actividades;
    }
}
*/