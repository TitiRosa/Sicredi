package simulacaoCredito;

import com.github.javafaker.Faker;
import org.json.JSONObject;
import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;


public class SimulacaoCreditoPost {
    String baseURL = "http://localhost:8080";
    Faker faker = new Faker(new Locale("pt-BR"));
    String cpf = faker.number().digits(11);

    public String lerJson(String caminhoJson) throws IOException {

        return new String(Files.readAllBytes(Paths.get(caminhoJson)));
    }

    @Test
    public void testQuandoInseridoNovaSimulacaoEntaoObtendoStatusCode201() throws IOException {
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
                .statusCode(201)
                .body("nome", is("Fulano"))
                .body("cpf", is(this.cpf))
                .body("email", is("fulano@gmail.com"))
                .body("valor", is(1500))
                .body("parcelas", is(3))
                .body("seguro", is(true));
    }

    @Test
    public void testDadoQueInsiroMesmoCpfQuandoConsultoEntaoRetornaMensagemErroStatusCode409() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");

        given()
                .contentType("application/json")
                .log().all()
                .body(jsonBody)
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(409)
                .body("mensagen",is("CPF duplicado"));
        //@Bug
        //Está retornando Status cod 400
    }


    @Test
    public void testDadoQueNaoInsiroCpfQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        Object cpfNull = JSONObject.NULL;
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("cpf", cpfNull);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.cpf", is("CPF não pode ser vazio"));
    }

    @Test
    public void testDadoQueNaoInsiroNomeQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("nome", JSONObject.NULL);
        simulacao2.put("cpf", cpf);


        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.nome", is("Nome não pode ser vazio"));
    }

    @Test
    public void testDadoQueNaoInsiroValorQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("valor", JSONObject.NULL);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.valor", is("Valor não pode ser vazio"));
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
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.valor", is("Valor deve ser igual ou maior a R$ 1.000"));
        //@Bug
        // API não está identificando valor menor a R$ 1.000, e retorna status cod 201
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
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.valor", is("Valor deve ser menor ou igual a R$ 40.000"));
    }

    @Test
    public void testDadoQueInsiroValorEntreMilEQuarentaMilQuandoSimuloEntaoObtenhoStatusCode201() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("valor", 20000);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(201)
                .body("nome", is("Fulano"))
                .body("cpf", is(this.cpf))
                .body("email", is("fulano@gmail.com"))
                .body("valor", is(20000))
                .body("parcelas", is(3))
                .body("seguro", is(true));
    }

    @Test
    public void testDadoQueNaoInformaEmailQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("email", JSONObject.NULL);
        simulacao2.put("cpf", cpf);


        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.email", is("E-mail não deve ser vazio"));
    }

    @Test
    public void testDadoQueInsiroEmailInvalidoQuandoGeroSimilacaoEntaoRetornaMensagemDeErroStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("email", "fuladogmail.com");
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.email" , is("não é um endereço de e-mail"));

        //@Bug
        //Test retorna com mensagens diferentes cada vez que é rodado
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
    public void testDadoQueNaoInformaNumeroDeParcelasQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("parcelas", JSONObject.NULL);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.parcelas", is("Parcelas não pode ser vazio"));
    }

    @Test
    public void testDadoQueNumeroDeParcelasSeraMenorQueDoisQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("parcelas", 1);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes" )
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.parcelas", is("Parcelas deve ser igual ou maior que 2"));
    }

    @Test
    public void testDadoQueNumeroDeParcelasDeveSerDeDoisAteQuarentaEOitoQuandoSimuloEntaoObtenhoStatusCode201() throws IOException {
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
                .statusCode(201)
                .body("nome " , is("Fulano"))
                .body("cpf " , is(this.cpf))
                .body("email " , is("fulano@gmail.com"))
                .body("valor " , is(1500))
                .body("parcelas " , is (3))
                .body("seguro " , is(true));
    }

    @Test
    public void testDadoQueNumeroDeParcelasNaoDeveSerMaiorQueQuarentaEOitoQuandoSimuloEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("parcelas", 60);
        simulacao2.put("cpf", cpf);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())
        .when()
                .post("api/v1/simulacoes")
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.parcelas", is(" "));
        //@Bug
        //Testado com número de parcelas 60, status cod 201, quando deveria ter dado o erro e apontado número de parcelas superior
    }

    @Test
    public void testDadoQueNaoInfomoSeguroQuandoRealizoSimulacaoEntaoObtenhoStatusCode400() throws IOException {
        String jsonBody = this.lerJson("src/Dados/simulacao2.json");
        JSONObject simulacao2 = new JSONObject(jsonBody);
        simulacao2.put("cpf", cpf);
        simulacao2.put("seguro", false);

        given()
                .contentType("application/json")
                .log().all()
                .body(simulacao2.toString())

        .when()
                .post("api/v1/simulacoes")
        .then()
                .log().all()
                .statusCode(400)
                .body("erros.seguros", is("Seguros não pode ser vazio"));
        // @bug
        // API não detecta Seguro: false, retorna status cod 201
    }
}
