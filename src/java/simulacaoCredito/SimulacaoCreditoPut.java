package simulacaoCredito;

import com.github.javafaker.Faker;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;


public class SimulacaoCreditoPut {
    String baseURL = "http://localhost:8080";
    Faker faker = new Faker(new Locale("pt-BR"));
    String cpf = faker.number().digits(11);

    public String lerJson(String caminhoJson) throws IOException {

        return new String(Files.readAllBytes(Paths.get(caminhoJson)));
    }

    @Before
    public void criarMassaSimulacaoCredito() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    public void testDadoQueInformoInexistenteQuandoAtualizoEntaoRetornaMensagemErroStatusCode404() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/00000000000")
        .then()
                .log().all()
                .statusCode(404)
                .body("mensagem", is("CPF 00000000000 não encontrado"));
    }

     @Test
    public void testDadoQueInformoMesmoCPFQuandoAtualizoEntaoRetornaMensagemErroStatusCode409() throws IOException {
         String jsonBody = this.lerJson("src/Dados/simulacao2.json");
         JSONObject simulacao2 = new JSONObject(jsonBody);
         simulacao2.put("cpf", "17822386034");

         given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(409)
                .body("mensagem", is("CPF já existente"));

        //API retorna STATUS 400, quando deveria ser 409
    }

    @Test
    public void testDadoQueNaoInformoNomeQuandoAtualizoSimilacaoEntaoRetornaMensagemDeErroStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("nome", JSONObject.NULL);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(400)
                .body("nome", is(JSONObject.NULL));
        //@Bug
        //Teste não está retornando Status 400 e sim 200, o que gera erro no teste
    }

    @Test
    public void testDadoQueNaoInformoValorQuandoAtualizoEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("valor", JSONObject.NULL);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(400);
         //@Bug
        //API não identifica que valor está null, aguarda status code 200
    }

    @Test
    public void testDadoQueValorMenorDeMilQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("valor", 800);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(400);
         //@Bug
        //API não identifica valor menor que 1.000
    }

    @Test
    public void testDadoQueValorMaiorDeQuarentaMilQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("valor", 50000);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().ifError()
                .statusCode(400);
        //API  não está identificando valor acima de 50.000
    }

    @Test
    public void testDadoQueAlteroValorEntreMilEQuarentaMilQuandoExecutoEntaoObtenhoStatusCode200() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("valor", 20000);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("Fulano"))
                .body("valor", is(20000));
         //@Bug
        //Não está identificando alteração de valor
    }

    @Test
    public void testDadoQueNaoInformaEmailQuandoExecutoAtualizacaoEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("email", JSONObject.NULL);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(400);
         //@Bug
        //API não identifica alteração de dados, ex: email null
    }
    @Test
    public void testDadoQueEmailEInvalidoQuandoExecutoEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("email", "emailemail.com");
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.email", is("E-mail deve ser um e-mail válido"));

        //este test retorna com mensagens diferentes cada vez que é rodado
        // "erros": {
        //        "email": "E-mail deve ser um e-mail válido"
        //    }
        //{
        //    "erros": {
        //        "email": "não é um endereço de e-mail"
        //    }
        //}
    }
    @Test
    public void testDadoQueNaoInformoNumeroDeParcelasQuandoExecutoEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("parcelas", JSONObject.NULL);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(400);
        //@Bug
        //API não está considerando quantidade de parcelas NULL
    }
    @Test
    public void testDadoQueNumeroDeParcelasDeveSerDeDoisAteQuarentaEOitoQuandoAtualizoNumeroDeParcelasDeDoisParaQuatroEntaoObtenhoStatusCode200() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("parcelas", 4);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("Fulano"))
                .body("email", is("fulano@gmail.com"))
                .body("parcelas", is(4));
    }

    @Test
    public void testDadoQueNumeroDeParcelasNaoDeveSerMaiorQueQuarentaEOitoQuandoAtualizoEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("parcelas", 60);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(400);

        //Testado com número de parcelas 60, retornando status cod 200
    }
    @Test
    public void testDadoQueNumeroDeParcelasDeveSerDeDoisAteQuarentaEoitoQuandoNaoInformoAsParcelasEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("parcelas", JSONObject.NULL);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(400);
        //@Bug
        //API não está identificando parcelas Null
    }

    @Test
    public void testDadoQueAlteroNomeDaSimulacaoQuandoExecutoEntaoObtenhoStatusCode200() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("nome", "Carlos de Tal Silva");
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("Carlos de Tal Silva"))
                .body("email", is("fulano@gmail.com"));
    }

    @Test
    public void testDadoQueAlteroEmailDaSimulacaoQuandoExecutoEntaoObtenhoStatusCode200() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("email", "carlossilva@email.com");
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("Fulano"))
                .body("email", is("carlossilva@email.com"));
    }

    @Test
    public void testDadoQueAlteroSeguroParaFalseQuandoExecutoEntaoObtenhoStatusCode200() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("seguro", false);
        simulacao2.put("cpf", cpf);


        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .put("api/v1/simulacoes/" + cpf)
        .then()
                .log().all()
                .statusCode(200)
                .body("nome", is("Fulano"))
                .body("email", is("fulano@gmail.com"))
                .body("parcelas", is(3))
                .body("seguro", is(false));
    }
}
