package com.openclassrooms.new_safety_net.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.new_safety_net.model.Persons;

@Repository
public interface PersonsRepository extends CrudRepository<Persons, String> {

}