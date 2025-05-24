/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author NITRO
 */
@Entity
@Table(name = "matriculas")
@NamedQueries({
    @NamedQuery(name = "Matriculas.findAll", query = "SELECT m FROM Matriculas m"),
    @NamedQuery(name = "Matriculas.findById", query = "SELECT m FROM Matriculas m WHERE m.id = :id"),
    @NamedQuery(name = "Matriculas.findByFechaMatricula", query = "SELECT m FROM Matriculas m WHERE m.fechaMatricula = :fechaMatricula"),
    @NamedQuery(name = "Matriculas.findByEstado", query = "SELECT m FROM Matriculas m WHERE m.estado = :estado")})
public class Matriculas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_matricula")
    @Temporal(TemporalType.DATE)
    private Date fechaMatricula;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(name = "estado")
    private String estado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "matriculas")
    private Collection<Calificaciones> calificacionesCollection;
    @JoinColumns({
        @JoinColumn(name = "id_asignacion", referencedColumnName = "id"),
        @JoinColumn(name = "id_asignacion", referencedColumnName = "id")})
    @ManyToOne(optional = false)
    private Asignaciones asignaciones;
    @JoinColumns({
        @JoinColumn(name = "id_estudiante", referencedColumnName = "id"),
        @JoinColumn(name = "id_estudiante", referencedColumnName = "id")})
    @ManyToOne(optional = false)
    private Estudiantes estudiantes;

    public Matriculas() {
    }

    public Matriculas(Integer id) {
        this.id = id;
    }

    public Matriculas(Integer id, Date fechaMatricula, String estado) {
        this.id = id;
        this.fechaMatricula = fechaMatricula;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(Date fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Collection<Calificaciones> getCalificacionesCollection() {
        return calificacionesCollection;
    }

    public void setCalificacionesCollection(Collection<Calificaciones> calificacionesCollection) {
        this.calificacionesCollection = calificacionesCollection;
    }

    public Asignaciones getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(Asignaciones asignaciones) {
        this.asignaciones = asignaciones;
    }

    public Estudiantes getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(Estudiantes estudiantes) {
        this.estudiantes = estudiantes;
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
        if (!(object instanceof Matriculas)) {
            return false;
        }
        Matriculas other = (Matriculas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Matriculas[ id=" + id + " ]";
    }
    
}
