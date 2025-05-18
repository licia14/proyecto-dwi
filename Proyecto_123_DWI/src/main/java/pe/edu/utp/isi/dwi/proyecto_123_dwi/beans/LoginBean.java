package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Cliente;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ClienteFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ColaboradorFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SecurityUtils;

import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private String correo;
    private String contrasena;

    @Inject
    private ClienteFacade clienteFacade;

    @Inject
    private ClienteSesionBean clienteSesionBean;

    @Inject
    private ColaboradorFacade colaboradorFacade;

    @Inject
    private ColaboradorSesionBean colaboradorSesionBean;
    
    @Inject
    private RolBean rolBean; // Inyecta RolBean
    
    public void iniciarSesion() {
        try {
            // Obtener la sesión HTTP
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

            // Intentar iniciar sesión como cliente
            Cliente clienteAutenticado = clienteFacade.buscarPorCorreoYContrasena(correo, contrasena);
            if (clienteAutenticado != null) {
                clienteSesionBean.setCliente(clienteAutenticado);
                session.setAttribute("clienteSesion", clienteAutenticado);
                redirigir("/faces/cliente/portal_cliente.xhtml");
                return;
            }

            // Intentar iniciar sesión como colaborador
            Colaborador colaboradorAutenticado = colaboradorFacade.buscarPorCorreoYContrasena(correo, contrasena);
            if (colaboradorAutenticado != null) {
                colaboradorSesionBean.setColaborador(colaboradorAutenticado);
                session.setAttribute("colaboradorSesion", colaboradorAutenticado);

                // **Guardar el rol del colaborador en la sesión**
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rolActual", colaboradorAutenticado.getRol().getNombreRol());
                
                // Agregar sincronización con SessionMap
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("colaboradorLogueado", colaboradorAutenticado);

                redirigir("/faces/colaborador/portal_colaborador.xhtml");
                return;
            }

            // Si no se encuentra en ninguna de las dos entidades, mostrar error
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Correo o contraseña incorrectos."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al iniciar sesión: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void cerrarSesion() {
        try {
            // Invalidar la sesión HTTP y todos los datos asociados
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

            // Redirigir al login
            FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/login.xhtml");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cerrar la sesión: " + e.getMessage()));
            e.printStackTrace();
        }
    }




    private void redirigir(String ruta) {
        try {
            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + ruta);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo redirigir: " + e.getMessage()));
        }
    }


    // Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
