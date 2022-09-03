package com.openclassrooms.new_safety_net.repository;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.openclassrooms.new_safety_net.model.ChildAlert;
import com.openclassrooms.new_safety_net.model.CommunityEmail;
import com.openclassrooms.new_safety_net.model.FireAddress;
import com.openclassrooms.new_safety_net.model.Firestations;
import com.openclassrooms.new_safety_net.model.FoyerChildrenAdultsToFireStation;
import com.openclassrooms.new_safety_net.model.Medicalrecords;
import com.openclassrooms.new_safety_net.model.PersonInfo;
import com.openclassrooms.new_safety_net.model.Persons;
import com.openclassrooms.new_safety_net.model.PersonsFireStation;
import com.openclassrooms.new_safety_net.model.PhoneAlert;

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

    public List<Persons> getAPerson(String firstNamelastName, String elemjson) throws IOException;

    boolean postPerson(Persons persons);

    boolean putPerson(Persons persons, String firstName, String lastName);

    boolean deletePerson(String firstName, String lastName);

    /*
     * http://localhost:8080/firestation
     * Cet endpoint permettra d’effectuer les actions suivantes via Post/Put/Delete
     * avec HTTP :
     * ● ajout d'un mapping caserne/adresse
     * ● mettre à jour le numéro de la caserne de pompiers d'une adresse
     * ● supprimer le mapping d'une caserne ou d'une adresse.
     */

    boolean postFirestation(Firestations firestation);

    boolean putFirestation(Firestations firestation, String address);

    boolean deleteFirestation(String address, String stationNumber);

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

    boolean postMedicalRecord(Medicalrecords medicalRecord);

    boolean putMedicalRecord(Medicalrecords medicalrecords, String firstName, String lastName);

    boolean deleteMedicalRecord(String firstName, String lastName);

    /*
     * L'utilisateur accède à l’URL :
     * 
     * http://localhost:9090/firestation?stationNumber=<station_number>
     * 
     * Le système retourne une liste des personnes (prénom, nom, adresse, numéro de
     * téléphone) couvertes par la caserne de pompiers correspondante ainsi qu’un
     * décompte du nombre d’adultes (>18 ans) et du nombre d’enfants (<=18 ans)
     * 
     */
    public List<FoyerChildrenAdultsToFireStation> personsOfStationAdultsAndChild(String stationNumber);

    /*
     * L'utilisateur accède à l’URL :
     *
     * http://localhost:9090/childAlert?address=<address>
     * 
     * Le système retourne une liste des enfants (<=18 ans) habitant à cette
     * adresse. La liste doit comprendre : prénom, nom, âge et une liste des autres
     * membres du foyer. S’il n’y a pas d’enfant, cette url peut renvoyer une chaîne
     * vide.
     */
    public List<ChildAlert> childPersonsAlertAddress(String address) throws IOException, ParseException;

    /*
     * L'utilisateur accède à l’URL :
     *
     * http://localhost:9090/phoneAlert?firestation=< firestation _number>
     *
     * Le système retourne une liste des numéros de téléphone des résidents
     * desservis par la caserne de pompiers.
     */
    public List<PhoneAlert> phoneAlertFirestation(String stationNumber) throws IOException;

    /*
     * L'utilisateur accède à l’URL :
     * 
     * http://localhost:9090/fire?address=<address>
     * 
     * Le système retourne une liste des habitants vivants à l’adresse donnée ainsi
     * que le numéro de la caserne de pompiers la desservant. La liste doit inclure
     * : le nom, le numéro de téléphone, l’âge et les antécédents médicaux
     * (médicaments, posologie et allergies) de chaque personne.
     * 
     */
    public List<FireAddress> fireAddress(String address) throws IOException, ParseException;

    /*
     * L'utilisateur accède à l’URL :
     * 
     * http://localhost:9090/flood/station?station=<a list of station_numbers>
     * 
     * Le système retourne une liste de tous les foyers desservis par la caserne.
     * Cette liste doit regrouper les personnes par adresse. La liste doit inclure :
     * le nom, le numéro de téléphone et l’âge des habitants et faire figurer les
     * antécédents médicaux (médicaments, posologie et allergies) à côté de chaque
     * nom.
     * 
     */
    public List<PersonsFireStation> stationListFirestation(List<String> station) throws IOException, ParseException;

    /*
     * L'utilisateur accède à l’URL :
     * 
     * http://localhost:9090/personInfo?firstName=<firstName>&lastName=<lastName>
     * 
     * Le système retourne le nom, l’adresse, l’âge, l’adresse mail et les
     * antécédents médicaux (médicaments, posologie et allergies) de chaque
     * habitant. Si plusieurs personnes portent le même nom, elles doivent toutes
     * apparaître.
     * 
     */
    public List<PersonInfo> personInfo(String firstName, String lastName) throws IOException, ParseException;

    /*
     * L'utilisateur accède à l’URL :
     * 
     * http://localhost:9090/communityEmail?city=<city>
     * 
     * Le système retourne les adresses mail de tous les habitants de la ville.
     * 
     */
    public List<CommunityEmail> communityEmail(String city) throws IOException;

}