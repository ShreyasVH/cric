package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;

import javax.persistence.*;

import io.ebean.annotation.Cache;
import io.ebean.annotation.CacheQueryTuning;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import requests.CreateCountryRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "countries")
@Getter
@Setter
@Cache(enableQueryCache=true)
@CacheQueryTuning(maxSecsToLive = 3600)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country extends Model
{
	@Id
	@Column(name = "id", columnDefinition = "int UNSIGNED", nullable = false)
	private Long id;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	public Country(CreateCountryRequest createCountryRequest)
	{
		this.id = createCountryRequest.getId();
		this.name = createCountryRequest.getName();
	}
}