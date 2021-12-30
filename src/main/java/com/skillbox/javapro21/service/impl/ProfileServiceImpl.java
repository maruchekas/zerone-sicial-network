package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.MessageOkContent;
import com.skillbox.javapro21.api.request.profile.EditProfileRequest;
import com.skillbox.javapro21.api.response.account.AuthData;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDateTime;

@Component
public class ProfileServiceImpl extends AbstractMethodClass implements ProfileService {
    private final PersonRepository personRepository;
    private final PostRepository postRepository;

    @Autowired
    protected ProfileServiceImpl(PersonRepository personRepository, PostRepository postRepository) {
        super(personRepository);
        this.personRepository = personRepository;
        this.postRepository = postRepository;
    }

    public DataResponse<AuthData> getPerson(Principal principal) {
        Person person = findPersonByEmail(principal.getName());
        return getDataResponse(person);
    }

    public DataResponse getPersonById(long id) throws PersonNotFoundException {
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
        dataResponse.setError("ok");
        dataResponse.setTimestamp(LocalDateTime.now());
        dataResponse.setData(postContent);
        postRepository.save(post);
        return dataResponse;
    }

    public ListDataResponse getWall(long id, int offset, int itemPerPage) throws PersonNotFoundException {
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException();
        }
        ListDataResponse<PostContent> listDataResponse = new ListDataResponse<>();
        List<PostContent> personContentList = getPersonPosts(id);

        listDataResponse.setError("ok");
        listDataResponse.setTimestamp(LocalDateTime.now());
        listDataResponse.setTotal(personContentList.size());
        listDataResponse.setOffset(offset);
        listDataResponse.setPerPage(itemPerPage);
        listDataResponse.setData(personContentList);
        return listDataResponse;
    }

    private Post createPost(Person person, long publishDate, PostRequest postRequest) {
        Post post = new Post();
        post.setTime(getLocalDateTime(publishDate));
        post.setTitle(postRequest.getTitle());
        post.setPostText(postRequest.getPostText());
        post.setIsBlocked(0);
        post.setAuthor(person);
        return post;
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

    public DataResponse deletePerson(Principal principal) {
        Person person = findPersonByEmail(principal.getName());
        person.setIsBlocked(2);
        person.setLastOnlineTime(LocalDateTime.now());
        personRepository.save(person);
        SecurityContextHolder.clearContext();
        return getMessageOkResponse();
    }

    public DataResponse<AuthData> editPerson(Principal principal, EditProfileRequest editProfileRequest) {
        Person person = findPersonByEmail(principal.getName());
        editPerson(person, editProfileRequest);
        return getDataResponse(person);
    }

    private DataResponse<AuthData> getDataResponse(Person person) {
        return new DataResponse<AuthData>()
                .setTimestamp(LocalDateTime.now())
                .setError("string")
                .setData(getAuthData(person, null));
    }

    private Person editPerson(Person person, EditProfileRequest editProfileRequest) {
        person
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

    private DataResponse<AuthData> getDataResponse(Person person) {
        return new DataResponse<AuthData>()
                .setTimestamp(LocalDateTime.now())
                .setError("string")
                .setData(getAuthData(person, null));
    }
}
