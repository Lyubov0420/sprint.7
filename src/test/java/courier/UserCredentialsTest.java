package courier;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class UserCredentialsTest {
    private CourierClient courierClient;
    private CourierCreate courier;
    private String courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierCreate.getGeneratorDataCourier();
        courierClient.createCourier(courier);
    }

    @Test
    public void checkAuthorizationWithAllParameters() {

        ValidatableResponse setCourierIdResponse = courierClient.setCourierID(UserCredentials.getUserCredentials(courier));
        courierId = setCourierIdResponse.extract().path("id").toString();
        // Проверка, Status Code = 200 и поле "id" не пустое
        setCourierIdResponse.assertThat()
                .statusCode(200)
                .and()
                .body("id", notNullValue());
    }

    @Test
    public void checkAuthorizationWithOutLogin() {

        courier.setLogin("");
        ValidatableResponse setCourierIdResponse = courierClient.setCourierID(UserCredentials.getUserCredentials(courier));
        // Проверка, что Status Code = 400 и возвращается ожидаемый текст сообщения
        setCourierIdResponse.assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void checkAuthorizationWithOutPassword() {

        courier.setPassword("");
        ValidatableResponse setCourierIdResponse = courierClient.setCourierID(UserCredentials.getUserCredentials(courier));
        // Проверка, что Status Code = 400 и возвращается ожидаемый текст сообщения
        setCourierIdResponse.assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void checkAuthorizationWithOutLoginAndPassword() {

        courier.setLogin("");
        courier.setPassword("");
        ValidatableResponse setCourierIdResponse = courierClient.setCourierID(UserCredentials.getUserCredentials(courier));
        // Проверка, что Status Code = 400 и возвращается ожидаемый текст сообщения
        setCourierIdResponse.assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void checkAuthorizationWithLoginNotRegistered() {

        courier.setLogin("null");
        ValidatableResponse setCourierIdResponse = courierClient.setCourierID(UserCredentials.getUserCredentials(courier));
        // Проверка, что Status Code = 404 и возвращается ожидаемый текст сообщения
        setCourierIdResponse.assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @After
    public void deleteCourierAfterTest() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId);
        }
    }
}