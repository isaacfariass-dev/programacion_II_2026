package com.zebop.sistemasCorrelativas.services;

import com.zebop.sistemasCorrelativas.models.Usuario;
import java.util.List;

public interface IUsuarioService {

    List<Usuario> obtenerTodos();

    Usuario obtenerPorId(Long id);

    Usuario obtenerPorNombreUsuario(String nombreUsuario);

    Usuario autenticar(String nombreUsuario, String clave);

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Long id, Usuario usuario);

    void eliminar(Long id);
}
