package pe.edu.utp.isi.dwi.proyecto_123_dwi.util;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendGridService {

    private static final Logger logger = Logger.getLogger(SendGridService.class.getName());
    private static final String API_KEY = System.getenv("SENDGRID_API_KEY"); // Obtén la API Key desde las variables de entorno

    /**
     * Método para enviar un correo utilizando una plantilla dinámica.
     *
     * @param destinatario Dirección de correo del cliente
     * @param templateId ID de la plantilla dinámica a usar
     * @param dynamicData Datos dinámicos que serán reemplazados en la plantilla
     */
    public static void enviarCorreoConPlantilla(String destinatario, String templateId, Map<String, String> dynamicData) {
        Email from = new Email("soportedigital123@hotmail.com"); // Cambia este email por el remitente configurado
        Email to = new Email(destinatario);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        // Agregar datos dinámicos a la plantilla
        for (Map.Entry<String, String> entry : dynamicData.entrySet()) {
            personalization.addDynamicTemplateData(entry.getKey(), entry.getValue());
        }

        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            logger.log(Level.INFO, "Correo enviado a {0} con código {1}",
                    new Object[]{destinatario, response.getStatusCode()});
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error al enviar correo a: " + destinatario, ex);
        }
    }
}
