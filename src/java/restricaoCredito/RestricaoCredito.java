package restricaoCredito;

import com.github.javafaker.Faker;
import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class RestricaoCredito {

    String baseURL = "http://localhost:8080";
    Faker faker = new Faker(new Locale("pt-BR"));
    String cpf = faker.number().digits(11);

    public String lerJson(String caminhoJson) throws IOException {

        return new String(Files.readAllBytes(Paths.get(caminhoJson)));
    }

    @Test
    public void testDadoVerificarRestricaoQuandoInseridoCpfEntaoObtenhoPossuiRestricao() throws IOException {
        String jsonBody = lerJson("src/Dados/restricao.json");

                given()
                  .contentType("application/json")
                  .log().all()

                .when()
                   .get("/api/v1/restricoes/97093236014")
                .then()
                   .log().all()
                   .statusCode(200)
                .body("mensagem", is("O CPF 97093236014 tem problema"));
    }

    @Test
    public void testDadoVerificarRestricaoQuandoInseridoCpfEntaoObtenhoNaoPossuiRestricao() throws IOException {

        given()
                .contentType("application/json")
                .log().all()

        .when()
                  .get("/api/v1/restricoes/" + this.cpf)
        .then()
                  .log().all()
                  .statusCode(204);

    }
}
