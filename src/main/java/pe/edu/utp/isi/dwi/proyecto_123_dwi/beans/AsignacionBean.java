package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Asignacion;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Solicitud;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.AsignacionFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ColaboradorFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.SolicitudFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class AsignacionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SolicitudFacade solicitudFacade;

    @Inject
    private ColaboradorFacade colaboradorFacade;

    @Inject
    private AsignacionFacade asignacionFacade;

    private List<Colaborador> colaboradoresDisponibles;
    private List<Colaborador> colaboradoresSeleccionados;
    private Solicitud solicitudSeleccionada;
    private Colaborador coordinadorSeleccionado;

    @PostConstruct
    public void init() {
        colaboradoresSeleccionados = new ArrayList<>();
        cargarColaboradoresDisponibles();
        cargarSolicitudSeleccionada();
    }

    public void cargarSolicitudSeleccionada() {
        String idSolicitud = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("idSolicitud");
        if (idSolicitud != null) {
            solicitudSeleccionada = solicitudFacade.buscarPorId(Integer.parseInt(idSolicitud));
        }
    }

    public void cargarColaboradoresDisponibles() {
        colaboradoresDisponibles = colaboradorFacade.listarTodos();
    }

    public void agregarColaborador(Colaborador colaborador) {
        if (!colaboradoresSeleccionados.contains(colaborador)) {
            colaboradoresSeleccionados.add(colaborador);
        }
    }

    public void eliminarColaborador(Colaborador colaborador) {
        colaboradoresSeleccionados.remove(colaborador);
        if (coordinadorSeleccionado != null && coordinadorSeleccionado.equals(colaborador)) {
            coordinadorSeleccionado = null;
        }
    }

    public void establecerCoordinadorSeleccionado(Colaborador colaborador) {
        this.coordinadorSeleccionado = colaborador;
    }

    public boolean isColaboradorCoordinador(Colaborador colaborador) {
        return coordinadorSeleccionado != null && colaborador.equals(coordinadorSeleccionado);
    }

    public void guardarAsignacion() {
        if (solicitudSeleccionada == null || colaboradoresSeleccionados.isEmpty() || coordinadorSeleccionado == null) {
            System.out.println("Error: Solicitud, colaboradores o coordinador no válidos.");
            return;
        }

        for (Colaborador colaborador : colaboradoresSeleccionados) {
            Asignacion asignacion = new Asignacion();
            asignacion.setSolicitud(solicitudSeleccionada);
            asignacion.setColaborador(colaborador);
            asignacion.setEsCoordinador(colaborador.equals(coordinadorSeleccionado));
            asignacionFacade.guardarAsignacion(asignacion);
        }

        solicitudSeleccionada.setEstado("En Proceso");
        solicitudFacade.actualizarSolicitud(solicitudSeleccionada);
        resetFormulario();
    }

    public void resetFormulario() {
        this.solicitudSeleccionada = null; // Limpia la solicitud seleccionada
        this.coordinadorSeleccionado = null; // Limpia el coordinador
        this.colaboradoresSeleccionados.clear(); // Limpia la lista de colaboradores seleccionados
    }
    
    public List<Asignacion> obtenerMisAsignaciones() {
        Colaborador colaboradorLogueado = obtenerColaboradorLogueado();
        if (colaboradorLogueado == null) {
            return new ArrayList<>(); // Si no hay colaborador logueado, retorna una lista vacía
        }
        return asignacionFacade.listarPorColaborador(colaboradorLogueado.getIdColaborador());
    }
    
    public void redireccionarPorSolicitud(Asignacion asignacion) {
        try {
            if (asignacion == null || asignacion.getSolicitud() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se pudo redirigir: la asignación o solicitud es nula."));
                return;
            }

            // Guardar la solicitud seleccionada en la sesión
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .put("solicitudSeleccionada", asignacion.getSolicitud());

            // Redirigir a control_actividad.xhtml
            FacesContext.getCurrentInstance().getExternalContext().redirect("control_actividad.xhtml");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo redirigir: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private Colaborador obtenerColaboradorLogueado() {
        return (Colaborador) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("colaboradorLogueado");
    }

    public List<Colaborador> getColaboradoresDisponibles() {
        return colaboradoresDisponibles;
    }

    public List<Colaborador> getColaboradoresSeleccionados() {
        return colaboradoresSeleccionados;
    }

    public Solicitud getSolicitudSeleccionada() {
        return solicitudSeleccionada;
    }

    public void setSolicitudSeleccionada(Solicitud solicitudSeleccionada) {
        this.solicitudSeleccionada = solicitudSeleccionada;
    }

    public Colaborador getCoordinadorSeleccionado() {
        return coordinadorSeleccionado;
    }

    public void setCoordinadorSeleccionado(Colaborador coordinadorSeleccionado) {
        this.coordinadorSeleccionado = coordinadorSeleccionado;
    }
}
