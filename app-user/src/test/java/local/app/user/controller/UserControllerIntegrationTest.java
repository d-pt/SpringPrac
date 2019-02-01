package local.app.user.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import local.app.user.Application;
import local.app.user.entity.AppRole;
import local.app.user.entity.AppUser;
import local.app.user.service.UserService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)

//To load PracProjectApplication, this will load mock DB as well
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)

// Cant use WebMvcTest(UserController.class), because of @SpringBootTest, another way to configure MockMvc
@AutoConfigureMockMvc //@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

    private static  AppUser DUMMY_USER
            = new AppUser(0, "C", Arrays.asList(
                    new AppRole(2, "user")));

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserService userService;

    @Test
    public void validInput_createUser() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(DUMMY_USER))
                    )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("name", CoreMatchers.is("C")))
                .andExpect(MockMvcResultMatchers.jsonPath("roles[0].rid", CoreMatchers.is(2)))
        ;
    }

    @Test
    public void update_with_non_existing() throws Exception {
        userService.add(DUMMY_USER).getUid();

        mvc.perform(
                put("/users")
                        .content("{\"name\":\"D\"}")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void update_with_existing() throws Exception {
        userService.add(DUMMY_USER);

        mvc.perform(
                put("/users")
                        .content("{\"name\":\"Z\", \"uid\":\"1\"}")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(MockMvcResultHandlers.print())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());


        AppUser afterUpdate = userService.findById(1L);
        Assert.assertEquals(1L, afterUpdate.getUid());
        Assert.assertEquals("Z", afterUpdate.getName());
    }


    static String toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

}
