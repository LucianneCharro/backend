
package br.com.fiap.api.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

import java.util.UUID;

import br.com.fiap.api.utils.VideoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class VideosControllerIT {

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setup() {
    RestAssured.port = port;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    // RestAssured.filters(new AllureRestAssured()); // desta forma como estamos utilizando nested class gera informação duplicada
  }

  @Nested
  class CriarVideos {

    @Test
    void devePermitirCriarVideos() {
      var videosRequest = VideoHelper.gerarVideoRequest();

      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(videosRequest)
              .when()
              .post("/videos")
              .then()
              .statusCode(HttpStatus.CREATED.value())
              .body("$", hasKey("id"))
              .body("$", hasKey("titulo"))
              .body("$", hasKey("descricao"))
              .body("$", hasKey("url"))
              .body("$", hasKey("dataPublicacao"))
              .body("$", hasKey("dataCriacao"))
              .body("$", hasKey("gostei"))
              .body("titulo", equalTo(videosRequest.getTitulo()))
              .body("descricao", equalTo(videosRequest.getDescricao()))
              .body("url", equalTo(videosRequest.getUrl()));
    }

    @Test
    void deveGerarExcecao_QuandoCriarVideos_TituloEmBranco() {
      var videoRequest = VideoHelper.gerarVideoRequest();
      videoRequest.setTitulo("");

      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(videoRequest)
              .when()
              .post("/videos")
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body("$", hasKey("message"))
              .body("$", hasKey("errors"))
              .body("message", equalTo("Validation error"))
              .body("errors[0]", equalTo("título não pode estar vazio"));
    }

    @Test
    void deveGerarExcecao_QuandoCriarVideos_DescricaoEmBranco() {
      var videoRequest = VideoHelper.gerarVideoRequest();
      videoRequest.setDescricao("");

      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(videoRequest)
              .when()
              .post("/videos")
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body("$", hasKey("message"))
              .body("$", hasKey("errors"))
              .body("message", equalTo("Validation error"))
              .body("errors[0]", equalTo("descrição não pode estar vazio"));
    }

    @Test
    void deveGerarExcecao_QuandoCriarVideos_CamposInvalidos() throws JsonProcessingException {
      var jsonPayload = new ObjectMapper().readTree(
              "{\"ping\": \"ping\", \"quack\": \"adalberto\"}");

      given()
              .filter(new AllureRestAssured())
              .contentType(ContentType.JSON)
              .body(jsonPayload)
              .when()
              .post("/videos")
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body("$", hasKey("message"))
              .body("$", hasKey("errors"))
              .body("message", equalTo("Validation error"))
              .body("errors[0]", equalTo("descrição não pode estar vazio"))
              .body("errors[1]", equalTo("título não pode estar vazio"));
    }

    @Test
    void deveGerarExcecao_QuandoCriarVideos_PayloadComXml() {
      String xmlPayload = "<video><titulo>John</titulo><descricao>Descrição da mensagem</descricao></video>";

      given()
              .contentType(MediaType.APPLICATION_XML_VALUE)
              .body(xmlPayload)
              .when()
              .post("/videos")
              .then()
              .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @Test
    void devePermitir_CriarVideos_ValidarSchema() {
      var videoRequest = VideoHelper.gerarVideoRequest();

      given()
              .header("Content-Type", "application/json")
              .body(videoRequest)
              .when()
              .post("/videos")
              .then()
              .statusCode(HttpStatus.CREATED.value())
              .header("Content-Type", notNullValue())
              .header("Content-Type", startsWith("application/json"))
              .body(matchesJsonSchemaInClasspath("schemas/VideoResponseSchema.json"));
    }
  }

  @Nested
  class BuscarVideos {

    @Test
    @Sql(scripts = {"/clean.sql",
            "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void devePermitirCriarVideo() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .get("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.OK.value())
              .body(matchesJsonSchemaInClasspath("schemas/VideoResponseSchema.json"));
    }

    @Test
    void deveGerarExcecao_QuandoBuscarVideo_IdNaoExistente() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .get("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.NOT_FOUND.value())
              .body(equalTo("video não encontrado"));
    }

    @Test
    void deveGerarExcecao_QuandoBuscarVideo_IdInvalido() {
      var id = "2";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .get("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(equalTo("ID inválido"));
    }
  }

  @Nested
  class AlterarVideos {

    @Test
    @Sql(scripts = {"/clean.sql",
            "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void devePermitirAlterarVideo() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
      var video = VideoHelper.gerarVideoCompleto();
      video.setId(UUID.fromString(id));

      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(video)
              .when()
              .put("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.OK.value())
              .body("descricao", equalTo(video.getDescricao()));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarVideo_IdNaoCoincide() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
      var video = VideoHelper.gerarVideoCompleto();

      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(video)
              .when()
              .put("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.NOT_FOUND.value())
              .body(equalTo("video não encontrado"));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarVideo_IdInvalido() {
      var id = "5";
      var videos = VideoHelper.gerarVideoCompleto();

      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(videos)
              .when()
              .put("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(equalTo("ID inválido"));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarVideo_PayloadComXml() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
      String xmlPayload = "<video><titulo>John</titulo><descricao>Descrição da mensagem</descricao></video>";

      given()
              .filter(new AllureRestAssured())
              .contentType(ContentType.XML)
              .body(xmlPayload)
              .when()
              .put("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }
  }

  @Nested
  class ApagarVideos {

    @Test
    @Sql(scripts = {"/clean.sql",
            "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void devePermitirApagarVideo() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .delete("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.OK.value())
              .body(equalTo("video removido"));
    }

    @Test
    void deveGerarExcecao_QuandoApagarVideo_IdNaoExistente() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .delete("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.NOT_FOUND.value())
              .body(equalTo("video não encontrado"));
    }

    @Test
    void deveGerarExcecao_QuandoIncrementarGostei_IdInvalido() {
      var id = "2";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .delete("/videos/{id}", id)
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(equalTo("ID inválido"));
    }

  }

  @Nested
  class IncrementarGostei {

    @Test
    @Sql(scripts = {"/clean.sql",
            "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void devePermitirIncrementarGostei() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .put("/videos/{id}/gostei", id)
              .then()
              .statusCode(HttpStatus.OK.value())
              .body("gostei", equalTo(1))
              .body(matchesJsonSchemaInClasspath("schemas/VideoResponseSchema.json"));
    }

    @Test
    void deveGerarExcecao_QuandoIncrementarGostei_IdNaoExistente() {
      var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .put("/videos/{id}/gostei", id)
              .then()
              .statusCode(HttpStatus.NOT_FOUND.value())
              .body(equalTo("video não encontrado"));
    }

    @Test
    void deveGerarExcecao_QuandoIncrementarGostei_IdInvalido() {
      var id = "2";
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .put("/videos/{id}/gostei", id)
              .then()
              .statusCode(HttpStatus.BAD_REQUEST.value())
              .body(equalTo("ID inválido"));
    }
  }

  @Nested
  class ListarVideos {

    @Test
    @Sql(scripts = {"/clean.sql",
            "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void devePermitirListarMensagens() {
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .get("/videos")
              .then()
              .statusCode(HttpStatus.OK.value())
              .body(matchesJsonSchemaInClasspath("schemas/VideoPaginationSchema.json"))
              .body("number", equalTo(0))
              .body("size", equalTo(10))
              .body("totalElements", equalTo(5));
    }

    @Test
    @Sql(scripts = {"/clean.sql",
            "/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void devePermitirListarVideos_QuandoInformadoParametros() {
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .queryParam("page", "2")
              .queryParam("size", "2")
              .when()
              .get("/videos")
              .then()
              .statusCode(HttpStatus.OK.value())
              .body(matchesJsonSchemaInClasspath("schemas/VideoPaginationSchema.json"))
              .body("number", equalTo(2))
              .body("size", equalTo(2))
              .body("totalElements", equalTo(5));
    }

    @Test
    void devePermitirListarVideos_QuandoNaoExisteRegistro() {
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when()
              .get("/videos")
              .then()
              .statusCode(HttpStatus.OK.value())
              .body(matchesJsonSchemaInClasspath("schemas/VideoPaginationSchema.json"))
              .body("number", equalTo(0))
              .body("size", equalTo(10))
              .body("totalElements", equalTo(0));
    }

    @Test
    void devePermitirListarVideos_QuandoReceberParametrosInvalidos() {
      given()
              .filter(new AllureRestAssured())
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .queryParam("page", "2")
              .queryParam("ping", "pong")
              .when()
              .get("/videos")
              .then()
              .statusCode(HttpStatus.OK.value())
              .body(matchesJsonSchemaInClasspath("schemas/VideoPaginationSchema.json"))
              .body("number", equalTo(2))
              .body("size", equalTo(10))
              .body("totalElements", equalTo(0));
    }
  }

}
