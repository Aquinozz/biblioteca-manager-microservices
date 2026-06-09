package auth_service.config;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TokenBlacklist {

    private final Set<String> blacklist = new HashSet<>();

    public void invalidate(String token) {
        blacklist.add(token);
    }

    public boolean isInvalid(String token) {
        return blacklist.contains(token);
    }
}