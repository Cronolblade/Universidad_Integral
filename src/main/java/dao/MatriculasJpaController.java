/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Asignaciones;
import dto.Estudiantes;
import dto.Calificaciones;
import dto.Matriculas;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author NITRO
 */
public class MatriculasJpaController implements Serializable {

    public MatriculasJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Matriculas matriculas) {
        if (matriculas.getCalificacionesCollection() == null) {
            matriculas.setCalificacionesCollection(new ArrayList<Calificaciones>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Asignaciones asignaciones = matriculas.getAsignaciones();
            if (asignaciones != null) {
                asignaciones = em.getReference(asignaciones.getClass(), asignaciones.getId());
                matriculas.setAsignaciones(asignaciones);
            }
            Estudiantes estudiantes = matriculas.getEstudiantes();
            if (estudiantes != null) {
                estudiantes = em.getReference(estudiantes.getClass(), estudiantes.getId());
                matriculas.setEstudiantes(estudiantes);
            }
            Collection<Calificaciones> attachedCalificacionesCollection = new ArrayList<Calificaciones>();
            for (Calificaciones calificacionesCollectionCalificacionesToAttach : matriculas.getCalificacionesCollection()) {
                calificacionesCollectionCalificacionesToAttach = em.getReference(calificacionesCollectionCalificacionesToAttach.getClass(), calificacionesCollectionCalificacionesToAttach.getId());
                attachedCalificacionesCollection.add(calificacionesCollectionCalificacionesToAttach);
            }
            matriculas.setCalificacionesCollection(attachedCalificacionesCollection);
            em.persist(matriculas);
            if (asignaciones != null) {
                asignaciones.getMatriculasCollection().add(matriculas);
                asignaciones = em.merge(asignaciones);
            }
            if (estudiantes != null) {
                estudiantes.getMatriculasCollection().add(matriculas);
                estudiantes = em.merge(estudiantes);
            }
            for (Calificaciones calificacionesCollectionCalificaciones : matriculas.getCalificacionesCollection()) {
                Matriculas oldMatriculasOfCalificacionesCollectionCalificaciones = calificacionesCollectionCalificaciones.getMatriculas();
                calificacionesCollectionCalificaciones.setMatriculas(matriculas);
                calificacionesCollectionCalificaciones = em.merge(calificacionesCollectionCalificaciones);
                if (oldMatriculasOfCalificacionesCollectionCalificaciones != null) {
                    oldMatriculasOfCalificacionesCollectionCalificaciones.getCalificacionesCollection().remove(calificacionesCollectionCalificaciones);
                    oldMatriculasOfCalificacionesCollectionCalificaciones = em.merge(oldMatriculasOfCalificacionesCollectionCalificaciones);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Matriculas matriculas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Matriculas persistentMatriculas = em.find(Matriculas.class, matriculas.getId());
            Asignaciones asignacionesOld = persistentMatriculas.getAsignaciones();
            Asignaciones asignacionesNew = matriculas.getAsignaciones();
            Estudiantes estudiantesOld = persistentMatriculas.getEstudiantes();
            Estudiantes estudiantesNew = matriculas.getEstudiantes();
            Collection<Calificaciones> calificacionesCollectionOld = persistentMatriculas.getCalificacionesCollection();
            Collection<Calificaciones> calificacionesCollectionNew = matriculas.getCalificacionesCollection();
            List<String> illegalOrphanMessages = null;
            for (Calificaciones calificacionesCollectionOldCalificaciones : calificacionesCollectionOld) {
                if (!calificacionesCollectionNew.contains(calificacionesCollectionOldCalificaciones)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Calificaciones " + calificacionesCollectionOldCalificaciones + " since its matriculas field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (asignacionesNew != null) {
                asignacionesNew = em.getReference(asignacionesNew.getClass(), asignacionesNew.getId());
                matriculas.setAsignaciones(asignacionesNew);
            }
            if (estudiantesNew != null) {
                estudiantesNew = em.getReference(estudiantesNew.getClass(), estudiantesNew.getId());
                matriculas.setEstudiantes(estudiantesNew);
            }
            Collection<Calificaciones> attachedCalificacionesCollectionNew = new ArrayList<Calificaciones>();
            for (Calificaciones calificacionesCollectionNewCalificacionesToAttach : calificacionesCollectionNew) {
                calificacionesCollectionNewCalificacionesToAttach = em.getReference(calificacionesCollectionNewCalificacionesToAttach.getClass(), calificacionesCollectionNewCalificacionesToAttach.getId());
                attachedCalificacionesCollectionNew.add(calificacionesCollectionNewCalificacionesToAttach);
            }
            calificacionesCollectionNew = attachedCalificacionesCollectionNew;
            matriculas.setCalificacionesCollection(calificacionesCollectionNew);
            matriculas = em.merge(matriculas);
            if (asignacionesOld != null && !asignacionesOld.equals(asignacionesNew)) {
                asignacionesOld.getMatriculasCollection().remove(matriculas);
                asignacionesOld = em.merge(asignacionesOld);
            }
            if (asignacionesNew != null && !asignacionesNew.equals(asignacionesOld)) {
                asignacionesNew.getMatriculasCollection().add(matriculas);
                asignacionesNew = em.merge(asignacionesNew);
            }
            if (estudiantesOld != null && !estudiantesOld.equals(estudiantesNew)) {
                estudiantesOld.getMatriculasCollection().remove(matriculas);
                estudiantesOld = em.merge(estudiantesOld);
            }
            if (estudiantesNew != null && !estudiantesNew.equals(estudiantesOld)) {
                estudiantesNew.getMatriculasCollection().add(matriculas);
                estudiantesNew = em.merge(estudiantesNew);
            }
            for (Calificaciones calificacionesCollectionNewCalificaciones : calificacionesCollectionNew) {
                if (!calificacionesCollectionOld.contains(calificacionesCollectionNewCalificaciones)) {
                    Matriculas oldMatriculasOfCalificacionesCollectionNewCalificaciones = calificacionesCollectionNewCalificaciones.getMatriculas();
                    calificacionesCollectionNewCalificaciones.setMatriculas(matriculas);
                    calificacionesCollectionNewCalificaciones = em.merge(calificacionesCollectionNewCalificaciones);
                    if (oldMatriculasOfCalificacionesCollectionNewCalificaciones != null && !oldMatriculasOfCalificacionesCollectionNewCalificaciones.equals(matriculas)) {
                        oldMatriculasOfCalificacionesCollectionNewCalificaciones.getCalificacionesCollection().remove(calificacionesCollectionNewCalificaciones);
                        oldMatriculasOfCalificacionesCollectionNewCalificaciones = em.merge(oldMatriculasOfCalificacionesCollectionNewCalificaciones);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = matriculas.getId();
                if (findMatriculas(id) == null) {
                    throw new NonexistentEntityException("The matriculas with id " + id + " no longer exists.");
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
            Matriculas matriculas;
            try {
                matriculas = em.getReference(Matriculas.class, id);
                matriculas.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The matriculas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Calificaciones> calificacionesCollectionOrphanCheck = matriculas.getCalificacionesCollection();
            for (Calificaciones calificacionesCollectionOrphanCheckCalificaciones : calificacionesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Matriculas (" + matriculas + ") cannot be destroyed since the Calificaciones " + calificacionesCollectionOrphanCheckCalificaciones + " in its calificacionesCollection field has a non-nullable matriculas field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Asignaciones asignaciones = matriculas.getAsignaciones();
            if (asignaciones != null) {
                asignaciones.getMatriculasCollection().remove(matriculas);
                asignaciones = em.merge(asignaciones);
            }
            Estudiantes estudiantes = matriculas.getEstudiantes();
            if (estudiantes != null) {
                estudiantes.getMatriculasCollection().remove(matriculas);
                estudiantes = em.merge(estudiantes);
            }
            em.remove(matriculas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Matriculas> findMatriculasEntities() {
        return findMatriculasEntities(true, -1, -1);
    }

    public List<Matriculas> findMatriculasEntities(int maxResults, int firstResult) {
        return findMatriculasEntities(false, maxResults, firstResult);
    }

    private List<Matriculas> findMatriculasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Matriculas.class));
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

    public Matriculas findMatriculas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Matriculas.class, id);
        } finally {
            em.close();
        }
    }

    public int getMatriculasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Matriculas> rt = cq.from(Matriculas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
