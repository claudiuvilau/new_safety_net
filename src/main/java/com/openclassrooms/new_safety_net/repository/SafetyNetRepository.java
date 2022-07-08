package com.openclassrooms.new_safety_net.repository;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.openclassrooms.new_safety_net.model.Persons;

@Repository
public interface SafetyNetRepository {

    /*
     * http://localhost:8080/person
     * Cet endpoint permettra d’effectuer les actions suivantes via Post/Put/Delete
     * avec HTTP :
     * ● ajouter une nouvelle personne
     * ● mettre à jour une personne existante (pour le moment, supposons que le
     * prénom et le nom de
     * famille ne changent pas, mais que les autres champs peuvent être modifiés)
     * ● supprimer une personne (utilisez une combinaison de prénom et de nom comme
     * identificateur
     * unique).
     */

    List<Persons> getPersons(String elemjson) throws IOException;

    void postPerson(String nomperson);

    void putPerson(String nomperson);

    void deletePerson(String nomperson);

    /*
     * http://localhost:8080/firestation
     * Cet endpoint permettra d’effectuer les actions suivantes via Post/Put/Delete
     * avec HTTP :
     * ● ajout d'un mapping caserne/adresse
     * ● mettre à jour le numéro de la caserne de pompiers d'une adresse
     * ● supprimer le mapping d'une caserne ou d'une adresse.
     */

    void getFirestation(String firestation);

    void postFirestation(String firestation);

    void putFirestation(String firestation);

    void deleteFirestation(String firestation);

    /*
     * http://localhost:8080/medicalRecord
     * Cet endpoint permettra d’effectuer les actions suivantes via Post/Put/Delete
     * HTTP :
     * ● ajouter un dossier médical
     * ● mettre à jour un dossier médical existant (comme évoqué précédemment,
     * supposer que le
     * prénom et le nom de famille ne changent pas)
     * ● supprimer un dossier médical (utilisez une combinaison de prénom et de nom
     * comme
     * identificateur unique)
     */

    void getMedicalRecord(String medicalrecord);

    void postMedicalRecord(String medicalrecord);

    void putMedicalRecord(String medicalrecord);

    void deleteMedicalRecord(String medicalrecord);
}