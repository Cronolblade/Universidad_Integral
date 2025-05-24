/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dto.Asignaciones;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Cursos;
import dto.Profesores;
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
public class AsignacionesJpaController implements Serializable {

    public AsignacionesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Asignaciones asignaciones) {
        if (asignaciones.getMatriculasCollection() == null) {
            asignaciones.setMatriculasCollection(new ArrayList<Matriculas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cursos idCurso = asignaciones.getIdCurso();
            if (idCurso != null) {
                idCurso = em.getReference(idCurso.getClass(), idCurso.getId());
                asignaciones.setIdCurso(idCurso);
            }
            Profesores idProfesor = asignaciones.getIdProfesor();
            if (idProfesor != null) {
                idProfesor = em.getReference(idProfesor.getClass(), idProfesor.getId());
                asignaciones.setIdProfesor(idProfesor);
            }
            Collection<Matriculas> attachedMatriculasCollection = new ArrayList<Matriculas>();
            for (Matriculas matriculasCollectionMatriculasToAttach : asignaciones.getMatriculasCollection()) {
                matriculasCollectionMatriculasToAttach = em.getReference(matriculasCollectionMatriculasToAttach.getClass(), matriculasCollectionMatriculasToAttach.getId());
                attachedMatriculasCollection.add(matriculasCollectionMatriculasToAttach);
            }
            asignaciones.setMatriculasCollection(attachedMatriculasCollection);
            em.persist(asignaciones);
            if (idCurso != null) {
                idCurso.getAsignacionesCollection().add(asignaciones);
                idCurso = em.merge(idCurso);
            }
            if (idProfesor != null) {
                idProfesor.getAsignacionesCollection().add(asignaciones);
                idProfesor = em.merge(idProfesor);
            }
            for (Matriculas matriculasCollectionMatriculas : asignaciones.getMatriculasCollection()) {
                Asignaciones oldAsignacionesOfMatriculasCollectionMatriculas = matriculasCollectionMatriculas.getAsignaciones();
                matriculasCollectionMatriculas.setAsignaciones(asignaciones);
                matriculasCollectionMatriculas = em.merge(matriculasCollectionMatriculas);
                if (oldAsignacionesOfMatriculasCollectionMatriculas != null) {
                    oldAsignacionesOfMatriculasCollectionMatriculas.getMatriculasCollection().remove(matriculasCollectionMatriculas);
                    oldAsignacionesOfMatriculasCollectionMatriculas = em.merge(oldAsignacionesOfMatriculasCollectionMatriculas);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Asignaciones asignaciones) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Asignaciones persistentAsignaciones = em.find(Asignaciones.class, asignaciones.getId());
            Cursos idCursoOld = persistentAsignaciones.getIdCurso();
            Cursos idCursoNew = asignaciones.getIdCurso();
            Profesores idProfesorOld = persistentAsignaciones.getIdProfesor();
            Profesores idProfesorNew = asignaciones.getIdProfesor();
            Collection<Matriculas> matriculasCollectionOld = persistentAsignaciones.getMatriculasCollection();
            Collection<Matriculas> matriculasCollectionNew = asignaciones.getMatriculasCollection();
            List<String> illegalOrphanMessages = null;
            for (Matriculas matriculasCollectionOldMatriculas : matriculasCollectionOld) {
                if (!matriculasCollectionNew.contains(matriculasCollectionOldMatriculas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Matriculas " + matriculasCollectionOldMatriculas + " since its asignaciones field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCursoNew != null) {
                idCursoNew = em.getReference(idCursoNew.getClass(), idCursoNew.getId());
                asignaciones.setIdCurso(idCursoNew);
            }
            if (idProfesorNew != null) {
                idProfesorNew = em.getReference(idProfesorNew.getClass(), idProfesorNew.getId());
                asignaciones.setIdProfesor(idProfesorNew);
            }
            Collection<Matriculas> attachedMatriculasCollectionNew = new ArrayList<Matriculas>();
            for (Matriculas matriculasCollectionNewMatriculasToAttach : matriculasCollectionNew) {
                matriculasCollectionNewMatriculasToAttach = em.getReference(matriculasCollectionNewMatriculasToAttach.getClass(), matriculasCollectionNewMatriculasToAttach.getId());
                attachedMatriculasCollectionNew.add(matriculasCollectionNewMatriculasToAttach);
            }
            matriculasCollectionNew = attachedMatriculasCollectionNew;
            asignaciones.setMatriculasCollection(matriculasCollectionNew);
            asignaciones = em.merge(asignaciones);
            if (idCursoOld != null && !idCursoOld.equals(idCursoNew)) {
                idCursoOld.getAsignacionesCollection().remove(asignaciones);
                idCursoOld = em.merge(idCursoOld);
            }
            if (idCursoNew != null && !idCursoNew.equals(idCursoOld)) {
                idCursoNew.getAsignacionesCollection().add(asignaciones);
                idCursoNew = em.merge(idCursoNew);
            }
            if (idProfesorOld != null && !idProfesorOld.equals(idProfesorNew)) {
                idProfesorOld.getAsignacionesCollection().remove(asignaciones);
                idProfesorOld = em.merge(idProfesorOld);
            }
            if (idProfesorNew != null && !idProfesorNew.equals(idProfesorOld)) {
                idProfesorNew.getAsignacionesCollection().add(asignaciones);
                idProfesorNew = em.merge(idProfesorNew);
            }
            for (Matriculas matriculasCollectionNewMatriculas : matriculasCollectionNew) {
                if (!matriculasCollectionOld.contains(matriculasCollectionNewMatriculas)) {
                    Asignaciones oldAsignacionesOfMatriculasCollectionNewMatriculas = matriculasCollectionNewMatriculas.getAsignaciones();
                    matriculasCollectionNewMatriculas.setAsignaciones(asignaciones);
                    matriculasCollectionNewMatriculas = em.merge(matriculasCollectionNewMatriculas);
                    if (oldAsignacionesOfMatriculasCollectionNewMatriculas != null && !oldAsignacionesOfMatriculasCollectionNewMatriculas.equals(asignaciones)) {
                        oldAsignacionesOfMatriculasCollectionNewMatriculas.getMatriculasCollection().remove(matriculasCollectionNewMatriculas);
                        oldAsignacionesOfMatriculasCollectionNewMatriculas = em.merge(oldAsignacionesOfMatriculasCollectionNewMatriculas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = asignaciones.getId();
                if (findAsignaciones(id) == null) {
                    throw new NonexistentEntityException("The asignaciones with id " + id + " no longer exists.");
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
            Asignaciones asignaciones;
            try {
                asignaciones = em.getReference(Asignaciones.class, id);
                asignaciones.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The asignaciones with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Matriculas> matriculasCollectionOrphanCheck = asignaciones.getMatriculasCollection();
            for (Matriculas matriculasCollectionOrphanCheckMatriculas : matriculasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Asignaciones (" + asignaciones + ") cannot be destroyed since the Matriculas " + matriculasCollectionOrphanCheckMatriculas + " in its matriculasCollection field has a non-nullable asignaciones field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cursos idCurso = asignaciones.getIdCurso();
            if (idCurso != null) {
                idCurso.getAsignacionesCollection().remove(asignaciones);
                idCurso = em.merge(idCurso);
            }
            Profesores idProfesor = asignaciones.getIdProfesor();
            if (idProfesor != null) {
                idProfesor.getAsignacionesCollection().remove(asignaciones);
                idProfesor = em.merge(idProfesor);
            }
            em.remove(asignaciones);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Asignaciones> findAsignacionesEntities() {
        return findAsignacionesEntities(true, -1, -1);
    }

    public List<Asignaciones> findAsignacionesEntities(int maxResults, int firstResult) {
        return findAsignacionesEntities(false, maxResults, firstResult);
    }

    private List<Asignaciones> findAsignacionesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Asignaciones.class));
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

    public Asignaciones findAsignaciones(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Asignaciones.class, id);
        } finally {
            em.close();
        }
    }

    public int getAsignacionesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Asignaciones> rt = cq.from(Asignaciones.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
