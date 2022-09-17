package school.hei.haapi.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import school.hei.haapi.repository.types.PostgresEnumType;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "\"event_participant\"")
@TypeDef(name = "pgsql_enum", typeClass = PostgresEnumType.class)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipant implements Serializable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private String id;

    @OneToOne
    private User user;

    @Type(type = "pgsql_enum")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name="event_id", nullable = false)
    private Event event;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public enum Status {
        EXPECTED, HERE, MISSING
    }
}
