package com.certimeter.myapp.repository.jpa;

import com.certimeter.myapp.enumeration.MatchMode;
import com.certimeter.myapp.resourcemodel.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*The UserSpecification class uses the Specification interface from Spring Data JPA to create dynamic queries based on different match modes. The matchMode method generates a Specification that can be used to build SQL queries dynamically.*/
public class UserSpecification {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Specification<User> matchMode(String field, String value, MatchMode matchMode) {

        switch (matchMode) {
            case STARTS_WITH:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), value + "%");
            case CONTAINS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), "%" + value + "%");
            case NOT_CONTAINS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.notLike(root.get(field), "%" + value + "%");
            case ENDS_WITH:
                return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(field), "%" + value);
            case EQUALS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field), value);
            case NOT_EQUALS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(field), value);
            case DATE_BEFORE:
                return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(field), LocalDate.parse(value, formatter));
            case DATE_AFTER:
                return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(field), LocalDate.parse(value, formatter));
            case DATE_IS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field), LocalDate.parse(value, formatter));
            case DATE_IS_NOT:
                return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(field), LocalDate.parse(value, formatter));
            default:
                return null;
        }
    }
    /*

STARTS_WITH:
Java Code: criteriaBuilder.like(root.get(field), value + "%")
SQL Query: SELECT * FROM users WHERE field LIKE 'value%'
Example: If field is username and value is Bilguun, the query will be SELECT * FROM users WHERE username LIKE 'Bilguun%'.
CONTAINS:
Java Code: criteriaBuilder.like(root.get(field), "%" + value + "%")
SQL Query: SELECT * FROM users WHERE field LIKE '%value%'
Example: If field is username and value is Bilguun, the query will be SELECT * FROM users WHERE username LIKE '%Bilguun%'.
NOT_CONTAINS:
Java Code: criteriaBuilder.notLike(root.get(field), "%" + value + "%")
SQL Query: SELECT * FROM users WHERE field NOT LIKE '%value%'
Example: If field is username and value is Bilguun, the query will be SELECT * FROM users WHERE username NOT LIKE '%Bilguun%'.
ENDS_WITH:
Java Code: criteriaBuilder.like(root.get(field), "%" + value)
SQL Query: SELECT * FROM users WHERE field LIKE '%value'
Example: If field is username and value is Bilguun, the query will be SELECT * FROM users WHERE username LIKE '%Bilguun'.
EQUALS:
Java Code: criteriaBuilder.equal(root.get(field), value)
SQL Query: SELECT * FROM users WHERE field = 'value'
Example: If field is username and value is Bilguun, the query will be SELECT * FROM users WHERE username = 'Bilguun'.
NOT_EQUALS:
Java Code: criteriaBuilder.notEqual(root.get(field), value)
SQL Query: SELECT * FROM users WHERE field != 'value'
Example: If field is username and value is Bilguun, the query will be SELECT * FROM users WHERE username != 'Bilguun'.
    * */
}