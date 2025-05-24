/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dto.Calificaciones;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import dto.Matriculas;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author NITRO
 */
public class CalificacionesJpaController implements Serializable {

    public CalificacionesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Calificaciones calificaciones) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Matriculas matriculas = calificaciones.getMatriculas();
            if (matriculas != null) {
                matriculas = em.getReference(matriculas.getClass(), matriculas.getId());
                calificaciones.setMatriculas(matriculas);
            }
            em.persist(calificaciones);
            if (matriculas != null) {
                matriculas.getCalificacionesCollection().add(calificaciones);
                matriculas = em.merge(matriculas);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Calificaciones calificaciones) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Calificaciones persistentCalificaciones = em.find(Calificaciones.class, calificaciones.getId());
            Matriculas matriculasOld = persistentCalificaciones.getMatriculas();
            Matriculas matriculasNew = calificaciones.getMatriculas();
            if (matriculasNew != null) {
                matriculasNew = em.getReference(matriculasNew.getClass(), matriculasNew.getId());
                calificaciones.setMatriculas(matriculasNew);
            }
            calificaciones = em.merge(calificaciones);
            if (matriculasOld != null && !matriculasOld.equals(matriculasNew)) {
                matriculasOld.getCalificacionesCollection().remove(calificaciones);
                matriculasOld = em.merge(matriculasOld);
            }
            if (matriculasNew != null && !matriculasNew.equals(matriculasOld)) {
                matriculasNew.getCalificacionesCollection().add(calificaciones);
                matriculasNew = em.merge(matriculasNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = calificaciones.getId();
                if (findCalificaciones(id) == null) {
                    throw new NonexistentEntityException("The calificaciones with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Calificaciones calificaciones;
            try {
                calificaciones = em.getReference(Calificaciones.class, id);
                calificaciones.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The calificaciones with id " + id + " no longer exists.", enfe);
            }
            Matriculas matriculas = calificaciones.getMatriculas();
            if (matriculas != null) {
                matriculas.getCalificacionesCollection().remove(calificaciones);
                matriculas = em.merge(matriculas);
            }
            em.remove(calificaciones);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Calificaciones> findCalificacionesEntities() {
        return findCalificacionesEntities(true, -1, -1);
    }

    public List<Calificaciones> findCalificacionesEntities(int maxResults, int firstResult) {
        return findCalificacionesEntities(false, maxResults, firstResult);
    }

    private List<Calificaciones> findCalificacionesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Calificaciones.class));
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

    public Calificaciones findCalificaciones(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Calificaciones.class, id);
        } finally {
            em.close();
        }
    }

    public int getCalificacionesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Calificaciones> rt = cq.from(Calificaciones.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
