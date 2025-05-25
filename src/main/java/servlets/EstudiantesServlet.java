package servlets;

import dao.EstudiantesJpaController;
import dto.Carreras;
import dto.Estudiantes;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.json.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/estudiantes")
public class EstudiantesServlet extends HttpServlet {

    private EstudiantesJpaController dao;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Prueba_UniversidadIntegral_war_1.0-SNAPSHOTPU");
        dao = new EstudiantesJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        List<Estudiantes> estudiantes = dao.findEstudiantesEntities();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Estudiantes e : estudiantes) {
            arrayBuilder.add(Json.createObjectBuilder()
                    .add("id", e.getId())
                    .add("dni", e.getDni())
                    .add("nombre", e.getNombre())
                    .add("apellido", e.getApellido())
                    .add("correo", e.getCorreo())
                    .add("genero", e.getGenero())
                    .add("fechaNacimiento", new SimpleDateFormat("yyyy-MM-dd").format(e.getFechaNacimiento()))
                    .add("idCarrera", e.getIdCarrera().getId())
            );
        }

        try ( JsonWriter writer = Json.createWriter(response.getWriter())) {
            writer.writeArray(arrayBuilder.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        JsonReader reader = Json.createReader(request.getReader());
        JsonObject json = reader.readObject();

        Estudiantes estudiante = new Estudiantes();
        estudiante.setDni(json.getString("dni"));
        estudiante.setNombre(json.getString("nombre"));
        estudiante.setApellido(json.getString("apellido"));
        estudiante.setCorreo(json.getString("correo"));
        estudiante.setGenero(json.getString("genero"));
        try {
            estudiante.setFechaNacimiento(new SimpleDateFormat("yyyy-MM-dd").parse(json.getString("fechaNacimiento")));
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fecha inválida");
            return;
        }
        Carreras carrera = new Carreras();
        carrera.setId(json.getInt("idCarrera"));
        estudiante.setIdCarrera(carrera);

        dao.create(estudiante);

        JsonObject responseJson = Json.createObjectBuilder()
                .add("message", "Estudiante creado exitosamente")
                .build();

        try ( JsonWriter writer = Json.createWriter(response.getWriter())) {
            writer.writeObject(responseJson);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        JsonReader reader = Json.createReader(request.getReader());
        JsonObject json = reader.readObject();

        Estudiantes estudiante = dao.findEstudiantes(json.getInt("id"));
        if (estudiante == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Estudiante no encontrado");
            return;
        }

        estudiante.setDni(json.getString("dni"));
        estudiante.setNombre(json.getString("nombre"));
        estudiante.setApellido(json.getString("apellido"));
        estudiante.setCorreo(json.getString("correo"));
        estudiante.setGenero(json.getString("genero"));
        try {
            estudiante.setFechaNacimiento(new SimpleDateFormat("yyyy-MM-dd").parse(json.getString("fechaNacimiento")));
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fecha inválida");
            return;
        }
        Carreras carrera = new Carreras();
        carrera.setId(json.getInt("idCarrera"));
        estudiante.setIdCarrera(carrera);

        try {
            dao.edit(estudiante);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar");
            return;
        }

        JsonObject responseJson = Json.createObjectBuilder()
                .add("message", "Estudiante actualizado exitosamente")
                .build();

        try ( JsonWriter writer = Json.createWriter(response.getWriter())) {
            writer.writeObject(responseJson);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        JsonReader reader = Json.createReader(request.getReader());
        JsonObject json = reader.readObject();

        int id = json.getInt("id");
        try {
            dao.destroy(id);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar");
            return;
        }

        JsonObject responseJson = Json.createObjectBuilder()
                .add("message", "Estudiante eliminado exitosamente")
                .build();

        try ( JsonWriter writer = Json.createWriter(response.getWriter())) {
            writer.writeObject(responseJson);
        }
    }
}
