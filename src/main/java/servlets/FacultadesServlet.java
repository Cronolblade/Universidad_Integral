package servlets;

import dao.FacultadesJpaController;
import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dto.Facultades;

import javax.annotation.Resource;
import javax.json.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;
import javax.persistence.Persistence;

@WebServlet(name = "FacultadesServlet", urlPatterns = {"/facultades"})
public class FacultadesServlet extends HttpServlet {

    private FacultadesJpaController controller;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Prueba_UniversidadIntegral_war_1.0-SNAPSHOTPU");
        controller = new FacultadesJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");
        List<Facultades> facultades = controller.findFacultadesEntities();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Facultades f : facultades) {
            arrayBuilder.add(Json.createObjectBuilder()
                    .add("id", f.getId())
                    .add("nombre", f.getNombre())
            );
        }
        try (JsonWriter writer = Json.createWriter(res.getWriter())) {
            writer.writeArray(arrayBuilder.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");

        try (InputStream is = req.getInputStream();
             JsonReader reader = Json.createReader(is)) {

            JsonObject json = reader.readObject();
            String nombre = json.getString("nombre", "").trim();

            if (nombre.isEmpty()) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\":\"El nombre es requerido\"}");
                return;
            }

            Facultades f = new Facultades();
            f.setNombre(nombre);

            controller.create(f);

            res.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("id", f.getId())
                    .add("nombre", f.getNombre())
                    .build();

            try (JsonWriter writer = Json.createWriter(res.getWriter())) {
                writer.writeObject(jsonResponse);
            }
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"error\":\"Error al crear la facultad.\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");

        try (InputStream is = req.getInputStream();
             JsonReader reader = Json.createReader(is)) {

            JsonObject json = reader.readObject();

            if (!json.containsKey("id") || !json.containsKey("nombre")) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("{\"error\":\"Se requieren id y nombre\"}");
                return;
            }

            int id = json.getInt("id");
            String nombre = json.getString("nombre").trim();

            Facultades f = controller.findFacultades(id);
            if (f == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("{\"error\":\"Facultad no encontrada\"}");
                return;
            }

            f.setNombre(nombre);
            try {
                controller.edit(f);
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                res.getWriter().write("{\"error\":\"Conflicto al editar la facultad\"}");
                return;
            } catch (Exception ex) {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().write("{\"error\":\"Error al editar la facultad\"}");
                return;
            }

            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("id", f.getId())
                    .add("nombre", f.getNombre())
                    .build();

            try (JsonWriter writer = Json.createWriter(res.getWriter())) {
                writer.writeObject(jsonResponse);
            }

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");
        String idParam = req.getParameter("id");
        if (idParam == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"Parámetro id requerido\"}");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            try {
                controller.destroy(id);
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalOrphanException e) {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                res.getWriter().write("{\"error\":\"No se puede eliminar porque existen registros dependientes\"}");
            } catch (NonexistentEntityException e) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.getWriter().write("{\"error\":\"Facultad no encontrada\"}");
            }
        } catch (NumberFormatException e) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().write("{\"error\":\"Id inválido\"}");
        }
    }
}
//