package com.zebop.sistemasCorrelativas.repository;

import com.zebop.sistemasCorrelativas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
