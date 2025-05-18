package pe.edu.utp.isi.dwi.proyecto_123_dwi.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.edu.utp.isi.dwi.proyecto_123_dwi.entities.Colaborador;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/faces/colaborador/control_rol.xhtml",
    "/faces/colaborador/control_dashboard.xhtml",
     "/faces/colaborador/registro_colaborador.xhtml",
})

public class RolFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización (si es necesario)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Obtener la sesión y verificar si hay un colaborador logueado
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            Colaborador colaborador = (Colaborador) session.getAttribute("colaboradorSesion");

            if (colaborador != null && colaborador.getRol() != null) {
                // Verificar si el rol del colaborador es "Administrador"
                String nombreRol = colaborador.getRol().getNombreRol();
                if ("Administrador".equals(nombreRol)) {
                    // Permitir el acceso
                    chain.doFilter(request, response);
                    return;
                }
            }
        }

        // Si no tiene el rol adecuado o no está logueado, redirigir
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/faces/colaborador/portal_colaborador.xhtml");
    }

    @Override
    public void destroy() {
        // Limpieza (si es necesario)
    }
}