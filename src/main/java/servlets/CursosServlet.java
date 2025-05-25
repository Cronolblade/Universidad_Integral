package servlets;

import dao.CursosJpaController;
import dto.Cursos;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import javax.json.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/cursos")
public class CursosServlet extends HttpServlet {

    private CursosJpaController dao;

    @Override
    public void init() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Prueba_UniversidadIntegral_war_1.0-SNAPSHOTPU");
        dao = new CursosJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json; charset=UTF-8");
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Cursos c : dao.findCursosEntities()) {
            arrayBuilder.add(Json.createObjectBuilder()
                .add("id", c.getId())
                .add("codigo", c.getCodigo())
                .add("nombre", c.getNombre())
                .add("descripcion", c.getDescripcion() == null ? "" : c.getDescripcion())
                .add("creditos", c.getCreditos())
                .add("semestre", c.getSemestre()));
        }
        Json.createWriter(res.getWriter()).writeArray(arrayBuilder.build());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        JsonObject data = Json.createReader(req.getReader()).readObject();

        Cursos nuevo = new Cursos();
        nuevo.setCodigo(data.getString("codigo"));
        nuevo.setNombre(data.getString("nombre"));
        nuevo.setDescripcion(data.getString("descripcion", ""));
        nuevo.setCreditos(data.getInt("creditos"));
        nuevo.setSemestre(data.getString("semestre"));

        dao.create(nuevo);
        res.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        JsonObject data = Json.createReader(req.getReader()).readObject();
        int id = data.getInt("id");

        Cursos existente = dao.findCursos(id);
        if (existente == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        existente.setCodigo(data.getString("codigo"));
        existente.setNombre(data.getString("nombre"));
        existente.setDescripcion(data.getString("descripcion", ""));
        existente.setCreditos(data.getInt("creditos"));
        existente.setSemestre(data.getString("semestre"));

        try {
            dao.edit(existente);
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        JsonObject data = Json.createReader(req.getReader()).readObject();
        int id = data.getInt("id");

        try {
            dao.destroy(id);
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
