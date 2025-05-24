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
import dto.Facultades;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author NITRO
 */
public class FacultadesJpaController implements Serializable {

    public FacultadesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facultades facultades) {
        if (facultades.getCarrerasCollection() == null) {
            facultades.setCarrerasCollection(new ArrayList<Carreras>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Carreras> attachedCarrerasCollection = new ArrayList<Carreras>();
            for (Carreras carrerasCollectionCarrerasToAttach : facultades.getCarrerasCollection()) {
                carrerasCollectionCarrerasToAttach = em.getReference(carrerasCollectionCarrerasToAttach.getClass(), carrerasCollectionCarrerasToAttach.getId());
                attachedCarrerasCollection.add(carrerasCollectionCarrerasToAttach);
            }
            facultades.setCarrerasCollection(attachedCarrerasCollection);
            em.persist(facultades);
            for (Carreras carrerasCollectionCarreras : facultades.getCarrerasCollection()) {
                Facultades oldIdFacultadOfCarrerasCollectionCarreras = carrerasCollectionCarreras.getIdFacultad();
                carrerasCollectionCarreras.setIdFacultad(facultades);
                carrerasCollectionCarreras = em.merge(carrerasCollectionCarreras);
                if (oldIdFacultadOfCarrerasCollectionCarreras != null) {
                    oldIdFacultadOfCarrerasCollectionCarreras.getCarrerasCollection().remove(carrerasCollectionCarreras);
                    oldIdFacultadOfCarrerasCollectionCarreras = em.merge(oldIdFacultadOfCarrerasCollectionCarreras);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Facultades facultades) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Facultades persistentFacultades = em.find(Facultades.class, facultades.getId());
            Collection<Carreras> carrerasCollectionOld = persistentFacultades.getCarrerasCollection();
            Collection<Carreras> carrerasCollectionNew = facultades.getCarrerasCollection();
            List<String> illegalOrphanMessages = null;
            for (Carreras carrerasCollectionOldCarreras : carrerasCollectionOld) {
                if (!carrerasCollectionNew.contains(carrerasCollectionOldCarreras)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Carreras " + carrerasCollectionOldCarreras + " since its idFacultad field is not nullable.");
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
            facultades.setCarrerasCollection(carrerasCollectionNew);
            facultades = em.merge(facultades);
            for (Carreras carrerasCollectionNewCarreras : carrerasCollectionNew) {
                if (!carrerasCollectionOld.contains(carrerasCollectionNewCarreras)) {
                    Facultades oldIdFacultadOfCarrerasCollectionNewCarreras = carrerasCollectionNewCarreras.getIdFacultad();
                    carrerasCollectionNewCarreras.setIdFacultad(facultades);
                    carrerasCollectionNewCarreras = em.merge(carrerasCollectionNewCarreras);
                    if (oldIdFacultadOfCarrerasCollectionNewCarreras != null && !oldIdFacultadOfCarrerasCollectionNewCarreras.equals(facultades)) {
                        oldIdFacultadOfCarrerasCollectionNewCarreras.getCarrerasCollection().remove(carrerasCollectionNewCarreras);
                        oldIdFacultadOfCarrerasCollectionNewCarreras = em.merge(oldIdFacultadOfCarrerasCollectionNewCarreras);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = facultades.getId();
                if (findFacultades(id) == null) {
                    throw new NonexistentEntityException("The facultades with id " + id + " no longer exists.");
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
            Facultades facultades;
            try {
                facultades = em.getReference(Facultades.class, id);
                facultades.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facultades with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Carreras> carrerasCollectionOrphanCheck = facultades.getCarrerasCollection();
            for (Carreras carrerasCollectionOrphanCheckCarreras : carrerasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facultades (" + facultades + ") cannot be destroyed since the Carreras " + carrerasCollectionOrphanCheckCarreras + " in its carrerasCollection field has a non-nullable idFacultad field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(facultades);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Facultades> findFacultadesEntities() {
        return findFacultadesEntities(true, -1, -1);
    }

    public List<Facultades> findFacultadesEntities(int maxResults, int firstResult) {
        return findFacultadesEntities(false, maxResults, firstResult);
    }

    private List<Facultades> findFacultadesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facultades.class));
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

    public Facultades findFacultades(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facultades.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacultadesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facultades> rt = cq.from(Facultades.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
