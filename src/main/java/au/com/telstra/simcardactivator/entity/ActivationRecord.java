package au.com.telstra.simcardactivator.entity;

import javax.persistence.*;

@Entity
@Table(name = "activation_records")
public class ActivationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String iccid;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private boolean active;

    public ActivationRecord() {}


    public ActivationRecord(String iccid, String customerEmail, boolean active) {
        this.iccid = iccid;
        this.customerEmail = customerEmail;
        this.active = active;
    }

    public Long getId() { return id; }

    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
