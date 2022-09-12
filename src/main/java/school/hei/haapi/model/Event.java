//package school.hei.haapi.model;
//
//import lombok.*;
//import org.hibernate.Hibernate;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.Objects;
//
//import static javax.persistence.GenerationType.IDENTITY;
//@RequiredArgsConstructor
//@Entity
//@Table(name = "\"event\"")
//@Getter
//@Setter
//@ToString
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class Event implements Serializable {
//    @Id
//    @GeneratedValue(strategy = IDENTITY)
//    private String id;
//    @OneToOne
//    @JoinColumn(name = "place_id",nullable = false)
//    private Place place;
//
//    private int start;
//
//    private int end;
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
//        Event event = (Event) o;
//        return id != null && Objects.equals(id, event.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }
//}