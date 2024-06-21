package com.rental.model;

import java.time.Instant;
import java.util.List;

import com.rental.enums.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="rental")
@Entity
public class HouseDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String title;

	private String address;

	private String streetAddress;

	private String apartmentName;

	private double houselatitude;

	private double houselongitude;

	private String propertytype;

	private double sqft;

	private double garage;

	private double bedrooms;

	private double bathrooms;

	private String constructiontype;

	private Integer yearbuilt;

	private double expectedrent;

	private double deposit;

//	@Enumerated(EnumType.STRING)
	private String rentType;

	private List<String> utilities;

	private Instant availabilityfrom;

	private Instant availabilitytill;

	private String description;

	private String  smokingpolicy;

	private String vegetarianpreference;

	private String petsFriendly;


	private List<String> addaminities;

	@Column(name="deleted")
	@Default
	private boolean deleted=false;

	@Column(name="status")
	@Enumerated(EnumType.STRING)
	private HouseStatus housestatus;

	@Column(name="validity_status")
	@Enumerated(EnumType.STRING)
	private RentalValidityStatus rentalValidityStatus;

	@OneToMany(mappedBy = "houseDetails", cascade = CascadeType.ALL)
	private List<Image> addimages;

	@CreationTimestamp
	private Instant createdAt;

	@UpdateTimestamp
	private Instant updatedAt;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id", referencedColumnName = "id")
	private User userId;
	public Integer getId() {
		return id;
	}
	public void setUserId(User userId) {
		this.userId = userId;
	}
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}


}




