package servlets;

import dao.AsignacionesJpaController;
import dto.Asignaciones;
import dto.Cursos;
import dto.Profesores;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import javax.json.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/asignaciones")
public class AsignacionesServlet extends HttpServlet {

    private AsignacionesJpaController controller;

    @Override
    public void init() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Prueba_UniversidadIntegral_war_1.0-SNAPSHOTPU");
        controller = new AsignacionesJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();

        for (Asignaciones a : controller.findAsignacionesEntities()) {
            jsonArray.add(Json.createObjectBuilder()
                    .add("id", a.getId())
                    .add("anio", a.getAnio())
                    .add("ciclo", a.getCiclo())
                    .add("idCurso", a.getIdCurso().getId())
                    .add("idProfesor", a.getIdProfesor().getId())
            );
        }

        JsonWriter writer = Json.createWriter(response.getWriter());
        writer.writeArray(jsonArray.build());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject input = Json.createReader(request.getReader()).readObject();

        Asignaciones asignacion = new Asignaciones();
        asignacion.setAnio(input.getInt("anio"));
        asignacion.setCiclo(input.getString("ciclo"));
        asignacion.setIdCurso(new Cursos(input.getInt("idCurso")));
        asignacion.setIdProfesor(new Profesores(input.getInt("idProfesor")));

        controller.create(asignacion);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject input = Json.createReader(request.getReader()).readObject();

        Asignaciones asignacion = new Asignaciones();
        asignacion.setId(input.getInt("id"));
        asignacion.setAnio(input.getInt("anio"));
        asignacion.setCiclo(input.getString("ciclo"));
        asignacion.setIdCurso(new Cursos(input.getInt("idCurso")));
        asignacion.setIdProfesor(new Profesores(input.getInt("idProfesor")));

        try {
            controller.edit(asignacion);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject input = Json.createReader(request.getReader()).readObject();
        int id = input.getInt("id");
        try {
            controller.destroy(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
