package com.zebop.sistemasCorrelativas.controllers;

import com.zebop.sistemasCorrelativas.models.Usuario;
import com.zebop.sistemasCorrelativas.exception.AutenticacionException;
import com.zebop.sistemasCorrelativas.services.ICarreraService;
import com.zebop.sistemasCorrelativas.services.IUsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de autenticación: login, logout y selección de carrera.
 * Acceso unificado: cualquier usuario autenticado ve las mismas secciones,
 * sin distinción de roles ni permisos.
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
     * Autentica con el servicio existente, guarda el usuario en sesión
     * y redirige siempre al plan de estudios (vista única para todos).
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
            redireccion = "redirect:/plan";
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
     * El filtrado de carreras por institución se resuelve en el servidor
     * (parámetro opcional "institucion"), sin JavaScript en el cliente:
     * el primer select se reenvía por GET y la página se repinta con
     * el segundo select ya filtrado.
     */
    @GetMapping("/selector-carrera")
    public String mostrarSelectorCarrera(@RequestParam(required = false) String institucion,
                                         HttpSession session, Model model) {
        String vista;
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            vista = "redirect:/login";
        } else {
            var carreras = carreraService.obtenerTodas();
            model.addAttribute("instituciones",
                carreras.stream().map(c -> c.getInstitucion()).distinct().sorted().toList());
            model.addAttribute("institucionSeleccionada", institucion);

            if (institucion != null && !institucion.isBlank()) {
                model.addAttribute("carrerasFiltradas",
                    carreras.stream().filter(c -> c.getInstitucion().equals(institucion)).toList());
            }
            vista = "auth/selector-carrera";
        }
        return vista;
    }

    /**
     * Procesa la selección de carrera.
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
