package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.profile.EditProfileResponse;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;

@Component
public class ProfileServiceImpl extends AbstractMethodClass implements ProfileService {
    private final PersonRepository personRepository;

    @Autowired
    protected ProfileServiceImpl(PersonRepository personRepository) {
        super(personRepository);
        this.personRepository = personRepository;
    }

    public DataResponse<EditProfileResponse> getPerson(Principal principal) {
        Person person = findPersonByEmail(principal.getName());
        return getDataResponse(person);
    }

    public DataResponse<EditProfileResponse> editPerson(Principal principal, EditProfileRequest editProfileRequest) {
        Person person = editPerson(editProfileRequest);
        return getDataResponse(person);
    }

    private DataResponse<Person> getDataResponse(Person person) {
        return new DataResponse<Person>()
                .setTimestamp(LocalDateTime.now())
                .setError("string")
                .setData(person);
    }

    private Person editPerson(EditProfileRequest editProfileRequest) {
        Person person = new Person()
                .setFirstName(editProfileRequest.getFirstName())
                .setLastName(editProfileRequest.getLastName())
                .setRegDate(editProfileRequest.getRegDate())
                .setBirthDate(editProfileRequest.getBirthDate())
                .setEmail(editProfileRequest.getEmail())
                .setPhone(editProfileRequest.getPhone())
                .setPhoto(editProfileRequest.getPhoto())
                .setAbout(editProfileRequest.getAbout())
                .setTown(editProfileRequest.getTown())
                .setCountry(editProfileRequest.getCountry());
        personRepository.save(person);
        return person;
    }

    public DataResponse<MessageOkContent> deletePerson(Principal principal) {
        Person person = findPersonByEmail(principal.getName())
                .setIsBlocked(2)
                .setLastOnlineTime(LocalDateTime.now());
        personRepository.save(person);
        SecurityContextHolder.clearContext();
        return getMessageOkResponse();
    }
}
