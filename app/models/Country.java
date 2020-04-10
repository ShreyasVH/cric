package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class Country extends Model
{
	@Id
	@Column(name = "id", columnDefinition = "int UNSIGNED", nullable = false)
	private Long id;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
	private Date createdAt;

	@Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP", nullable = false)
	private Date updatedAt;
}