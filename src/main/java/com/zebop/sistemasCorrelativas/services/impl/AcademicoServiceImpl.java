package com.zebop.sistemasCorrelativas.services.impl;

import com.zebop.sistemasCorrelativas.dto.EstadoVisualMateria;
import com.zebop.sistemasCorrelativas.dto.MateriaEstadoDTO;
import com.zebop.sistemasCorrelativas.dto.PlanEstudioDTO;
import com.zebop.sistemasCorrelativas.dto.RequisitoInfoDTO;
import com.zebop.sistemasCorrelativas.dto.SimulacionResultadoDTO;
import com.zebop.sistemasCorrelativas.models.Alumno;
import com.zebop.sistemasCorrelativas.models.Carrera;
import com.zebop.sistemasCorrelativas.models.Correlatividad;
import com.zebop.sistemasCorrelativas.models.HistorialAcademico;
import com.zebop.sistemasCorrelativas.models.Materia;
import com.zebop.sistemasCorrelativas.models.enums.EstadoMateria;
import com.zebop.sistemasCorrelativas.models.enums.TipoRequisito;
import com.zebop.sistemasCorrelativas.exception.RecursoNoEncontradoException;
import com.zebop.sistemasCorrelativas.exception.ReglaNegocioException;
import com.zebop.sistemasCorrelativas.repositories.AlumnoRepository;
import com.zebop.sistemasCorrelativas.repositories.CorrelatividadRepository;
import com.zebop.sistemasCorrelativas.repositories.HistorialAcademicoRepository;
import com.zebop.sistemasCorrelativas.repositories.MateriaRepository;
import com.zebop.sistemasCorrelativas.services.IAcademicoService;
import com.zebop.sistemasCorrelativas.util.Constantes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del motor académico de SICRA.
 * Resuelve el cálculo de correlatividades directas, habilitación de materias,
 * actualización de historial y simulación de impacto.
 */
@Service
public class AcademicoServiceImpl implements IAcademicoService {

    private final AlumnoRepository alumnoRepository;
    private final MateriaRepository materiaRepository;
    private final HistorialAcademicoRepository historialRepository;
    private final CorrelatividadRepository correlatividadRepository;

    public AcademicoServiceImpl(AlumnoRepository alumnoRepository,
                                MateriaRepository materiaRepository,
                                HistorialAcademicoRepository historialRepository,
                                CorrelatividadRepository correlatividadRepository) {
        this.alumnoRepository = alumnoRepository;
        this.materiaRepository = materiaRepository;
        this.historialRepository = historialRepository;
        this.correlatividadRepository = correlatividadRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PlanEstudioDTO obtenerPlanEstudio(Long idAlumno) {
        PlanEstudioDTO plan = new PlanEstudioDTO();

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_ALUMNO_NO_ENCONTRADA));

        Carrera carrera = alumno.getCarrera();
        plan.setAlumno(alumno);
        plan.setCarrera(carrera);

        List<Materia> materias = materiaRepository.findByCarrera_IdCarreraOrderByAnioPlanAscCodigoAsc(carrera.getIdCarrera());
        List<HistorialAcademico> historiales = historialRepository.findByAlumno_IdAlumno(idAlumno);

        Map<Long, HistorialAcademico> historialMap = new HashMap<>();
        for (HistorialAcademico h : historiales) {
            historialMap.put(h.getMateria().getIdMateria(), h);
        }

        Map<Integer, List<MateriaEstadoDTO>> materiasPorAnio = new LinkedHashMap<>();
        int aprobadas = 0;
        int regulares = 0;
        int habilitadas = 0;
        int bloqueadas = 0;
        int desaprobadas = 0;
        double sumaNotas = 0.0;
        int cantidadNotas = 0;

        for (Materia m : materias) {
            MateriaEstadoDTO dto = evaluarEstadoMateria(m, historialMap);

            if (dto.getNotaFinal() != null) {
                sumaNotas += dto.getNotaFinal();
                cantidadNotas++;
            }

            switch (dto.getEstadoVisual()) {
                case APROBADA -> aprobadas++;
                case REGULAR -> regulares++;
                case HABILITADA -> habilitadas++;
                case BLOQUEADA -> bloqueadas++;
                case DESAPROBADA -> desaprobadas++;
            }

            Integer anio = m.getAnioPlan();
            materiasPorAnio.computeIfAbsent(anio, k -> new ArrayList<>()).add(dto);
        }

        plan.setMateriasPorAnio(materiasPorAnio);
        plan.setTotalMaterias(materias.size());
        plan.setMateriasAprobadas(aprobadas);
        plan.setMateriasRegulares(regulares);
        plan.setMateriasHabilitadas(habilitadas);
        plan.setMateriasBloqueadas(bloqueadas);
        plan.setMateriasDesaprobadas(desaprobadas);

        double promedio = (cantidadNotas > 0) ? (Math.round((sumaNotas / cantidadNotas) * 100.0) / 100.0) : 0.0;
        plan.setPromedioNotas(promedio);

        double porcentaje = (materias.size() > 0) ? (Math.round(((double) aprobadas / materias.size()) * 1000.0) / 10.0) : 0.0;
        plan.setPorcentajeAvance(porcentaje);

        return plan;
    }

    @Override
    @Transactional
    public void actualizarEstadoMateria(Long idAlumno, Long idMateria, EstadoMateria nuevoEstado, Double notaFinal) {
        if (notaFinal != null && (notaFinal < Constantes.NOTA_MINIMA || notaFinal > Constantes.NOTA_MAXIMA)) {
            throw new ReglaNegocioException("La nota debe estar entre " + Constantes.NOTA_MINIMA + " y " + Constantes.NOTA_MAXIMA);
        }

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_ALUMNO_NO_ENCONTRADA));

        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_MATERIA_NO_ENCONTRADA));

        Optional<HistorialAcademico> historialOpt = historialRepository.findByAlumno_IdAlumnoAndMateria_IdMateria(idAlumno, idMateria);

        HistorialAcademico registro;
        if (historialOpt.isPresent()) {
            registro = historialOpt.get();
        } else {
            registro = new HistorialAcademico();
            registro.setAlumno(alumno);
            registro.setMateria(materia);
        }

        registro.setEstado(nuevoEstado);
        registro.setNotaFinal(notaFinal);
        historialRepository.save(registro);
    }

    @Override
    @Transactional
    public void resetearEstadoMateria(Long idAlumno, Long idMateria) {
        Optional<HistorialAcademico> historialOpt = historialRepository.findByAlumno_IdAlumnoAndMateria_IdMateria(idAlumno, idMateria);
        if (historialOpt.isPresent()) {
            historialRepository.delete(historialOpt.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SimulacionResultadoDTO simularAprobacion(Long idAlumno, Long idMateria) {
        SimulacionResultadoDTO resultado = new SimulacionResultadoDTO();

        Materia materiaSimulada = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new RecursoNoEncontradoException(Constantes.ERROR_MATERIA_NO_ENCONTRADA));
        resultado.setMateriaSimulada(materiaSimulada);

        List<Correlatividad> correlativasQueHabilita = correlatividadRepository.findByIdMateriaRequisito(idMateria);
        List<Materia> dependientesDirectas = new ArrayList<>();
        for (Correlatividad c : correlativasQueHabilita) {
            dependientesDirectas.add(c.getMateriaDestino());
        }
        resultado.setMateriasQueDependenDirectamente(dependientesDirectas);

        List<HistorialAcademico> historiales = historialRepository.findByAlumno_IdAlumno(idAlumno);
        Map<Long, HistorialAcademico> historialSimulado = new HashMap<>();
        for (HistorialAcademico h : historiales) {
            historialSimulado.put(h.getMateria().getIdMateria(), h);
        }

        HistorialAcademico simulado = new HistorialAcademico();
        simulado.setMateria(materiaSimulada);
        simulado.setEstado(EstadoMateria.APROBADA);
        historialSimulado.put(idMateria, simulado);

        List<Materia> recienDesbloqueadas = new ArrayList<>();
        for (Materia dependiente : dependientesDirectas) {
            HistorialAcademico actual = historialSimulado.get(dependiente.getIdMateria());
            boolean yaAprobada = (actual != null && actual.getEstado() == EstadoMateria.APROBADA);

            if (!yaAprobada) {
                boolean habilitadaPrevia = verificarHabilitacion(dependiente.getIdMateria(), historialesToMap(historiales));
                boolean habilitadaConSimulacion = verificarHabilitacion(dependiente.getIdMateria(), historialSimulado);

                if (!habilitadaPrevia && habilitadaConSimulacion) {
                    recienDesbloqueadas.add(dependiente);
                }
            }
        }

        resultado.setMateriasDesbloqueadas(recienDesbloqueadas);
        resultado.setCantidadNuevasHabilitadas(recienDesbloqueadas.size());

        return resultado;
    }

    /**
     * Evalúa el estado visual de una materia y arma la lista de requisitos y materias que habilita.
     */
    private MateriaEstadoDTO evaluarEstadoMateria(Materia materia, Map<Long, HistorialAcademico> historialMap) {
        MateriaEstadoDTO dto = new MateriaEstadoDTO();
        dto.setMateria(materia);

        HistorialAcademico hist = historialMap.get(materia.getIdMateria());
        if (hist != null) {
            dto.setNotaFinal(hist.getNotaFinal());
            switch (hist.getEstado()) {
                case APROBADA -> dto.setEstadoVisual(EstadoVisualMateria.APROBADA);
                case REGULAR, CURSANDO -> dto.setEstadoVisual(EstadoVisualMateria.REGULAR);
                case DESAPROBADA -> dto.setEstadoVisual(EstadoVisualMateria.DESAPROBADA);
            }
        }

        List<Correlatividad> requisitos = correlatividadRepository.findByIdMateriaDestino(materia.getIdMateria());
        List<RequisitoInfoDTO> reqList = new ArrayList<>();
        boolean cumpleTodos = true;

        for (Correlatividad c : requisitos) {
            Materia reqMateria = c.getMateriaRequisito();
            HistorialAcademico reqHist = historialMap.get(c.getIdMateriaRequisito());

            boolean cumplido;
            String estadoTexto;

            if (reqHist == null) {
                cumplido = false;
                estadoTexto = "No cursada";
            } else if (c.getTipoRequisito() == TipoRequisito.REGULAR) {
                cumplido = (reqHist.getEstado() == EstadoMateria.REGULAR || reqHist.getEstado() == EstadoMateria.APROBADA);
                estadoTexto = reqHist.getEstado().name();
            } else {
                cumplido = (reqHist.getEstado() == EstadoMateria.APROBADA);
                estadoTexto = reqHist.getEstado().name();
            }

            if (!cumplido) {
                cumpleTodos = false;
            }

            reqList.add(new RequisitoInfoDTO(
                    reqMateria.getIdMateria(),
                    reqMateria.getCodigo(),
                    reqMateria.getNombre(),
                    c.getTipoRequisito(),
                    cumplido,
                    estadoTexto
            ));
        }

        dto.setRequisitosPrevios(reqList);

        if (dto.getEstadoVisual() == null) {
            if (cumpleTodos) {
                dto.setEstadoVisual(EstadoVisualMateria.HABILITADA);
            } else {
                dto.setEstadoVisual(EstadoVisualMateria.BLOQUEADA);
            }
        }

        List<Correlatividad> habilitadas = correlatividadRepository.findByIdMateriaRequisito(materia.getIdMateria());
        List<Materia> posteriores = new ArrayList<>();
        for (Correlatividad c : habilitadas) {
            posteriores.add(c.getMateriaDestino());
        }
        dto.setMateriasQueHabilita(posteriores);

        return dto;
    }

    private boolean verificarHabilitacion(Long idMateriaDestino, Map<Long, HistorialAcademico> historialMap) {
        boolean habilitada = true;
        List<Correlatividad> requisitos = correlatividadRepository.findByIdMateriaDestino(idMateriaDestino);

        for (Correlatividad c : requisitos) {
            HistorialAcademico h = historialMap.get(c.getIdMateriaRequisito());
            boolean requisitoCumplido;

            if (h == null) {
                requisitoCumplido = false;
            } else if (c.getTipoRequisito() == TipoRequisito.REGULAR) {
                requisitoCumplido = (h.getEstado() == EstadoMateria.REGULAR || h.getEstado() == EstadoMateria.APROBADA);
            } else {
                requisitoCumplido = (h.getEstado() == EstadoMateria.APROBADA);
            }

            if (!requisitoCumplido) {
                habilitada = false;
                break;
            }
        }

        return habilitada;
    }

    private Map<Long, HistorialAcademico> historialesToMap(List<HistorialAcademico> lista) {
        Map<Long, HistorialAcademico> mapa = new HashMap<>();
        for (HistorialAcademico h : lista) {
            mapa.put(h.getMateria().getIdMateria(), h);
        }
        return mapa;
    }
}
