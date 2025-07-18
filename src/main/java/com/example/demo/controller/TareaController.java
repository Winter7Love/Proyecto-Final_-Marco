package com.example.demo.controller;

import com.example.demo.model.Tarea;
import com.example.demo.model.Usuario;
import com.example.demo.service.TareaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils; // Importar StreamUtils para leer bytes de recursos

// Importaciones de iText para PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controlador que gestiona las operaciones relacionadas con las tareas de
 * inventario.
 * Proporciona endpoints para visualizar, añadir, editar y eliminar tareas,
 * y ahora también para generar y descargar reportes PDF.
 */
@Controller
@RequestMapping("/tareas")
public class TareaController {

    private static final Logger logger = LoggerFactory.getLogger(TareaController.class);

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    /**
     * Muestra la página principal de gestión de tareas de inventario.
     * Incluye el formulario para añadir nuevas tareas y la tabla con las tareas
     * existentes.
     *
     * @param usuario El objeto Usuario autenticado (inyectado por Spring Security).
     * @param model   El modelo de Spring MVC para pasar datos a la vista.
     * @return El nombre de la vista "inventario.html".
     */
    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    public String mostrarTareas(@AuthenticationPrincipal Usuario usuario, Model model) {
        logger.info("Accediendo a la gestión de tareas. Usuario: {}",
                usuario != null ? usuario.getNombreUsuario() : "No autenticado");

        model.addAttribute("usuario", usuario);
        model.addAttribute("tarea", new Tarea());

        List<Tarea> tareas = tareaService.obtenerTodasTareas();
        model.addAttribute("tareas", tareas);

        return "inventario";
    }

    /**
     * Procesa la adición o edición de una tarea enviada desde el formulario.
     *
     * @param tarea              El objeto Tarea con los datos del formulario.
     * @param redirectAttributes Para añadir mensajes flash después de la
     *                           redirección.
     * @return Redirecciona de vuelta a la página de gestión de tareas.
     */
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String agregarTarea(@ModelAttribute Tarea tarea, RedirectAttributes redirectAttributes) {
        if (tarea.getId() == null || tarea.getId() == 0) {
            Tarea savedTarea = tareaService.guardarTarea(tarea);
            logger.info("Nueva tarea agregada (ID: {}): {}", savedTarea.getId(), savedTarea.getTitulo());
            redirectAttributes.addFlashAttribute("mensaje",
                    "Tarea '" + savedTarea.getTitulo() + "' agregada exitosamente.");
        } else {
            Optional<Tarea> existingTareaOpt = tareaService.obtenerTareaPorId(tarea.getId());
            if (existingTareaOpt.isPresent()) {
                Tarea existingTarea = existingTareaOpt.get();
                existingTarea.setTitulo(tarea.getTitulo());
                existingTarea.setDescripcion(tarea.getDescripcion());
                existingTarea.setFecha(tarea.getFecha());
                existingTarea.setPrioridad(tarea.getPrioridad());
                existingTarea.setStock(tarea.getStock());
                tareaService.actualizarTarea(existingTarea);
                logger.info("Tarea actualizada (ID: {}): {}", existingTarea.getId(), existingTarea.getTitulo());
                redirectAttributes.addFlashAttribute("mensaje",
                        "Tarea '" + existingTarea.getTitulo() + "' actualizada exitosamente.");
            } else {
                logger.warn("Intento de actualizar tarea con ID no encontrado: {}", tarea.getId());
                redirectAttributes.addFlashAttribute("error",
                        "Error: Tarea con ID " + tarea.getId() + " no encontrada para actualizar.");
            }
        }
        return "redirect:/tareas/";
    }

    /**
     * Obtiene los datos de una tarea específica para pre-llenar el modal de
     * edición.
     *
     * @param id El ID de la tarea a editar.
     * @return Un objeto Tarea en formato JSON.
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public Tarea getTareaParaEditar(@PathVariable Long id) {
        logger.info("Solicitud para editar tarea con ID: {}", id);
        return tareaService.obtenerTareaPorId(id).orElse(null);
    }

    /**
     * Procesa la eliminación de una tarea.
     *
     * @param id                 El ID de la tarea a eliminar.
     * @param redirectAttributes Para añadir mensajes flash después de la
     *                           redirección.
     * @return Redirecciona de vuelta a la página de gestión de tareas.
     */
    @PostMapping("/confirmarEliminar")
    @PreAuthorize("isAuthenticated()")
    public String confirmarEliminarTarea(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Tarea> tareaToDeleteOpt = tareaService.obtenerTareaPorId(id);
        if (tareaToDeleteOpt.isPresent()) {
            tareaService.eliminarTarea(id);
            logger.info("Tarea con ID {} eliminada exitosamente.", id);
            redirectAttributes.addFlashAttribute("mensaje", "Tarea eliminada exitosamente.");
        } else {
            logger.warn("Intento de eliminar tarea con ID no encontrado: {}", id);
            redirectAttributes.addFlashAttribute("error",
                    "Error: Tarea con ID " + id + " no encontrada para eliminar.");
        }
        return "redirect:/tareas/";
    }

    /**
     * Genera y descarga un reporte de todas las tareas en formato PDF.
     * Solo accesible para usuarios autenticados (o roles específicos si se desea).
     *
     * @param usuario El objeto Usuario autenticado.
     * @return ResponseEntity con el archivo PDF para descargar.
     */
    @GetMapping("/reporte/descargar-pdf") // Nueva ruta para la descarga de PDF
    @PreAuthorize("isAuthenticated()") // Puedes cambiar a hasRole('ADMIN')
    public ResponseEntity<byte[]> descargarReportePdf(@AuthenticationPrincipal Usuario usuario) {
        logger.info("Generando reporte PDF de tareas para el usuario: {}", usuario.getNombreCompleto());

        List<Tarea> tareas = tareaService.obtenerTodasTareas();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Documento de tamaño A4 con márgenes
            Document document = new Document(PageSize.A4, 36, 36, 36, 36); // Márgenes de 0.5 pulgadas
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            // --- Definición de Fuentes y Colores ---
            BaseColor colorPrimary = new BaseColor(78, 115, 223); // Azul principal (#4e73df)
            BaseColor colorSecondary = new BaseColor(108, 117, 125); // Gris medio (#6c757d)
            BaseColor colorAccentGreen = new BaseColor(28, 200, 138); // Verde (#1cc88a)
            BaseColor colorAccentRed = new BaseColor(231, 74, 59); // Rojo (#e74a3b)
            BaseColor colorAccentYellow = new BaseColor(246, 194, 62); // Amarillo (#f6c23e)

            BaseColor tableHeaderBg = new BaseColor(248, 249, 250); // Gris muy claro para encabezados de tabla
                                                                    // (#f8f9fa)
            BaseColor tableRowEvenBg = new BaseColor(255, 255, 255); // Blanco para filas pares
            BaseColor tableRowOddBg = new BaseColor(248, 249, 250); // Gris muy claro para filas impares
            BaseColor tableBorderColor = new BaseColor(222, 226, 230); // Gris claro para bordes de tabla (#dee2e6)

            BaseColor textColorDark = new BaseColor(33, 37, 41); // Texto oscuro (#212529)
            BaseColor textColorMedium = new BaseColor(73, 80, 87); // Texto medio (#495057)

            // Fuentes
            Font fontMainTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 32, colorPrimary);
            Font fontReportTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, textColorDark);
            Font fontSectionTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, colorPrimary);
            Font fontInfoLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, textColorMedium);
            Font fontInfoValue = FontFactory.getFont(FontFactory.HELVETICA, 9, textColorDark);
            Font fontTableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, textColorDark);
            Font fontTableCell = FontFactory.getFont(FontFactory.HELVETICA, 8, textColorMedium);
            Font fontPriorityBadge = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE);
            Font fontFooter = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, colorSecondary);

            // --- Evento de Página para Pie de Página (Paginación) ---
            writer.setPageEvent(new com.itextpdf.text.pdf.PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    PdfContentByte cb = writer.getDirectContent();
                    Phrase footer = new Phrase(String.format("Página %d", writer.getPageNumber()), fontFooter);
                    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer,
                            document.right(), document.bottom() - 10, 0);
                }
            });

            document.open();

            // --- Contenido del Documento ---

            // Encabezado con Logos
            try {
                // Logo Izquierdo (LOGOIN.png)
                InputStream logoInStream = getClass().getClassLoader()
                        .getResourceAsStream("static/imagenes/LOGOIN.png");
                if (logoInStream != null) {
                    byte[] logoInBytes = StreamUtils.copyToByteArray(logoInStream);
                    Image logoIn = Image.getInstance(logoInBytes);
                    logoIn.scaleToFit(100, 60); // Ajustar tamaño
                    logoIn.setAbsolutePosition(document.left(), document.top() - logoIn.getScaledHeight() - 10); // Posición
                                                                                                                 // absoluta
                    document.add(logoIn);
                } else {
                    logger.warn("LOGOIN.png no encontrado en classpath.");
                }

                // Logo Derecho (head.png)
                InputStream headPngStream = getClass().getClassLoader().getResourceAsStream("static/imagenes/head.png");
                if (headPngStream != null) {
                    byte[] headPngBytes = StreamUtils.copyToByteArray(headPngStream);
                    Image headPng = Image.getInstance(headPngBytes);
                    headPng.scaleToFit(110, 110); // Ajustar tamaño
                    headPng.setAbsolutePosition(document.right() - headPng.getScaledWidth(),
                            document.top() - headPng.getScaledHeight() - 10); // Posición absoluta
                    document.add(headPng);
                } else {
                    logger.warn("head.png no encontrado en classpath.");
                }

            } catch (IOException e) {
                logger.error("Error al cargar imágenes para el PDF: {}", e.getMessage(), e);
            }

            // Espacio después de los logos
            document.add(new Paragraph("\n\n\n")); // Ajusta el espacio según el tamaño de tus logos

            // Título principal del reporte
            Paragraph mainTitle = new Paragraph("INMY STORE", fontMainTitle);
            mainTitle.setAlignment(Paragraph.ALIGN_CENTER);
            mainTitle.setSpacingAfter(10);
            document.add(mainTitle);

            Paragraph reportTitle = new Paragraph("Reporte de Inventario de Periféricos", fontReportTitle);
            reportTitle.setAlignment(Paragraph.ALIGN_CENTER);
            reportTitle.setSpacingAfter(25);
            document.add(reportTitle);

            // Sección de Información del Reporte
            Paragraph infoSectionTitle = new Paragraph("Información del Reporte", fontSectionTitle);
            infoSectionTitle.setSpacingAfter(10);
            document.add(infoSectionTitle);

            PdfPTable infoTable = new PdfPTable(2); // Dos columnas para etiquetas y valores
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20f);
            infoTable.getDefaultCell().setBorder(0); // Sin bordes para esta tabla de información
            infoTable.getDefaultCell().setPadding(3);

            infoTable.addCell(createInfoCell("Fecha del Reporte:", fontInfoLabel, Element.ALIGN_LEFT));
            infoTable.addCell(createInfoCell(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(java.time.LocalDateTime.now()),
                    fontInfoValue, Element.ALIGN_RIGHT));

            infoTable.addCell(createInfoCell("Total de Tareas:", fontInfoLabel, Element.ALIGN_LEFT));
            infoTable.addCell(createInfoCell(String.valueOf(tareas.size()), fontInfoValue, Element.ALIGN_RIGHT));

            infoTable.addCell(createInfoCell("Generado por:", fontInfoLabel, Element.ALIGN_LEFT));
            infoTable.addCell(createInfoCell(usuario.getNombreCompleto(), fontInfoValue, Element.ALIGN_RIGHT));

            document.add(infoTable);

            // Sección de Listado de Tareas
            Paragraph taskListTitle = new Paragraph("Listado Detallado de Tareas", fontSectionTitle);
            taskListTitle.setSpacingAfter(15);
            document.add(taskListTitle);

            // Tabla de tareas
            PdfPTable table = new PdfPTable(new float[] { 0.5f, 1.5f, 2.5f, 1f, 1f, 0.8f }); // Anchos relativos de
                                                                                             // columnas
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.getDefaultCell().setBorderColor(tableBorderColor); // Color de borde de celda

            // Cabeceras de la tabla
            table.addCell(createStyledCell("ID", fontTableHeader, tableHeaderBg, Element.ALIGN_CENTER, 10f));
            table.addCell(createStyledCell("Título", fontTableHeader, tableHeaderBg, Element.ALIGN_LEFT, 10f));
            table.addCell(createStyledCell("Descripción", fontTableHeader, tableHeaderBg, Element.ALIGN_LEFT, 10f));
            table.addCell(createStyledCell("Fecha", fontTableHeader, tableHeaderBg, Element.ALIGN_CENTER, 10f));
            table.addCell(createStyledCell("Prioridad", fontTableHeader, tableHeaderBg, Element.ALIGN_CENTER, 10f));
            table.addCell(createStyledCell("Stock", fontTableHeader, tableHeaderBg, Element.ALIGN_CENTER, 10f));

            // Datos de la tabla
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            boolean isEvenRow = false; // Para alternar colores de fila

            for (Tarea tarea : tareas) {
                BaseColor rowBgColor = isEvenRow ? tableRowEvenBg : tableRowOddBg;

                // ID
                table.addCell(createStyledCell(String.valueOf(tarea.getId()), fontTableCell, rowBgColor,
                        Element.ALIGN_CENTER, 8f));
                // Título
                table.addCell(createStyledCell(tarea.getTitulo(), fontTableCell, rowBgColor, Element.ALIGN_LEFT, 8f));
                // Descripción
                table.addCell(
                        createStyledCell(tarea.getDescripcion(), fontTableCell, rowBgColor, Element.ALIGN_LEFT, 8f));
                // Fecha
                table.addCell(createStyledCell(tarea.getFecha().format(dateFormatter), fontTableCell, rowBgColor,
                        Element.ALIGN_CENTER, 8f));
                // Prioridad (con color de fondo tipo "badge")
                BaseColor priorityBg = null;
                BaseColor priorityText = BaseColor.WHITE; // Texto blanco por defecto para badges
                if ("Alta".equalsIgnoreCase(tarea.getPrioridad())) {
                    priorityBg = colorAccentRed;
                } else if ("Media".equalsIgnoreCase(tarea.getPrioridad())) {
                    priorityBg = colorAccentYellow;
                    priorityText = BaseColor.BLACK; // Texto negro para amarillo
                } else if ("Baja".equalsIgnoreCase(tarea.getPrioridad())) {
                    priorityBg = colorAccentGreen;
                }
                Font currentPriorityFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, priorityText);
                table.addCell(createStyledCell(tarea.getPrioridad(), currentPriorityFont, priorityBg,
                        Element.ALIGN_CENTER, 8f));
                // Stock
                table.addCell(createStyledCell(String.valueOf(tarea.getStock()), fontTableCell, rowBgColor,
                        Element.ALIGN_CENTER, 8f));

                isEvenRow = !isEvenRow; // Alternar para la siguiente fila
            }

            document.add(table);

            document.close();

        } catch (Exception e) {
            logger.error("Error al generar el reporte PDF de tareas: {}", e.getMessage(), e);
            return new ResponseEntity<>(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Configurar las cabeceras HTTP para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_inventario_INMY.pdf"); // Nombre de archivo más
                                                                                            // descriptivo
        headers.setContentLength(baos.size());

        logger.info("Reporte PDF de tareas generado exitosamente. Tamaño: {} bytes", baos.size());
        return new ResponseEntity<>(baos.toByteArray(), headers, org.springframework.http.HttpStatus.OK);
    }

    // Método auxiliar para crear celdas con estilo para la tabla PDF
    private com.itextpdf.text.pdf.PdfPCell createStyledCell(String content, Font font, BaseColor backgroundColor,
            int alignment, float padding) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(padding);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(new BaseColor(222, 226, 230)); // Color de borde de celda más suave

        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }
        return cell;
    }

    // Método auxiliar para crear celdas de información sin bordes
    private com.itextpdf.text.pdf.PdfPCell createInfoCell(String content, Font font, int alignment) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new Phrase(content, font));
        cell.setBorder(0); // Sin borde
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(2); // Padding mínimo
        return cell;
    }
}
