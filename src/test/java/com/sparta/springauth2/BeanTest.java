package com.sparta.springauth2;

import com.sparta.springauth2.food.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BeanTest {

    @Autowired
    @Qualifier("pizza")
    Food food;  // Chicken과 Pizza 중에 무엇을 받아올 지 몰라 오류 발생
//    Food pizza;

//    @Autowired
//    Food chicken;

    /* @Autowired가 기본적으로 Bean Type (Food)로 DI를 지원하며, 연결이 되지 않을 경우는 Bean Name으로 찾는다는 것을 알 수 있음 */
    @Test
    @DisplayName("Primary와 Qualifier 우선순위 확인")
    void test1() {
        // Chicken은 Primary, Pizza는 Qualifier
        food.eat();
    }
}
