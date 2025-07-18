package com.example.demo.service;

import com.example.demo.model.Pagos;
import com.example.demo.repository.PagosRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException; // Para manejar casos donde no se encuentra un pago

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio para la gestión de la entidad Pagos.
 * Proporciona operaciones de negocio para crear, leer, actualizar y eliminar pagos.
 */
@Service
public class PagosService {

    private static final Logger logger = LoggerFactory.getLogger(PagosService.class);

    private final PagosRepository pagosRepository;

    /**
     * Constructor para inyectar la dependencia de PagosRepository.
     * @param pagosRepository Repositorio para operaciones CRUD de Pagos.
     */

    public PagosService(PagosRepository pagosRepository) {
        this.pagosRepository = pagosRepository;
    }

    /**
     * Guarda un nuevo pago en la base de datos.
     * @param pago El objeto Pagos a guardar.
     * @return El pago guardado.
     */
    public Pagos guardarPago(Pagos pago) {
        logger.info("Guardando pago en la base de datos. Monto: {}. Método: {}", pago.getMontoTotal(), pago.getMetodoPago());
        return pagosRepository.save(pago);
    }

    /**
     * Obtiene todos los pagos disponibles en la base de datos.
     * @return Una lista de objetos Pagos.
     */
    public List<Pagos> obtenerTodosLosPagos() {
        return pagosRepository.findAll();
    }

    /**
     * Obtiene un pago por su identificador único.
     * @param id El ID del pago a buscar.
     * @return El objeto Pagos si se encuentra.
     * @throws EntityNotFoundException Si no se encuentra ningún pago con el ID proporcionado.
     */
    public Pagos obtenerPagoPorId(Integer id) {
        logger.info("Buscando pago con ID: {}", id);
        return pagosRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago con ID " + id + " no encontrado."));
    }

    /**
     * Elimina un pago de la base de datos por su identificador único.
     * @param id El ID del pago a eliminar.
     */
    public void eliminarPago(Integer id) {
        logger.info("Intentando eliminar pago con ID: {}", id);
        if (!pagosRepository.existsById(id)) {
            logger.warn("No se puede eliminar. Pago con ID {} no encontrado.", id);
            throw new EntityNotFoundException("No se puede eliminar. Pago con ID " + id + " no encontrado.");
        }
        pagosRepository.deleteById(id);
        logger.info("Pago con ID {} eliminado exitosamente.", id);
    }

    /**
     * Actualiza un pago existente en la base de datos.
     * @param pago El objeto Pagos a actualizar. Debe contener un ID válido.
     * @return El pago actualizado.
     * @throws EntityNotFoundException Si el pago con el ID proporcionado no existe.
     */
    public Pagos actualizarPago(Pagos pago) {
        logger.info("Intentando actualizar pago con ID: {}", pago.getIdPago());
        if (pago.getIdPago() == null || !pagosRepository.existsById(pago.getIdPago())) {
            logger.warn("No se puede actualizar. Pago con ID {} no encontrado.", pago.getIdPago());
            throw new EntityNotFoundException("No se puede actualizar. Pago con ID " + pago.getIdPago() + " no encontrado.");
        }
        Pagos updatedPago = pagosRepository.save(pago);
        logger.info("Pago con ID {} actualizado exitosamente.", updatedPago.getIdPago());
        return updatedPago;
    }

    // Puedes añadir métodos personalizados si son necesarios, por ejemplo:
    // public List<Pagos> obtenerPagosPorUsuario(Integer usuarioId) {
    //     return pagosRepository.findByUsuarioId(usuarioId);
    // }
    // public List<Pagos> obtenerPagosPorRangoDeFechas(LocalDateTime startDate, LocalDateTime endDate) {
    //     return pagosRepository.findByFechaPagoBetween(startDate, endDate);
    // }
}
