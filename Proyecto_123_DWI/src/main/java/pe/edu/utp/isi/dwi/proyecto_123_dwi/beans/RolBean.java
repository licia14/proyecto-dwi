package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Rol;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.RolFacade;

import java.util.List;

@Named
@RequestScoped
public class RolBean {

    @Inject
    private RolFacade rolFacade;

    private Rol rol = new Rol(); // Objeto actual para guardar/editar
    private List<Rol> listaRoles; // Lista de roles visibles en la tabla
    private String rolActual; // Rol del colaborador logueado
    
    
    // Getter para listaRoles como propiedad
    public List<Rol> getListaRoles() {
        if (listaRoles == null) {
            listaRoles = rolFacade.listarRoles(); // Carga los roles si aún no se han cargado
        }
        return listaRoles;
    }

    // Setters y Getters para rol
    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    

    // Guardar un nuevo rol
    public void guardarRol() {
        try {
            if (rol.getIdRol() == 0) { // Crear nuevo rol
                rolFacade.guardarRol(rol);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Rol creado con éxito."));
            } else { // Actualizar rol existente
                rolFacade.actualizarRol(rol);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Rol actualizado con éxito."));
            }
            listaRoles = null; // Recargar lista de roles
            limpiarFormulario();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar el rol: " + e.getMessage()));
        }
    }

    // Cargar el rol seleccionado
    public void cargarRol(Rol rolSeleccionado) {
        if (rolSeleccionado != null) {
            this.rol = rolSeleccionado; // Mantén el objeto seleccionado
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cargar el rol seleccionado."));
        }
    }

    // Eliminar un rol
    public void eliminarRol(Rol rolSeleccionado) {
        try {
            rolFacade.eliminarRol(rolSeleccionado);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Rol eliminado con éxito."));
            listaRoles = null; // Recargar lista de roles
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el rol: " + e.getMessage()));
        }
    }

    // Limpiar el formulario
    public void limpiarFormulario() {
        this.rol = new Rol(); // Reinicia el objeto rol
    }
}
