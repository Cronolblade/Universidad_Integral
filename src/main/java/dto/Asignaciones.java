/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author NITRO
 */
@Entity
@Table(name = "asignaciones")
@NamedQueries({
    @NamedQuery(name = "Asignaciones.findAll", query = "SELECT a FROM Asignaciones a"),
    @NamedQuery(name = "Asignaciones.findById", query = "SELECT a FROM Asignaciones a WHERE a.id = :id"),
    @NamedQuery(name = "Asignaciones.findByAnio", query = "SELECT a FROM Asignaciones a WHERE a.anio = :anio"),
    @NamedQuery(name = "Asignaciones.findByCiclo", query = "SELECT a FROM Asignaciones a WHERE a.ciclo = :ciclo")})
public class Asignaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio")
    private int anio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "ciclo")
    private String ciclo;
    @JoinColumn(name = "id_curso", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Cursos idCurso;
    @JoinColumn(name = "id_profesor", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Profesores idProfesor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asignaciones")
    private Collection<Matriculas> matriculasCollection;

    public Asignaciones() {
    }

    public Asignaciones(Integer id) {
        this.id = id;
    }

    public Asignaciones(Integer id, int anio, String ciclo) {
        this.id = id;
        this.anio = anio;
        this.ciclo = ciclo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public Cursos getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Cursos idCurso) {
        this.idCurso = idCurso;
    }

    public Profesores getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(Profesores idProfesor) {
        this.idProfesor = idProfesor;
    }

    public Collection<Matriculas> getMatriculasCollection() {
        return matriculasCollection;
    }

    public void setMatriculasCollection(Collection<Matriculas> matriculasCollection) {
        this.matriculasCollection = matriculasCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Asignaciones)) {
            return false;
        }
        Asignaciones other = (Asignaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Asignaciones[ id=" + id + " ]";
    }
    
}
