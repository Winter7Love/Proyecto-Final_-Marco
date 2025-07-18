package com.example.demo.controller;

import com.example.demo.model.Producto;
import com.example.demo.model.Usuario; // Necesario para @AuthenticationPrincipal
import com.example.demo.service.ProductoService;
import org.springframework.security.access.prepost.PreAuthorize; // Para protección de rutas
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Para obtener el usuario autenticado
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controlador que gestiona la visualización de productos por categorías.
 * Cada método mapea a una categoría específica y carga los productos correspondientes.
 * Requiere que el usuario esté autenticado para acceder a estas vistas.
 */
@Controller
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductoService productoService;

    public ProductController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Muestra todos los productos disponibles. Este es un endpoint general
     * para ver el catálogo completo de productos.
     * @param model El modelo para pasar la lista de productos.
     * @param usuario El usuario autenticado.
     * @return El nombre de la vista para listar todos los productos (ej. "productos_lista").
     */
    @GetMapping("/productos")
    @PreAuthorize("isAuthenticated()")
    public String listarTodosLosProductos(Model model, @AuthenticationPrincipal Usuario usuario) {
        List<Producto> productos = productoService.listarProductos();
        model.addAttribute("productos", productos);
        model.addAttribute("usuario", usuario); // Pasar el usuario para la navbar
        logger.info("Usuario {} viendo todos los productos. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "productos_lista"; // Asume que tienes una vista productos_lista.html
    }

    @GetMapping("/accesorios")
    @PreAuthorize("isAuthenticated()")
    public String listarAccesorios(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario); // Pasar el usuario para la navbar
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Accesorios");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo accesorios. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "accesorios"; // Asume que tienes un archivo accesorios.html
    }

    @GetMapping("/audifonos")
    @PreAuthorize("isAuthenticated()")
    public String listarAudifonos(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Audifono");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo audífonos. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "audifonos"; // Asume que tienes un archivo audifonos.html
    }

    @GetMapping("/adaptadores")
    @PreAuthorize("isAuthenticated()")
    public String listarAdaptadores(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Adaptadores y Conectores");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo adaptadores. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "adaptadores"; // Asume que tienes un archivo adaptadores.html
    }

    @GetMapping("/almacenamiento")
    @PreAuthorize("isAuthenticated()")
    public String listarAlmacenamiento(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Almacenamiento");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo almacenamiento. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "almacenamiento"; // Asume que tienes un archivo almacenamiento.html
    }

    @GetMapping("/cables")
    @PreAuthorize("isAuthenticated()")
    public String listarCables(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Cables");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo cables. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "cables"; // Asume que tienes un archivo cables.html
    }

    @GetMapping("/camaras")
    @PreAuthorize("isAuthenticated()")
    public String listarCamaras(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Camaras web");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo cámaras web. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "camaras"; // Asume que tienes un archivo camaras.html
    }

    @GetMapping("/combosYkits")
    @PreAuthorize("isAuthenticated()")
    public String listarCombos(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Combos y Kits");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo combos y kits. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "combosYkits"; // Asume que tienes un archivo combosYkits.html
    }

    @GetMapping("/cooler")
    @PreAuthorize("isAuthenticated()")
    public String listarCoolers(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Cooler de Laptop");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo coolers de laptop. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "cooler"; // Asume que tienes un archivo cooler.html
    }

    @GetMapping("/estabilizadores")
    @PreAuthorize("isAuthenticated()")
    public String listarEstabilizadores(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Estabilizadores");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo estabilizadores. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "estabilizadores"; // Asume que tienes un archivo estabilizadores.html
    }

    @GetMapping("/mouseGaming")
    @PreAuthorize("isAuthenticated()")
    public String listarMouseGaming(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Mouse Gaming");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo mouse gaming. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "mouseGaming"; // Asume que tienes un archivo mouseGaming.html
    }

    @GetMapping("/mouseOficina")
    @PreAuthorize("isAuthenticated()")
    public String listarMouseOficina(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Mouse Oficina");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo mouse de oficina. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "mouseOficina"; // Asume que tienes un archivo mouseOficina.html
    }

    @GetMapping("/accesoriosMouse")
    @PreAuthorize("isAuthenticated()")
    public String listarAccesoriosMouse(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Accesorios de Mouse");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo accesorios de mouse. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "accesoriosMouse"; // Asume que tienes un archivo accesoriosMouse.html
    }

    @GetMapping("/tecladosGaming")
    @PreAuthorize("isAuthenticated()")
    public String listarTecladosGaming(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Teclados Gaming");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo teclados gaming. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "tecladosGaming"; // Asume que tienes un archivo tecladosGaming.html
    }

    @GetMapping("/tecladosOficina")
    @PreAuthorize("isAuthenticated()")
    public String listarTecladosOficina(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Teclados Oficina");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo teclados de oficina. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "tecladosOficina"; // Asume que tienes un archivo tecladosOficina.html
    }

    @GetMapping("/keycaps")
    @PreAuthorize("isAuthenticated()")
    public String listarKeycaps(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Keycaps");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo keycaps. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "keycaps"; // Asume que tienes un archivo keycaps.html
    }

    @GetMapping("/switches")
    @PreAuthorize("isAuthenticated()")
    public String listarSwitches(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Switches");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo switches. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "switches"; // Asume que tienes un archivo switches.html
    }

    @GetMapping("/accesoriosTeclado")
    @PreAuthorize("isAuthenticated()")
    public String listarAccesoriosTeclado(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Accesorios de Teclado");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo accesorios de teclado. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "accesoriosTeclado"; // Asume que tienes un archivo accesoriosTeclado.html
    }

    @GetMapping("/microfonos")
    @PreAuthorize("isAuthenticated()")
    public String listarMicrofonos(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Microfonos");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo micrófonos. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "microfonos"; // Asume que tienes un archivo microfonos.html
    }

    @GetMapping("/brazoDeMicrofono")
    @PreAuthorize("isAuthenticated()")
    public String listarBrazoDeMicrofono(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Brazo de microfono");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo brazos de micrófono. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "brazoDeMicrofono"; // Asume que tienes un archivo brazoDeMicrofono.html
    }

    @GetMapping("/streaming")
    @PreAuthorize("isAuthenticated()")
    public String listarStreaming(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Streaming");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo productos de streaming. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "streaming"; // Asume que tienes un archivo streaming.html
    }

    @GetMapping("/liquidacion")
    @PreAuthorize("isAuthenticated()")
    public String listarLiquidacion(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Liquidación");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo productos en liquidación. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "liquidacion"; // Asume que tienes un archivo liquidacion.html
    }

    @GetMapping("/sillasGamer")
    @PreAuthorize("isAuthenticated()")
    public String listarSillasGamer(Model model, @AuthenticationPrincipal Usuario usuario) {
        model.addAttribute("usuario", usuario);
        List<Producto> productos = productoService.obtenerProductosPorCategoria("Sillas Gamer");
        model.addAttribute("productos", productos);
        logger.info("Usuario {} viendo sillas gamer. Total: {}", usuario.getNombreUsuario(), productos.size());
        return "sillasGamer"; // Asume que tienes un archivo sillasGamer.html
    }
}
