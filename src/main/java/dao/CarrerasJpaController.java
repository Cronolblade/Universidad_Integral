/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dto.Carreras;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Facultades;
import dto.Cursos;
import java.util.ArrayList;
import java.util.Collection;
import dto.Estudiantes;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author NITRO
 */
public class CarrerasJpaController implements Serializable {

    public CarrerasJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Carreras carreras) {
        if (carreras.getCursosCollection() == null) {
            carreras.setCursosCollection(new ArrayList<Cursos>());
        }
        if (carreras.getEstudiantesCollection() == null) {
            carreras.setEstudiantesCollection(new ArrayList<Estudiantes>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facultades idFacultad = carreras.getIdFacultad();
            if (idFacultad != null) {
                idFacultad = em.getReference(idFacultad.getClass(), idFacultad.getId());
                carreras.setIdFacultad(idFacultad);
            }
            Collection<Cursos> attachedCursosCollection = new ArrayList<Cursos>();
            for (Cursos cursosCollectionCursosToAttach : carreras.getCursosCollection()) {
                cursosCollectionCursosToAttach = em.getReference(cursosCollectionCursosToAttach.getClass(), cursosCollectionCursosToAttach.getId());
                attachedCursosCollection.add(cursosCollectionCursosToAttach);
            }
            carreras.setCursosCollection(attachedCursosCollection);
            Collection<Estudiantes> attachedEstudiantesCollection = new ArrayList<Estudiantes>();
            for (Estudiantes estudiantesCollectionEstudiantesToAttach : carreras.getEstudiantesCollection()) {
                estudiantesCollectionEstudiantesToAttach = em.getReference(estudiantesCollectionEstudiantesToAttach.getClass(), estudiantesCollectionEstudiantesToAttach.getId());
                attachedEstudiantesCollection.add(estudiantesCollectionEstudiantesToAttach);
            }
            carreras.setEstudiantesCollection(attachedEstudiantesCollection);
            em.persist(carreras);
            if (idFacultad != null) {
                idFacultad.getCarrerasCollection().add(carreras);
                idFacultad = em.merge(idFacultad);
            }
            for (Cursos cursosCollectionCursos : carreras.getCursosCollection()) {
                cursosCollectionCursos.getCarrerasCollection().add(carreras);
                cursosCollectionCursos = em.merge(cursosCollectionCursos);
            }
            for (Estudiantes estudiantesCollectionEstudiantes : carreras.getEstudiantesCollection()) {
                Carreras oldIdCarreraOfEstudiantesCollectionEstudiantes = estudiantesCollectionEstudiantes.getIdCarrera();
                estudiantesCollectionEstudiantes.setIdCarrera(carreras);
                estudiantesCollectionEstudiantes = em.merge(estudiantesCollectionEstudiantes);
                if (oldIdCarreraOfEstudiantesCollectionEstudiantes != null) {
                    oldIdCarreraOfEstudiantesCollectionEstudiantes.getEstudiantesCollection().remove(estudiantesCollectionEstudiantes);
                    oldIdCarreraOfEstudiantesCollectionEstudiantes = em.merge(oldIdCarreraOfEstudiantesCollectionEstudiantes);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Carreras carreras) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Carreras persistentCarreras = em.find(Carreras.class, carreras.getId());
            Facultades idFacultadOld = persistentCarreras.getIdFacultad();
            Facultades idFacultadNew = carreras.getIdFacultad();
            Collection<Cursos> cursosCollectionOld = persistentCarreras.getCursosCollection();
            Collection<Cursos> cursosCollectionNew = carreras.getCursosCollection();
            Collection<Estudiantes> estudiantesCollectionOld = persistentCarreras.getEstudiantesCollection();
            Collection<Estudiantes> estudiantesCollectionNew = carreras.getEstudiantesCollection();
            List<String> illegalOrphanMessages = null;
            for (Estudiantes estudiantesCollectionOldEstudiantes : estudiantesCollectionOld) {
                if (!estudiantesCollectionNew.contains(estudiantesCollectionOldEstudiantes)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Estudiantes " + estudiantesCollectionOldEstudiantes + " since its idCarrera field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idFacultadNew != null) {
                idFacultadNew = em.getReference(idFacultadNew.getClass(), idFacultadNew.getId());
                carreras.setIdFacultad(idFacultadNew);
            }
            Collection<Cursos> attachedCursosCollectionNew = new ArrayList<Cursos>();
            for (Cursos cursosCollectionNewCursosToAttach : cursosCollectionNew) {
                cursosCollectionNewCursosToAttach = em.getReference(cursosCollectionNewCursosToAttach.getClass(), cursosCollectionNewCursosToAttach.getId());
                attachedCursosCollectionNew.add(cursosCollectionNewCursosToAttach);
            }
            cursosCollectionNew = attachedCursosCollectionNew;
            carreras.setCursosCollection(cursosCollectionNew);
            Collection<Estudiantes> attachedEstudiantesCollectionNew = new ArrayList<Estudiantes>();
            for (Estudiantes estudiantesCollectionNewEstudiantesToAttach : estudiantesCollectionNew) {
                estudiantesCollectionNewEstudiantesToAttach = em.getReference(estudiantesCollectionNewEstudiantesToAttach.getClass(), estudiantesCollectionNewEstudiantesToAttach.getId());
                attachedEstudiantesCollectionNew.add(estudiantesCollectionNewEstudiantesToAttach);
            }
            estudiantesCollectionNew = attachedEstudiantesCollectionNew;
            carreras.setEstudiantesCollection(estudiantesCollectionNew);
            carreras = em.merge(carreras);
            if (idFacultadOld != null && !idFacultadOld.equals(idFacultadNew)) {
                idFacultadOld.getCarrerasCollection().remove(carreras);
                idFacultadOld = em.merge(idFacultadOld);
            }
            if (idFacultadNew != null && !idFacultadNew.equals(idFacultadOld)) {
                idFacultadNew.getCarrerasCollection().add(carreras);
                idFacultadNew = em.merge(idFacultadNew);
            }
            for (Cursos cursosCollectionOldCursos : cursosCollectionOld) {
                if (!cursosCollectionNew.contains(cursosCollectionOldCursos)) {
                    cursosCollectionOldCursos.getCarrerasCollection().remove(carreras);
                    cursosCollectionOldCursos = em.merge(cursosCollectionOldCursos);
                }
            }
            for (Cursos cursosCollectionNewCursos : cursosCollectionNew) {
                if (!cursosCollectionOld.contains(cursosCollectionNewCursos)) {
                    cursosCollectionNewCursos.getCarrerasCollection().add(carreras);
                    cursosCollectionNewCursos = em.merge(cursosCollectionNewCursos);
                }
            }
            for (Estudiantes estudiantesCollectionNewEstudiantes : estudiantesCollectionNew) {
                if (!estudiantesCollectionOld.contains(estudiantesCollectionNewEstudiantes)) {
                    Carreras oldIdCarreraOfEstudiantesCollectionNewEstudiantes = estudiantesCollectionNewEstudiantes.getIdCarrera();
                    estudiantesCollectionNewEstudiantes.setIdCarrera(carreras);
                    estudiantesCollectionNewEstudiantes = em.merge(estudiantesCollectionNewEstudiantes);
                    if (oldIdCarreraOfEstudiantesCollectionNewEstudiantes != null && !oldIdCarreraOfEstudiantesCollectionNewEstudiantes.equals(carreras)) {
                        oldIdCarreraOfEstudiantesCollectionNewEstudiantes.getEstudiantesCollection().remove(estudiantesCollectionNewEstudiantes);
                        oldIdCarreraOfEstudiantesCollectionNewEstudiantes = em.merge(oldIdCarreraOfEstudiantesCollectionNewEstudiantes);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = carreras.getId();
                if (findCarreras(id) == null) {
                    throw new NonexistentEntityException("The carreras with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Carreras carreras;
            try {
                carreras = em.getReference(Carreras.class, id);
                carreras.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The carreras with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Estudiantes> estudiantesCollectionOrphanCheck = carreras.getEstudiantesCollection();
            for (Estudiantes estudiantesCollectionOrphanCheckEstudiantes : estudiantesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Carreras (" + carreras + ") cannot be destroyed since the Estudiantes " + estudiantesCollectionOrphanCheckEstudiantes + " in its estudiantesCollection field has a non-nullable idCarrera field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Facultades idFacultad = carreras.getIdFacultad();
            if (idFacultad != null) {
                idFacultad.getCarrerasCollection().remove(carreras);
                idFacultad = em.merge(idFacultad);
            }
            Collection<Cursos> cursosCollection = carreras.getCursosCollection();
            for (Cursos cursosCollectionCursos : cursosCollection) {
                cursosCollectionCursos.getCarrerasCollection().remove(carreras);
                cursosCollectionCursos = em.merge(cursosCollectionCursos);
            }
            em.remove(carreras);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Carreras> findCarrerasEntities() {
        return findCarrerasEntities(true, -1, -1);
    }

    public List<Carreras> findCarrerasEntities(int maxResults, int firstResult) {
        return findCarrerasEntities(false, maxResults, firstResult);
    }

    private List<Carreras> findCarrerasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Carreras.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Carreras findCarreras(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Carreras.class, id);
        } finally {
            em.close();
        }
    }

    public int getCarrerasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Carreras> rt = cq.from(Carreras.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
