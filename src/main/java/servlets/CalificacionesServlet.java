package servlets;

import dao.CalificacionesJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Calificaciones;
import dto.Matriculas;

import javax.json.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.Persistence;

@WebServlet("/calificaciones")
public class CalificacionesServlet extends HttpServlet {

    private CalificacionesJpaController controller;

    @Override
    public void init() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Prueba_UniversidadIntegral_war_1.0-SNAPSHOTPU");
        controller = new CalificacionesJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");

        List<Calificaciones> lista = controller.findCalificacionesEntities();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Calificaciones c : lista) {
            arrayBuilder.add(Json.createObjectBuilder()
                .add("id", c.getId())
                .add("nota", c.getNota().toString())
                .add("observaciones", c.getObservaciones() != null ? c.getObservaciones() : "")
                .add("fechaRegistro", sdf.format(c.getFechaRegistro()))
                .add("idMatricula", c.getMatriculas().getId()));
        }

        try (JsonWriter writer = Json.createWriter(res.getWriter())) {
            writer.writeArray(arrayBuilder.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");

        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            JsonObject json = reader.readObject();

            BigDecimal nota = new BigDecimal(json.getString("nota"));
            String observaciones = json.getString("observaciones", "");
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(json.getString("fechaRegistro"));
            int idMatricula = json.getInt("idMatricula");

            Calificaciones cal = new Calificaciones();
            cal.setNota(nota);
            cal.setObservaciones(observaciones);
            cal.setFechaRegistro(fecha);
            Matriculas m = new Matriculas();
            m.setId(idMatricula);
            cal.setMatriculas(m);

            controller.create(cal);

            res.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject respuesta = Json.createObjectBuilder()
                .add("id", cal.getId())
                .build();

            try (JsonWriter writer = Json.createWriter(res.getWriter())) {
                writer.writeObject(respuesta);
            }
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"Error al crear calificación\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");

        try (JsonReader reader = Json.createReader(req.getInputStream())) {
            JsonObject json = reader.readObject();

            int id = json.getInt("id");
            Calificaciones cal = controller.findCalificaciones(id);
            if (cal == null) {
                res.setStatus(404);
                res.getWriter().write("{\"error\":\"No encontrado\"}");
                return;
            }

            cal.setNota(new BigDecimal(json.getString("nota")));
            cal.setObservaciones(json.getString("observaciones", ""));
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(json.getString("fechaRegistro"));
            cal.setFechaRegistro(fecha);
            Matriculas m = new Matriculas();
            m.setId(json.getInt("idMatricula"));
            cal.setMatriculas(m);

            controller.edit(cal);

            JsonObject respuesta = Json.createObjectBuilder()
                .add("id", cal.getId())
                .build();

            try (JsonWriter writer = Json.createWriter(res.getWriter())) {
                writer.writeObject(respuesta);
            }
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"Error al actualizar\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");

        String idParam = req.getParameter("id");
        if (idParam == null) {
            res.setStatus(400);
            res.getWriter().write("{\"error\":\"ID requerido\"}");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            controller.destroy(id);
            res.setStatus(204);
        } catch (NonexistentEntityException e) {
            res.setStatus(404);
            res.getWriter().write("{\"error\":\"No encontrado\"}");
        } catch (Exception e) {
            res.setStatus(500);
            res.getWriter().write("{\"error\":\"Error al eliminar\"}");
        }
    }
}
