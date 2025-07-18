package com.example.demo.service;

import com.example.demo.model.Tarea; // Usamos 'Tarea' (singular)
import com.example.demo.repository.TareaRepository; // Usamos 'TareaRepository' (singular)
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de la entidad Tarea.
 * Proporciona operaciones de negocio para crear, leer, actualizar y eliminar tareas.
 */
@Service
public class TareaService { // Cambiado de TareasService a TareaService

    private final TareaRepository tareaRepository; // Cambiado de tareasRepository a tareaRepository

    /**
     * Constructor para inyectar la dependencia de TareaRepository.
     * @param tareaRepository Repositorio para operaciones CRUD de Tarea.
     */
    //@Autowired // @Autowired en el constructor es una buena práctica
    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    /**
     * Obtiene todas las tareas disponibles en la base de datos.
     * @return Una lista de objetos Tarea.
     */
    public List<Tarea> obtenerTodasTareas() {
        return tareaRepository.findAll(); // Obtiene todas las tareas de la base de datos
    }

    /**
     * Guarda una nueva tarea o actualiza una existente en la base de datos.
     * @param tarea El objeto Tarea a guardar o actualizar.
     * @return La tarea guardada/actualizada.
     */
    public Tarea guardarTarea(Tarea tarea) {
        return tareaRepository.save(tarea); // Guarda la tarea en la base de datos
    }

    /**
     * Obtiene una tarea por su identificador único.
     * @param id El ID de la tarea a buscar.
     * @return Un Optional que contiene la Tarea si se encuentra, o vacío si no.
     */
    public Optional<Tarea> obtenerTareaPorId(Long id) {
        return tareaRepository.findById(id); // Retorna la tarea encontrada por su id
    }

    /**
     * Elimina una tarea de la base de datos por su identificador único.
     * @param id El ID de la tarea a eliminar.
     */
    public void eliminarTarea(Long id) {
        tareaRepository.deleteById(id); // Elimina la tarea de la base de datos por su id
    }

    /**
     * Actualiza una tarea existente en la base de datos.
     * Nota: Este método es idéntico a `guardarTarea` cuando el objeto `tarea` ya tiene un ID.
     * @param tarea El objeto Tarea a actualizar.
     * @return La tarea actualizada.
     */
    public Tarea actualizarTarea(Tarea tarea) {
        return tareaRepository.save(tarea); // Guarda (actualiza) la tarea
    }
}
