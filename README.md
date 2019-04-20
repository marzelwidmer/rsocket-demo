# RSocket Messaging in Spring Boot 2.2 with Kotlin


Based on 
- https://spring.io/blog/2019/04/15/spring-tips-rsocket-messaging-in-spring-boot-2-2
- https://www.youtube.com/watch?v=BxHqeq58xrE

## Index Controller
```bash
$ http :8080
{
    "_links": {
        "greet": {
            "href": "http://localhost:8080/greet/{name}",
            "templated": true
        },
        "greet-stream": {
            "href": "http://localhost:8080/greet/sse/{name}",
            "templated": true
        },
        "self": {
            "href": "http://localhost:8080/"
        }
    }
}
```

## RSocket 
```bash
$ http :8080/greet/Basco
{
    "greetings": "Hello Basco @2019-04-21T10:24:32.472Z"
}
```

## RSocket Stream
```bash
$ http :8080/greet/sse/John --stream                                                                  1007ms  Sun Apr 21 12:24:32 2019

HTTP/1.1 200
Content-Type: text/event-stream;charset=UTF-8
Date: Sun, 21 Apr 2019 10:24:44 GMT
Transfer-Encoding: chunked

data:{"greetings":"Hello John 1 @2019-04-21T10:24:44.871Z"}
data:{"greetings":"Hello John 2 @2019-04-21T10:24:45.876Z"}
data:{"greetings":"Hello John 3 @2019-04-21T10:24:46.879Z"}
```