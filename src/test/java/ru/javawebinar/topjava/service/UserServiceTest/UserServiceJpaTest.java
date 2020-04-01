package ru.javawebinar.topjava.service.UserServiceTest;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.service.basic.UserServiceTest;
import ru.javawebinar.topjava.service.profile.JpaTest;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public class UserServiceJpaTest
        extends UserServiceTest implements JpaTest {
}
