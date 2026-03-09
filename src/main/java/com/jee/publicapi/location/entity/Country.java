package com.jee.publicapi.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    @OneToMany(mappedBy="country")
    private List<State> states;

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

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}
    
    
    
}