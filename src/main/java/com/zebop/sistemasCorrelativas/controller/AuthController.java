package com.zebop.sistemasCorrelativas.controller;

import com.zebop.sistemasCorrelativas.entity.Usuario;
import com.zebop.sistemasCorrelativas.entity.enums.Rol;
import com.zebop.sistemasCorrelativas.exception.AutenticacionException;
import com.zebop.sistemasCorrelativas.service.ICarreraService;
import com.zebop.sistemasCorrelativas.service.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de autenticación: login, logout y selección de carrera.
 * Gestiona la sesión HTTP almacenando el usuario logueado.
 */
@Controller
public class AuthController {

    private final IUsuarioService usuarioService;
    private final ICarreraService carreraService;

    public AuthController(IUsuarioService usuarioService, ICarreraService carreraService) {
        this.usuarioService = usuarioService;
        this.carreraService = carreraService;
    }

    /**
     * Muestra el formulario de login.
     * Si ya hay sesión activa, redirige directamente al plan.
     */
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        String vista;
        if (session.getAttribute("usuarioLogueado") != null) {
            vista = "redirect:/plan";
        } else {
            vista = "auth/login";
        }
        return vista;
    }

    /**
     * Procesa el formulario de login.
     * Autentica con el servicio existente y guarda el usuario en sesión.
     * Si el usuario es ALUMNO, lo redirige al selector de carrera.
     * Si es ADMIN, lo redirige directamente al panel de carreras.
     */
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String nombreUsuario,
                                @RequestParam String clave,
                                HttpSession session,
                                RedirectAttributes redirectAttrs) {
        String redireccion;
        try {
            Usuario usuario = usuarioService.autenticar(nombreUsuario, clave);
            session.setAttribute("usuarioLogueado", usuario);

            if (usuario.getRol() == Rol.ADMIN) {
                redireccion = "redirect:/carreras";
            } else {
                redireccion = "redirect:/selector-carrera";
            }
        } catch (AutenticacionException e) {
            redirectAttrs.addFlashAttribute("errorLogin", "Usuario o contraseña incorrectos.");
            redireccion = "redirect:/login";
        }
        return redireccion;
    }

    /**
     * Cierra la sesión del usuario y redirige al login.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * Muestra la pantalla de selección de Facultad y Carrera.
     * Solo accesible para ALUMNO. Carga todas las carreras para los selects.
     */
    @GetMapping("/selector-carrera")
    public String mostrarSelectorCarrera(HttpSession session, Model model) {
        String vista;
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            vista = "redirect:/login";
        } else if (usuario.getRol() == Rol.ADMIN) {
            vista = "redirect:/carreras";
        } else {
            model.addAttribute("carreras", carreraService.obtenerTodas());
            // Extraer instituciones únicas para el primer select (Facultad)
            model.addAttribute("instituciones",
                carreraService.obtenerTodas().stream()
                    .map(c -> c.getInstitucion())
                    .distinct()
                    .sorted()
                    .toList()
            );
            vista = "auth/selector-carrera";
        }
        return vista;
    }

    /**
     * Procesa la selección de carrera del alumno.
     * Guarda la carrera elegida en sesión y redirige al plan de estudios.
     */
    @PostMapping("/selector-carrera")
    public String procesarSelectorCarrera(@RequestParam(required = false) Long carreraId,
                                          HttpSession session,
                                          RedirectAttributes redirectAttrs) {
        String redireccion;
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redireccion = "redirect:/login";
        } else if (carreraId == null) {
            redirectAttrs.addFlashAttribute("errorSelector", "Por favor, seleccioná una carrera válida.");
            redireccion = "redirect:/selector-carrera";
        } else {
            try {
                var carrera = carreraService.obtenerPorId(carreraId);
                session.setAttribute("carreraSeleccionada", carrera);
                redireccion = "redirect:/plan";
            } catch (Exception e) {
                redirectAttrs.addFlashAttribute("errorSelector", "La carrera seleccionada no es válida.");
                redireccion = "redirect:/selector-carrera";
            }
        }
        return redireccion;
    }
}
