package com.certimetergroup.myapp.service;

import com.certimetergroup.myapp.dto.UserDTO;
import com.certimetergroup.myapp.resourcemodel.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {

    @Mapping(target = "age", source = "birthdate", qualifiedByName = "calculateAge")
    @Mapping(target = "birthdate", source = "birthdate")
        // Explicitly map birthdate
    UserDTO userToUserDto(User user);

    //org.hibernate.HibernateException: identifier of an instance of com.certimetergroup.myapp.resourcemodel.User was altered from 0 to 102
    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(User user, @MappingTarget User existingUser);

    List<UserDTO> usersToUserDtos(List<User> users);

    User userDtoToUser(UserDTO userDTO);

    @Named("calculateAge")
    static int calculateAge(LocalDate birthdate) {
        if (birthdate == null) {
            return 0;
        }
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}




