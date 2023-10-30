package com.openApi2.OpenApi2.repository;

import com.openApi2.OpenApi2.service.entity.User;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
@N1qlPrimaryIndexed
@ViewIndexed(designDoc = "users",viewName = "all")
public interface CoachRepository extends CouchbaseRepository<User, String> {
    // Puedes definir métodos de consulta personalizados aquí si es necesario.
}
