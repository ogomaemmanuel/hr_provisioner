package com.ogoma.hr_provisioner.auth.repositories;

import com.ogoma.hr_provisioner.auth.entities.UserEntity;
import com.ogoma.hr_provisioner.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends BaseRepository<UserEntity> {

    public Optional<UserEntity> findByEmail(String email);
}
