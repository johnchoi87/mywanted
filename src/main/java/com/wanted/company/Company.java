package com.wanted.company;

import javax.persistence.*;

@Entity
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
