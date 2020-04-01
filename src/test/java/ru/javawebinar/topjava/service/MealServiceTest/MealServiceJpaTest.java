package ru.javawebinar.topjava.service.MealServiceTest;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.service.basic.MealServiceTest;
import ru.javawebinar.topjava.service.profile.JpaTest;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public class MealServiceJpaTest
        extends MealServiceTest implements JpaTest {
}
