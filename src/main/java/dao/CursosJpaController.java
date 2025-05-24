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
import dto.Carreras;
import java.util.ArrayList;
import java.util.Collection;
import dto.Asignaciones;
import dto.Cursos;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author NITRO
 */
public class CursosJpaController implements Serializable {

    public CursosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cursos cursos) {
        if (cursos.getCarrerasCollection() == null) {
            cursos.setCarrerasCollection(new ArrayList<Carreras>());
        }
        if (cursos.getAsignacionesCollection() == null) {
            cursos.setAsignacionesCollection(new ArrayList<Asignaciones>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Carreras> attachedCarrerasCollection = new ArrayList<Carreras>();
            for (Carreras carrerasCollectionCarrerasToAttach : cursos.getCarrerasCollection()) {
                carrerasCollectionCarrerasToAttach = em.getReference(carrerasCollectionCarrerasToAttach.getClass(), carrerasCollectionCarrerasToAttach.getId());
                attachedCarrerasCollection.add(carrerasCollectionCarrerasToAttach);
            }
            cursos.setCarrerasCollection(attachedCarrerasCollection);
            Collection<Asignaciones> attachedAsignacionesCollection = new ArrayList<Asignaciones>();
            for (Asignaciones asignacionesCollectionAsignacionesToAttach : cursos.getAsignacionesCollection()) {
                asignacionesCollectionAsignacionesToAttach = em.getReference(asignacionesCollectionAsignacionesToAttach.getClass(), asignacionesCollectionAsignacionesToAttach.getId());
                attachedAsignacionesCollection.add(asignacionesCollectionAsignacionesToAttach);
            }
            cursos.setAsignacionesCollection(attachedAsignacionesCollection);
            em.persist(cursos);
            for (Carreras carrerasCollectionCarreras : cursos.getCarrerasCollection()) {
                carrerasCollectionCarreras.getCursosCollection().add(cursos);
                carrerasCollectionCarreras = em.merge(carrerasCollectionCarreras);
            }
            for (Asignaciones asignacionesCollectionAsignaciones : cursos.getAsignacionesCollection()) {
                Cursos oldIdCursoOfAsignacionesCollectionAsignaciones = asignacionesCollectionAsignaciones.getIdCurso();
                asignacionesCollectionAsignaciones.setIdCurso(cursos);
                asignacionesCollectionAsignaciones = em.merge(asignacionesCollectionAsignaciones);
                if (oldIdCursoOfAsignacionesCollectionAsignaciones != null) {
                    oldIdCursoOfAsignacionesCollectionAsignaciones.getAsignacionesCollection().remove(asignacionesCollectionAsignaciones);
                    oldIdCursoOfAsignacionesCollectionAsignaciones = em.merge(oldIdCursoOfAsignacionesCollectionAsignaciones);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cursos cursos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cursos persistentCursos = em.find(Cursos.class, cursos.getId());
            Collection<Carreras> carrerasCollectionOld = persistentCursos.getCarrerasCollection();
            Collection<Carreras> carrerasCollectionNew = cursos.getCarrerasCollection();
            Collection<Asignaciones> asignacionesCollectionOld = persistentCursos.getAsignacionesCollection();
            Collection<Asignaciones> asignacionesCollectionNew = cursos.getAsignacionesCollection();
            List<String> illegalOrphanMessages = null;
            for (Asignaciones asignacionesCollectionOldAsignaciones : asignacionesCollectionOld) {
                if (!asignacionesCollectionNew.contains(asignacionesCollectionOldAsignaciones)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Asignaciones " + asignacionesCollectionOldAsignaciones + " since its idCurso field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Carreras> attachedCarrerasCollectionNew = new ArrayList<Carreras>();
            for (Carreras carrerasCollectionNewCarrerasToAttach : carrerasCollectionNew) {
                carrerasCollectionNewCarrerasToAttach = em.getReference(carrerasCollectionNewCarrerasToAttach.getClass(), carrerasCollectionNewCarrerasToAttach.getId());
                attachedCarrerasCollectionNew.add(carrerasCollectionNewCarrerasToAttach);
            }
            carrerasCollectionNew = attachedCarrerasCollectionNew;
            cursos.setCarrerasCollection(carrerasCollectionNew);
            Collection<Asignaciones> attachedAsignacionesCollectionNew = new ArrayList<Asignaciones>();
            for (Asignaciones asignacionesCollectionNewAsignacionesToAttach : asignacionesCollectionNew) {
                asignacionesCollectionNewAsignacionesToAttach = em.getReference(asignacionesCollectionNewAsignacionesToAttach.getClass(), asignacionesCollectionNewAsignacionesToAttach.getId());
                attachedAsignacionesCollectionNew.add(asignacionesCollectionNewAsignacionesToAttach);
            }
            asignacionesCollectionNew = attachedAsignacionesCollectionNew;
            cursos.setAsignacionesCollection(asignacionesCollectionNew);
            cursos = em.merge(cursos);
            for (Carreras carrerasCollectionOldCarreras : carrerasCollectionOld) {
                if (!carrerasCollectionNew.contains(carrerasCollectionOldCarreras)) {
                    carrerasCollectionOldCarreras.getCursosCollection().remove(cursos);
                    carrerasCollectionOldCarreras = em.merge(carrerasCollectionOldCarreras);
                }
            }
            for (Carreras carrerasCollectionNewCarreras : carrerasCollectionNew) {
                if (!carrerasCollectionOld.contains(carrerasCollectionNewCarreras)) {
                    carrerasCollectionNewCarreras.getCursosCollection().add(cursos);
                    carrerasCollectionNewCarreras = em.merge(carrerasCollectionNewCarreras);
                }
            }
            for (Asignaciones asignacionesCollectionNewAsignaciones : asignacionesCollectionNew) {
                if (!asignacionesCollectionOld.contains(asignacionesCollectionNewAsignaciones)) {
                    Cursos oldIdCursoOfAsignacionesCollectionNewAsignaciones = asignacionesCollectionNewAsignaciones.getIdCurso();
                    asignacionesCollectionNewAsignaciones.setIdCurso(cursos);
                    asignacionesCollectionNewAsignaciones = em.merge(asignacionesCollectionNewAsignaciones);
                    if (oldIdCursoOfAsignacionesCollectionNewAsignaciones != null && !oldIdCursoOfAsignacionesCollectionNewAsignaciones.equals(cursos)) {
                        oldIdCursoOfAsignacionesCollectionNewAsignaciones.getAsignacionesCollection().remove(asignacionesCollectionNewAsignaciones);
                        oldIdCursoOfAsignacionesCollectionNewAsignaciones = em.merge(oldIdCursoOfAsignacionesCollectionNewAsignaciones);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cursos.getId();
                if (findCursos(id) == null) {
                    throw new NonexistentEntityException("The cursos with id " + id + " no longer exists.");
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
            Cursos cursos;
            try {
                cursos = em.getReference(Cursos.class, id);
                cursos.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cursos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Asignaciones> asignacionesCollectionOrphanCheck = cursos.getAsignacionesCollection();
            for (Asignaciones asignacionesCollectionOrphanCheckAsignaciones : asignacionesCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cursos (" + cursos + ") cannot be destroyed since the Asignaciones " + asignacionesCollectionOrphanCheckAsignaciones + " in its asignacionesCollection field has a non-nullable idCurso field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Carreras> carrerasCollection = cursos.getCarrerasCollection();
            for (Carreras carrerasCollectionCarreras : carrerasCollection) {
                carrerasCollectionCarreras.getCursosCollection().remove(cursos);
                carrerasCollectionCarreras = em.merge(carrerasCollectionCarreras);
            }
            em.remove(cursos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cursos> findCursosEntities() {
        return findCursosEntities(true, -1, -1);
    }

    public List<Cursos> findCursosEntities(int maxResults, int firstResult) {
        return findCursosEntities(false, maxResults, firstResult);
    }

    private List<Cursos> findCursosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cursos.class));
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

    public Cursos findCursos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cursos.class, id);
        } finally {
            em.close();
        }
    }

    public int getCursosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cursos> rt = cq.from(Cursos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
