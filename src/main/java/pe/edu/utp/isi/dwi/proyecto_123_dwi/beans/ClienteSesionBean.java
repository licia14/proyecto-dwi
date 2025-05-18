package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Cliente;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ClienteFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SecurityUtils;

@Named
@SessionScoped
public class ClienteSesionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Cliente cliente; // Cliente autenticado
    private Part fotoPerfil; // Archivo de la foto subido
    private String nuevaContrasena; // Campo para la nueva contraseña ingresada
    
    @Inject
    private ClienteFacade clienteFacade;

    private static final String FOTO_POR_DEFECTO = "/resources/images/foto_perfil_default.jpg";

    /**
     * Verifica si el cliente está autenticado.
     *
     * @return true si hay un cliente autenticado, false en caso contrario.
     */
    public boolean isClienteAutenticado() {
        return cliente != null;
    }
   
    /**
     * Actualiza los datos del cliente y redirige a la página de actualización.
     *
     * @return la página a redirigir en caso de éxito, null en caso de error.
     */
    public String actualizarDatos() {
        try {
            // Manejo de la foto de perfil
            if (fotoPerfil != null) {
                // Obtén el nombre del archivo subido
                String fileName = fotoPerfil.getSubmittedFileName(); 
                String uploadDir = "/path/to/uploads"; // Cambia esta ruta según tu entorno
                File uploads = new File(uploadDir);

                // Crea el directorio si no existe
                if (!uploads.exists()) {
                    uploads.mkdirs();
                }

                // Ruta completa del archivo
                String filePath = uploadDir + File.separator + fileName;

                // Guarda el archivo en el servidor
                try (InputStream input = fotoPerfil.getInputStream()) {
                    Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                // Asigna la ruta relativa al cliente
                cliente.setFotoPerfil("uploads/" + fileName); // Guarda solo la ruta relativa
            } else {
                System.out.println("Foto no seleccionada o nula");
            }

            // Si se proporciona una nueva contraseña, generar su hash
            if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
                String hashedPassword = SecurityUtils.hashPasswordWithSalt(nuevaContrasena, cliente.getSalt());
                cliente.setContrasena(hashedPassword);
            }

            // Actualiza el cliente en la base de datos
            clienteFacade.actualizarCliente(cliente);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Datos actualizados correctamente."));

            return "perfil_cliente.xhtml?faces-redirect=true"; // Redirige al perfil

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron actualizar los datos."));
            e.printStackTrace();
            return null; // Permanece en la misma página en caso de error
        }
    }

    /**
     * Obtiene la ruta de la foto de perfil del cliente.
     *
     * @return la ruta de la foto del cliente o una foto por defecto si no está definida.
     */
    public String getRutaFotoPerfil() {
        if (cliente != null && cliente.getFotoPerfil() != null && !cliente.getFotoPerfil().isEmpty()) {
            // Genera la ruta accesible desde el navegador
            return "/uploads/" + cliente.getFotoPerfil();
        } else {
            // Ruta de la imagen por defecto
            return "/resources/images/foto_perfil_default.jpg";
        }
    }
    
    public void procesarSubidaDeArchivo() {
        try {
            if (fotoPerfil != null) {
                // Nombre del archivo subido
                String fileName = fotoPerfil.getSubmittedFileName();
                System.out.println("Archivo recibido: " + fileName);

                // Ruta del directorio donde se guardarán los archivos
                String uploadDir = "C:/Usuarios/Joshua/Payara_Server/glassfish/uploads";
                File uploads = new File(uploadDir);

                // Verifica si la carpeta existe, si no, la crea
                if (!uploads.exists()) {
                    System.out.println("La carpeta uploads no existe. Creándola...");
                    uploads.mkdirs();
                }

                // Ruta completa del archivo
                String filePath = uploadDir + File.separator + fileName;

                // Guarda el archivo en la ruta especificada
                try (InputStream input = fotoPerfil.getInputStream()) {
                    Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Archivo guardado en: " + filePath);
                }

                // Guarda solo el nombre del archivo en la base de datos
                cliente.setFotoPerfil(fileName);
                System.out.println("Nombre del archivo guardado en BD: " + fileName);
            } else {
                System.out.println("No se seleccionó ningún archivo para subir.");
            }
        } catch (Exception e) {
            System.out.println("Error al subir el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Getters y Setters
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
    
     public Part getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Part fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
    
}
