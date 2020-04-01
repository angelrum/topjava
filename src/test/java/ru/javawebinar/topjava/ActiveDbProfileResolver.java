package ru.javawebinar.topjava;

import org.springframework.test.context.ActiveProfilesResolver;
import ru.javawebinar.topjava.service.MealServiceTest.MealServiceDatajpaTest;
import ru.javawebinar.topjava.service.MealServiceTest.MealServiceJdbcTest;
import ru.javawebinar.topjava.service.MealServiceTest.MealServiceJpaTest;
import ru.javawebinar.topjava.service.profile.DataJpaTest;
import ru.javawebinar.topjava.service.profile.JdbcTest;
import ru.javawebinar.topjava.service.profile.JpaTest;

import java.lang.reflect.AnnotatedType;

//http://stackoverflow.com/questions/23871255/spring-profiles-simple-example-of-activeprofilesresolver
public class ActiveDbProfileResolver implements ActiveProfilesResolver {

    @Override
    public String[] resolve(Class<?> aClass) {
        for( AnnotatedType interfaces : aClass.getAnnotatedInterfaces()) {
            if (interfaces.getType()==DataJpaTest.class)
                return new String[]{Profiles.getActiveDbProfile(), Profiles.DATAJPA};
            else if (interfaces.getType()== JpaTest.class)
                return new String[]{Profiles.getActiveDbProfile(), Profiles.JPA};
            else if (interfaces.getType() == JdbcTest.class)
                return new String[]{Profiles.getActiveDbProfile(), Profiles.JDBC};
        }
        return new String[]{Profiles.getActiveDbProfile()};
    }
}