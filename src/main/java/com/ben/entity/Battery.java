package com.ben.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Data
@Accessors(chain = true)
public class Battery {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private long postcode;
    private Double capacity;

    @Override
    public boolean equals(Object o){
        if (o == this)
            return true;
        if (!(o instanceof Battery))
            return false;
        Battery that = (Battery) o;
        return new EqualsBuilder()
                .append(this.postcode, that.postcode)
                .append(this.capacity, that.capacity)
                .append(this.name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder()
                .append(this.postcode).toHashCode();
    }

}
