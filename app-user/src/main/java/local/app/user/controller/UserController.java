package local.app.user.controller;

import local.app.user.entity.AppUser;
import local.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * Finds all users
     * @return List of AppUsers and HTTP status OK(Even with empty list)
     */
    @GetMapping
    public List<AppUser> findUser() {
        return  userService.findAll();
    }

    /**
     * Find user by id
     * @param id
     * @return  If user found AppUsers and HTTP status OK
     *          Else HTTP NOT FOUND (404)
     */
    @GetMapping("/{id}")
    public AppUser findUser(@PathVariable("id") Long id) {
        return  userService.findById(id);
    }

    /**
     * Create user
     * @param user
     * @return If user param is valid then returns created AppUser with HTTP CREATED (201)
     *          Else HTTP BAD REQUEST(400)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) //201
    public AppUser create(@Valid @RequestBody AppUser user) {
        return userService.add(user);
    }

    /**
     *  Update user
     * @param user
     *
     * Returns HTTP NO CONTENT(204), HTTP BAD REQUEST(400), HTTP NOT FOUND(404)
     */
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT) //204
    public void update(@Valid @RequestBody AppUser user) {
        userService.update(user);
    }

    /**
     * Delete user
     * @param id
     * Returns HTTP NO CONTENT(204), HTTP NOT FOUND(404)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //204
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }
}
