package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.Part;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Rol;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ColaboradorFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.RolFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SecurityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@Named
@RequestScoped
public class ColaboradorRegistroBean implements Serializable {

    private Colaborador colaborador = new Colaborador();
    private String confirmarContrasena;
    private int idRolSeleccionado; // ID del rol seleccionado desde la vista
    private Part fotoPerfil; // Archivo de la foto de perfil

    @Inject
    private ColaboradorFacade colaboradorFacade;

    @Inject
    private RolFacade rolFacade;

    private List<Colaborador> colaboradores;
    
    public void registrarColaborador() {
        try {
            // Validar contraseñas
            if (!colaborador.getContrasena().equals(confirmarContrasena)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }

            // Hashear la contraseña con un salt
            String salt = SecurityUtils.generateSalt();
            String hashedPassword = SecurityUtils.hashPasswordWithSalt(colaborador.getContrasena(), salt);
            colaborador.setSalt(salt);
            colaborador.setContrasena(hashedPassword);

            // Asignar el objeto Rol al colaborador
            Rol rolSeleccionado = rolFacade.buscarRolPorId(idRolSeleccionado);
            if (rolSeleccionado == null) {
                throw new IllegalArgumentException("El rol seleccionado no es válido.");
            }
            colaborador.setRol(rolSeleccionado);

            // Manejar la subida de la foto de perfil si existe
            if (fotoPerfil != null) {
                procesarFotoPerfil();
            }

            // Registrar la fecha actual
            colaborador.setFechaRegistro(LocalDate.now());

            // Guardar colaborador
            colaboradorFacade.guardarColaborador(colaborador);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Colaborador registrado correctamente."));

            limpiarFormulario();

            // Redirigir a una página de éxito
            FacesContext.getCurrentInstance().getExternalContext().redirect("control_colaborador.xhtml");
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el colaborador: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private void procesarFotoPerfil() {
        try {
            String fileName = fotoPerfil.getSubmittedFileName();
            String uploadDir = "/uploads/colaboradores"; // Cambia esta ruta según tu entorno
            File uploads = new File(uploadDir);
            if (!uploads.exists()) {
                uploads.mkdirs(); // Crear carpeta si no existe
            }

            String filePath = uploadDir + "/" + fileName;
            try (InputStream input = fotoPerfil.getInputStream()) {
                Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            colaborador.setFotoPerfil(filePath); // Guardar la ruta del archivo en el colaborador
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al subir el archivo: " + e.getMessage()));
        }
    }

    public List<Rol> getListaRoles() {
        return rolFacade.listarRoles();
    }
    
    public void limpiarFormulario() {
        colaborador = new Colaborador();
        confirmarContrasena = null;
        idRolSeleccionado = 0;
        fotoPerfil = null;
    }

    // Getters y Setters
    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }

    public int getIdRolSeleccionado() {
        return idRolSeleccionado;
    }

    public void setIdRolSeleccionado(int idRolSeleccionado) {
        this.idRolSeleccionado = idRolSeleccionado;
    }

    public Part getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Part fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    // cargar  lista de colaboradores en la vista
    public void cargarColaboradores() {
        colaboradores = colaboradorFacade.listarTodos();
    }

    public List<Colaborador> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(List<Colaborador> colaboradores) {
        this.colaboradores = colaboradores;
    }
    
    @PostConstruct
    public void init() {
        cargarColaboradores();
    }
}
