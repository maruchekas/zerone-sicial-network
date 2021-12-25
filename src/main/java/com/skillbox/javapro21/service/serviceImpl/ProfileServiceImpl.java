package com.skillbox.javapro21.service.serviceImpl;

import com.skillbox.javapro21.api.request.profile.PostRequest;
import com.skillbox.javapro21.api.response.DataResponse;
import com.skillbox.javapro21.api.response.ListDataResponse;
import com.skillbox.javapro21.api.response.profile.PersonContent;
import com.skillbox.javapro21.api.response.profile.PostContent;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.exception.PersonNotFoundException;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileServiceImpl implements ProfileService {

    private final PostRepository postRepository;
    private final PersonRepository personRepository;

    @Autowired
    public ProfileServiceImpl(PostRepository postRepository, PersonRepository personRepository) {
        this.postRepository = postRepository;
        this.personRepository = personRepository;
    }

    public Person findPersonById(long id) throws PersonNotFoundException {
        return personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());
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

    public ListDataResponse getWall(long id, int offset, int itemPerPage) {
        ListDataResponse<PostContent> listDataResponse = new ListDataResponse<>();
        List<PostContent> personContentList = getPersonPosts(id);

        listDataResponse.setTimestamp(LocalDateTime.now());
        listDataResponse.setTotal(personContentList.size());
        listDataResponse.setOffset(offset);
        listDataResponse.setPerPage(itemPerPage);
        listDataResponse.setData(personContentList);
        return listDataResponse;
    }

    private Post createPost(Person person, long publishDate, PostRequest postRequest) {
        Post post = new Post();
        post.setTime(Instant.ofEpochMilli(publishDate).atZone(ZoneId.systemDefault()).toLocalDateTime());
        post.setTitle(postRequest.getTitle());
        post.setPostText(postRequest.getPostText());
        post.setIsBlocked(0);
        post.setAuthor(person);
        return post;
    }
}
