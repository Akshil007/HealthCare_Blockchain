package blockchain.medicalRecords.HealthCareData.services;

import blockchain.medicalRecords.HealthCareData.model.SQL_Details;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.sql.*;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    SQL_Details sqlDetails;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(sqlDetails.getUrl(),sqlDetails.getUn(),sqlDetails.getPass());
            String sql = "Select password from user_details where user_name = " + "\'"+userName+"\'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            String pass_word = rs.getString("password");
            con.close();
            return new User(userName, pass_word, new ArrayList<>());
        } catch (Exception e) {
            System.out.println("loadUserByUsername exception " + e);
            return null;
        }

    }
}
