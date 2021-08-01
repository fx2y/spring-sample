package com.example.audit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
    Integer id;
    String firstName;
    String lastName;
    Integer age;
}
