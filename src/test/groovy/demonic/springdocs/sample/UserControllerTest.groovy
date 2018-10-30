package demonic.springdocs.sample

import demonic.springdocs.SpringDocsApplication
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import org.junit.Rule
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*
import static org.springframework.restdocs.payload.PayloadDocumentation.*
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Autowired 등 옵션을 설정하는것을 쓰기 위해선 ContexntConfiguration 이 필요
@ContextConfiguration(classes = SpringDocsApplication)
class UserControllerTest extends Specification {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation()

    private RequestSpecification spec

    @LocalServerPort
    private int port

    @Value('${test.server.http.scheme}')
    String httpScheme;
    @Value('${test.server.http.host}')
    String httpHost;
    @Value('${test.server.http.port}')
    int serverPort;

    void setup() {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(this.restDocumentation))
                .build()
    }

    /**
     * Get 통신
     */
    def "user-get"(){
        expect:
        Response res = given(this.spec)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            // build/generated-snippets  폴더 내 API 정보가 쌓이는 폴더 지정(이후 웹으로 보여줄때 사용)
            //      - {method-name} 은 실행중인 메서드 이름
            .filter(document("user/{method-name}",
                preprocessRequest(modifyUris()
                        .scheme(httpScheme)
                        .host(httpHost)
                        // 만약 포트가 없다면 removePort() 를 사용하거나 포트번호에 80을 주면 됩니다.
                        // .removePort()
                        .port(serverPort), prettyPrint()),
//                preprocessResponse(prettyPrint()),
                // Request Fields (Get 호출)
                //      - 표 형태로 보여진다.
                requestParameters(
                        parameterWithName("name").description("이름")
                ),
                // 결과값에서 하나라도 빠지면 에러발생.
                //      - null 로 리턴된다 하더라도 등록해두어야 함
                //      - null 이 허용된다면 optional() 을 꼭 주어야 함.
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                        // data 오브젝트 안의 데이터
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("유저 명"),
                        fieldWithPath("data.age").type(JsonFieldType.NUMBER).description("나이").optional(),
                        fieldWithPath("data.address").type(JsonFieldType.STRING).description("주소").optional(),
                        fieldWithPath("data.comment").type(JsonFieldType.STRING).description("유저 메세지"),
                )
            ))
            // 실제 전송할 때 쓸 파라미터 설정
            .param("name","철수")
            // 테스트 URL
            //      - port 는 Random으로 띄워지기 때문에(WebEnvironment.RANDOM_PORT)
            //          위에 @LocalServerPort 을 이용해 테스트에 쓰인 port를 사용하여 호출
            .when().port(this.port).get("/user-get")

        // 1) 통신 결과
        res.then().assertThat().statusCode(200)

        // 2) 결과 값
        def result = res.then().extract().response().asString()
        println "결과=> " + result
    }

    /**
     * Post 통신
     */
    def "user-post"(){
        given:
        Map testParam = new HashMap();
        testParam.put("name", "홍길동")
        testParam.put("age", 20)
        testParam.put("addr", "서울")

        expect:
        Response res = given(this.spec)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            // build/generated-snippets  폴더 내 API 정보가 쌓이는 폴더 지정(이후 웹으로 보여줄때 사용)
            //      {method-name} 은 실행중인 메서드 이름
            .filter(document("user/{method-name}",
                preprocessRequest(modifyUris()
                        .scheme(httpScheme)
                        .host(httpHost)
                        .port(serverPort), prettyPrint()),
                preprocessResponse(prettyPrint()),
                // post의 경우 requestFields로 설정. (requestParameters는 GET 호출시 사용)
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이"),
                        fieldWithPath("addr").type(JsonFieldType.STRING).description("주소"),
                ),
                // 결과값에서 하나라도 빠지면 에러발생.
                //      - null 로 리턴된다 하더라도 등록해두어야 함
                //      - null 이 허용된다면 optional() 을 꼭 주어야 함.
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("이름"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지"),
                        // data 오브젝트 안의 데이터
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("유저 명"),
                        fieldWithPath("data.age").type(JsonFieldType.NUMBER).description("나이").optional(),
                        fieldWithPath("data.address").type(JsonFieldType.STRING).description("주소").optional(),
                        fieldWithPath("data.comment").type(JsonFieldType.STRING).description("유저 메세지"),
                )
            ))
            // 실제 전송할 때 쓸 파라미터 설정
            .body(testParam)
            .when().port(this.port).post("/user-post")

        // 1) 통신 결과
        res.then().assertThat().statusCode(200)

        // 2) 결과 값
        def result = res.then().extract().response().asString()
        println "결과=> " + result
    }
}
