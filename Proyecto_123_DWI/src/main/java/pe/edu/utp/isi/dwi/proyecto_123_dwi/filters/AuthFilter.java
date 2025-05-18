package pe.edu.utp.isi.dwi.proyecto_123_dwi.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        // Verificar si hay una sesión activa con usuario autenticado
        boolean clienteAutenticado = session != null && session.getAttribute("clienteSesion") != null;
        boolean colaboradorAutenticado = session != null && session.getAttribute("colaboradorSesion") != null;

        String requestedPath = httpRequest.getRequestURI();

        // Mensajes de depuración
        System.out.println("Requested Path: " + requestedPath);
        System.out.println("Session: " + session);
        System.out.println("Cliente autenticado: " + clienteAutenticado);
        System.out.println("Colaborador autenticado: " + colaboradorAutenticado);

        // Si no está autenticado y está accediendo a rutas protegidas
        if ((!clienteAutenticado && requestedPath.contains("/cliente/")) ||
            (!colaboradorAutenticado && requestedPath.contains("/colaborador/"))) {
            System.out.println("Redirigiendo a login.xhtml");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
            return;
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
