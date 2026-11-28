package es.udc.paproject.backend.test.rendimiento;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import etm.core.aggregation.Aggregate;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;

/**
 * Acceso a las medidas que JETM recoge en los puntos instrumentados del código
 * de producción (EtmPoint en CarteleraServiceImpl y CompraServiceImpl).
 * Los tiempos de JETM se expresan en milisegundos.
 */
public final class MedicionJetm {

    private static final EtmMonitor MONITOR = EtmManager.getEtmMonitor();
    private static final Path DIRECTORIO_INFORMES = Path.of("target", "jetm");

    private MedicionJetm() {
    }

    /** Monitor anidado: cada punto interno queda como hijo del punto que lo contiene. */
    public static void iniciar() {
        BasicEtmConfigurator.configure(true);
        MONITOR.start();
    }

    public static void detener() {
        MONITOR.stop();
    }

    /** Descarta las medidas acumuladas, por ejemplo las del calentamiento. */
    public static void reiniciar() {
        // El agregador guarda medidas en un búfer; se vuelcan antes de descartarlas.
        MONITOR.aggregate();
        MONITOR.reset();
    }

    /** Agregado de un punto de medida, buscado también entre los puntos hijos. */
    public static Aggregate agregado(String nombre) {
        Aggregate encontrado = buscar(puntos(), nombre);
        if (encontrado == null) {
            throw new IllegalStateException("Sin medidas para " + nombre);
        }
        return encontrado;
    }

    public static boolean tieneMedidas(String nombre) {
        return buscar(puntos(), nombre) != null;
    }

    /** Añade el informe de JETM a target/jetm/&lt;fichero&gt;.txt con un título. */
    public static void guardarInforme(String fichero, String titulo) {
        MONITOR.aggregate();
        try {
            Files.createDirectories(DIRECTORIO_INFORMES);
            try (Writer writer = new FileWriter(DIRECTORIO_INFORMES.resolve(fichero + ".txt").toFile(), true)) {
                writer.write(System.lineSeparator() + "== " + titulo + System.lineSeparator());
                MONITOR.render(new SimpleTextRenderer(writer, Locale.ROOT));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Aggregate> puntos() {
        MONITOR.aggregate();
        Map<String, Aggregate> puntos = new HashMap<>();
        MONITOR.render(mapa -> puntos.putAll(mapa));
        return puntos;
    }

    @SuppressWarnings("unchecked")
    private static Aggregate buscar(Map<String, Aggregate> puntos, String nombre) {
        for (Aggregate aggregate : puntos.values()) {
            if (aggregate.getName().equals(nombre)) {
                return aggregate;
            }
            if (aggregate.hasChilds()) {
                Aggregate hijo = buscar(aggregate.getChilds(), nombre);
                if (hijo != null) {
                    return hijo;
                }
            }
        }
        return null;
    }
}
