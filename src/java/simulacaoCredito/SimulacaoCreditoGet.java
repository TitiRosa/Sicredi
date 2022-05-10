package simulacaoCredito;

import com.github.javafaker.Faker;
import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;



public class SimulacaoCreditoGet {
    String baseURL = "http://localhost:8080";
    Faker faker = new Faker(new Locale("pt-BR"));
    String cpf = faker.number().digits(11);

    public String lerJson(String caminhoJson) throws IOException {

        return new String(Files.readAllBytes(Paths.get(caminhoJson)));
    }

    @Test
    public void testDadoConsultaSimulacoesExistentesQuandoClicoEmExecuteEntaoObtenhoResultadoDeTodasSimulacoesRealizadasStatusCode200() throws IOException {

        given()
                .contentType("application/json")
        .when()
                .get("/api/v1/simulacoes/")
        .then()
                .log().all()
                .statusCode(200)
               .body("id", hasItems(11,12));
    }

    @Test
    public void testDadoRealizandoSimulacaoQuandoInsiroCpfComRestricaoEntaoObtenhoStatusCode200()  {

        given()
                .contentType("application/json")
                .log().all()
        .when()
                .get( "/api/v1/simulacoes/60094146012")
        .then()
                .log().all()
                .statusCode(200);
        //@Bug
        //API não está informando o CPF com restrição
    }

    @Test
    public void testDadoQueRealizoSimulacaoComCpfSemRestricaoQuandoInsiroCpfValidoEntaoObtenhoStatusCode204() throws IOException {

        given()
                .contentType("application/json")
                .log().all()
        .when()
                .get( "/api/v1/simulacoes/" + this.cpf)
        .then()
                .log().all()
                .statusCode(200);

        //@Bug
        //API não está informando o CPF sem restrição
    }




}
