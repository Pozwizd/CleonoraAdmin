package com.example.cleonoraadmin.model.customer;

import com.example.cleonoraadmin.entity.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link Customer}
 */

@Data
public class CustomerRequest implements Serializable {
    Long id;
    @NotEmpty(message = "Поле не может быть пустым")
    @Size(max = 25, message = "Имя должно быть не более 25 символов")
    String name;
    @NotEmpty(message = "Поле не может быть пустым")
    @Size(max = 100, message = "Размер поля должен быть не более 100 символов")
    @Email(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-z]{2,3}", message = "Неверный формат email")
    String email;
    @NotEmpty(message = "Поле не может быть пустым")
    @Size(max = 13, message = "Размер номера должен быть не более 13 символов")
    @Pattern(regexp = "\\+380(50|66|95|99|67|68|96|97|98|63|93|73)[0-9]{7}", message = "Неверный формат номера")
    String phoneNumber;
    @Size(min = 3, max = 100, message = "Размер поля должен быть не более 100 символов")
    String password;
    Boolean isActive;
}