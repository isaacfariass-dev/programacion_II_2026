package com.zebop.sistemasCorrelativas.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de autenticación simplificado: acceso unificado sin roles.
 * Solo exige sesión activa; no distingue permisos por tipo de usuario.
 * Las rutas públicas (/login, /css, /js) se excluyen en WebMvcConfig.
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        boolean esApi = request.getRequestURI().startsWith("/api/");
        boolean autenticado = session != null && session.getAttribute("usuarioLogueado") != null;

        if (!autenticado) {
            if (esApi) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sesión requerida");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
        }
        return autenticado;
    }
}
