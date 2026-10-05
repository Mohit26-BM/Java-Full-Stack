package testbank.entity;

public class Customer {

    private int customerId;
    private String name;
    private String email;
    private String phone;

    // Default constructor
    public Customer() {
    }

    // Parameterized constructor
    public Customer(int customerId, String name, String email, String phone) {
        setCustomerId(customerId);
        setName(name);
        setEmail(email);
        setPhone(phone);
    }

    // Customer ID
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be greater than 0."
            );
        }

        this.customerId = customerId;
    }

    // Customer Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer name cannot be empty."
            );
        }

        if (!name.matches("[a-zA-Z ]+")) {
            throw new IllegalArgumentException(
                    "Customer name can contain only alphabets and spaces."
            );
        }

        this.name = name.trim();
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email address cannot be empty."
            );
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException(
                    "Please enter a valid email address."
            );
        }

        this.email = email.trim();
    }

    // Phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Phone number cannot be empty."
            );
        }

        if (!phone.matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits."
            );
        }

        this.phone = phone.trim();
    }

    @Override
    public String toString() {
        return "Customer [customerId=" + customerId
                + ", name=" + name
                + ", email=" + email
                + ", phone=" + phone + "]";
    }
}
