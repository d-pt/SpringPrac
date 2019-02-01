package local.app.user.service;

import local.app.user.entity.AppUser;
import local.app.user.repo.AppUserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractService<AppUser, Long, AppUserRepo> {

    private AppUserRepo userRepo;

    public UserService(AppUserRepo repo) {
        super(repo);
        userRepo = repo;
    }

    @Override
    public Long getId(AppUser user) {
        return user.getUid();
    }
}
