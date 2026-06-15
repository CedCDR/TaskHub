package org.taskhub.roles;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
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
    @Column(unique = true) //Keine Rollen mit identischen Namen
    @Nonnull
    private String name;
}
