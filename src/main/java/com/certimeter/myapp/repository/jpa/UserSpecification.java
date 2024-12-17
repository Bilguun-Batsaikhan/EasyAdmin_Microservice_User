package com.certimeter.myapp.repository.jpa;

import com.certimeter.myapp.enumeration.MatchMode;
import com.certimeter.myapp.resourcemodel.User;
import org.springframework.data.jpa.domain.Specification;

/*The UserSpecification class uses the Specification interface from Spring Data JPA to create dynamic queries based on different match modes. The matchMode method generates a Specification that can be used to build SQL queries dynamically.*/
public class UserSpecification {

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