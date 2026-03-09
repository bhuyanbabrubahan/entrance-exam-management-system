package com.jee.publicapi.location.entity;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="district_id")
    private District district;

    @OneToMany(mappedBy="city")
    private List<Pincode> pincodes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public List<Pincode> getPincodes() {
		return pincodes;
	}

	public void setPincodes(List<Pincode> pincodes) {
		this.pincodes = pincodes;
	}
    
    
}