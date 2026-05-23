# Javalin Annotation Wrapper

`javalin-annotation-wrapper` to lekka biblioteka zaprojektowana w celu uproszczenia tworzenia interfejsów API REST za pomocą Javalin, wykorzystując adnotacje, podobnie jak frameworki takie jak Spring Boot. Umożliwia programistom definiowanie punktów końcowych API, obsługę parametrów żądań i zarządzanie treścią żądań za pomocą deklaratywnych adnotacji, redukując powtarzalny kod i poprawiając czytelność.

## Funkcje

*   **Definicja punktów końcowych oparta na adnotacjach**: Definiuj metody HTTP, ścieżki i inne właściwości punktów końcowych bezpośrednio w metodach obsługujących żądania.
*   **Automatyczne wiązanie parametrów**: Łatwo wiąż parametry ścieżki, parametry zapytania i zawartość ciała żądania z argumentami metod.
*   **Zintegrowane bezpieczeństwo**: Zapewnia mechanizm kontroli bezpieczeństwa na poziomie punktu końcowego za pomocą niestandardowych implementacji `SecurityProvider`.
*   **Integracja z Javalin**: Bezproblemowo integruje się z systemem routingu Javalin.
*   **Obsługa JSON**: Wykorzystuje Gson do automatycznej serializacji i deserializacji treści żądań i odpowiedzi.

## Jak to działa

Biblioteka działa poprzez skanowanie klas w poszukiwaniu określonych adnotacji i dynamiczne rejestrowanie ich w instancji Javalin.

1.  **`@ApiEndpoint`**: Oznacza metodę jako punkt końcowy API, określając jej ścieżkę HTTP, typ (GET, POST, PUT, DELETE itp.) oraz opcjonalne wymagania bezpieczeństwa.
2.  **`@Parameter`**: Używana na parametrach metody do wyodrębniania wartości ze zmiennych ścieżki lub parametrów zapytania.
3.  **`@RequestBody`**: Używana na parametrze metody, aby wskazać, że parametr powinien zostać wypełniony z ciała żądania HTTP (np. ładunku JSON).
4.  **Klasa `Apj`**: Centralna klasa do inicjalizacji wrappera. Przyjmuje instancję `Gson` i instancję `Javalin`.
5.  **`EndpointRegistry`**: Skanuje dostarczone klasy w poszukiwaniu adnotowanych metod i rejestruje je w Javalin za pośrednictwem `JavalinAdapter`.
6.  **`ApiHandler`**: Przetwarza przychodzące żądania, używa refleksji do wywołania odpowiedniej adnotowanej metody oraz obsługuje wiązanie parametrów i deserializację ciała żądania.
7.  **`SecurityFactory` i `EndpointSecurityHandler`**: Zapewniają elastyczny sposób implementacji i stosowania reguł bezpieczeństwa do punktów końcowych.

## Przykład użycia

Stwórzmy proste API do zarządzania użytkownikami.

Najpierw zdefiniuj model `User`:

```java
package me.adrjan.apj.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
}
```

Następnie utwórz klasę `UserProvider`, która będzie zawierać nasze punkty końcowe API:

```java
package me.adrjan.apj.example.provider;

import io.javalin.http.HandlerType;
import me.adrjan.apj.annotation.ApiEndpoint;
import me.adrjan.apj.annotation.Parameter;
import me.adrjan.apj.annotation.RequestBody;
import me.adrjan.apj.rest.EndPointProvider;
import me.adrjan.apj.example.model.User;
import me.adrjan.apj.security.provider.SecurityProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserProvider implements EndPointProvider {

    private final Map<String, User> users = new HashMap<>();

    public UserProvider() {
        users.put("1", new User("1", "John Doe", "john@example.com"));
        users.put("2", new User("2", "Jane Smith", "jane@example.com"));
    }

    @ApiEndpoint(path = "/users", type = HandlerType.GET)
    public Map<String, User> getAllUsers() {
        return users;
    }

    @ApiEndpoint(path = "/users/{id}", type = HandlerType.GET)
    public User getUserById(@Parameter(value = "id", type = Parameter.Type.PATH) String id) {
        return users.get(id);
    }

    @ApiEndpoint(path = "/users", type = HandlerType.POST, requireAuthentication = true, securityProvider = MySecurityProvider.class)
    public User createUser(@RequestBody User user) {
        String newId = UUID.randomUUID().toString();
        user.setId(newId);
        users.put(newId, user);
        return user;
    }

    @ApiEndpoint(path = "/users/{id}", type = HandlerType.PUT)
    public User updateUser(@Parameter(value = "id", type = Parameter.Type.PATH) String id, @RequestBody User user) {
        if (users.containsKey(id)) {
            user.setId(id);
            users.put(id, user);
            return user;
        }
        return null;
    }

    @ApiEndpoint(path = "/users/{id}", type = HandlerType.DELETE)
    public String deleteUser(@Parameter(value = "id", type = Parameter.Type.PATH) String id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return "Użytkownik " + id + " usunięty.";
        }
        return "Użytkownik " + id + " nie znaleziony.";
    }
}
```

Dla przykładu bezpieczeństwa zdefiniujmy prosty `MySecurityProvider`:

```java
package me.adrjan.apj.example.security;

import io.javalin.http.Context;
import me.adrjan.apj.security.SecurityResponse;
import me.adrjan.apj.security.provider.SecurityProvider;

import java.lang.reflect.Method;

public class MySecurityProvider implements SecurityProvider {
    @Override
    public SecurityResponse handle(Context context, Method method) {
        String authHeader = context.header("Token");
        if ("secret".equals(authHeader)) {
            return SecurityResponse.ACCEPT;
        }
        return SecurityResponse.DENY;
    }
}
```

Na koniec zainicjuj Javalin i zarejestruj swój `UserProvider`:

```java
package me.adrjan.apj.example;

import com.google.gson.Gson;
import io.javalin.Javalin;
import me.adrjan.apj.Apj;
import me.adrjan.apj.example.provider.UserProvider;
import me.adrjan.apj.example.security.MySecurityProvider;
import me.adrjan.apj.security.SecurityFactory;

public class Application {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        Gson gson = new Gson();

        Apj apj = new Apj(gson, app);

        apj.getSecurityFactory().registerProvider(MySecurityProvider.class, new MySecurityProvider());

        apj.getEndpointRegistry().register(new UserProvider());

        System.out.println("Serwer Javalin uruchomiony na porcie 7070. Spróbuj uzyskać dostęp:");
        System.out.println("GET http://localhost:7070/users");
        System.out.println("GET http://localhost:7070/users/1");
        System.out.println("POST http://localhost:7070/users (z ciałem JSON i nagłówkiem X-Auth: secret)");
    }
}
