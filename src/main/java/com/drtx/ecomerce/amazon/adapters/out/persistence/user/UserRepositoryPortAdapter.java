package com.drtx.ecomerce.amazon.adapters.out.persistence.user;

import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.ports.out.UserRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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
    @Transactional
    public User updateById(Long id, User user) {
        UserEntity existingUser=repository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("User: "+id));

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAddress(user.getAddress());
        existingUser.setRole(user.getRole());
        existingUser.setPhone(user.getPhone());
        UserEntity updatedUser= repository.save(existingUser);
        return mapper.toDomain(updatedUser);
    }

    @Override
    public void delete(Long id) {
        if(!repository.existsById(id))
            throw  new EntityNotFoundException("User: "+id);
        repository.deleteById(id);
    }
}
