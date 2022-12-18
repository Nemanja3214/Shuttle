package com.shuttle.credentials;

import java.util.Collection;

public interface ICredentialService {
    public Collection<Credentials> getAll();

    public Credentials findCredentials(Long credentialsId);

    public Credentials insert(Credentials student);
    public Credentials insert(String email,String password);

    public Credentials update(Credentials student);

    public Credentials delete(Long studentId);

    public void deleteAll();
}
