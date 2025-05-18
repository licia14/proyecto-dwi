package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ColaboradorFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SecurityUtils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Named
@SessionScoped
public class ColaboradorSesionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Colaborador colaborador; // Colaborador autenticado en la sesión
    private Part fotoPerfil; // Archivo de la foto subido
    private String nuevaContrasena; // Campo para la nueva contraseña ingresada
    
    @Inject
    private ColaboradorFacade colaboradorFacade;

    private static final String FOTO_POR_DEFECTO = "/resources/images/foto_perfil_default.jpg";

    // Verifica si hay un colaborador autenticado
    public boolean isColaboradorAutenticado() {
        return colaborador != null;
    }

    // Cierra la sesión actual
    public void cerrarSesion() {
        colaborador = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }

    // Devuelve el idRol del colaborador actual
    public int getIdRol() {
        if (colaborador != null && colaborador.getRol() != null) {
            return colaborador.getRol().getIdRol();
        }
        return -1;
    }

    // Verifica si el colaborador es administrador
    public boolean esAdministrador() {
        return getIdRol() == 1;
    }

    // Verifica si el colaborador es analista
    public boolean esAnalista() {
        return getIdRol() == 2;
    }

    // Actualiza los datos del colaborador y redirige al perfil
    public String actualizarDatos() {
        try {
            // Manejo de la foto de perfil
            if (fotoPerfil != null) {
                String fileName = fotoPerfil.getSubmittedFileName();
                String uploadDir = "C:/Usuarios/Joshua/Payara_Server/glassfish/uploads"; // Cambia según tu entorno
                File uploads = new File(uploadDir);

                if (!uploads.exists()) {
                    uploads.mkdirs();
                }

                String filePath = uploadDir + File.separator + fileName;

                try (InputStream input = fotoPerfil.getInputStream()) {
                    Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                colaborador.setFotoPerfil(fileName); // Guarda el nombre del archivo
            }

            // Si se proporciona una nueva contraseña, generar su hash
            if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
                String hashedPassword = SecurityUtils.hashPasswordWithSalt(nuevaContrasena, colaborador.getSalt());
                colaborador.setContrasena(hashedPassword);
            }

            // Actualiza el colaborador en la base de datos
            colaboradorFacade.actualizarColaborador(colaborador);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Datos actualizados correctamente."));

            return "perfil_colaborador.xhtml?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron actualizar los datos."));
            e.printStackTrace();
            return null;
        }
    }

    // Devuelve la ruta de la foto de perfil del colaborador
    public String getRutaFotoPerfil() {
        if (colaborador != null && colaborador.getFotoPerfil() != null && !colaborador.getFotoPerfil().isEmpty()) {
            String ruta = "/uploads/" + colaborador.getFotoPerfil();
            System.out.println("Ruta generada: " + ruta);
            return ruta;
        } else {
            System.out.println("Ruta por defecto: " + FOTO_POR_DEFECTO);
            return FOTO_POR_DEFECTO;
        }
    }


    // Getters y Setters
    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Part getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Part fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}
