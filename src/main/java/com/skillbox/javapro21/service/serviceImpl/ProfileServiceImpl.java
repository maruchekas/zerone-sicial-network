package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.profile.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.response.profile.PersonContent;
import com.skillbox.javapro21.api.response.profile.PostContent;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileServiceImpl extends AbstractMethodClass implements ProfileService {
    private final PersonRepository personRepository;

    @Autowired
    protected ProfileServiceImpl(PersonRepository personRepository) {
        super(personRepository);
        this.personRepository = personRepository;
        this.postRepository = postRepository;
}

    public List<PostContent> getPersonPosts(long id) {
        List<Post> postsList = postRepository.findAll(Sort.by(Sort.Direction.DESC, "time"));
        List<PostContent> personPostContents = new ArrayList<>();
        for (Post post : postsList) {
            if (post.getAuthor().getId() == id) {
                PostContent postContent = new PostContent();
                postContent.setPost(post);
                personPostContents.add(postContent);
            }
        }
        return personPostContents;
    }

    public DataResponse getPerson(long id) throws PersonNotFoundException {
        DataResponse<PersonContent> dataResponse = new DataResponse<>();
        dataResponse.setTimestamp(LocalDateTime.now());
        PersonContent personContent = new PersonContent();
        personContent.setPerson(findPersonById(id));
        dataResponse.setData(personContent);
        return dataResponse;
    }

    public DataResponse post(long id, long publishDate, PostRequest postRequest) throws PersonNotFoundException {
        DataResponse<PostContent> dataResponse = new DataResponse<>();
        Post post = createPost(findPersonById(id), publishDate, postRequest);
        PostContent postContent = new PostContent();
        postContent.setPost(post);
        dataResponse.setTimestamp(LocalDateTime.now());
        dataResponse.setData(postContent);
        postRepository.save(post);
        return dataResponse;
    }



        public DataResponse<MessageOkContent> deletePerson(Principal principal) {
        Person person = findPersonByEmail(principal.getName())
                .setIsBlocked(2)
                .setLastOnlineTime(LocalDateTime.now());
        personRepository.save(person);
        SecurityContextHolder.clearContext();
        return getMessageOkResponse();
    }

    public DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest) {
        Person person = editPerson(editProfileRequest);
        return getDataResponse(person);
    }

    private DataResponse<AuthData> getDataResponse(Person person) {
        return new DataResponse<AuthData>()
                .setTimestamp(LocalDateTime.now())
                .setError("string")
                .setData(getAuthData(person, null));
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
}
