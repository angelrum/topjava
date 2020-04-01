package ru.javawebinar.topjava.service.MealServiceTest;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.service.basic.MealServiceTest;
import ru.javawebinar.topjava.service.profile.JdbcTest;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public class MealServiceJdbcTest
        extends MealServiceTest implements JdbcTest {
}
