package servlets;

import dao.CarrerasJpaController;
import dto.Carreras;
import dto.Facultades;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CarrerasServlet", urlPatterns = {"/carreras"})
public class CarrerasServlet extends HttpServlet {

    private CarrerasJpaController controller = new CarrerasJpaController(Persistence.createEntityManagerFactory("com.mycompany_Prueba_UniversidadIntegral_war_1.0-SNAPSHOTPU"));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        List<Carreras> carrerasList = controller.findCarrerasEntities();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Carreras carrera : carrerasList) {
            arrayBuilder.add(Json.createObjectBuilder()
                    .add("id", carrera.getId())
                    .add("nombre", carrera.getNombre())
                    .add("duracionAnios", carrera.getDuracionAnios())
                    .add("idFacultad", carrera.getIdFacultad().getId()));
        }

        try (PrintWriter out = response.getWriter()) {
            Json.createWriter(out).writeArray(arrayBuilder.build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonReader reader = Json.createReader(request.getInputStream());
        JsonObject json = reader.readObject();

        Carreras carrera = new Carreras();
        carrera.setNombre(json.getString("nombre"));
        carrera.setDuracionAnios(json.getInt("duracionAnios"));
        carrera.setIdFacultad(new Facultades(json.getInt("idFacultad")));

        controller.create(carrera);

        response.setContentType("application/json;charset=UTF-8");
        try (JsonWriter writer = Json.createWriter(response.getWriter())) {
            writer.writeObject(Json.createObjectBuilder().add("message", "Carrera creada").build());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonReader reader = Json.createReader(request.getInputStream());
        JsonObject json = reader.readObject();

        Carreras carrera = controller.findCarreras(json.getInt("id"));
        if (carrera != null) {
            carrera.setNombre(json.getString("nombre"));
            carrera.setDuracionAnios(json.getInt("duracionAnios"));
            carrera.setIdFacultad(new Facultades(json.getInt("idFacultad")));

            try {
                controller.edit(carrera);
                response.setContentType("application/json;charset=UTF-8");
                try (JsonWriter writer = Json.createWriter(response.getWriter())) {
                    writer.writeObject(Json.createObjectBuilder().add("message", "Carrera actualizada").build());
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Carrera no encontrada");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonReader reader = Json.createReader(request.getInputStream());
        JsonObject json = reader.readObject();

        int id = json.getInt("id");

        try {
            controller.destroy(id);
            response.setContentType("application/json;charset=UTF-8");
            try (JsonWriter writer = Json.createWriter(response.getWriter())) {
                writer.writeObject(Json.createObjectBuilder().add("message", "Carrera eliminada").build());
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
