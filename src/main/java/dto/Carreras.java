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
import javax.persistence.ManyToMany;
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
@Table(name = "carreras")
@NamedQueries({
    @NamedQuery(name = "Carreras.findAll", query = "SELECT c FROM Carreras c"),
    @NamedQuery(name = "Carreras.findById", query = "SELECT c FROM Carreras c WHERE c.id = :id"),
    @NamedQuery(name = "Carreras.findByNombre", query = "SELECT c FROM Carreras c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Carreras.findByDuracionAnios", query = "SELECT c FROM Carreras c WHERE c.duracionAnios = :duracionAnios")})
public class Carreras implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duracion_anios")
    private int duracionAnios;
    @ManyToMany(mappedBy = "carrerasCollection")
    private Collection<Cursos> cursosCollection;
    @JoinColumn(name = "id_facultad", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Facultades idFacultad;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCarrera")
    private Collection<Estudiantes> estudiantesCollection;

    public Carreras() {
    }

    public Carreras(Integer id) {
        this.id = id;
    }

    public Carreras(Integer id, String nombre, int duracionAnios) {
        this.id = id;
        this.nombre = nombre;
        this.duracionAnios = duracionAnios;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDuracionAnios() {
        return duracionAnios;
    }

    public void setDuracionAnios(int duracionAnios) {
        this.duracionAnios = duracionAnios;
    }

    public Collection<Cursos> getCursosCollection() {
        return cursosCollection;
    }

    public void setCursosCollection(Collection<Cursos> cursosCollection) {
        this.cursosCollection = cursosCollection;
    }

    public Facultades getIdFacultad() {
        return idFacultad;
    }

    public void setIdFacultad(Facultades idFacultad) {
        this.idFacultad = idFacultad;
    }

    public Collection<Estudiantes> getEstudiantesCollection() {
        return estudiantesCollection;
    }

    public void setEstudiantesCollection(Collection<Estudiantes> estudiantesCollection) {
        this.estudiantesCollection = estudiantesCollection;
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
        if (!(object instanceof Carreras)) {
            return false;
        }
        Carreras other = (Carreras) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Carreras[ id=" + id + " ]";
    }
    
}
