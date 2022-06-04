package woowacourse.shoppingcart.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.customer.Customer;
import woowacourse.shoppingcart.dto.ChangePasswordRequest;
import woowacourse.shoppingcart.dto.CustomerRequest;
import woowacourse.shoppingcart.dto.CustomerResponse;
import woowacourse.shoppingcart.exception.DuplicateCustomerException;

@Service
@Transactional(rollbackFor = Exception.class)
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void save(CustomerRequest customerRequest) {
        final Customer customer = new Customer(customerRequest.getEmail(), customerRequest.getPassword(),
            customerRequest.getUsername());
        try {
            customerDao.save(customer);
        } catch (final DuplicateKeyException e) {
            throw new DuplicateCustomerException("해당 아이디가 이미 존재합니다.");
        }
    }

    @Transactional(readOnly = true)
    public CustomerResponse findByEmail(String email) {
        final Customer customer = customerDao.findByEmail(email);

        return new CustomerResponse(customer.getEmail(), customer.getUsername());
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        final Customer foundCustomer = customerDao.findByEmail(email);
        foundCustomer.checkPasswordIsSame(request.getOldPassword());

        final Customer customer = new Customer(foundCustomer.getId(), email, request.getNewPassword(),
            foundCustomer.getUsername());
        customerDao.update(customer);
    }

    public CustomerResponse update(String email, String username) {
        updateCustomer(email, username);

        return findByEmail(email);
    }

    private void updateCustomer(String email, String username) {
        final Customer customer = customerDao.findByEmail(email);

        final Customer updateCustomer = new Customer(customer.getId(), email, customer.getPassword(), username);
        customerDao.update(updateCustomer);
    }

    public void delete(String email, String password) {
        final Customer customer = customerDao.findByEmail(email);
        customer.checkPasswordIsSame(password);

        customerDao.delete(customer.getId());
    }
}
