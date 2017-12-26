package entities;

//import entities.UserGroup.GROUP;
import entities.UserGroup.GROUP;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import util.Security;

@Entity
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u")

public class User implements Serializable {

    @Id
    protected String username;
    
    @Column(nullable = false)
    protected String password;
    
    @Column(nullable = false)
    protected String name;
    
    @Column(nullable = false)
    protected String email;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "user")
    protected UserGroup group;

    //@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "user")
    //protected UserGroup group;

    protected User() {
        
    }

    protected User(String username, String password, String name, String email, GROUP group) {
        this.username = username;
        //this.password = password;
        this.password=Security.hashPassword(password);
        this.name = name;
        this.email = email;
        this.group = new UserGroup(group, this);
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
