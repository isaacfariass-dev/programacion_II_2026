package com.zebop.sistemasCorrelativas.interceptor;

import com.zebop.sistemasCorrelativas.entity.Usuario;
import com.zebop.sistemasCorrelativas.entity.enums.Rol;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de autenticación y autorización.
 * Protege todas las rutas privadas del sistema verificando la sesión activa.
 * Las rutas públicas (/login, /css, /js) se configuran como excluidas en WebMvcConfig.
 */
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Verifica antes de cada request que el usuario tenga sesión activa.
     * Adicionalmente verifica que ALUMNO no acceda a rutas de ADMIN y viceversa.
     * Retorna true para continuar, false para bloquear (redirige al login).
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        boolean permitido;

        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            permitido = false;
        } else {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            String uri = request.getRequestURI();
            boolean esRutaAdmin = uri.startsWith("/carreras") || uri.startsWith("/materias");
            boolean esAdmin = usuario.getRol() == Rol.ADMIN;

            if (esRutaAdmin && !esAdmin) {
                response.sendRedirect(request.getContextPath() + "/plan");
                permitido = false;
            } else {
                permitido = true;
            }
        }
        return permitido;
    }
}
