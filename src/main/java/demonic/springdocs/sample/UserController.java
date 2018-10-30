package demonic.springdocs.sample;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 테스트 컨트롤러
 */
@RestController
public class UserController {

    /**
     * GET 방식 호출
     * @param name
     * @return
     */
    @GetMapping("/user-get")
    public Map user_get(@RequestParam String name){
        User user = new User();
        user.setName(name);
        user.setAge(24);
        user.setAddress("서울");
        user.setComment("GET 호출 입니다");

        Map result = new HashMap<>();
        result.put("code", "0000");
        result.put("message", "OK");
        result.put("data", user);

        return result;
    }


    /**
     * POST 방식 호출
     */
    @PostMapping("/user-post")
    public Map user_post(@RequestBody User user){
        user.setComment("POST 호출 입니다");

        Map result = new HashMap<>();
        result.put("code", "0000");
        result.put("message", "OK");
        result.put("data", user);

        return result;
    }
}
