package org.govithub.govac.consumerapi.dao;

import org.govithub.govac.consumerapi.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findById(long id);
    
    User findByUsername(String username);
    
}