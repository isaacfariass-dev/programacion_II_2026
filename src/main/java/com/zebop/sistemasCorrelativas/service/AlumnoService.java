package java.com.zebop.sistemasCorrelativas.service;
import java.com.zebop.sistemasCorrelativas.entity.Alumno;
import java.com.zebop.sistemasCorrelativas.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class AlumnoService {
@Autowired
private AlumnoRepository aluRepo;
public List<Alumno> obtenerAlumnos() {
return aluRepo.findAll();
}
}