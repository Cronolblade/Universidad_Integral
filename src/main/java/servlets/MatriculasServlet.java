package servlets;

import dao.MatriculasJpaController;
import dto.Matriculas;
import dto.Asignaciones;
import dto.Estudiantes;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.json.*;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/matriculas")
public class MatriculasServlet extends HttpServlet {

    private MatriculasJpaController controller = new MatriculasJpaController(Persistence.createEntityManagerFactory("com.mycompany_Prueba_UniversidadIntegral_war_1.0-SNAPSHOTPU"));

    @Override
protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json; charset=UTF-8");
    List<Matriculas> matriculas = controller.findMatriculasEntities();

    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    for (Matriculas m : matriculas) {
        arrayBuilder.add(Json.createObjectBuilder()
            .add("id", m.getId())
            .add("fechaMatricula", sdf.format(m.getFechaMatricula()))
            .add("estado", m.getEstado())
            .add("idAsignacion" ,m.getAsignaciones().getId())
            .add("idEstudiante", m.getEstudiantes().getId())
        );
    }

    Json.createWriter(res.getWriter()).writeArray(arrayBuilder.build());
}


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        JsonObject json = Json.createReader(request.getInputStream()).readObject();
        Matriculas m = new Matriculas();
        String fechaStr = json.getString("fechaMatricula");
        java.sql.Date fechaMatricula = java.sql.Date.valueOf(fechaStr); // formato correcto: yyyy-MM-dd
        m.setFechaMatricula(fechaMatricula);
        m.setEstado(json.getString("estado"));
        m.setAsignaciones(new Asignaciones(json.getInt("idAsignacion")));
        m.setEstudiantes(new Estudiantes(json.getInt("idEstudiante")));
        controller.create(m);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        JsonObject json = Json.createReader(request.getInputStream()).readObject();
        try {
            Matriculas m = controller.findMatriculas(json.getInt("id"));
            String fechaStr = json.getString("fechaMatricula");
            java.sql.Date fechaMatricula = java.sql.Date.valueOf(fechaStr); // formato correcto: yyyy-MM-dd
            m.setFechaMatricula(fechaMatricula);
            m.setEstado(json.getString("estado"));
            m.setAsignaciones(new Asignaciones(json.getInt("idAsignacion")));
            m.setEstudiantes(new Estudiantes(json.getInt("idEstudiante")));
            controller.edit(m);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            controller.destroy(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar.");
        }
    }
}
