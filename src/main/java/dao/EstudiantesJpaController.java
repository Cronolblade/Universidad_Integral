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
import dto.Estudiantes;
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
public class EstudiantesJpaController implements Serializable {

    public EstudiantesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estudiantes estudiantes) {
        if (estudiantes.getMatriculasCollection() == null) {
            estudiantes.setMatriculasCollection(new ArrayList<Matriculas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Carreras idCarrera = estudiantes.getIdCarrera();
            if (idCarrera != null) {
                idCarrera = em.getReference(idCarrera.getClass(), idCarrera.getId());
                estudiantes.setIdCarrera(idCarrera);
            }
            Collection<Matriculas> attachedMatriculasCollection = new ArrayList<Matriculas>();
            for (Matriculas matriculasCollectionMatriculasToAttach : estudiantes.getMatriculasCollection()) {
                matriculasCollectionMatriculasToAttach = em.getReference(matriculasCollectionMatriculasToAttach.getClass(), matriculasCollectionMatriculasToAttach.getId());
                attachedMatriculasCollection.add(matriculasCollectionMatriculasToAttach);
            }
            estudiantes.setMatriculasCollection(attachedMatriculasCollection);
            em.persist(estudiantes);
            if (idCarrera != null) {
                idCarrera.getEstudiantesCollection().add(estudiantes);
                idCarrera = em.merge(idCarrera);
            }
            for (Matriculas matriculasCollectionMatriculas : estudiantes.getMatriculasCollection()) {
                Estudiantes oldEstudiantesOfMatriculasCollectionMatriculas = matriculasCollectionMatriculas.getEstudiantes();
                matriculasCollectionMatriculas.setEstudiantes(estudiantes);
                matriculasCollectionMatriculas = em.merge(matriculasCollectionMatriculas);
                if (oldEstudiantesOfMatriculasCollectionMatriculas != null) {
                    oldEstudiantesOfMatriculasCollectionMatriculas.getMatriculasCollection().remove(matriculasCollectionMatriculas);
                    oldEstudiantesOfMatriculasCollectionMatriculas = em.merge(oldEstudiantesOfMatriculasCollectionMatriculas);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estudiantes estudiantes) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estudiantes persistentEstudiantes = em.find(Estudiantes.class, estudiantes.getId());
            Carreras idCarreraOld = persistentEstudiantes.getIdCarrera();
            Carreras idCarreraNew = estudiantes.getIdCarrera();
            Collection<Matriculas> matriculasCollectionOld = persistentEstudiantes.getMatriculasCollection();
            Collection<Matriculas> matriculasCollectionNew = estudiantes.getMatriculasCollection();
            List<String> illegalOrphanMessages = null;
            for (Matriculas matriculasCollectionOldMatriculas : matriculasCollectionOld) {
                if (!matriculasCollectionNew.contains(matriculasCollectionOldMatriculas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Matriculas " + matriculasCollectionOldMatriculas + " since its estudiantes field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCarreraNew != null) {
                idCarreraNew = em.getReference(idCarreraNew.getClass(), idCarreraNew.getId());
                estudiantes.setIdCarrera(idCarreraNew);
            }
            Collection<Matriculas> attachedMatriculasCollectionNew = new ArrayList<Matriculas>();
            for (Matriculas matriculasCollectionNewMatriculasToAttach : matriculasCollectionNew) {
                matriculasCollectionNewMatriculasToAttach = em.getReference(matriculasCollectionNewMatriculasToAttach.getClass(), matriculasCollectionNewMatriculasToAttach.getId());
                attachedMatriculasCollectionNew.add(matriculasCollectionNewMatriculasToAttach);
            }
            matriculasCollectionNew = attachedMatriculasCollectionNew;
            estudiantes.setMatriculasCollection(matriculasCollectionNew);
            estudiantes = em.merge(estudiantes);
            if (idCarreraOld != null && !idCarreraOld.equals(idCarreraNew)) {
                idCarreraOld.getEstudiantesCollection().remove(estudiantes);
                idCarreraOld = em.merge(idCarreraOld);
            }
            if (idCarreraNew != null && !idCarreraNew.equals(idCarreraOld)) {
                idCarreraNew.getEstudiantesCollection().add(estudiantes);
                idCarreraNew = em.merge(idCarreraNew);
            }
            for (Matriculas matriculasCollectionNewMatriculas : matriculasCollectionNew) {
                if (!matriculasCollectionOld.contains(matriculasCollectionNewMatriculas)) {
                    Estudiantes oldEstudiantesOfMatriculasCollectionNewMatriculas = matriculasCollectionNewMatriculas.getEstudiantes();
                    matriculasCollectionNewMatriculas.setEstudiantes(estudiantes);
                    matriculasCollectionNewMatriculas = em.merge(matriculasCollectionNewMatriculas);
                    if (oldEstudiantesOfMatriculasCollectionNewMatriculas != null && !oldEstudiantesOfMatriculasCollectionNewMatriculas.equals(estudiantes)) {
                        oldEstudiantesOfMatriculasCollectionNewMatriculas.getMatriculasCollection().remove(matriculasCollectionNewMatriculas);
                        oldEstudiantesOfMatriculasCollectionNewMatriculas = em.merge(oldEstudiantesOfMatriculasCollectionNewMatriculas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = estudiantes.getId();
                if (findEstudiantes(id) == null) {
                    throw new NonexistentEntityException("The estudiantes with id " + id + " no longer exists.");
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
            Estudiantes estudiantes;
            try {
                estudiantes = em.getReference(Estudiantes.class, id);
                estudiantes.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estudiantes with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Matriculas> matriculasCollectionOrphanCheck = estudiantes.getMatriculasCollection();
            for (Matriculas matriculasCollectionOrphanCheckMatriculas : matriculasCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estudiantes (" + estudiantes + ") cannot be destroyed since the Matriculas " + matriculasCollectionOrphanCheckMatriculas + " in its matriculasCollection field has a non-nullable estudiantes field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Carreras idCarrera = estudiantes.getIdCarrera();
            if (idCarrera != null) {
                idCarrera.getEstudiantesCollection().remove(estudiantes);
                idCarrera = em.merge(idCarrera);
            }
            em.remove(estudiantes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estudiantes> findEstudiantesEntities() {
        return findEstudiantesEntities(true, -1, -1);
    }

    public List<Estudiantes> findEstudiantesEntities(int maxResults, int firstResult) {
        return findEstudiantesEntities(false, maxResults, firstResult);
    }

    private List<Estudiantes> findEstudiantesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estudiantes.class));
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

    public Estudiantes findEstudiantes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estudiantes.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstudiantesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estudiantes> rt = cq.from(Estudiantes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
