package servlets;

import dao.ProfesoresJpaController;
import dto.Profesores;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.json.*;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "ProfesoresServlet", urlPatterns = {"/profesores"})
public class ProfesoresServlet extends HttpServlet {

    private final ProfesoresJpaController controller =
        new ProfesoresJpaController(Persistence.createEntityManagerFactory("com.mycompany_Prueba_UniversidadIntegral_war_1.0-SNAPSHOTPU"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        for (Profesores p : controller.findProfesoresEntities()) {
            jsonArray.add(Json.createObjectBuilder()
                .add("id", p.getId())
                .add("nombre", p.getNombre())
                .add("apellido", p.getApellido())
                .add("correo", p.getCorreo())
                .add("telefono", p.getTelefono() != null ? p.getTelefono() : "")
                .add("tituloAcademico", p.getTituloAcademico() != null ? p.getTituloAcademico() : "")
                .add("fechaIngreso", dateFormat.format(p.getFechaIngreso())));
        }
        JsonWriter writer = Json.createWriter(response.getWriter());
        writer.writeArray(jsonArray.build());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        JsonObject json = parseJson(request);
        Profesores p = new Profesores();
        p.setNombre(json.getString("nombre"));
        p.setApellido(json.getString("apellido"));
        p.setCorreo(json.getString("correo"));
        p.setTelefono(json.getString("telefono", null));
        p.setTituloAcademico(json.getString("tituloAcademico", null));
        try {
            Date fecha = dateFormat.parse(json.getString("fechaIngreso"));
            p.setFechaIngreso(fecha);
            controller.create(p);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        JsonObject json = parseJson(request);
        try {
            Profesores p = controller.findProfesores(json.getInt("id"));
            if (p == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            p.setNombre(json.getString("nombre"));
            p.setApellido(json.getString("apellido"));
            p.setCorreo(json.getString("correo"));
            p.setTelefono(json.getString("telefono", null));
            p.setTituloAcademico(json.getString("tituloAcademico", null));
            p.setFechaIngreso(dateFormat.parse(json.getString("fechaIngreso")));
            controller.edit(p);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = parseJson(request);
        try {
            controller.destroy(json.getInt("id"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private JsonObject parseJson(HttpServletRequest request) throws IOException {
        JsonReader reader = Json.createReader(new StringReader(request.getReader().lines().reduce("", (a, b) -> a + b)));
        return reader.readObject();
    }
}
