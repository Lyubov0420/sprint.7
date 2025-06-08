package courier;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {
    private CourierClient courierClient;
    private CourierCreate courier;
    private String courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierCreate.getGeneratorDataCourier();
    }

    @Test
    public void createCourierWithAllParameters() {
        // Создание курьера с параметрами
        ValidatableResponse courierResponse = courierClient.createCourier(courier);
        // Проверка, Status Code = 201 и поле "ok" не пустое
        courierResponse.assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
        // Получение ID курьера из ответа
        ValidatableResponse setCourierIdResponse = courierClient.setCourierID(UserCredentials.getUserCredentials(courier));
        courierId = setCourierIdResponse.extract().path("id").toString();
    }

    @Test
    public void createCourierWithOutFirstName() {

        courier.setFirstName("");

        ValidatableResponse courierResponse = courierClient.createCourier(courier);
        // Проверка, Status Code = 201 и поле "ok" = true
        courierResponse.assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
        // Получение ID курьера из ответа
        ValidatableResponse setCourierIdResponse = courierClient.setCourierID(UserCredentials.getUserCredentials(courier));
        courierId = setCourierIdResponse.extract().path("id").toString();
    }

    @Test
    public void createDuplicateLoginCourier() {

        courierClient.createCourier(courier);
        ValidatableResponse courierResponse = courierClient.createCourier(courier);
        // Проверка, Status Code = 409 и возвращается ожидаемый текст сообщения
        courierResponse.assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
        // Получение ID курьера из ответа
        ValidatableResponse responseCredentials = courierClient.setCourierID(UserCredentials.getUserCredentials(courier));
        courierId = responseCredentials.extract().path("id").toString();
    }

    @Test
    public void createCourierWithOutPassword() {

        courier.setPassword("");

        ValidatableResponse courierResponse = courierClient.createCourier(courier);
        // Проверка, Status Code = 400 и возвращается ожидаемый текст сообщения
        courierResponse.assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }

    @Test
    public void createCourierWithOutLogin() {

        courier.setLogin("");

        ValidatableResponse courierResponse = courierClient.createCourier(courier);
        // Проверка, Status Code = 400 и возвращается ожидаемый текст сообщения
        courierResponse.assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test

    public void createCourierWithOutLoginPasswordFirstName() {

        courier.setLogin("");
        courier.setPassword("");
        courier.setFirstName("");

        ValidatableResponse courierResponse = courierClient.createCourier(courier);
        // Проверка, Status Code = 400 и возвращается ожидаемый текст сообщения
        courierResponse.assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void deleteCourierAfterTest() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId);
        }
    }
}