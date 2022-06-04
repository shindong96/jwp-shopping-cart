package woowacourse.shoppingcart.domain.customer;

import woowacourse.shoppingcart.domain.customer.vo.Email;
import woowacourse.shoppingcart.domain.customer.vo.Password;

public class Customer {

    private final Long id;
    private final Email email;
    private final Password password;
    private final String username;

    public Customer(String email, String password, String username) {
        this(null, email, password, username);
    }

    public Customer(Long id, String email, String password, String username) {
        this.id = id;
        this.email = Email.from(email);
        this.password = Password.from(password);
        this.username = username;
    }

    public void checkPasswordIsSame(String password) {
        this.password.checkPasswordIsSame(password);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email.getEmail();
    }

    public String getPassword() {
        return password.getPassword();
    }

    public String getUsername() {
        return username;
    }
}
