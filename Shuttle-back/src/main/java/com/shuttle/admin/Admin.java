package com.shuttle.admin;

import com.shuttle.credentials.Credentials;
import com.shuttle.user.GenericUser;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Admin extends GenericUser {
    boolean active;
}
