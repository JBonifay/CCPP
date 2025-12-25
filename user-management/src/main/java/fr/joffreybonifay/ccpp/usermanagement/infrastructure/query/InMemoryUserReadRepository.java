package fr.joffreybonifay.ccpp.usermanagement.infrastructure.query;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.UserDTO;
import fr.joffreybonifay.ccpp.usermanagement.application.query.repository.UserReadRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserReadRepository implements UserReadRepository {

    private final Map<UserId, UserDTO> store = new ConcurrentHashMap<>();

    @Override
    public void save(UserDTO user) {
        store.put(user.userId(), user);
    }

    @Override
    public void update(UserDTO user) {
        save(user);
    }

    @Override
    public Optional<UserDTO> findById(UserId userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return store.values().stream()
                .filter(dto -> dto.email().equals(email))
                .findFirst();
    }
}
