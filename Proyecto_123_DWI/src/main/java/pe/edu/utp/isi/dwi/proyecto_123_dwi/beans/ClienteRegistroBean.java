package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Cliente;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ClienteFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SecurityUtils;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SendGridService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Named
@RequestScoped
public class ClienteRegistroBean {

    private Cliente cliente = new Cliente();
    private String confirmarContrasena;
    private Part fotoPerfil;

    @Inject
    private ClienteFacade clienteFacade;

    private List<Cliente> clientes;

    @PostConstruct
    public void init() {
        cargarClientes();
    }

    // Lógica para registrar un nuevo cliente.
    public void registrarCliente() throws Exception {
        try {
            // Validar contraseñas
            if (!cliente.getContrasena().equals(confirmarContrasena)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }

            // Validar tipo de documento y número de documento
            validarNumeroDocumento();

            // Procesar foto de perfil si existe
            if (fotoPerfil != null) {
                procesarFoto();
            }

            // Generar salt y hashear contraseña
            String salt = SecurityUtils.generateSalt();
            String hashedPassword = SecurityUtils.hashPasswordWithSalt(cliente.getContrasena(), salt);
            cliente.setSalt(salt);
            cliente.setContrasena(hashedPassword);

            // Registrar fecha actual como fecha de registro
            cliente.setFechaRegistro(LocalDate.now());

            // Guardar cliente en la base de datos
            clienteFacade.guardarCliente(cliente);

           // Enviar correo de bienvenida
           enviarCorreoBienvenida(cliente);
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente registrado correctamente."));

            // Redirigir a la página de éxito
            FacesContext.getCurrentInstance().getExternalContext().redirect("/Proyecto_123_DWI/registro_exitoso.xhtml");

        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el cliente: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    // Método para enviar correo de bienvenida utilizando una plantilla dinámica
    private void enviarCorreoBienvenida(Cliente cliente) {
        try {
            String destinatario = cliente.getCorreo(); // Correo del cliente
            String templateId = "d-18e5826e917547e2bb933acfbcdfc74c"; // ID de la plantilla de bienvenida

            // Datos dinámicos para reemplazar en la plantilla
            Map<String, String> dynamicData = Map.of(
                    "nombre", cliente.getNombreCliente(),
                    "email", cliente.getCorreo(),
                    "mensaje", "Gracias por registrarte en nuestra plataforma. Estamos emocionados de tenerte con nosotros."
            );

            // Llamar al servicio de envío de correos
            SendGridService.enviarCorreoConPlantilla(destinatario, templateId, dynamicData);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "El correo de bienvenida fue enviado con éxito."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "El cliente fue registrado, pero no se pudo enviar el correo de bienvenida."));
            e.printStackTrace();
        }
    }


    /**
     * Valida el número de documento según el tipo.
     */
    private void validarNumeroDocumento() {
        String tipoDocumento = cliente.getTipoDocumento();
        String numDocumento = cliente.getNumDocumento();

        if (tipoDocumento == null || tipoDocumento.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de documento.");
        }

        if (numDocumento == null || numDocumento.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el número de documento.");
        }

        if ("DNI".equals(tipoDocumento) && !numDocumento.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }

        if ("RUC".equals(tipoDocumento) && !numDocumento.matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe tener exactamente 11 dígitos numéricos.");
        }
    }

    /**
     * Procesa la foto de perfil y guarda el archivo.
     */
    private void procesarFoto() throws Exception {
        String fileName = fotoPerfil.getSubmittedFileName();
        String uploadDir = "/uploads/clientes"; // Cambia esta ruta según tu entorno
        File uploads = new File(uploadDir);

        if (!uploads.exists()) {
            uploads.mkdirs();
        }

        String filePath = uploadDir + "/" + fileName;
        try (InputStream input = fotoPerfil.getInputStream()) {
            Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        cliente.setFotoPerfil(filePath);
    }

    // Cargar lista de clientes
    public void cargarClientes() {
        clientes = clienteFacade.listarTodos();
    }

    // Getters y Setters
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }

    public Part getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Part fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}


/*
package pe.edu.utp.isi.dwi.proyecto_123_dwi.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Cliente;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.facade.ClienteFacade;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.util.SecurityUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

@Named
@RequestScoped
public class ClienteRegistroBean {

    private Cliente cliente = new Cliente();
    private String confirmarContrasena;
    private Part fotoPerfil;

    @Inject
    private ClienteFacade clienteFacade;

    private List<Cliente> clientes;
    
    // Lógica para registrar un nuevo cliente.
    public void registrarCliente() throws Exception {
        // Validar contraseñas
        if (!cliente.getContrasena().equals(confirmarContrasena)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        // Validar tipo de documento y número de documento
        validarNumeroDocumento();

        // Procesar foto de perfil si existe
        if (fotoPerfil != null) {
            procesarFoto();
        }

        // Generar salt y hashear contraseña
        String salt = SecurityUtils.generateSalt();
        String hashedPassword = SecurityUtils.hashPasswordWithSalt(cliente.getContrasena(), salt);
        cliente.setSalt(salt);
        cliente.setContrasena(hashedPassword);

        // Registrar fecha actual como fecha de registro
        cliente.setFechaRegistro(LocalDate.now());

        // Guardar cliente en la base de datos
        clienteFacade.guardarCliente(cliente);

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente registrado correctamente."));
        
        try {
            // Redirigir o mostrar mensaje de éxito
            FacesContext.getCurrentInstance().getExternalContext().redirect("/Proyecto_123_DWI/registro_exitoso.xhtml");
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar el cliente: " + e.getMessage()));
        }
    }

    // Lógica para actualizar un cliente existente.
    private void actualizarClienteExistente() throws Exception {
        // Validar tipo de documento y número de documento (si aplicable)
        validarNumeroDocumento();

        // Procesar foto de perfil si existe
        if (fotoPerfil != null) {
            procesarFoto();
        }

        // Actualizar contraseña solo si se proporciona
        if (cliente.getContrasena() != null && !cliente.getContrasena().isEmpty()) {
            String hashedPassword = SecurityUtils.hashPasswordWithSalt(cliente.getContrasena(), cliente.getSalt());
            cliente.setContrasena(hashedPassword);
        }

        // Actualizar cliente en la base de datos
        clienteFacade.actualizarCliente(cliente);

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Datos del cliente actualizados correctamente."));
    }

    //Valida el número de documento según el tipo.
    private void validarNumeroDocumento() {
        String tipoDocumento = cliente.getTipoDocumento();
        String numDocumento = cliente.getNumDocumento();

        if (tipoDocumento == null || tipoDocumento.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de documento.");
        }

        if (numDocumento == null || numDocumento.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el número de documento.");
        }

        if ("DNI".equals(tipoDocumento) && !numDocumento.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
        }

        if ("RUC".equals(tipoDocumento) && !numDocumento.matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe tener exactamente 11 dígitos numéricos.");
        }
    }

    // Procesa la foto de perfil y guarda el archivo.

    private void procesarFoto() throws Exception {
        String fileName = fotoPerfil.getSubmittedFileName();
        String uploadDir = "/uploads/clientes"; // Cambia esta ruta según tu entorno
        File uploads = new File(uploadDir);

        if (!uploads.exists()) {
            uploads.mkdirs();
        }

        String filePath = uploadDir + "/" + fileName;
        try (InputStream input = fotoPerfil.getInputStream()) {
            Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        cliente.setFotoPerfil(filePath);
    }

    // Getters y Setters
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }

    public Part getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Part fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    // cargar  lista de clientes en la vista
    public void cargarClientes() {
        clientes = clienteFacade.listarTodos();
    }
    
    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
    
    @PostConstruct
    public void init() {
        cargarClientes();
    }
}
*/