package org.taskhub.roles;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
// Ohne explicitlyIncluded würde die von der Datenbank generierten ID beim Vergleich
// von zwei Rollen-Objekten verwendet werden
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @EqualsAndHashCode.Include //Nur Namen vergleichen
    @Nonnull
    private String name;
}
