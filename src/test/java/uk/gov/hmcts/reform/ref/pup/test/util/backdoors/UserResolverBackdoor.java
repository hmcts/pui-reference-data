package uk.gov.hmcts.reform.ref.pup.test.util.backdoors;

import com.google.common.collect.ImmutableSet;
import uk.gov.hmcts.reform.auth.checker.core.SubjectResolver;
import uk.gov.hmcts.reform.auth.checker.core.exceptions.AuthCheckerException;
import uk.gov.hmcts.reform.auth.checker.core.user.User;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

public class UserResolverBackdoor implements SubjectResolver<User> {
    private final ConcurrentHashMap<String, String> tokenToUserMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        registerToken("user", "user");
        registerToken("user2", "user2");
        registerToken("userCaseWorker", "userCaseWorker");
    }

    @Override
    public User getTokenDetails(String token) {
        String userId = tokenToUserMap.get(token);

        if (userId == null) {
            throw new AuthCheckerException("Token not found");
        }

        if (token.equals("userCaseWorker")) {
            return new User(userId, ImmutableSet.of("caseworker-probate"));
        }
        return new User(userId, ImmutableSet.of("citizen"));
    }

    public void registerToken(String token, String userId) {
        tokenToUserMap.put(token, userId);
    }
}
