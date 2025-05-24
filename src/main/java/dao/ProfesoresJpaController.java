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
import dto.Profesores;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author NITRO
 */
public class ProfesoresJpaController implements Serializable {

    public ProfesoresJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Profesores profesores) {
        if (profesores.getAsignacionesCollection() == null) {
            profesores.setAsignacionesCollection(new ArrayList<Asignaciones>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Asignaciones> attachedAsignacionesCollection = new ArrayList<Asignaciones>();
            for (Asignaciones asignacionesCollectionAsignacionesToAttach : profesores.getAsignacionesCollection()) {
                asignacionesCollectionAsignacionesToAttach = em.getReference(asignacionesCollectionAsignacionesToAttach.getClass(), asignacionesCollectionAsignacionesToAttach.getId());
                attachedAsignacionesCollection.add(asignacionesCollectionAsignacionesToAttach);
            }
            profesores.setAsignacionesCollection(attachedAsignacionesCollection);
            em.persist(profesores);
            for (Asignaciones asignacionesCollectionAsignaciones : profesores.getAsignacionesCollection()) {
                Profesores oldIdProfesorOfAsignacionesCollectionAsignaciones = asignacionesCollectionAsignaciones.getIdProfesor();
                asignacionesCollectionAsignaciones.setIdProfesor(profesores);
                asignacionesCollectionAsignaciones = em.merge(asignacionesCollectionAsignaciones);
                if (oldIdProfesorOfAsignacionesCollectionAsignaciones != null) {
                    oldIdProfesorOfAsignacionesCollectionAsignaciones.getAsignacionesCollection().remove(asignacionesCollectionAsignaciones);
                    oldIdProfesorOfAsignacionesCollectionAsignaciones = em.merge(oldIdProfesorOfAsignacionesCollectionAsignaciones);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Profesores profesores) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesores persistentProfesores = em.find(Profesores.class, profesores.getId());
            Collection<Asignaciones> asignacionesCollectionOld = persistentProfesores.getAsignacionesCollection();
            Collection<Asignaciones> asignacionesCollectionNew = profesores.getAsignacionesCollection();
            List<String> illegalOrphanMessages = null;
            for (Asignaciones asignacionesCollectionOldAsignaciones : asignacionesCollectionOld) {
                if (!asignacionesCollectionNew.contains(asignacionesCollectionOldAsignaciones)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Asignaciones " + asignacionesCollectionOldAsignaciones + " since its idProfesor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Asignaciones> attachedAsignacionesCollectionNew = new ArrayList<Asignaciones>();
            for (Asignaciones asignacionesCollectionNewAsignacionesToAttach : asignacionesCollectionNew) {
                asignacionesCollectionNewAsignacionesToAttach = em.getReference(asignacionesCollectionNewAsignacionesToAttach.getClass(), asignacionesCollectionNewAsignacionesToAttach.getId());
                attachedAsignacionesCollectionNew.add(asignacionesCollectionNewAsignacionesToAttach);
            }
            asignacionesCollectionNew = attachedAsignacionesCollectionNew;
            profesores.setAsignacionesCollection(asignacionesCollectionNew);
            profesores = em.merge(profesores);
            for (Asignaciones asignacionesCollectionNewAsignaciones : asignacionesCollectionNew) {
                if (!asignacionesCollectionOld.contains(asignacionesCollectionNewAsignaciones)) {
                    Profesores oldIdProfesorOfAsignacionesCollectionNewAsignaciones = asignacionesCollectionNewAsignaciones.getIdProfesor();
                    asignacionesCollectionNewAsignaciones.setIdProfesor(profesores);
                    asignacionesCollectionNewAsignaciones = em.merge(asignacionesCollectionNewAsignaciones);
                    if (oldIdProfesorOfAsignacionesCollectionNewAsignaciones != null && !oldIdProfesorOfAsignacionesCollectionNewAsignaciones.equals(profesores)) {
                        oldIdProfesorOfAsignacionesCollectionNewAsignaciones.getAsignacionesCollection().remove(asignacionesCollectionNewAsignaciones);
                        oldIdProfesorOfAsignacionesCollectionNewAsignaciones = em.merge(oldIdProfesorOfAsignacionesCollectionNewAsignaciones);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = profesores.getId();
                if (findProfesores(id) == null) {
                    throw new NonexistentEntityException("The profesores with id " + id + " no longer exists.");
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
            Profesores profesores;
            try {
                profesores = em.getReference(Profesores.class, id);
                profesores.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The profesores with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Asignaciones> asignacionesCollectionOrphanCheck = profesores.getAsignacionesCollection();
            for (Asignaciones asignacionesCollectionOrphanCheckAsignaciones : asignacionesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Profesores (" + profesores + ") cannot be destroyed since the Asignaciones " + asignacionesCollectionOrphanCheckAsignaciones + " in its asignacionesCollection field has a non-nullable idProfesor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(profesores);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Profesores> findProfesoresEntities() {
        return findProfesoresEntities(true, -1, -1);
    }

    public List<Profesores> findProfesoresEntities(int maxResults, int firstResult) {
        return findProfesoresEntities(false, maxResults, firstResult);
    }

    private List<Profesores> findProfesoresEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Profesores.class));
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

    public Profesores findProfesores(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Profesores.class, id);
        } finally {
            em.close();
        }
    }

    public int getProfesoresCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Profesores> rt = cq.from(Profesores.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
