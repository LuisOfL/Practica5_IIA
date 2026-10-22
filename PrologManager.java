import org.jpl7.Query;
import org.jpl7.Term;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class PrologManager {
    
    public PrologManager(String kbFilename) {
        // Carga la base de conocimiento una sola vez al iniciar la aplicación
        String consultQuery = "consult('" + kbFilename.replace("\\", "/") + "')";
        Query q = new Query(consultQuery);
        if (!q.hasSolution()) {
            throw new RuntimeException("No se pudo cargar la base de conocimiento Prolog: " + kbFilename);
        }
    }

    public double calcularTMB(char sexo, int edad, double estaturaM, double pesoKg) {
        // Convierte la estatura capturada en metros a centímetros antes de calcular
        double estaturaCm = estaturaM * 100.0;
        double tmb;
        if (sexo == 'M' || sexo == 'm') {
            tmb = (10.0 * pesoKg) + (6.25 * estaturaCm) - (5.0 * edad) + 5.0;
        } else {
            tmb = (10.0 * pesoKg) + (6.25 * estaturaCm) - (5.0 * edad) - 161.0;
        }
        return tmb;
    }

    public List<Map<String, Object>> buscarMenusValidos(double tmb, int limite) {
        List<Map<String, Object>> menus = new ArrayList<>();
        String queryString = String.format(java.util.Locale.US, "validar_restricciones(%.2f, Des, Alm, Com, Mer, Cen, Tot)", tmb);
        Query query = new Query(queryString);

        int count = 0;
        while (query.hasMoreSolutions() && count < limite) {
            Map<String, Term> soln = query.nextSolution();
            Map<String, Object> menu = new HashMap<>();
            
            String desId = soln.get("Des").name();
            String almId = soln.get("Alm").name();
            String comId = soln.get("Com").name();
            String merId = soln.get("Mer").name();
            String cenId = soln.get("Cen").name();
            double totalKcal = soln.get("Tot").doubleValue();

            menu.put("total_kcal", totalKcal);
            menu.put("desayuno", obtenerDetalleAlimento(desId));
            menu.put("almuerzo", obtenerDetalleAlimento(almId));
            menu.put("comida", obtenerDetalleAlimento(comId));
            menu.put("merienda", obtenerDetalleAlimento(merId));
            menu.put("cena", obtenerDetalleAlimento(cenId));

            menus.add(menu);
            count++;
        }
        return menus;
    }

    public Map<String, Object> obtenerDetalleAlimento(String id) {
        Map<String, Object> detalle = new HashMap<>();
        String queryString = String.format(java.util.Locale.US, "obtener_detalle('%s', Nombre, Porcion, Kcal, Tiempo)", id);
        Query query = new Query(queryString);
        if (query.hasMoreSolutions()) {
            Map<String, Term> soln = query.nextSolution();
            detalle.put("id", id);
            detalle.put("nombre", soln.get("Nombre").toString().replace("'", ""));
            detalle.put("porcion", soln.get("Porcion").toString().replace("'", ""));
            detalle.put("kcal", soln.get("Kcal").doubleValue());
            detalle.put("tiempo", soln.get("Tiempo").name());
        }
        return detalle;
    }
}