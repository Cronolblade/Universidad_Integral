/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "calificaciones")
@NamedQueries({
    @NamedQuery(name = "Calificaciones.findAll", query = "SELECT c FROM Calificaciones c"),
    @NamedQuery(name = "Calificaciones.findById", query = "SELECT c FROM Calificaciones c WHERE c.id = :id"),
    @NamedQuery(name = "Calificaciones.findByNota", query = "SELECT c FROM Calificaciones c WHERE c.nota = :nota"),
    @NamedQuery(name = "Calificaciones.findByFechaRegistro", query = "SELECT c FROM Calificaciones c WHERE c.fechaRegistro = :fechaRegistro")})
public class Calificaciones implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "nota")
    private BigDecimal nota;
    @Lob
    @Size(max = 65535)
    @Column(name = "observaciones")
    private String observaciones;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;
    @JoinColumns({
        @JoinColumn(name = "id_matricula", referencedColumnName = "id"),
        @JoinColumn(name = "id_matricula", referencedColumnName = "id")})
    @ManyToOne(optional = false)
    private Matriculas matriculas;

    public Calificaciones() {
    }

    public Calificaciones(Integer id) {
        this.id = id;
    }

    public Calificaciones(Integer id, BigDecimal nota, Date fechaRegistro) {
        this.id = id;
        this.nota = nota;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Matriculas getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(Matriculas matriculas) {
        this.matriculas = matriculas;
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
        if (!(object instanceof Calificaciones)) {
            return false;
        }
        Calificaciones other = (Calificaciones) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Calificaciones[ id=" + id + " ]";
    }
    
}
