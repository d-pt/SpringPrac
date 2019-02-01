package local.app.user.controller;

import local.app.user.controller.UserController;
import local.app.user.entity.AppRole;
import local.app.user.entity.AppUser;
import local.app.user.exception.AppNoRecordFound;
import local.app.user.service.UserService;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mvc;

    List<AppUser> userList;

    @Before
    public void before() {
        userList = Arrays.asList(
                new AppUser(1, "A", Arrays.asList(
                        new AppRole(1, "admin")
                )),
                new AppUser(2, "B", Arrays.asList(
                        new AppRole(2, "normalUser")
                ))
        );
    }

    @Test
    public void findUser() throws Exception {
        /*
        //Mockito version

        when(userService.findAll())
                .thenReturn(mockUserList()));
        */


        //BDDMockito (Extension of Mockito and provides better readability of method names)
        given(userService.findAll())
                .willReturn(userList);

        mvc.perform(
                    get("/users")
                    .accept(MediaType.APPLICATION_JSON) //just to demo, actually not required
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", IsCollectionWithSize.hasSize(2)))
            .andExpect(jsonPath("$[0].name", Is.is("A")))
            .andExpect(jsonPath("$[1].name", Is.is("B")))
            .andExpect(jsonPath("$[0].roles[0].name", Is.is("admin")))
            ;
    }

    @Test
    public void findUserWithId_exist() throws Exception {
        given(userService.findById(ArgumentMatchers.eq(1L)))
                .willReturn(userList.get(0));

        mvc.perform(get("/users/1")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("name", Is.is("A")))
            .andExpect(jsonPath("roles[0].name", Is.is("admin")))
            ;
    }

    @Test
    public void findUserWithId_does_not_exist() throws Exception {
        given(userService.findById(ArgumentMatchers.eq(3L)))
                .willThrow(new AppNoRecordFound());

        mvc.perform(get("/users/3")
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("errorCode", Is.is(404)))
            ;
    }


    @Test
    public void create_with_bad_req() throws Exception {
        mvc.perform(
                        post("/users")
                            .content("{\"name1\":\" test\"}")
                            .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("errorCode", Is.is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    public void create_with_correct_req() throws Exception {
        given(userService.add(any()))
                .willReturn(new AppUser(1, "test", null));

        mvc.perform(
                post("/users")
                        .content("{\"name\":\" test\"}")
                        .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("uid", Is.is(1)))
            .andExpect(jsonPath("name", Is.is("test")))
            .andExpect(jsonPath("roles", IsNull.nullValue()))
            ;
    }

    @Test
    public void create_with_correct_req_with_role() throws Exception {
        given(userService.add(any()))
                .willReturn(userList.get(0));

        mvc.perform(
                post("/users")
                        .content("{\"name\":\"A\"" +
                                ", \"roles\":[{\"name\":\"admin\"}]}")
                        .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("uid", Is.is(1)))
            .andExpect(jsonPath("name", Is.is("A")))
            .andExpect(jsonPath("roles[0].name", Is.is("admin")))
            ;
    }

    @Test
    public void update_with_null_msg() throws Exception {
        mvc.perform(
                        put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("errorCode", Is.is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    public void update_with_invalid_msg() throws Exception {
        mvc.perform(
                put("/users")
                        .content("{\"name1\":\"A\"}")
                        .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("errorCode", Is.is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    public void update_with_valid_msg() throws Exception {
        given(userService.add(any()))
                .willReturn(userList.get(0));

        mvc.perform(
                put("/users")
                        .content("{\"name\":\"A\"}")
                        .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent());
    }

    @Test
    public void update_with_non_existing() throws Exception {
        willThrow(new AppNoRecordFound())
                .given(userService)
                .update(any());

        mvc.perform(
                put("/users")
                        .content("{\"name\":\"D\"}")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_with_exist() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void delete_with_does_exist() throws Exception {
        willThrow(new AppNoRecordFound())
                .given(userService)
                .delete(1L);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());
    }
}