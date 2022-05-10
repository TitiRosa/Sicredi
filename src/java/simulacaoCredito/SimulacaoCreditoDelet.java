package simulacaoCredito;

import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class SimulacaoCreditoDelet {
    String baseURL = "http://localhost:8080";
    Faker faker = new Faker(new Locale("pt-BR"));
    String cpf = faker.number().digits(11);
    Response response;
    Integer id;

    public String lerJson(String caminhoJson) throws IOException {

        return new String(Files.readAllBytes(Paths.get(caminhoJson)));
    }

    @Before
    public void criarIdParaDelet() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("cpf", cpf);

        response = given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
                .when()
                .post("api/v1/simulacoes" );
        this.id = response.body().jsonPath().getInt("id");
    }

    @Test
    public void testDadoQueQueroExcluirSimulacaoQuandoInformoCpfEntaoSimulacaoExcluidaObtenhoStatusCode200(){

        given()
                .contentType("application/json")
                .log().all()
        .when()
                .delete("/api/v1/simulacoes/" + id)
        .then()
                .log().all()
                .statusCode(200);
    }
    @Test
    public void testDadoQueInformoIdInexistenteQuandoTentoExcluirSimulacaoEntaoObtenhoStatusCode404(){

        given()
                .contentType("application/json")
                .log().all()
            .when()
                .delete("/api/v1/simulacoes/00" )
            .then()
                .log().all()
                .statusCode(404)
                .body("erros.id", is("Simulação não encontrada"));
        //@Bug
        //API não identifica ID inexistente
    }
}
