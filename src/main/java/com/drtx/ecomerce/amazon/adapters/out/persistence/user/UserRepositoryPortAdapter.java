package com.drtx.ecomerce.amazon.adapters.out.persistence.user;

import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.out.persistence.UserRepositoryPort;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserRepositoryPortAdapter implements UserRepositoryPort {

    private final UserPersistenceRepository repository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryPortAdapter(UserPersistenceRepository repository, UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        if(repository.findByEmail(user.getEmail()).isPresent())
            throw new RuntimeException("Email already exists");
        UserEntity savedEntity=repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll(){
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<User> findByEmail(String email){
        return repository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid){
        return repository.findByUuid(uuid)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public User updateByUuid(UUID uuid, User user) {
        UserEntity existingUser = repository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User with UUID: " + uuid));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAddress(user.getAddress());
        existingUser.setRole(user.getRole());
        existingUser.setPhone(user.getPhone());
        UserEntity updatedUser = repository.save(existingUser);
        return mapper.toDomain(updatedUser);
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        UserEntity user = repository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User with UUID: " + uuid));
        repository.deleteById(user.getId());
    }
}
