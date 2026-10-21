package com.zebop.sistemasCorrelativas.service.impl;

import com.zebop.sistemasCorrelativas.entity.Usuario;
import com.zebop.sistemasCorrelativas.exception.AutenticacionException;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.repository.UsuarioRepository;
import com.zebop.sistemasCorrelativas.service.IUsuarioService;
import com.zebop.sistemasCorrelativas.util.Constantes;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = usuarioRepository.findAll();
        return lista;
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_USUARIO_NO_ENCONTRADO));
        return usuario;
    }

    @Override
    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_USUARIO_NO_ENCONTRADO));
        return usuario;
    }

    @Override
    public Usuario autenticar(String nombreUsuario, String clave) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new AutenticacionException(Constantes.ERROR_CREDENCIALES));

        if (!usuario.getClave().equals(clave)) {
            throw new AutenticacionException(Constantes.ERROR_CREDENCIALES);
        }

        return usuario;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario.getNombreUsuario() != null && usuarioRepository.existsByNombreUsuario(usuario.getNombreUsuario())) {
            throw new ReglaNegocioException(Constantes.ERROR_USUARIO_DUPLICADO);
        }
        Usuario guardado = usuarioRepository.save(usuario);
        return guardado;
    }

    @Override
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario existente = obtenerPorId(id);

        if (usuarioActualizado.getNombreUsuario() != null && !usuarioActualizado.getNombreUsuario().equals(existente.getNombreUsuario())) {
            Optional<Usuario> conMismoNombre = usuarioRepository.findByNombreUsuario(usuarioActualizado.getNombreUsuario());
            if (conMismoNombre.isPresent() && !conMismoNombre.get().getIdUsuario().equals(id)) {
                throw new ReglaNegocioException(Constantes.ERROR_USUARIO_DUPLICADO);
            }
        }

        existente.setNombreUsuario(usuarioActualizado.getNombreUsuario());
        if (usuarioActualizado.getClave() != null && !usuarioActualizado.getClave().trim().isEmpty()) {
            existente.setClave(usuarioActualizado.getClave());
        }
        existente.setRol(usuarioActualizado.getRol());
        existente.setAlumno(usuarioActualizado.getAlumno());

        Usuario resultado = usuarioRepository.save(existente);
        return resultado;
    }

    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(Constantes.ERROR_USUARIO_NO_ENCONTRADO);
        }
        usuarioRepository.deleteById(id);
    }
}
