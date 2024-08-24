package ru.javawebinar.topjava.service.impl;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveDbProfileResolver;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.BaseMealServiceTest;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class, value = Profiles.DATAJPA)
public class DataJpaMealServiceTest extends BaseMealServiceTest {
}
