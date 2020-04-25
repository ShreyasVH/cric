package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "dismissal_modes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DismissalMode extends Model
{
    @Id
    private Long id;

    private String name;
}
